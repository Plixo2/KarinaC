package org.karina.lang.compiler.utils;

import com.google.common.collect.ImmutableList;

import java.util.List;

public sealed interface TypeImport {

    //import the class
    record Base() implements TypeImport { }
    //import all classes and static methods inside a class
    record All() implements TypeImport { }
    //import classes and static methods inside a class via name
    record Names(ImmutableList<String> names) implements TypeImport {}
}
