package org.karina.lang.compiler;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KType;

import java.util.List;

public sealed interface BranchPattern {
    KType type();

    record Cast(Span region, KType type, SpanOf<String> castedName, @Nullable Variable symbol) implements BranchPattern {
    }

    record Destruct(Span region, KType type, List<NameAndOptType> variables)
            implements BranchPattern {
    }
}
