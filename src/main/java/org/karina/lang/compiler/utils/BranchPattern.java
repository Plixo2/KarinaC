package org.karina.lang.compiler.utils;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public sealed interface BranchPattern {
    KType type();
    Region region();

    record Cast(Region region, KType type, RegionOf<String> castedName, @Nullable Variable symbol) implements BranchPattern { }
    record Destruct(Region region, KType type, List<NameAndOptType> variables) implements BranchPattern { }
    //this would be just a instance check and this is only used in the optional else pattern
    record JustType(Region region, KType type) implements BranchPattern { }
}
