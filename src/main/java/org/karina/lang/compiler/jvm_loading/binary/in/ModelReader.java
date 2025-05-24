package org.karina.lang.compiler.jvm_loading.binary.in;

import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.KarinaCompiler;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.FileLoadError;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.Model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class ModelReader {
    private final InputStream stream;

    public ModelReader(InputStream stream) {
        this.stream = stream;

    }

    public Model read() throws IOException {
        var builder = new ModelBuilder();
        var ttyl = new TTYL();

        var outerReader = new ClassReader(this.stream);
        var magicNumber = outerReader.readInt();
        if (magicNumber != KarinaCompiler.BINARY_MAGIC_NUMBER) {
            throw new IOException(
                    "Invalid Karina binary file format, expected magic number " +
                    KarinaCompiler.BINARY_MAGIC_NUMBER +
                    " but got " +
                    magicNumber
            );
        }

        var formatVersion = outerReader.readInt();
        if (formatVersion != KarinaCompiler.BINARY_VERSION) {
            throw new IOException(
                    "Invalid Karina binary file version, expected " +
                    KarinaCompiler.BINARY_VERSION +
                    " but got " +
                    formatVersion
            );
        }

        var offsets = outerReader.readIntList();



        var remainingBytes = this.stream.readAllBytes();

        //Fork and Join
        List<Future<?>> futures = new ArrayList<>();
        var availableProcessors = Runtime.getRuntime().availableProcessors();

        Log.record("cache with " + offsets.length + " offsets and " + availableProcessors + " threads");

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Log.begin("setup");
            var tasks = new ArrayList<Runnable>();
            for (var index = 0; index < offsets.length; index++) {
                int finalIndex = index;
                Runnable runnable = () -> {
                    try {
                        var innerReader = getClassReader(finalIndex, offsets, remainingBytes);
                        var _ = innerReader.read(ttyl, builder);
                    } catch (IOException e) {
                        Log.fileError(new FileLoadError.Resource(e));
                        throw new Log.KarinaException();
                    }
                };
                tasks.add(runnable);
            }
            Log.end("setup");

            Log.begin("submit");
            for (var task : tasks) {
                futures.add(executor.submit(task));
            }
            Log.end("submit");

            Log.begin("link");
            for (var future : futures) {
                future.get();
            }
            Log.end("link");

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.fileError(new FileLoadError.Resource(e));
            throw new Log.KarinaException();
        }

        var finished = builder.build();
        Log.begin("resolve");
        ttyl.resolve(finished);
        Log.end("resolve");
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
