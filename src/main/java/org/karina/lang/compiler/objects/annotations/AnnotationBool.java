package org.karina.lang.compiler.objects.annotations;

import org.karina.lang.compiler.utils.Region;

public record AnnotationBool(Region region, boolean value) implements AnnotationValue {
}
