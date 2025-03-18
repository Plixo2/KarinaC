package org.karina.lang.compiler.objects;

import org.karina.lang.compiler.objects.annotations.AnnotationValue;
import org.karina.lang.compiler.utils.Region;

public record KAnnotation(Region region, String name, AnnotationValue value) {
}
