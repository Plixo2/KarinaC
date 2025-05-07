package org.karina.lang.compiler.utils;

/**
 * Represents a unique resource.
 * Used for identifying files in error messages.
 * {@link TextSource} stores the data of the file.
 */
public interface Resource {
    String identifier();
}
