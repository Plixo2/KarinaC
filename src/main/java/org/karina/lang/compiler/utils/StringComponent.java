package org.karina.lang.compiler.utils;


public sealed interface StringComponent {

    record StringLiteralComponent(String value) implements StringComponent {}
    record ExpressionComponent(Region region, KExpr expression) implements StringComponent {}

}
