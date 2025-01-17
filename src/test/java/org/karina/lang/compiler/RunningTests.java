package org.karina.lang.compiler;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.boot.FileLoader;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
            TextSource source = null;
            try {
                source = FileLoader.loadUTF8(ref.getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            TextSource finalSource = source;
            return DynamicTest.dynamicTest(
                    ref.getName(), () -> {
                        var toTest = new TestFile(name, finalSource);
                        testSingleFile(toTest, true);
                    }
            );
        }).toList();
    }

    private static void testSingleFile(TestFile file, Object expected) {
        if (file.source.lines().isEmpty()) {
            throw new AssertionError("Empty file: " + file.name);
        }
        var object = file.run("src." + file.name + ".test");
        if (object == null) {
            throw new AssertionError("No object returned from file: " + file.name);
        }
        if (Objects.equals(expected, object)) {
            return;
        }

        throw new AssertionError("Expected " + expected + " but got " + object + " or file: " + file.name);

    }


}

