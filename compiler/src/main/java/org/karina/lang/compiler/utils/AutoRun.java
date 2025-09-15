package org.karina.lang.compiler.utils;

import lombok.SneakyThrows;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.stages.generate.JarCompilation;
import org.karina.lang.compiler.utils.logging.Colored;
import org.karina.lang.compiler.utils.logging.ConsoleColor;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

/**
 * Runs the compiled jar file from memory.
 */
public class AutoRun implements AutoCloseable {
    private final ExecutorService executor;
    private boolean ended = false;

    public AutoRun() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    /// This method invokes the main method of the compiled jar file.
    /// This method is big for 'correct' stack traces
    /// @return the kind of error that occurred, or null if the main method was executed successfully.
    @CheckReturnValue
    public @Nullable MainInvocationResult runWithPrints(JarCompilation compilation, boolean colors, String[] args) {

        try {
            System.out.println();
            var mainMethod = getMainMethod(compilation, colors);
            if (mainMethod == null) {
                // errors already printed
                // TODO throw an error and print later
                return new MainInvocationResult.OtherError(new IllegalStateException());
            }

            Future<?> future = this.executor.submit(() -> {
                try {
                    mainMethod.invokeExact(args);
                    this.ended = true;
                } catch (WrongMethodTypeException e) {
                    // should not happen
                    throw e;
                } catch (Throwable e) {
                    // e is the exception thrown by the main method, not a wrapper
                    throw new ExpectedMainException(e);
                }
                return null;
            });

            try {
                future.get();
            } catch (ExecutionException e) {
                if (e.getCause() instanceof ExpectedMainException m) {
                    throw m; // rethrow the expected exception
                } if (e.getCause() instanceof WrongMethodTypeException m) {
                    throw m; // should not happen, as the method type is checked
                } else {
                    // should not happen
                    throw new ExpectedMainException(e);
                }
            }

            if (colors) {
                Colored.begin(ConsoleColor.GRAY)
                       .append("> Execution finished ")
                       .println(System.out);
            } else {
                System.out.println("> Execution finished ");
            }
            return null; // no error occurred, main method executed successfully
        } catch (IOException | InterruptedException | WrongMethodTypeException e) {
            return new MainInvocationResult.OtherError(e);
        } catch (ExpectedMainException e) {
            return new MainInvocationResult.MainError(e.getCause());
        }

    }

    @Override
    public void close() {
        this.executor.close();
    }

    public boolean cancel() {
        this.executor.shutdown();
        this.executor.shutdownNow();
        this.executor.close();
        return this.ended;
    }

    private static @Nullable MethodHandle getMainMethod(JarCompilation compilation, boolean colors) throws IOException {
        var mainClass = compilation.manifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
        if (mainClass == null) {
            if (colors) {
                Colored.begin(ConsoleColor.RED)
                       .append("No main class found in the manifest")
                       .println(System.out);
            } else {
                System.out.println("No main class found in the manifest");
            }
            return null;
        }

        var karinaLib = karinaLibLoader();
        var classLoader = new CompilationClassLoader(karinaLib, compilation);

        Class<?> cls;
        try {
            cls = classLoader.loadClass(mainClass);
        } catch (ClassNotFoundException e) {
            if (colors) {
                Colored.begin(ConsoleColor.RED)
                       .append("Main class '")
                       .append(mainClass)
                       .append("' not found in the compiled jar")
                       .println(System.out);
            } else {
                System.out.println("Main class '" + mainClass + "' not found in the compiled jar");
            }
            return null;
        }
        MethodType methodType = MethodType.methodType(void.class, String[].class);
        MethodHandle handle;
        try {
            handle = MethodHandles.lookup().findStatic(cls, "main", methodType);
        } catch (NoSuchMethodException | IllegalAccessException _) {
            if (colors) {
                Colored.begin(ConsoleColor.RED)
                       .append("Could not find method 'pub fn main(args: [string]) -> void'")
                       .append(" in class '")
                       .append(mainClass)
                       .append("'")
                       .println(System.out);
            } else {
                System.out.println(
                        "Could not find method 'pub fn main(args: [string]) -> void' in class '"
                        + mainClass + "'"
                );
            }
            return null;
        }

        if (colors) {
            Colored.begin(ConsoleColor.GRAY)
                   .append("> Executing '")
                   .append(mainClass)
                   .append(".main()':")
                   .println(System.out);
        } else {
            System.out.println("> Executing '" + mainClass + ".main()': ");
        }
        return handle;
    }

    public static ClassLoader karinaLibLoader() throws IOException {
        var resource = "/karina.base.jar";
        var url = AutoRun.class.getResource(resource);
        if (url != null) {
            return new URLClassLoader(new URL[]{url});
        }
        // backup, as url might not be available
        try (var resourceStream = ModelLoader.class.getResourceAsStream(resource)) {
            if (resourceStream == null) {
                 throw new IOException("Could not load the standard library: " + resource + ". Is the jar file missing?");
            }
            return new InMemoryJarClassLoader(resourceStream);
        }

    }


    private static class InMemoryJarClassLoader extends ClassLoader {
        private final Map<String, byte[]> classBytes = new HashMap<>();

        public InMemoryJarClassLoader(InputStream jarInputStream) throws IOException {
            try (JarInputStream jis = new JarInputStream(jarInputStream)) {
                JarEntry entry;
                while ((entry = jis.getNextJarEntry()) != null) {
                    if (entry.getName().endsWith(".class")) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        jis.transferTo(baos);
                        String className = entry.getName()
                                                .replace('/', '.')
                                                .replace(".class", "");
                        this.classBytes.put(className, baos.toByteArray());
                    }
                }
            }
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = this.classBytes.get(name);
            if (bytes == null) throw new ClassNotFoundException(name);
            return defineClass(name, bytes, 0, bytes.length);
        }
    }


    private static class CompilationClassLoader extends ClassLoader {
        Map<String, JarCompilation.JarOutput> files;

        public CompilationClassLoader(ClassLoader parent, JarCompilation compilation) {
            super(parent);
            this.files = compilation.files().stream().collect(Collectors.toMap(
                    ref -> ref.path()
                              .replace(".class", "")
                              .replace("/", "."),
                    Function.identity()
            ));
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass != null) {
                return loadedClass;
            }

            var output = this.files.get(name);

            if (output == null) {
                throw new ClassNotFoundException("Class not found: " + name);
            }
            var bytes = output.data();
            return defineClass(name, bytes, 0, bytes.length);
        }

        @Override
        public String toString() {
            return "CompilationClassLoader{}";
        }
    }

    private static class ExpectedMainException extends RuntimeException {
        public ExpectedMainException(Throwable cause) {
            super(cause);
        }
    }

    public sealed interface MainInvocationResult {
        Throwable cause();
        // Represents the result of invoking the main method of a compiled Karina program.
        record MainError(Throwable cause) implements MainInvocationResult { }

        // When a other error occurs while invoking the main method, that is not expected.
        record OtherError(Throwable cause) implements MainInvocationResult { }

    }

}
