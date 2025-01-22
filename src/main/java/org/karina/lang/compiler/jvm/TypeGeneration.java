package org.karina.lang.compiler.jvm;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Type.*;

public class TypeGeneration {

    public KType fromType(String desc, @Nullable String signature) {
        var type = Type.getType(desc);
        return fromType(type, signature);
    }

    public KType fromType(Type desc, @Nullable String signature) {
        return switch (desc.getSort()) {
            case VOID -> new KType.PrimitiveType(KType.KPrimitive.VOID);
            case BOOLEAN -> new KType.PrimitiveType(KType.KPrimitive.BOOL);
            case CHAR -> new KType.PrimitiveType(KType.KPrimitive.CHAR);
            case BYTE -> new KType.PrimitiveType(KType.KPrimitive.BYTE);
            case SHORT -> new KType.PrimitiveType(KType.KPrimitive.SHORT);
            case INT -> new KType.PrimitiveType(KType.KPrimitive.INT);
            case FLOAT -> new KType.PrimitiveType(KType.KPrimitive.FLOAT);
            case LONG -> new KType.PrimitiveType(KType.KPrimitive.LONG);
            case DOUBLE -> new KType.PrimitiveType(KType.KPrimitive.DOUBLE);
            case ARRAY -> {
                var elementType = fromType(desc.getElementType().getDescriptor(), null);
                yield new KType.ArrayType(elementType);
            }
            case OBJECT -> {
                var pointer = internalNameToPointer(desc.getInternalName());
                yield new KType.ClassType(pointer, List.of());
            }
            default -> throw new IllegalStateException("Unexpected value: " + desc.getSort());
        };
    }

    public KType getReturnType(String desc, @Nullable String signature) {
        var returnType = Type.getReturnType(desc);
        return fromType(returnType, null);
    }

    public List<KType> getParameters(String desc, @Nullable String signature) {
        var returnType = Type.getArgumentTypes(desc);
        List<KType> types = new ArrayList<>();
        for (var type : returnType) {
            types.add(fromType(type, null));
        }
        return types;
    }



    public ClassPointer internalNameToPointer(String name) {
        var nameSplit = name.split("/");
        var path = new ObjectPath();
        for (String s : nameSplit) {
            path = path.append(s);
        }
        //TODO test existance of class
        return ClassPointer.of(path);
    }

}
