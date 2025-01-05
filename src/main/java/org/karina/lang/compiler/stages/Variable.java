package org.karina.lang.compiler.stages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.objects.KType;


@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public class Variable {
    private final @NotNull Span region;
    private final @NotNull String name;
    private final @NotNull KType type;
    private final boolean mutable;
    private final boolean parameter;

//    public Variable(Span region, String name, KType type, boolean mutable, boolean parameter) {
//        this.region = region;
//        this.name = name;
//        this.type = type;
//        this.mutable = mutable;
//        this.parameter = parameter;
//    }
}
