package org.karina.lang.compiler;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.karina.lang.compiler.utils.DefaultFileTree;
import org.karina.lang.compiler.utils.FileLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RunningTests {
    private static final String TEST_DIR = "../tests/running/";



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
