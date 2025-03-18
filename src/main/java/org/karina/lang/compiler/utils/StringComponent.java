package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;

public sealed interface StringComponent {

    record StringLiteralComponent(String value) implements StringComponent {}
    record VariableComponent(Region region, String name, @Nullable @Symbol Variable variable) implements StringComponent {}

}
