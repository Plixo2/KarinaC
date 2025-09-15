package org.karina.lang.compiler.utils;

/**
 * @see DefaultFileTree.DefaultFileNode
 */
public interface FileNode {
    ObjectPath path();
    String name();
    TextSource content();
}
