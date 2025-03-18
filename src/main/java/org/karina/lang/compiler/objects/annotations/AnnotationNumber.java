package org.karina.lang.compiler.objects.annotations;

import org.karina.lang.compiler.utils.Region;

import java.math.BigDecimal;

public record AnnotationNumber(Region region, BigDecimal value) implements AnnotationValue {
}
