package org.karina.lang.compiler.jvm.binary.in;

import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.FileLoadError;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.jvm.model.JKModelBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ModelReader {
    private final InputStream stream;

    public ModelReader(InputStream stream) {
        this.stream = stream;

    }

    public JKModel read(File file) throws IOException {
        var builder = new JKModelBuilder();

        var outerReader = new ClassReader(this.stream);
        var offsets = outerReader.readIntList();

        var remainingBytes = this.stream.readAllBytes();

        //Fork and Join
        List<Future<?>> futures = new ArrayList<>();
        var availableProcessors = Runtime.getRuntime().availableProcessors();
        try (var executor = Executors.newFixedThreadPool(availableProcessors)) {
            for (var index = 0; index < offsets.length; index++) {
                int finalIndex = index;
                var task = executor.submit(() -> {
                    try {
                        var innerReader = getClassReader(finalIndex, offsets, remainingBytes);
                        var bytecodeClass = innerReader.read();
                        builder.addClass(bytecodeClass);
                    } catch (IOException e) {
                        Log.fileError(new FileLoadError.IO(file, e));
                        throw new Log.KarinaException();
                    }
                });
                futures.add(task);
            }

            for (var future : futures) {
                future.get();
            }

        } catch (ExecutionException | InterruptedException e) {
            Log.fileError(new FileLoadError.IO(file, e));
            throw new Log.KarinaException();
        }


        return builder.build();
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

    public long readHash() throws IOException {
        return (
                (long) this.stream.read() << 56) |
                ((long) this.stream.read() << 48) |
                ((long) this.stream.read() << 40) |
                ((long) this.stream.read() << 32) |
                ((long) this.stream.read() << 24) |
                ((long) this.stream.read() << 16) |
                ((long) this.stream.read() << 8) |
                (long) this.stream.read();
    }

}
