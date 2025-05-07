package org.karina.lang.compiler.stages.lower;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Region;

public class LowerType {
    private final LoweringContext context;

    public LowerType(LoweringContext context) {
        this.context = context;
    }

    public KType lowerType(Region region, KType type) {
        return switch (type) {
            case KType.ArrayType arrayType -> new KType.ArrayType(lowerType(region, arrayType.elementType()));
            case KType.ClassType classType -> lowerClassType(region, classType);
            case KType.FunctionType functionType -> lowerFunctionType(region, functionType);
            case KType.GenericLink genericLink -> genericLink;
            case KType.PrimitiveType primitiveType -> primitiveType;
            case KType.Resolvable resolvable -> {
                if (resolvable.isResolved()) {
                    assert resolvable.get() != null;
                    yield lowerType(region, resolvable.get());
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

    public KType lowerClassType(Region region, KType.ClassType type) {
        var pointer = type.pointer();
        var generics = type.generics().stream().map(ref -> lowerType(region, ref)).toList();
        return new KType.ClassType(pointer, generics);
    }

    private KType lowerFunctionType(Region region, KType.FunctionType functionType) {
        return this.context.getOrCreateInterface(region, functionType);
    }

}
