package org.karina.lang.compiler.utils;

/// Interface for classes that can convert themselves into a {@link Context}.
/// This is useful for passing around context information in the compiler.
public interface IntoContext {
    Context intoContext();
}