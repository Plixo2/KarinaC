package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.logging.Logging;


/// Interface for classes that can convert themselves into a {@link Context}.
/// This is useful for passing around context information in the compiler.
public interface IntoContext {
    Context intoContext();

    default boolean log(Class<? extends Logging> type) {
        return intoContext().log(type);
    }

    default void tag(String text, Object... args) {
        intoContext().tagWrapper(text, args);
    }

    default @Nullable Context.OpenSection section(Class<? extends Logging> type, String name) {
        return intoContext().section(type, name, 1);
    }

}