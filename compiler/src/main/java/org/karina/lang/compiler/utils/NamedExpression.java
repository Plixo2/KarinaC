package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;

public record NamedExpression(Region region, RegionOf<String> name, KExpr expr, @Nullable @Symbol KType symbol) {
}
