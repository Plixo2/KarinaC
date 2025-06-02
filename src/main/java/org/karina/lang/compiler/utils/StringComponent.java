package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;

public sealed interface StringComponent {

    record StringLiteralComponent(String value) implements StringComponent {}
    record ExpressionComponent(Region region, KExpr expression) implements StringComponent {}

}
