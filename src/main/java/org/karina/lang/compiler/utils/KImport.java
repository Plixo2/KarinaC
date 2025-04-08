package org.karina.lang.compiler.utils;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record KImport(
        @NonNull Region region, @NonNull TypeImport importType, @NonNull ObjectPath path
) {

    public sealed interface TypeImport {

        //import the class
        record Base() implements TypeImport { }
        //import all classes and static methods inside a class
        record All() implements TypeImport { }
        //import classes and static methods inside a class via name
        record Names(ImmutableList<String> names) implements TypeImport {}

        //import the class with a alias
        record BaseAs(Region region, String alias) implements TypeImport { }
    }

}
