package org.karina.lang.compiler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KType;

//On the Variable stage, type is always Void
//In the Infer stage, is the hint if it exists, otherwise is the inferred type
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class Variable {
    private final Span region;
    private final String name;
    private final KType type;
    private final boolean mutable;
    private final boolean parameter;
}
