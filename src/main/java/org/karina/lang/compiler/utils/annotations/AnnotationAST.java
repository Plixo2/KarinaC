package org.karina.lang.compiler.utils.annotations;

import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Region;

public sealed interface AnnotationAST extends AnnotationValue {

    record Expression(Region region, KExpr expr) implements AnnotationAST {}
    record Type(Region region, KType type) implements AnnotationAST {}
}
