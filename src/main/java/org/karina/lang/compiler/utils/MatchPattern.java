package org.karina.lang.compiler.utils;

import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;

import java.util.List;

public sealed interface MatchPattern {
    KExpr expr();
    Region region();

    record Default(Region region, KExpr expr) implements MatchPattern {}
    record Cast(Region region, KType type, RegionOf<String> name, KExpr expr) implements MatchPattern {}
    record Destruct(Region region, KType type, List<NameAndOptType> variables, KExpr expr) implements MatchPattern {}

}
