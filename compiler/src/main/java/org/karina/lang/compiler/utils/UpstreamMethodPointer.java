package org.karina.lang.compiler.utils;

import org.karina.lang.compiler.model_api.pointer.MethodPointer;

/**
 * TODO description
 */
public record UpstreamMethodPointer(MethodPointer pointer, KType.ClassType mappedUpstreamType) {
}
