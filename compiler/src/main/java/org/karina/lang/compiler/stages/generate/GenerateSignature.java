package org.karina.lang.compiler.stages.generate;

import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Generic;

public class GenerateSignature {

    public static String getClassSignature(Model model, KClassModel classModel) {

        var builder = new StringBuilder();
        if (!classModel.generics().isEmpty()) {
            builder.append("<");
            for (var generic : classModel.generics()) {
                builder.append(typeParameter(model, generic));
            }
            builder.append(">");
        }
        var superClass = classModel.superClass();
        if (superClass == null) {
            superClass = KType.ROOT;
        }
        builder.append(typeToString(model, superClass));

        for (var anInterface : classModel.interfaces()) {
            builder.append(typeToString(model, anInterface));
        }

        return builder.toString();
    }

    public static String fieldSignature(Model model, KType type) {
        return typeToString(model, type);
    }

    public static String methodSignature(Model model, KMethodModel methodModel) {
        var builder = new StringBuilder();

        if (!methodModel.generics().isEmpty()) {
            builder.append("<");
            for (var generic : methodModel.generics()) {
                builder.append(typeParameter(model, generic));
            }
            builder.append(">");
        }
        builder.append("(");

        for (var parameter : methodModel.signature().parameters()) {
            builder.append(typeToString(model, parameter));
        }

        builder.append(")");

        builder.append(typeToString(model, methodModel.signature().returnType()));

        return builder.toString();
    }

    private static String typeParameter(Model model, Generic generic) {
        var builder = new StringBuilder();
        builder.append(generic.name());

        builder.append(":");
        if (generic.superType() != null) {
            builder.append(typeToString(model, generic.superType()));
        }

        for (var bound : generic.bounds()) {
            builder.append(":");
            builder.append(typeToString(model, bound));
        }
        if (generic.superType() == null && generic.bounds().isEmpty()) {
            builder.append(typeToString(model, KType.ROOT));
        }

        return builder.toString();

    }

    private static String typeToString(Model model, KType type) {
        type = type.unpack();

        switch (type) {
            case KType.ArrayType arrayType -> {
                return "[" + typeToString(model, arrayType.elementType());
            }
            case KType.ClassType classType -> {
                var path = TypeEncoding.toJVMPath(model, classType.pointer());

                if (classType.generics().isEmpty()) {
                    return "L" + path + ";";
                }
                var types = new StringBuilder();

                for (var generic : classType.generics()) {
                    types.append(typeToString(model, generic));
                }

                return "L" + path + "<" + types + ">;";
            }
            case KType.FunctionType functionType -> {
                //TODO this should be replaced with the primary interface in the lower phase
                if (functionType.interfaces().isEmpty()) {
                    //Should not happen
                    return typeToString(model, KType.ROOT);
                }
                return typeToString(model, functionType.interfaces().getFirst());
            }
            case KType.GenericLink genericLink -> {
                return "T" + genericLink.link().name() + ";";
            }
            case KType.PrimitiveType primitiveType -> {
                return switch (primitiveType.primitive()) {
                    case INT -> "I";
                    case FLOAT -> "F";
                    case BOOL -> "Z";
                    case CHAR -> "C";
                    case DOUBLE -> "D";
                    case BYTE -> "B";
                    case SHORT -> "S";
                    case LONG -> "J";
                };
            }
            case KType.Resolvable resolvable -> {
                //resolved case is covered by type.unpack()
                return typeToString(model, KType.ROOT);
            }
            case KType.UnprocessedType unprocessedType -> {
                return typeToString(model, KType.ROOT);
            }
            case KType.VoidType voidType -> {
                return "V";
            }
        }

    }
}
