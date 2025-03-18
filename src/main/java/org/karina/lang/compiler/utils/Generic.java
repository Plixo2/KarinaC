package org.karina.lang.compiler.utils;


import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.objects.KType;

import java.util.List;

@Getter
@Accessors(fluent = true)
public class Generic {
    private final Region region;
    private final String name;
    private final @Nullable KType superType;
    private final List<KType> interfaces;


    public Generic(Region region, String name) {
        this.region = region;
        this.name = name;
        this.superType = null;
        this.interfaces = List.of();
    }

    public Generic(Region region, String name, @Nullable KType superType, List<KType> interfaces) {
        this.region = region;
        this.name = name;
        this.superType = superType;
        this.interfaces = interfaces;
    }

    @Override
    public String toString() {
        return "Generic{" + "region=" + region + ", name='" + name + '\'' + ", superType=" +
                superType + ", interfaces=" + interfaces + '}';
    }
}
