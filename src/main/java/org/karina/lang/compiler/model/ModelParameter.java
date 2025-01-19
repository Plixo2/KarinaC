package org.karina.lang.compiler.model;


import org.karina.lang.compiler.objects.KType;

public interface ModelParameter {
    String name();
    KType type();
}
