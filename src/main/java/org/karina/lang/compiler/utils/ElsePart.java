package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;

public record ElsePart(KExpr expr, @Nullable BranchPattern elsePattern) {
}
