package org.karina.lang.compiler.model;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.utils.Generic;

public interface MethodModel {
    int modifiers();
    String name();
    ImmutableList<String> parameters();
    Signature signature();
    ImmutableList<Generic> generics();
    @Nullable KExpr expression();
}
