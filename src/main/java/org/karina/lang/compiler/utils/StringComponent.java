package org.karina.lang.compiler.utils;

import org.karina.lang.compiler.objects.KExpr;

public sealed interface StringComponent {

    record StringLiteralComponent(String value) implements StringComponent {}
    record ExpressionComponent(Region region, String name, KExpr expression) implements StringComponent {}

}
