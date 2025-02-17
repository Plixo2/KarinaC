package org.karina.lang.compiler.model;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.model.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.Region;

public interface MethodModel {
    int modifiers();
    String name();
    ImmutableList<String> parameters();
    Signature signature();
    ImmutableList<Generic> generics();
    @Nullable KExpr expression();
    Region region();
    MethodPointer pointer();
    ClassPointer classPointer();
}
