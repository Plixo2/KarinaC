package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm_loading.loading.ModelLoader;
import org.karina.lang.compiler.logging.ColorOut;
import org.karina.lang.compiler.logging.LogColor;
import org.karina.lang.compiler.stages.generate.JarCompilation;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

/**
 * Runs the compiled jar file from memory.
 */
public class AutoRun {

    public static @Nullable Exception run(JarCompilation compilation) {

        var mainClass = compilation.manifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
        if (mainClass == null) {
            return new RuntimeException("No main class found in the manifest");
        }
        try {
            var karinaLib = karinaLibLoader();
            var classLoader = new CompilationClassLoader(karinaLib, compilation);
            var cls = classLoader.loadClass(mainClass);
            var mainMethod = cls.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[]{});
            return null;
        } catch (Exception e) {
            return e;
        }
    }

    public static void runWithPrints(JarCompilation compilation, boolean colors) {

        var mainClass = compilation.manifest().getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
        if (mainClass == null) {
            if (colors) {
            ColorOut.begin(LogColor.RED)
                    .append("No main class found in the manifest")
                    .out(System.out);
            } else {
                System.out.println("No main class found in the manifest");
            }
            return;
        }
        try {
            System.out.println();

            var karinaLib = karinaLibLoader();
            var classLoader = new CompilationClassLoader(karinaLib, compilation);
            var cls = classLoader.loadClass(mainClass);
            var mainMethod = cls.getMethod("main", String[].class);

            if (colors) {
                ColorOut.begin(LogColor.GRAY)
                        .append("> Executing '")
                        .append(mainClass)
                        .append(".main()':")
                        .out(System.out);
            } else {
                System.out.println("> Executing '" + mainClass + ".main()': ");
            }

            mainMethod.invoke(null, (Object) new String[]{});

        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new RuntimeException("Failed to run main method: " + e);
        }
    }

    public static ClassLoader karinaLibLoader() throws IOException {
        var resource = "/karina_base.jar";
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

    public static class InMemoryJarClassLoader extends ClassLoader {
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
    }
}
