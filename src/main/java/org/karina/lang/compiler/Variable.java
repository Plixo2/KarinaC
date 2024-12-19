package org.karina.lang.compiler;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KType;

//On the Variable stage, type is always Void
//In the Infer stage, is the hint if it exists, otherwise is the inferred type
@RequiredArgsConstructor
public class Variable {
    private final String name;
    private final KType type;
    private final boolean mutable;
    private final boolean parameter;
}
