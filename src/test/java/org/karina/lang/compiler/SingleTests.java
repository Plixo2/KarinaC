package org.karina.lang.compiler;

import org.junit.jupiter.api.*;
import org.karina.lang.compiler.utils.FileLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class SingleTests {
    private static final String TEST_DIR = "tests/files/";

    @Test
    public void testMain() throws IOException {
        KarinaCompiler.cache = null;
        System.setProperty("karina.binary", "false");
        Main.main(new String[]{"--test"});
        KarinaCompiler.cache = null;
        System.setProperty("karina.binary", "true");
        Main.main(new String[]{"--test"});
    }



    @AfterAll
    public static void runMain()
            throws MalformedURLException, ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {

        var clsLoader = new URLClassLoader(
                new URL[] {
                        new File(System.getProperty("karina.out", "resources/out/build.jar")).toURI().toURL()
                },
                Main.class.getClassLoader()
        );
        var classToLoad = Class.forName("main", true, clsLoader);
        var method = classToLoad.getDeclaredMethod("main", String[].class);
        var args = new Object[] {
                new String[] {}
        };
        var ignored = method.invoke(null, args);
    }

    @TestFactory
    List<DynamicTest> testValid() {
        return getDynamicTests("ok/", true);
    }

    @TestFactory
    List<DynamicTest> testFailing() {
        return getDynamicTests("fail/", false);
    }

    private static List<DynamicTest> getDynamicTests(String x, boolean expectedResult) {
        var files = loadSingleFiles(TEST_DIR + x);
        if (files.isEmpty()) {
            throw new AssertionError("No test files found in directory: " + TEST_DIR);
        }

        return files.stream().map(ref -> {
            var name = FileLoader.getFileNameWithoutExtension(ref.getName());
            try {
                var source = FileLoader.loadUTF8(ref.getAbsolutePath());
                return DynamicTest.dynamicTest(
                        ref.getName(), () -> {
                            var toTest = new TestFile(name, source);
                            toTest.expect(expectedResult);
                        }
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    private static List<File> loadSingleFiles(String testDir) {
        var files = new ArrayList<File>();

        var directory = new File(testDir);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new AssertionError("Test directory does not exist: " + testDir);
        }

        for (var file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile()) {
                files.add(file);
            }
        }

        return files;
    }




}
