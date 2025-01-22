package org.karina.lang.compiler.jvm.binary.out;

import org.karina.lang.compiler.jvm.model.JKModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ModelWriter {

    private final OutputStream stream;

    public ModelWriter(OutputStream stream) {
        this.stream = stream;

    }

    public void write(JKModel model) throws IOException {
        var classes = model.getBytecodeClasses();

        var outerWriter = new ClassWriter(this.stream);

        var offsets = new int[classes.size()];

        var bytecodes = new byte[classes.size()][];
        var currentOffset = 0;
        for (var i = 0; i < classes.size(); i++) {
            var bytecodeClass = classes.get(i);

            var innerStream = new ByteArrayOutputStream();
            var innerWriter = new ClassWriter(innerStream);
            innerWriter.write(bytecodeClass);
            var byteArray = innerStream.toByteArray();
            offsets[i] = currentOffset + byteArray.length;
            currentOffset += byteArray.length;
            bytecodes[i] = byteArray;
        }

        outerWriter.writeIntList(offsets);
        for (var i = 0; i < offsets.length; i++) {
            outerWriter.writeBytes(bytecodes[i]);
        }

    }

    public void writeHash(long hash) throws IOException {
        this.stream.write((int) (hash >> 56));
        this.stream.write((int) (hash >> 48));
        this.stream.write((int) (hash >> 40));
        this.stream.write((int) (hash >> 32));
        this.stream.write((int) (hash >> 24));
        this.stream.write((int) (hash >> 16));
        this.stream.write((int) (hash >> 8));
        this.stream.write((int) hash);
    }
}
