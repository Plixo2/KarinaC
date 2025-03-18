package org.karina.lang.compiler.model_api;


import org.karina.lang.compiler.objects.KType;

public interface ModelParameter {
    String name();
    KType type();
}
