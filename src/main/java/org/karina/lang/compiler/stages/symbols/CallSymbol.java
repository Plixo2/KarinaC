package org.karina.lang.compiler.stages.symbols;

import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.objects.KType;

import java.util.List;

public sealed interface CallSymbol {

    KType returnType();

    record CallStatic(ObjectPath path, List<KType> generics, KType returnType) implements CallSymbol { }

    record CallVirtual(Span nameRegion, KType.ClassType classType, ObjectPath path, List<KType> generics, KType returnType) implements CallSymbol { }
    record CallDynamic(KType returnType) implements CallSymbol { }

}
