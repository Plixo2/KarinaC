package org.karina.lang.compiler.utils;

import org.karina.lang.compiler.logging.ColorOut;
import org.karina.lang.compiler.logging.LogColor;
import org.karina.lang.compiler.stages.generate.JarCompilation;


import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

/**
 * Runs the compiled jar file from memory.
 */
public class AutoRun {

    public static void run(JarCompilation compilation, boolean failWithException) {
        var classLoader = new CompilationClassLoader(compilation);
        var mainClass = compilation.manifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
        if (mainClass == null) {
            ColorOut.begin(LogColor.RED)
                    .append("No main class found in the manifest")
                    .out(System.out);
            return;
        }

        try {
            var cls = classLoader.loadClass(mainClass);
            var mainMethod = cls.getMethod("main", String[].class);

            System.out.println();
            ColorOut.begin(LogColor.GRAY)
                    .append("> Executing '")
                    .append(mainClass)
                    .append(".main()':")
                    .out(System.out);

            mainMethod.invoke(null, (Object) new String[]{});
        } catch (Exception e) {
            if (failWithException) {
                throw new RuntimeException("Failed to run main method", e);
            }
            Throwable inner = e;
            while (inner.getCause() != null) {
                inner = inner.getCause();
            }

            ColorOut.begin(LogColor.RED)
                    .append(inner.getClass().getName())
                    .append(" ")
                    .append(Objects.requireNonNullElse(inner.getMessage(), ""))
                    .out(System.out);

            System.out.flush();
            e.printStackTrace();
        }
    }

    private static class CompilationClassLoader extends ClassLoader {
        Map<String, JarCompilation.JarOutput> files;
        Manifest manifest;

        public CompilationClassLoader(JarCompilation compilation) {
            this.files = compilation.files().stream().collect(Collectors.toMap(
                    ref -> ref.path()
                              .replace(".class", "")
                              .replace("/", "."),
                    Function.identity()
            ));
            this.manifest = compilation.manifest();
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
    }
}
