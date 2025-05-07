package org.karina.lang.compiler.jvm;


import org.karina.lang.compiler.api.Resource;

/**
 * Represents a Java file.
 */
public record JavaResource(String identifier) implements Resource {

}
