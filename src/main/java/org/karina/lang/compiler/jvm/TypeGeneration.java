package org.karina.lang.compiler.jvm;

import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.generate.TypeConversion;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Type.*;

/**
 * Helper for loading ASM Types (JVM -> Karina)
 * {@link TypeConversion} is used for the opposite direction (Karina -> JVM)
 */
public class TypeGeneration {

    public KType fromType(Region region, String desc) {
        var type = Type.getType(desc);
        return fromType(region, type);
    }

    public KType fromType(Region region, Type desc) {
        return switch (desc.getSort()) {
            case VOID -> KType.NONE;
            case BOOLEAN -> KType.BOOL;
            case CHAR -> KType.CHAR;
            case BYTE -> KType.BYTE;
            case SHORT -> KType.SHORT;
            case INT -> KType.INT;
            case FLOAT -> KType.FLOAT;
            case LONG -> KType.LONG;
            case DOUBLE -> KType.DOUBLE;
            case ARRAY -> {
                var elementType = fromType(region, desc.getElementType().getDescriptor());
                yield new KType.ArrayType(elementType);
            }
            case OBJECT -> {
                var pointer = internalNameToPointer(region, desc.getInternalName());
                yield new KType.ClassType(pointer, List.of());
            }
            default -> throw new IllegalStateException("Unexpected value: " + desc.getSort());
        };
    }

    public KType getReturnType(Region region, String desc) {
        var returnType = Type.getReturnType(desc);
        return fromType(region, returnType);
    }

    public List<KType> getParameters(Region region, String desc) {
        var returnType = Type.getArgumentTypes(desc);
        List<KType> types = new ArrayList<>();
        for (var type : returnType) {
            types.add(fromType(region, type));
        }
        return types;
    }



    public static ClassPointer internalNameToPointer(Region region, String name) {
        var path = ObjectPath.fromJavaPath(name);
        //TODO test existence of class
        return ClassPointer.of(region, path);
    }

}
