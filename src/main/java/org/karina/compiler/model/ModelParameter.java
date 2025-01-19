package org.karina.compiler.model;


import org.karina.lang.compiler.objects.KType;

public interface ModelParameter {
    String name();
    KType type();
}
