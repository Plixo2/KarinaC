package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;

/**
 * Else Part of an Branch
 * @param expr body of the else
 * @param elsePattern optional else pattern
 */
public record ElsePart(KExpr expr, @Nullable BranchPattern elsePattern) {
}
