package org.karina.lang.compiler.logging.errors;

import org.karina.lang.compiler.utils.Region;

public sealed interface LowerError extends Error {
    record NotValidAnymore(Region region, String message) implements LowerError {}
}
