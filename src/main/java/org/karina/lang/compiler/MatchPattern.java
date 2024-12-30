package org.karina.lang.compiler;

import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;

import java.util.List;

public sealed interface MatchPattern {
    KExpr expr();
    Span region();

    record Default(Span region, KExpr expr) implements MatchPattern {}
    record Cast(Span region, KType type, SpanOf<String> name, KExpr expr) implements MatchPattern {}
    record Destruct(Span region, KType type, List<NameAndOptType> variables, KExpr expr) implements MatchPattern {}

}
