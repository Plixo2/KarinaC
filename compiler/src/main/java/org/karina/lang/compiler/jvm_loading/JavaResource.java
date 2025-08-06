package org.karina.lang.compiler.jvm_loading;


import org.karina.lang.compiler.utils.Resource;

/**
 * Represents a Java file.
 */
public record JavaResource(String identifier) implements Resource {

}
