package org.karina.lang.compiler.utils;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;


@Getter
@Accessors(fluent = true)
public class Variable {
    private final @NotNull Region region;
    private final @NotNull String name;
    private final @NotNull KType type;
    private final boolean mutable;
    private final boolean parameter;
    private final boolean isSynthetic;

    public Variable(
            @NotNull Region region, @NotNull String name, @NotNull KType type, boolean mutable,
            boolean parameter
    ) {
        this(region, name, type, mutable, parameter, false);
    }

    public Variable(
            @NotNull Region region, @NotNull String name, @NotNull KType type, boolean mutable,
            boolean parameter, boolean isSynthetic
    ) {
        this.region = region;
        this.name = name;
        this.type = type;
        this.mutable = mutable;
        this.parameter = parameter;
        this.isSynthetic = isSynthetic;
    }

    //used for capturing
    private int usageCount = 0;

    public void incrementUsageCount() {
        this.usageCount++;
    }

    public int usageCount() {
        return this.usageCount;
    }

    @Override
    public String toString() {
        return "Variable{" + "region=" + region + ", name='" + name + '\'' + ", type=" + type +
                ", mutable=" + mutable + ", parameter=" + parameter + ", isSynthetic=" +
                isSynthetic + ", usageCount=" + usageCount + '}';
    }
}
