package org.karina.lang.compiler.api;

import org.karina.lang.compiler.utils.ObjectPath;

public interface FileNode {
    ObjectPath path();
    String name();
    TextSource text();
}
