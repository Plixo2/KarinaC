package org.karina.lang.compiler.stages.generate;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.jar.Manifest;

/**
 * Result of the Generation Stage.
 * Represents a jar file.
 */
@AllArgsConstructor
@Getter
@Accessors(fluent = true)
public class JarCompilation {
    List<JarOutput> files;
    Manifest manifest;


    public record JarOutput(String path, byte[] data) {}
}
