package org.karina.lang.compiler.logging.errors;

import org.karina.lang.compiler.api.Resource;
import org.karina.lang.compiler.utils.Region;

public sealed interface Error
        permits AttribError, Error.BytecodeLoading, Error.Warn, Error.InternalException,
        Error.InvalidState, Error.ParseError, Error.SyntaxError, Error.TemporaryErrorRegion,
        FileLoadError, ImportError {

    record TemporaryErrorRegion(Region region, String message) implements Error {}
    record Warn(String message) implements Error {}
    record InternalException(Throwable exception) implements Error {}

    record SyntaxError(Region region, String msg) implements Error {}

    record BytecodeLoading(Resource resource, String location, String msg) implements Error {}

    record InvalidState(Region region, Class<?> aClass, String expectedState) implements Error {}

    record ParseError(String msg) implements Error {}
}
