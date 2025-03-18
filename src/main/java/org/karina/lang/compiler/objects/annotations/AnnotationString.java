package org.karina.lang.compiler.objects.annotations;

import org.karina.lang.compiler.utils.Region;

public record AnnotationString(Region region, String value) implements AnnotationValue {
}
