package org.karina.lang.compiler.symbols;

import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.InvocationType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.objects.KType;

import java.util.List;

public sealed interface CallSymbol {

    KType returnType();

    //CallStatic cannot use the object 'left' that is defined in the Call expression.
    // The MethodPointer is the only thing to get information about the method.
    // Also only used for compilation.
    record CallStatic(MethodPointer pointer, List<KType> generics, KType returnType) implements CallSymbol { }

    record CallVirtual(MethodPointer pointer, List<KType> generics, KType returnType) implements CallSymbol { }
    record CallInterface(MethodPointer pointer, List<KType> generics, KType returnType) implements CallSymbol { }


    record CallSuper(MethodPointer pointer, List<KType> generics, KType returnType, InvocationType invocationType) implements CallSymbol { }
    record CallDynamic(Region region, KType returnType) implements CallSymbol { }

}
