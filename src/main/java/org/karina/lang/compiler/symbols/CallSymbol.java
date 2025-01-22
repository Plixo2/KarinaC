package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.objects.KType;

import java.util.List;

public sealed interface CallSymbol {

    KType returnType();

    record CallStatic(ObjectPath path, List<KType> generics, KType returnType, List<KType> argTypeStatic, KType returnTypeStatic, boolean inInterface) implements CallSymbol { }

    record CallVirtual(Region nameRegion, KType.ClassType classType, ObjectPath path, List<KType> generics, KType returnType, List<KType> argTypeStatic, KType returnTypeStatic) implements CallSymbol { }
    record CallInterface(Region nameRegion, KType.ClassType classType, ObjectPath path, List<KType> generics, KType returnType , List<KType> argTypeStatic, KType returnTypeStatic) implements CallSymbol { }
    record CallDynamic(Region region, KType returnType) implements CallSymbol { }

}
