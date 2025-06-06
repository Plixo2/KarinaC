package org.karina.lang.compiler.utils;


import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Getter
@Accessors(fluent = true)
public class Generic {
    private final Region region;
    private final String name;
    private List<KType> bounds;
    private @Nullable KType superType;

    public Generic(Region region, String name) {
        this.region = region;
        this.name = name;
        this.bounds = List.of();
    }

    public void updateBounds(@Nullable KType superType, List<KType> bounds) {
        this.superType = superType;
        this.bounds = ImmutableList.copyOf(bounds);
    }

    @Override
    public String toString() {
        return "Generic{" + "region=" + this.region + ", name='" + this.name + '\'' + '}';
    }
}
