package org.karina.lang.compiler.objects.annotations;

import org.karina.lang.compiler.utils.Region;

import java.util.List;

public record AnnotationArray(Region region, List<AnnotationValue> values)
        implements AnnotationValue {
}
