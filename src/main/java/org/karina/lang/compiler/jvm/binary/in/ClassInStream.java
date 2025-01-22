package org.karina.lang.compiler.jvm.binary.in;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.jvm.model.jvm.JFieldModel;
import org.karina.lang.compiler.jvm.model.jvm.JMethodModel;
import org.karina.lang.compiler.model.Signature;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ClassInStream {
    private final InputStream in;

    public ClassInStream(InputStream in) {
        this.in = in;
    }

    public int readInt() throws IOException {
        return (this.in.read() << 24) | (this.in.read() << 16) | (this.in.read() << 8) | this.in.read();
    }

    public int readByte() throws IOException {
        return this.in.read();
    }


    public String readString() throws IOException {
        int length = readInt();
        byte[] bytes = new byte[length];
        var read = this.in.read(bytes);
        if (read != length) {
            throw new IOException("Expected " + length + " bytes, but got " + read);
        }
        return new String(bytes);
    }

    public ImmutableList<String> readStrings() throws IOException {
        int length = readInt();
        String[] strings = new String[length];
        for (int i = 0; i < length; i++) {
            strings[i] = readString();
        }
        return ImmutableList.copyOf(strings);
    }

    public ObjectPath readObjectPath() throws IOException {
        return new ObjectPath(readStrings());
    }

    public Signature readSignature(TextSource source) throws IOException {
        var returnType = readType(source);
        int length = readInt();
        var parameters = new KType[length];
        for (int i = 0; i < length; i++) {
            parameters[i] = readType(source);
        }
        return new Signature(ImmutableList.copyOf(parameters), returnType);
    }

    public @Nullable ClassPointer readClassPointer() throws IOException {
        var type = readByte();
        if (type == 0) {
            return null;
        } else if (type == 1) {
            var path = readObjectPath();
            //TODO test existance of class
            return ClassPointer.of(path);
        } else {
            throw new IOException("Unknown class pointer type: " + type);
        }
    }

    public ImmutableList<ClassPointer> readClassPointerList() throws IOException {
        int length = readInt();
        var pointers = new ClassPointer[length];
        for (int i = 0; i < length; i++) {
            pointers[i] = readClassPointer();
            if (pointers[i] == null) {
                throw new IOException("Class pointer cannot be null in a list");
            }
        }
        return ImmutableList.copyOf(pointers);
    }

    public JFieldModel readField(ClassPointer owningClass, TextSource source) throws IOException {
        var name = readString();
        var type = readType(source);
        var modifiers = readInt();
        return new JFieldModel(name, type, modifiers, source.emptyRegion(), owningClass);
    }

    public ImmutableList<JFieldModel> readFieldList(ClassPointer owningClass, TextSource source) throws IOException {
        int length = readInt();
        var fields = new JFieldModel[length];
        for (int i = 0; i < length; i++) {
            fields[i] = readField(owningClass, source);
        }
        return ImmutableList.copyOf(fields);
    }

    public JMethodModel loadMethod(ClassPointer owningClass, TextSource source) throws IOException {
        var emptyRegion = source.emptyRegion();

        var name = readString();
        var modifiers = readInt();
        var signature = readSignature(source);
        var parameters = readStrings();
        var generics = readStrings().stream().map(ref -> new Generic(emptyRegion, ref)).toList();
        return new JMethodModel(
                name,
                modifiers,
                signature,
                ImmutableList.copyOf(parameters),
                ImmutableList.copyOf(generics),
                null,
                emptyRegion,
                owningClass
        );
    }

    public ImmutableList<JMethodModel> readMethodList(ClassPointer owningClass, TextSource source) throws IOException {
        int length = readInt();
        var methods = new JMethodModel[length];
        for (int i = 0; i < length; i++) {
            methods[i] = loadMethod(owningClass, source);
        }
        return ImmutableList.copyOf(methods);
    }


    public KType readType(TextSource source) throws IOException {
        var type = readByte();
        return switch (type) {
            case 1 -> new KType.AnyClass();
            case 2 -> {
                var inner = readType(source);
                yield new KType.ArrayType(inner);
            }
            case 3 -> {
                var classPointer = readClassPointer();
                var genericCount = readInt();
                var generics = new KType[genericCount];
                for (int i = 0; i < genericCount; i++) {
                    generics[i] = readType(source);
                }
                yield new KType.ClassType(classPointer, List.of(generics));
            }
            case 4 -> {
                var returnType = readType(source);
                var argumentCount = readInt();
                var arguments = new KType[argumentCount];
                for (int i = 0; i < argumentCount; i++) {
                    arguments[i] = readType(source);
                }
                var interfaceCount = readInt();
                var interfaces = new KType[interfaceCount];
                for (int i = 0; i < interfaceCount; i++) {
                    interfaces[i] = readType(source);
                }
                yield new KType.FunctionType(List.of(arguments), returnType, List.of(interfaces));
            }
            case 5 -> {
                var name = readString();
                yield new KType.GenericLink(new Generic(source.emptyRegion(), name));
            }
            case 6 -> new KType.PrimitiveType(KType.KPrimitive.BOOL);
            case 7 -> new KType.PrimitiveType(KType.KPrimitive.BYTE);
            case 8 -> new KType.PrimitiveType(KType.KPrimitive.CHAR);
            case 9 -> new KType.PrimitiveType(KType.KPrimitive.DOUBLE);
            case 10 -> new KType.PrimitiveType(KType.KPrimitive.FLOAT);
            case 11 -> new KType.PrimitiveType(KType.KPrimitive.INT);
            case 12 -> new KType.PrimitiveType(KType.KPrimitive.LONG);
            case 13 -> new KType.PrimitiveType(KType.KPrimitive.SHORT);
            case 14 -> new KType.PrimitiveType(KType.KPrimitive.VOID);
            default -> {
                throw new IOException("Unknown type: " + type);
            }
        };
    }

    public int[] readIntList() throws IOException {
        int length = readInt();
        var ints = new int[length];
        for (int i = 0; i < length; i++) {
            ints[i] = readInt();
        }
        return ints;
    }

    public void assertEmpty() throws IOException {
        if (this.in.read() != -1) {
            throw new IOException("Expected no more data, but got " + this.in.available() + " bytes");
        }
    }
}
