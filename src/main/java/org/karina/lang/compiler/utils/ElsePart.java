package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;

public record ElsePart(KExpr expr, @Nullable BranchPattern elsePattern) {
}
