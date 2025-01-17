package org.karina.lang.compiler;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.api.config.ConfigurationParseException;
import org.karina.lang.compiler.boot.FileLoader;
import org.karina.lang.compiler.boot.Main;
import org.karina.lang.compiler.errors.DidYouMean;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.errors.types.Error;
import org.karina.lang.compiler.errors.types.FileLoadError;
import org.karina.lang.compiler.errors.types.Error.*;
import org.karina.lang.compiler.errors.types.ImportError;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SyntaxTests {
    private static final String TEST_DIR = "resources/tests/files/";

    private static Map<String, Class<? extends Error>> map = new HashMap<>();

    static {
        var map = new HashMap<String, Class<? extends Error> >();

        map.put("io", FileLoadError.IO.class);
        map.put("InvalidJson", FileLoadError.InvalidJson.class);
        map.put("NOPermission", FileLoadError.NOPermission.class);
        map.put("NotFound", FileLoadError.NotFound.class);
        map.put("NotAFile", FileLoadError.NotAFile.class);
        map.put("NotAFolder", FileLoadError.NotAFolder.class);

        map.put("InvalidState", InvalidState.class);
        map.put("SyntaxError", SyntaxError.class);

        map.put("DuplicateItem", ImportError.DuplicateItem.class);
        map.put("NoItemFound", ImportError.NoItemFound.class);
        map.put("UnknownImportType", ImportError.UnknownImportType.class);
        map.put("GenericCountMismatch", ImportError.GenericCountMismatch.class);
        map.put("NoUnitFound", ImportError.NoUnitFound.class);
        map.put("JavaNotSupported", ImportError.JavaNotSupported.class);

        map.put("temp", TemporaryErrorRegion.class);

        map.put("TypeCycle", AttribError.TypeCycle.class);
        map.put("UnqualifiedSelf", AttribError.UnqualifiedSelf.class);
        map.put("FinalAssignment", AttribError.FinalAssignment.class);
        map.put("DuplicateVariable", AttribError.DuplicateVariable.class);
        map.put("TypeMismatch", AttribError.TypeMismatch.class);
        map.put("UnknownIdentifier", AttribError.UnknownIdentifier.class);
        map.put("NotAStruct", AttribError.NotAStruct.class);
        map.put("NotAInterface", AttribError.NotAInterface.class);
        map.put("ControlFlow", AttribError.ControlFlow.class);
        map.put("NotAArray", AttribError.NotAArray.class);
        map.put("NotSupportedType", AttribError.NotSupportedType.class);
        map.put("ParameterCountMismatch", AttribError.ParameterCountMismatch.class);
        map.put("ScopeFinalityAssignment", AttribError.ScopeFinalityAssignment.class);

        map.put("MissingField", AttribError.MissingField.class);
        map.put("UnknownField", AttribError.UnknownField.class);

        SyntaxTests.map = new HashMap<>();
        map.forEach((key, value) -> SyntaxTests.map.put(key.toLowerCase(), value));
    }

    @Test
    public void testWorkingSource() throws IOException, ConfigurationParseException {
        Main.main(new String[] {
                "--src",
                "resources/src",
                "-v"
        });
    }

    @TestFactory
    List<DynamicTest> testSingleFiles() {
        var files = loadSingleFiles(TEST_DIR);
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
                        testSingleFile(toTest);
                    }
            );
        }).toList();

    }



    private static void testSingleFile(TestFile file) {
        if (file.source.lines().isEmpty()) {
            throw new AssertionError("Empty file: " + file.name);
        }
        var expectedType = file.source.lines().getFirst();
        var testType = getTestType(expectedType, file.name);
        switch (testType) {
            case TestType.Error error -> {
                file.expectError(error.errorClass);
            }
            case TestType.Ok ok -> {
                file.expect();
            }
        }
    }

    private static TestType getTestType(String line, String name)  {
        var stripped = line.strip();
        if (!stripped.startsWith("/*") || !stripped.endsWith("*/")) {
            throw new AssertionError("Expected type not found in file: " + name);
        }
        var substring = stripped.substring(2, stripped.length() - 2).strip();
        if (substring.toLowerCase().startsWith("ok")) {
            return new TestType.Ok();
        }
        if (substring.toLowerCase().startsWith("fail")) {
            var errorType = substring.substring(4).strip();
            return new TestType.Error(errorClasFromName(errorType));
        } else {
            throw new AssertionError("Unknown test type: " + substring + " in file: " + name);
        }

    }

    public sealed interface TestType {
        record Ok() implements TestType {}
        record Error(Class<?> errorClass) implements TestType {}
    }

    public static Class<? extends Error> errorClasFromName(String name) {
        var lowerCase = name.toLowerCase();
        if (map.containsKey(lowerCase)) {
            return map.get(lowerCase);
        } else {
            var limit = 7;
            var suggestions = String.join(
                    "\n ",
                    DidYouMean.suggestions(map.keySet(), lowerCase, limit)
            );
            throw new AssertionError("Unknown error type: '" + name + "'\n Did you mean: " + suggestions);
        }
    }


    public static List<File> loadSingleFiles(String testDir) {
        var files = new ArrayList<File>();

        var file1 = new File(testDir);
        if (!file1.exists() || !file1.isDirectory()) {
            throw new AssertionError("Test directory does not exist: " + testDir);
        }

        for (var file : Objects.requireNonNull(file1.listFiles())) {
            if (file.isFile()) {
                files.add(file);
            }
        }

        return files;
    }
}
