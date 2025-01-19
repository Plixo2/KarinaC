package org.karina.compiler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.objects.KType;

import java.util.List;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class Signature {
    public List<KType> parameters;
    public KType returnType;
}
