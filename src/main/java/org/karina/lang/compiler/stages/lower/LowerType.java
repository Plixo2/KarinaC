package org.karina.lang.compiler.stages.lower;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KType;

import java.util.List;

public class LowerType {
    private final LoweringContext context;

    public LowerType(LoweringContext context) {
        this.context = context;
    }

    public KType lowerType(KType type) {
        return switch (type) {
            case KType.ArrayType arrayType -> new KType.ArrayType(lowerType(arrayType.elementType()));
            case KType.ClassType classType -> lowerClassType(classType);
            case KType.FunctionType functionType -> lowerFunctionType(functionType);
            case KType.GenericLink genericLink -> genericLink;
            case KType.PrimitiveType primitiveType -> primitiveType;
            case KType.Resolvable resolvable -> {
                if (resolvable.isResolved()) {
                    assert resolvable.get() != null;
                    yield lowerType(resolvable.get());
                }
                yield KType.ROOT;
            }
            case KType.VoidType voidType -> voidType;
            case KType.UnprocessedType unprocessedType -> {
                Log.temp(this.context.owningMethod().region(), "Unprocessed type");
                throw new Log.KarinaException();
            }
        };

    }

    public KType lowerClassType(KType.ClassType type) {
        var pointer = type.pointer();
        var generics = type.generics().stream().map(this::lowerType).toList();
        return new KType.ClassType(pointer, generics);
    }

    private KType lowerFunctionType(KType.FunctionType functionType) {
        return this.context.getOrCreateInterface(functionType);
    }

}
