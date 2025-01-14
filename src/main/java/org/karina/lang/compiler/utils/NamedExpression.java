package org.karina.lang.compiler.utils;

import org.karina.lang.compiler.objects.KExpr;

public record NamedExpression(Span region, SpanOf<String> name, KExpr expr) {
}
