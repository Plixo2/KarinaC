package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.objects.KType;

import java.util.List;

public sealed interface CallSymbol {

    KType returnType();

    record CallStatic(ObjectPath path, List<KType> generics, KType returnType, List<KType> argTypeStatic, KType returnTypeStatic, boolean inInterface) implements CallSymbol { }

    record CallVirtual(Span nameRegion, KType.ClassType classType, ObjectPath path, List<KType> generics, KType returnType, List<KType> argTypeStatic, KType returnTypeStatic) implements CallSymbol { }
    record CallInterface(Span nameRegion, KType.ClassType classType, ObjectPath path, List<KType> generics, KType returnType , List<KType> argTypeStatic, KType returnTypeStatic) implements CallSymbol { }
    record CallDynamic(Span region, KType returnType) implements CallSymbol { }

}
