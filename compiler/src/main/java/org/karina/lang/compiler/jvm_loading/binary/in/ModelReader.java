package org.karina.lang.compiler.jvm_loading.binary.in;

import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.Logging;
import org.karina.lang.compiler.utils.logging.errors.FileLoadError;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.Context;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModelReader {
    private final InputStream stream;

    public ModelReader(InputStream stream) {
        this.stream = stream;

    }

    public Model read(Context c) throws IOException {
        var builder = new ModelBuilder();
        var ttyl = new TTYL();

        var outerReader = new ClassReader(this.stream);
        var magicNumber = outerReader.readInt();
        if (magicNumber != KarinaCompiler.BINARY_MAGIC_NUMBER) {
            Log.fileError(c, new FileLoadError.BinaryFile(
                    "Invalid Karina binary file format, expected magic number " +
                    KarinaCompiler.BINARY_MAGIC_NUMBER +
                    " but got " +
                    magicNumber
            ));
            throw new Log.KarinaException();
        }

        var formatVersion = outerReader.readInt();
        if (formatVersion != KarinaCompiler.BINARY_VERSION) {
            var major = KarinaCompiler.BINARY_VERSION >> 16;
            var minor = KarinaCompiler.BINARY_VERSION & 0xFFFF;
            var formatMajor = formatVersion >> 16;
            var formatMinor = formatVersion & 0xFFFF;
            Log.fileError(c, new FileLoadError.BinaryFile(
                    "Invalid Karina binary file version, expected version " +
                    major + "." + minor +
                    " but got " +
                    formatMajor + "." + formatMinor
            ));
            throw new Log.KarinaException();
        }

        var offsets = outerReader.readIntList();

        byte[] remainingBytes;
        try (var _ = c.section(Logging.BinaryFile.class,"reading stream")) {
            remainingBytes = this.stream.readAllBytes();
        }

        //Fork and Join
        int availableProcessors = 1;
        if (c.infos().threading()) {
            availableProcessors = Runtime.getRuntime().availableProcessors();
        }

        if (c.log(Logging.BinaryFile.class)) {
            c.tag("number of offsets", offsets.length);
            c.tag("number of threads", availableProcessors);
        }

        var readers = new ClassReader[offsets.length];
        try (var _ = c.section(Logging.BinaryFile.class,"calculating offsets")) {
            for (var index = 0; index < offsets.length; index++) {
                readers[index] = getClassReader(index, offsets, remainingBytes);
            }
        }



        try (var _ = c.section(Logging.BinaryFile.class,"building classes")) {
            try (var fork = c.fork()) {
                for (var index = 0; index < offsets.length; index++) {
                    var reader = readers[index];
                    fork.collect(subC -> {
                        try {
                            var _ = reader.read(subC, ttyl, builder);
                        } catch (IOException e) {
                            Log.fileError(subC, new FileLoadError.Resource(e));
                            throw new Log.KarinaException();
                        }
                        //return null, and mutate thread-safe ModelBuilder
                        return null;
                    });
                }
                var _ = fork.dispatchParallel();
            }
        }

        var finished = builder.build(c);
        try (var _ = c.section(Logging.BinaryFile.class,"resolving classes")) {
            ttyl.resolve(finished);
        }
        return finished;
    }

    private static @NotNull ClassReader getClassReader(int index, int[] offsets, byte[] remainingBytes) {
        int modelOffset;
        if (index == 0) {
            modelOffset = 0;
        } else {
            modelOffset = offsets[index - 1];
        }
        var length = offsets[index] - modelOffset;
        var innerBuffer = new ByteArrayInputStream(remainingBytes, modelOffset, length);
        return new ClassReader(innerBuffer);
    }


    /// Talk To You Later
    public static class TTYL {
        List<Consumer<Model>> tasks = new ArrayList<>();

        public void add(Consumer<Model> task) {
            synchronized (this) {
                this.tasks.add(task);
            }
        }

        private void resolve(Model model) {
            for (var task : this.tasks) {
                task.accept(model);
            }
            this.tasks.clear();
        }
    }


}
