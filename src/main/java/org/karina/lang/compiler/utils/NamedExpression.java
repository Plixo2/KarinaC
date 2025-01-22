package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;

public record NamedExpression(Region region, RegionOf<String> name, KExpr expr, @Nullable @Symbol KType symbol) {
}
