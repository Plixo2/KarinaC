package org.karina.lang.compiler.jvm;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Type.*;

public class TypeGeneration {

    public KType fromType(Region region, String desc) {
        var type = Type.getType(desc);
        return fromType(region, type);
    }

    public KType fromType(Region region, Type desc) {
        return switch (desc.getSort()) {
            case VOID -> KType.VOID;
            case BOOLEAN -> new KType.PrimitiveType(KType.KPrimitive.BOOL);
            case CHAR -> new KType.PrimitiveType(KType.KPrimitive.CHAR);
            case BYTE -> new KType.PrimitiveType(KType.KPrimitive.BYTE);
            case SHORT -> new KType.PrimitiveType(KType.KPrimitive.SHORT);
            case INT -> new KType.PrimitiveType(KType.KPrimitive.INT);
            case FLOAT -> new KType.PrimitiveType(KType.KPrimitive.FLOAT);
            case LONG -> new KType.PrimitiveType(KType.KPrimitive.LONG);
            case DOUBLE -> new KType.PrimitiveType(KType.KPrimitive.DOUBLE);
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



    public ClassPointer internalNameToPointer(Region region, String name) {
        var nameSplit = name.split("[/$]");
        var path = new ObjectPath();
        for (String s : nameSplit) {
            path = path.append(s);
        }
        //TODO test existance of class
        return ClassPointer.of(region, path);
    }

}
