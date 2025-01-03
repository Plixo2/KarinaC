package org.karina.lang.compiler;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KType;

public record NameAndOptType(Span region, SpanOf<String> name, @Nullable KType type, @Nullable KType symbol) {
}
