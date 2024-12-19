package org.karina.lang.lsp.fs;

import lombok.AllArgsConstructor;

import java.nio.file.Path;

@AllArgsConstructor
public final class ConfigFile {
    public Path rootToSourceDirectory;
    public String sourceDirectoryName;
}
