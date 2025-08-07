package org.karina.lang.compiler;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.karina.lang.compiler.utils.AutoRun;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldExampleTest {
    public static final String RESOURCES_OUT_BUILD_JAR = "../resources/out/build.jar";

    @Test
    public void testMain() throws IOException {
        KarinaCompiler.cache = null;
        System.setProperty("karina.binary", "false");
        testLikeMain();
        KarinaCompiler.cache = null;
        System.setProperty("karina.binary", "true");
        testLikeMain();
    }

    static void testLikeMain() throws IOException {


        var config = Config.fromProperties();
        var result = ConsoleCompiler.compile(config);

        if (!result) {
            throw new RuntimeException("Compilation failed. Check the logs for details.");
        }
    }


    @AfterAll
    public static void runMain() throws IOException, ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {


        var clsLoader = new URLClassLoader(
                new URL[] {
                        new File(System.getProperty("karina.out", RESOURCES_OUT_BUILD_JAR)).toURI().toURL()
                },
                AutoRun.karinaLibLoader()
        );
        var classToLoad = Class.forName("main", true, clsLoader);
        var method = classToLoad.getDeclaredMethod("main", String[].class);
        var args = new Object[] {
                new String[] {}
        };
        var originalOut = System.out;
        var outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        var ignored = method.invoke(null, args);
        var contentString = outContent.toString();
        assertEquals("Hello, World!", contentString.trim());
        assert contentString.contains("\n");
        System.setOut(originalOut);

    }
}
