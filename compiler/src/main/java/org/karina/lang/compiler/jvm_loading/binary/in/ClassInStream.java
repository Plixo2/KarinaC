package org.karina.lang.compiler.jvm_loading.binary.in;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.TextSource;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.utils.ObjectPath;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

    public Signature readSignature(TextSource source, KnownGenerics generics) throws IOException {
        var returnType = readType(source, generics);
        int length = readInt();
        var parameters = new KType[length];
        for (int i = 0; i < length; i++) {
            parameters[i] = readType(source, generics);
        }
        return new Signature(ImmutableList.copyOf(parameters), returnType);
    }

    public @Nullable ClassPointer readClassPointer(TextSource source) throws IOException {
        var type = readByte();
        if (type == 0) {
            return null;
        } else if (type == 1) {
            var path = readObjectPath();
            //TODO test existence of class
            return ClassPointer.of(source.emptyRegion(), path);
        } else {
            throw new IOException("Unknown class methodPointer fieldType: " + type);
        }
    }

    public ImmutableList<ClassPointer> readClassPointerList(TextSource source) throws IOException {
        int length = readInt();
        var pointers = new ClassPointer[length];
        for (int i = 0; i < length; i++) {
            pointers[i] = readClassPointer(source);
            if (pointers[i] == null) {
                throw new IOException("Class methodPointer cannot be null in a list");
            }
        }
        return ImmutableList.copyOf(pointers);
    }

//    public JFieldModel readField(ClassPointer owningClass, TextSource source, KnownGenerics generics) throws IOException {
//        var name = readString();
//        var type = readType(source, generics);
//        var modifiers = readInt();
//        return new JFieldModel(name, type, modifiers, source.emptyRegion(), owningClass);
//    }
//
//    public ImmutableList<JFieldModel> readFieldList(ClassPointer owningClass, TextSource source, KnownGenerics generics) throws IOException {
//        int length = readInt();
//        var fields = new JFieldModel[length];
//        for (int i = 0; i < length; i++) {
//            fields[i] = readField(owningClass, source, generics);
//        }
//        return ImmutableList.copyOf(fields);
//    }

    public List<Generic> readGenericList(TextSource source, KnownGenerics generics) throws IOException {
        int length = readInt();

        var thisGenerics = new ArrayList<Generic>();

        for (int i = 0; i < length; i++) {
            var name = readString();
            var generic = new Generic(source.emptyRegion(), name);
            thisGenerics.add(generic);
            generics.add(generic);
        }
        for (int i = 0; i < length; i++) {
            var generic = thisGenerics.get(i);

            var boundsCount = readInt();
            var bounds = new KType[boundsCount];
            for (int boundI = 0; boundI < boundsCount; boundI++) {
                bounds[boundI] = readType(source, generics);
            }
            var containsSuper = readByte() == 1;
            var superType = containsSuper ? readType(source, generics) : null;
            generic.updateBounds(superType, Arrays.stream(bounds).toList());

        }

        return thisGenerics;
    }



//    public JMethodModel loadMethod(ClassPointer owningClass, TextSource source, KnownGenerics generics) throws IOException {
//        var emptyRegion = source.emptyRegion();
//
//        var name = readString();
//        var modifiers = readInt();
//        var signature = readSignature(source, generics);
//        var parameters = readStrings();
//        var generics = readStrings().stream().map(ref -> new Generic(emptyRegion, ref)).toList();
//        return new JMethodModel(
//                name,
//                modifiers,
//                signature,
//                ImmutableList.copyOf(parameters),
//                ImmutableList.copyOf(generics),
//                null,
//                emptyRegion,
//                owningClass
//        );
//    }
//
//    public ImmutableList<JMethodModel> readMethodList(ClassPointer owningClass, TextSource source) throws IOException {
//        int length = readInt();
//        var methods = new JMethodModel[length];
//        for (int i = 0; i < length; i++) {
//            methods[i] = loadMethod(owningClass, source);
//        }
//        return ImmutableList.copyOf(methods);
//    }


    public KType readType(TextSource source, KnownGenerics generics) throws IOException {
        var type = readByte();
        return switch (type) {
            case 1 -> KType.ROOT;
            case 2 -> {
                var inner = readType(source, generics);
                yield new KType.ArrayType(inner);
            }
            case 3 -> {
                var classPointer = readClassPointer(source);
                var genericCount = readInt();
                var genericsArray = new KType[genericCount];
                for (int i = 0; i < genericCount; i++) {
                    genericsArray[i] = readType(source, generics);
                }
                assert classPointer != null;
                yield classPointer.implement(List.of(genericsArray));
            }
            case 4 -> {
                var returnType = readType(source, generics);
                var argumentCount = readInt();
                var arguments = new KType[argumentCount];
                for (int i = 0; i < argumentCount; i++) {
                    arguments[i] = readType(source, generics);
                }
                var interfaceCount = readInt();
                var interfaces = new KType[interfaceCount];
                for (int i = 0; i < interfaceCount; i++) {
                    interfaces[i] = readType(source, generics);
                }
                yield new KType.FunctionType(List.of(arguments), returnType, List.of(interfaces));
            }
            case 5 -> {
                var name = readString();
                var generic = generics.get(name);
                if (generic != null) {
                    yield new KType.GenericLink(generic);
                } else {
                    throw  new IOException("Unknown generic type: " + name + ", known generics: " + generics.generics.keySet());
                }
            }
            case 6 -> KType.BOOL;
            case 7 -> KType.BYTE;
            case 8 -> KType.CHAR;
            case 9 -> KType.DOUBLE;
            case 10 -> KType.FLOAT;
            case 11 -> KType.INT;
            case 12 -> KType.LONG;
            case 13 -> KType.SHORT;
            case 14 -> KType.NONE;
            default -> {
                throw new IOException("Unknown fieldType: " + type);
            }
        };
    }

    public KType.ClassType readClassType(TextSource source, KnownGenerics generics) throws IOException {
        var type = readByte();
        if (type != 3) {
            throw new IOException("Expected class type, but got " + type);
        }
        var classPointer = readClassPointer(source);
        var genericCount = readInt();
        var genericsArray = new KType[genericCount];
        for (int i = 0; i < genericCount; i++) {
            genericsArray[i] = readType(source, generics);
        }
        assert classPointer != null;
        return classPointer.implement(List.of(genericsArray));
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

    public static class KnownGenerics {
        private final Map<String, Generic> generics;

        private KnownGenerics(Map<String, Generic> generics) {
            this.generics = generics;
        }

        public KnownGenerics() {
            this.generics = new HashMap<>();
        }

        public void add(Generic generic) {
            this.generics.put(generic.name(), generic);
        }

        public KnownGenerics copy() {
            return new KnownGenerics(new HashMap<>(this.generics));
        }

        public @Nullable Generic get(String name) {
            return this.generics.get(name);
        }
    }
}
