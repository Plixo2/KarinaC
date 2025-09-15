package org.karina.lang.lsp.lib;

import com.google.errorprone.annotations.CheckReturnValue;
import karina.lang.Option;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public interface VirtualFileSystem {


    @CheckReturnValue
    @Contract(mutates = "this")
    Option<FileTransaction> openFile(URI uri, String content, int version);


    @CheckReturnValue
    @Contract(mutates = "this")
    Option<FileTransaction> updateFile(URI uri, String content, int version);


    @CheckReturnValue
    @Contract(mutates = "this")
    Option<FileTransaction> closeFile(URI uri);


    @CheckReturnValue
    @Contract(mutates = "this")
    Option<FileTransaction> saveFile(URI uri);


    @CheckReturnValue
    @Contract(mutates = "this")
    Option<FileTransaction> deleteFile(URI uri);


    @CheckReturnValue
    @Contract(mutates = "this")
    Option<FileTransaction> reloadFromDisk(URI uri, String diskContent);



    /// @return if the file exists and is open
    @Contract(pure = true)
    boolean isFileOpen(URI uri);

    /// @return null if the file does not exist, otherwise the content of the file
    @Contract(pure = true)
    Option<String> getContent(URI uri);

    @Contract(pure = true)
    @UnmodifiableView
    List<VirtualFile> files();



    /// @return normalized and absolute URI from a string representation.
    static URI toUri(String uri) {
        var uriObj = URI.create(uri);
        if (!Objects.equals(uriObj.getScheme(), "file")) {
            throw new IllegalArgumentException("URI scheme must be 'file': " + uri);
        }
        Path path = Paths.get(uriObj).toAbsolutePath().normalize();
        return path.toUri();
    }

}
