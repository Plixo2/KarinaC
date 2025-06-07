package org.karina.lang.compiler;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.karina.lang.compiler.utils.DefaultFileTree;
import org.karina.lang.compiler.utils.FileLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RunningTests {
    private static final String TEST_DIR = "tests/running/";

    @Test
    public void testMain() throws IOException {
        KarinaCompiler.cache = null; // Clear the cache before running tests
        System.setProperty("karina.binary", "false");
        Main.main(new String[]{"--test", "--run"});
        KarinaCompiler.cache = null;
        System.setProperty("karina.binary", "true");
        Main.main(new String[]{"--test", "--run"});
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
    List<DynamicTest> testProjects() throws IOException {
        var files = loadProjects(TEST_DIR + "projects/");
        if (files.isEmpty()) {
            throw new AssertionError("No test projects found in directory: " + TEST_DIR);
        }

        return files.stream().map(ref -> {
            var name = ref.name();
            return DynamicTest.dynamicTest(
                    name, () -> {
                        var toTest = new TestFile(name, ref);
                        toTest.run();
                    }
            );
        }).toList();
    }

    private static List<DefaultFileTree> loadProjects(String testDir) throws IOException {
        var files = new ArrayList<DefaultFileTree>();

        var directory = new File(testDir);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new AssertionError("Project directory does not exist: " + testDir);
        }

        for (var file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                files.add(FileLoader.loadTree(file.toPath()));
            } else {
                throw new AssertionError("Expected directories, but found a file: " + file.getAbsolutePath());
            }
        }

        return files;
    }




}
