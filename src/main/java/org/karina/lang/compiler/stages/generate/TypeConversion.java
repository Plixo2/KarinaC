package org.karina.lang.compiler.stages.generate;

import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.Types;
import org.karina.lang.compiler.utils.ObjectPath;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureWriter;

import java.util.List;
import java.util.Set;

public class TypeConversion {

    private static final Set<String> KEYWORDS = Set.of(
            "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package",
            "synchronized", "boolean", "do", "if", "private", "this", "break", "double",
            "implements", "protected", "throw", "byte", "else", "import", "public", "throws",
            "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short",
            "try", "char", "final", "interface", "static", "void", "class", "finally", "long",
            "strictfp", "volatile", "const", "float", "native", "super", "while"
    );

    public static Type getType(ClassPointer classPointer) {
        return Type.getObjectType(toJVMPath(classPointer.path()));
    }

    public static Type getType(KType type) {
        switch (type) {
            case KType.ArrayType arrayType -> {
                return arrayType(arrayType);
            }
            case KType.ClassType classType -> {
                return Type.getObjectType(toJVMPath(classType.pointer().path()));
            }
            case KType.FunctionType functionType -> {
                if (functionType.interfaces().isEmpty()) {
                    throw new NullPointerException("no valid interface found for function type " + functionType);
                }
                return getType(functionType.interfaces().getFirst());
                //                return getType(PostExpr.toClassType(functionType));
            }
            case KType.GenericLink genericLink -> {
                // TODO replace with correct erased type
                return Type.getType(Object.class);
            }
            case KType.PrimitiveType primitiveType -> {
                return switch (primitiveType.primitive()) {
                    case BOOL -> Type.BOOLEAN_TYPE;
                    case BYTE -> Type.BYTE_TYPE;
                    case CHAR -> Type.CHAR_TYPE;
                    case DOUBLE -> Type.DOUBLE_TYPE;
                    case FLOAT -> Type.FLOAT_TYPE;
                    case INT -> Type.INT_TYPE;
                    case LONG -> Type.LONG_TYPE;
                    case SHORT -> Type.SHORT_TYPE;
                };
            }
            case KType.Resolvable resolvable -> {
                if (resolvable.isResolved()) {
                    assert resolvable.get() != null;
                    return getType(resolvable.get());
                } else {
                    return Type.getType(Object.class);
                }
            }
            case KType.UnprocessedType unprocessedType -> {
                Log.temp(unprocessedType.region(), "Unprocessed type " + unprocessedType + " should not exist");
                throw new Log.KarinaException();
            }
            case KType.VoidType voidType -> {
                return Type.VOID_TYPE;
            }
        }
    }

    private static Type arrayType(KType.ArrayType arrayType) {
        return Type.getType("[" + getType(arrayType.elementType()));
    }

//    public static String toJVMName(String name) {
//        if (KEYWORDS.contains(name)) {
//            name =  "K" + name;
//        }
//        var buff = new StringBuilder(name.length());
//        for (int i = 0; i < name.length(); i++) {
//            var c = name.charAt(i);
//            boolean isAccepted = c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (i != 0 && c >= '0' && c <= '9');
//            if (isAccepted) {
//                buff.append(c);
//            } else {
//                buff.append("K");
//                buff.append(padLeft(Integer.toHexString(c), 4, "0"));
//            }
//        }
//        return buff.toString();
//    }

    private static String padLeft(String str, int length, String pad) {

        if (str.length() >= length) {
            return str;
        }

        var append = length - str.length();
        return pad.repeat(append) + str;
    }

    //if double or long, return 2, else return 1
    public static int jvmSize(Type type) {
        return switch (type.getSort()) {
            case Type.DOUBLE, Type.LONG -> 2;
            default -> 1;
        };
    }

    public static String toJVMPath(ObjectPath path) {
        var builder = new StringBuilder();
        var iterator = path.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append("/");
            }
        }

        return builder.toString();
    }

    public static int getNewArrayConstant(Type type) {
        return switch (type.getSort()) {
            case Type.BOOLEAN -> Opcodes.T_BOOLEAN;
            case Type.CHAR -> Opcodes.T_CHAR;
            case Type.FLOAT -> Opcodes.T_FLOAT;
            case Type.DOUBLE -> Opcodes.T_DOUBLE;
            case Type.BYTE -> Opcodes.T_BYTE;
            case Type.SHORT -> Opcodes.T_SHORT;
            case Type.INT -> Opcodes.T_INT;
            case Type.LONG -> Opcodes.T_LONG;
            default -> throw new IllegalArgumentException("Unsupported fieldType for NEWARRAY: " + type);
        };
    }
    public static String getDesc(MethodPointer pointer, KType returnType) {
        var erasedReturnType = Types.erase(returnType);
        return TypeConversion.getDesc(pointer.erasedParameters(), erasedReturnType);
    }
    public static String getDesc(@NotNull Signature signature) {
        return TypeConversion.getDesc(signature.parametersErased(), signature.returnType());
    }
    public static String getDesc(List<KType> params, KType returnType) {
        var returnT = getType(Types.erase(returnType));
        var paramsT = new Type[params.size()];
        for (var i = 0; i < params.size(); i++) {
            paramsT[i] = getType(Types.erase(params.get(i)));
        }

        return Type.getMethodDescriptor(returnT, paramsT);
    }



}
