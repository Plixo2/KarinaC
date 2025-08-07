package org.karina.lang.compiler;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.karina.lang.compiler.utils.FileLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SingleTests {
    private static final String TEST_DIR = "../tests/files/";

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
