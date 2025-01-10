package org.karina.lang.compiler;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.karina.lang.compiler.boot.FileLoader;

import java.util.List;

public class RunningTests {
    private static final String TEST_DIR = "resources/tests/running/";


    @TestFactory
    List<DynamicTest> testSingleFiles() {
        var files = SyntaxTests.loadSingleFiles(TEST_DIR);
        if (files.isEmpty()) {
            throw new AssertionError("No test files found in directory: " + TEST_DIR);
        }

        return files.stream().map(ref -> {
            var name = FileLoader.getFileNameWithoutExtension(ref.getName());
            var source = FileLoader.loadUTF8(ref.getAbsolutePath());

            return DynamicTest.dynamicTest(
                    ref.getName(), () -> {
                        var toTest = new TestFile(name, source);
                        testSingleFile(toTest);
                    }
            );
        }).toList();
    }

    private static void testSingleFile(TestFile file) {
        if (file.source.lines().isEmpty()) {
            throw new AssertionError("Empty file: " + file.name);
        }
        var object = file.run("src." + file.name + ".test");
        if (object == null) {
            throw new AssertionError("No object returned from file: " + file.name);
        }
        if (!(object instanceof Boolean b)) {
            throw new AssertionError("Expected boolean return from file: " + file.name);
        }
        if (!b) {
            throw new AssertionError("Expected success for file: " + file.name);
        }
    }


}

