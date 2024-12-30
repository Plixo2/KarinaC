package org.karina.lang.compiler;

import org.karina.lang.compiler.objects.KExpr;

public record NamedExpression(Span region, SpanOf<String> name, KExpr expr) {
}
