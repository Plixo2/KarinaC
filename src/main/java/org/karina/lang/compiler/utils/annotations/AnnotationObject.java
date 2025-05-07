package org.karina.lang.compiler.utils.annotations;

import org.karina.lang.compiler.utils.Region;

import java.util.Map;

public record AnnotationObject(Region region, Map<String, AnnotationValue> values)
        implements AnnotationValue {
}
