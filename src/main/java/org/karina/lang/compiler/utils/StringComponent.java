package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;

public sealed interface StringComponent {

    record StringLiteralComponent(String value) implements StringComponent {}
    record ExpressionComponent(Region region, String name, @Nullable @Symbol KExpr expression) implements StringComponent {}

}
