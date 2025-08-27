package org.karina.lang.compiler.jvm_loading.binary.out;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClassOutStream {
    private final OutputStream out;

    public ClassOutStream(OutputStream out) {
        this.out = out;
    }

    public void writeInt(int value) throws IOException {
        this.out.write(value >>> 24);
        this.out.write(value >>> 16);
        this.out.write(value >>> 8);
        this.out.write(value);
    }

    public void writeByte(int value) throws IOException {
        this.out.write(value);
    }

    public void writeBytes(byte[] bytes) throws IOException {
        this.out.write(bytes);
    }


    public void writeString(String value) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeInt(bytes.length);
        this.out.write(bytes);
    }

    public void writeStrings(List<String> list) throws IOException {
        writeInt(list.size());
        for (String string : list) {
            writeString(string);
        }
    }

    public void writeObjectPath(ObjectPath path) throws IOException {
        writeStrings(path.asList());
    }

    public void writeSignature(Signature signature) throws IOException {
        writeType(signature.returnType());
        writeInt(signature.parameters().size());
        for (KType type : signature.parameters()) {
            writeType(type);
        }
    }



    public void writeType(KType type) throws IOException {
        switch (type) {
            case KType.ArrayType arrayType -> {
                writeByte(2);
                writeType(arrayType.elementType());
            }
            case KType.ClassType classType -> {
                writeClassType(classType);
            }
            case KType.FunctionType functionType -> {
                writeByte(4);
                writeType(functionType.returnType());
                writeInt(functionType.arguments().size());
                for (KType argument : functionType.arguments()) {
                    writeType(argument);
                }
                writeInt(functionType.interfaces().size());
                for (KType impl : functionType.interfaces()) {
                    writeType(impl);
                }
            }
            case KType.GenericLink genericLink -> {
                writeByte(5);
                writeString(genericLink.link().name());
            }
            case KType.PrimitiveType primitiveType -> {
                switch (primitiveType.primitive()) {
                    case BOOL -> writeByte(6);
                    case BYTE -> writeByte(7);
                    case CHAR -> writeByte(8);
                    case DOUBLE -> writeByte(9);
                    case FLOAT -> writeByte(10);
                    case INT -> writeByte(11);
                    case LONG -> writeByte(12);
                    case SHORT -> writeByte(13);
                }
            }
            case KType.Resolvable resolvable -> {
                throw new IOException("Resolvable fieldType should not be written to binary");
            }
            case KType.UnprocessedType unprocessedType -> {
                throw new IOException("Unprocessed fieldType should not be written to binary");
            }
            case KType.VoidType voidType -> {
                writeByte(14);
            }
        }
    }

    public void writeClassType(KType.ClassType classType) throws IOException {
        writeByte(3);
        writeClassPointer(classType.pointer());
        writeInt(classType.generics().size());
        for (KType generic : classType.generics()) {
            writeType(generic);
        }
    }


    public void writeClassPointer(@Nullable ClassPointer classPointer) throws IOException {
        if (classPointer == null) {
            writeByte(0);
        } else {
            writeByte(1);
            writeObjectPath(classPointer.path());
        }
    }

    public void writeClassPointerList(List<ClassPointer> list) throws IOException {
        writeInt(list.size());
        for (ClassPointer pointer : list) {
            writeClassPointer(pointer);
        }
    }


    public void writeIntList(int[] list) throws IOException {
        writeInt(list.length);
        for (var i : list) {
            writeInt(i);
        }
    }
    public void writeGenericList(List<Generic> generics) throws IOException {
        writeInt(generics.size());

        for (var generic : generics) {
            writeString(generic.name());
        }
        for (var generic : generics) {
            writeInt(generic.bounds().size());
            for (var bound : generic.bounds()) {
                writeType(bound);
            }
            if (generic.superType() == null) {
                writeByte(0);
            } else {
                writeByte(1);
                writeType(generic.superType());
            }
        }

    }
}
