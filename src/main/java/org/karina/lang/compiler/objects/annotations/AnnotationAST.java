package org.karina.lang.compiler.objects.annotations;

import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Region;

public sealed interface AnnotationAST extends AnnotationValue {

    record Expression(Region region, KExpr expr) implements AnnotationAST {}
    record Type(Region region, KType type) implements AnnotationAST {}
}
