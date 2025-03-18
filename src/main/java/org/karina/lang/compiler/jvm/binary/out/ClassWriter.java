package org.karina.lang.compiler.jvm.binary.out;

import lombok.EqualsAndHashCode;
import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.utils.Generic;

import java.io.IOException;
import java.io.OutputStream;


public class ClassWriter {
    private final ClassOutStream stream;

    public ClassWriter(OutputStream out) {
        this.stream = new ClassOutStream(out);
    }

    public void write(JClassModel model) throws IOException {
        //only used for validation
        this.stream.writeInt(768928090);
        this.stream.writeString(model.name());
        this.stream.writeObjectPath(model.path());
        this.stream.writeInt(model.version());
        this.stream.writeInt(model.modifiers());
        var superClass = model.superClass();
        this.stream.writeClassPointer(superClass == null ? null: superClass.pointer());
        var outerClass = model.outerClass();
        this.stream.writeClassPointer(outerClass == null ? null: outerClass.pointer());
        this.stream.writeInt(model.interfaces().size());
        for (var anInterface : model.interfaces()) {
            this.stream.writeClassPointer(anInterface.pointer());
        }
        this.stream.writeClassPointerList( model.innerClasses().stream().map(ClassModel::pointer).toList());
        this.stream.writeFieldList(model.fields());
        this.stream.writeMethodList(model.methods());

        this.stream.writeStrings(model.generics().stream().map(Generic::name).toList());
        this.stream.writeClassPointerList(model.permittedSubclasses());
        this.stream.writeString(model.resource().resource().identifier());
        //only used for validation
        this.stream.writeInt(902939823);
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
