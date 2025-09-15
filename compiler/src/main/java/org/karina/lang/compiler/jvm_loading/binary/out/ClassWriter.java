package org.karina.lang.compiler.jvm_loading.binary.out;

import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;

import java.io.IOException;
import java.io.OutputStream;


public class ClassWriter {
    private final ClassOutStream stream;

    public ClassWriter(OutputStream out) {
        this.stream = new ClassOutStream(out);
    }

    public ClassWriter(ClassWriter outer) {
        this.stream = outer.stream;
    }

    public void write(JClassModel model) throws IOException {
        //only used for validation
        this.stream.writeInt(768928090);
        this.stream.writeString(model.name());
        this.stream.writeObjectPath(model.path());
        this.stream.writeString(model.resource().resource().identifier());
        this.stream.writeInt(model.version());
        this.stream.writeInt(model.modifiers());

        this.stream.writeGenericList(model.generics());

        var superClass = model.superClass();
        if (superClass == null) {
            this.stream.writeByte(0);
        } else {
            this.stream.writeByte(1);
            this.stream.writeClassType(superClass);
        }

        var outerClass = model.outerClass();
        if (outerClass == null) {
            this.stream.writeClassPointer(null);
        } else {
            this.stream.writeClassPointer(outerClass.pointer());
        }
        var nestHost = model.nestHost();
        if (outerClass == null) {
            this.stream.writeClassPointer(null);
        } else {
            this.stream.writeClassPointer(nestHost);
        }

        this.stream.writeInt(model.interfaces().size());
        for (var anInterface : model.interfaces()) {
            this.stream.writeClassType(anInterface);
        }
//        this.stream.writeClassPointerList(model.innerClasses().stream().map(ClassModel::pointer).toList());

        this.stream.writeInt(model.innerClasses().size());
        var innerWriter = new ClassWriter(this);
        for (var innerClass : model.innerClasses()) {
            if (innerClass instanceof JClassModel inner) {
                innerWriter.write(inner);
            } else {
                throw new IOException("Inner class is not a JClassModel, found: " + innerClass.getClass().getName());
            }
        }

        this.stream.writeInt(model.fields().size());
        for (var field : model.fields()) {
            this.stream.writeString(field.name());
            this.stream.writeType(field.type());
            this.stream.writeInt(field.modifiers());
        }

        this.stream.writeInt(model.methods().size());
        for (var method : model.methods()) {
            this.stream.writeString(method.name());

            this.stream.writeGenericList(method.generics());

            this.stream.writeInt(method.modifiers());
            this.stream.writeSignature(method.signature());
            this.stream.writeStrings(method.parameters());
        }

        this.stream.writeClassPointerList(model.permittedSubclasses());
        this.stream.writeClassPointerList(model.nestMembers());


//        //only used for validation
        this.stream.writeInt(902939823);
    }
}
