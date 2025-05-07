package org.karina.lang.compiler.utils.annotations;

import org.karina.lang.compiler.utils.Region;

public record AnnotationBool(Region region, boolean value) implements AnnotationValue {
}
