package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KType;

public record NameAndOptType(
        Region region,
        RegionOf<String> name,
        @Nullable KType type,
        @Nullable @Symbol Variable symbol
) { }
