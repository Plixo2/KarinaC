package org.karina.lang.compiler.utils;

import org.karina.lang.compiler.utils.annotations.AnnotationValue;

public record KAnnotation(Region region, String name, AnnotationValue value) {
}
