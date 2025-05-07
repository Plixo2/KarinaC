package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;

public record NameAndOptType(
        Region region,
        RegionOf<String> name,
        @Nullable KType type,
        @Nullable @Symbol Variable symbol
) { }
