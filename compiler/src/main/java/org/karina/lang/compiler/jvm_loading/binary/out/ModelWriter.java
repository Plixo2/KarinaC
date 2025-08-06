package org.karina.lang.compiler.jvm_loading.binary.out;

import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.model_api.Model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ModelWriter {

    private final OutputStream stream;

    public ModelWriter(OutputStream stream) {
        this.stream = stream;
    }

    public void write(Model model) throws IOException {
        var classes = new ArrayList<JClassModel>();

        // Collect all top-level classes (those without an outer class)
        for (var binaryClass : model.getBinaryClasses()) {
            if (binaryClass.outerClass() == null) {
                classes.add(binaryClass);
            }
        }

        var outerWriter = new ClassOutStream(this.stream);
        outerWriter.writeInt(KarinaCompiler.BINARY_MAGIC_NUMBER);
        outerWriter.writeInt(KarinaCompiler.BINARY_VERSION);

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


}
