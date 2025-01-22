package org.karina.lang.compiler.jvm.binary.out;

import lombok.EqualsAndHashCode;
import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.utils.Generic;

import java.io.IOException;
import java.io.OutputStream;


public class ClassWriter {
    private final ClassOutStream stream;

    public ClassWriter(OutputStream out) {
        this.stream = new ClassOutStream(out);
    }

    public void write(JClassModel model) throws IOException {
        this.stream.writeString(model.name());
        this.stream.writeObjectPath(model.path());
        this.stream.writeString(model.resource().resource().identifier());
        this.stream.writeInt(model.version());
        this.stream.writeInt(model.modifiers());
        this.stream.writeClassPointer(model.superClass());
        this.stream.writeClassPointer(model.outerClass());
        this.stream.writeClassPointerList(model.interfaces());
        this.stream.writeClassPointerList(model.innerClasses());
        this.stream.writeFieldList(model.fields());
        this.stream.writeMethodList(model.methods());
        this.stream.writeStrings(model.generics().stream().map(Generic::name).toList());
        this.stream.writeClassPointerList(model.permittedSubclasses());
    }

    public void writeBytes(byte[] bytes) throws IOException {
        this.stream.writeBytes(bytes);
    }

    public void writeInt(int length) throws IOException {
        this.stream.writeInt(length);
    }

    public void writeIntList(int[] list) throws IOException {
        this.stream.writeIntList(list);
    }
}
