package org.karina.lang.lsp.fs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.Path;

@Getter
@AllArgsConstructor
public class ContentRoot {
    private Path absolutFSPath;

    //if this field is null, that means we have no config and no source tree
    @Getter
    private @Nullable InitializedContentRoot content;

    public record InitializedContentRoot(ConfigFile configFile, SyncFileTree sourceTree) {

    }


}
