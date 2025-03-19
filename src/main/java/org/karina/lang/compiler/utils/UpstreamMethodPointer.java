package org.karina.lang.compiler.utils;

import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KType;

public record UpstreamMethodPointer(MethodPointer pointer, KType.ClassType mappedUpstreamType) {
}
