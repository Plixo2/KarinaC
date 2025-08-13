package org.karina.lang.compiler.stages.generate;

import com.google.common.base.Joiner;
import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.jvm_loading.TypeDecoding;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Types;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Helper for generating ASM Types from Karina types (Karina -> JVM)
 * {@link TypeDecoding} is used for the opposite direction (JVM -> Karina)
 */
public class TypeEncoding {

    private static final Set<String> KEYWORDS = Set.of(
            "abstract", "continue", "for", "new", "switch", "assert", "default", "goto", "package",
            "synchronized", "boolean", "do", "if", "private", "this", "break", "double",
            "implements", "protected", "throw", "byte", "else", "import", "public", "throws",
            "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short",
            "try", "char", "final", "interface", "static", "void", "class", "finally", "long",
            "strictfp", "volatile", "const", "float", "native", "super", "while"
    );


    public static String getDescriptor(Model model, KType type) {
        type = type.unpack();
        switch (type) {
            case KType.ArrayType arrayType -> {
                return "[" + getDescriptor(model, arrayType.elementType());
            }
            case KType.ClassType classType -> {
                return "L" + toJVMPath(model, classType.pointer()) + ";";
            }
            case KType.FunctionType functionType -> {
                if (functionType.interfaces().isEmpty()) {
                    throw new NullPointerException("no valid interface found for function type " + functionType);
                }
                if (!(functionType.interfaces().getFirst() instanceof KType.ClassType classType)) {
                    throw new IllegalStateException("Function type interface is not a class type: " + functionType.interfaces().getFirst());
                }
                return "L" + toJVMPath(model, classType.pointer()) + ";";
            }
            case KType.GenericLink genericLink -> {
                var generic = genericLink.link();

                if (generic.superType() != null) {
                    return getDescriptor(model, generic.superType());
                } else if (!generic.bounds().isEmpty()) {
                    return getDescriptor(model, generic.bounds().getFirst());
                } else {
                    return "Ljava/lang/Object;";
                }
            }
            case KType.PrimitiveType primitiveType -> {
                return switch (primitiveType.primitive()) {
                    case BOOL -> "Z";
                    case BYTE -> "B";
                    case CHAR -> "C";
                    case DOUBLE -> "D";
                    case FLOAT -> "F";
                    case INT -> "I";
                    case LONG -> "J";
                    case SHORT -> "S";
                };
            }
            case KType.Resolvable resolvable -> {
                resolvable.collapseToObject();
                return "Ljava/lang/Object;";
            }
            case KType.UnprocessedType unprocessedType -> {
                throw new IllegalStateException("Unprocessed type " + unprocessedType + " should not exist");
            }
            case KType.VoidType voidType -> {
                return "V";
            }
        }
    }


    public static String getInternalName(Model model, KType type) {
        type = type.unpack();
        switch (type) {
            case KType.ArrayType arrayType -> {
                return "[" + getDescriptor(model, arrayType.elementType());
            }
            case KType.ClassType classType -> {
                return toJVMPath(model, classType.pointer());
            }
            case KType.FunctionType functionType -> {
                if (functionType.interfaces().isEmpty()) {
                    throw new NullPointerException("no valid interface found for function type " + functionType);
                }
                if (!(functionType.interfaces().getFirst() instanceof KType.ClassType classType)) {
                    throw new IllegalStateException("Function type interface is not a class type: " + functionType.interfaces().getFirst());
                }
                return toJVMPath(model, classType.pointer());
            }
            case KType.GenericLink genericLink -> {
                var generic = genericLink.link();

                if (generic.superType() != null) {
                    return getInternalName(model, generic.superType());
                } else if (!generic.bounds().isEmpty()) {
                    return getInternalName(model, generic.bounds().getFirst());
                } else {
                    return "java/lang/Object";
                }
            }

            case KType.Resolvable resolvable -> {
                resolvable.collapseToObject();
                return "java/lang/Object";
            }
            case KType.UnprocessedType unprocessedType -> {
                throw new IllegalStateException("Unprocessed type " + unprocessedType + " should not exist");
            }
            case KType.PrimitiveType primitiveType -> {
                throw new IllegalStateException("Primitive types should not be used as internal names: " + primitiveType);
            }
            case KType.VoidType _ -> {
                throw new IllegalStateException("Void type should not be used as internal name");
            }
        }
    }


    private static String padLeft(String str, int length, String pad) {

        if (str.length() >= length) {
            return str;
        }

        var append = length - str.length();
        return pad.repeat(append) + str;
    }


    //if double or long, return 2, else return 1
    public static int jvmSize(KType type) {
        type = type.unpack();
        if (type instanceof KType.PrimitiveType(var primitive)) {
            return switch (primitive) {
                case LONG, DOUBLE -> 2;
                default -> 1;
            };
        }
        return 1;
    }


    public static String toJVMPath(Model model, ClassPointer pointer) {
        var outerMostClass = model.getClass(pointer);
        var suffix = new ArrayList<String>();

        while (true) {
            var outer = outerMostClass.outerClass();
            if (outer == null) {
                break;
            }
            suffix.add(outerMostClass.name());
            outerMostClass = outer;
        }
        var prefixStr = outerMostClass.path().mkString("/");
        if (suffix.isEmpty()) {
            return prefixStr;
        } else {
            var suffixStr = String.join("$", suffix.reversed());
            return prefixStr + "$" + suffixStr;
        }
    }


    public static int getNewPrimitiveArrayConstant(KType type) {
        type = type.unpack();
        if (type instanceof KType.PrimitiveType(var primitive)) {
            return switch (primitive) {
                case KType.KPrimitive.BOOL -> Opcodes.T_BOOLEAN;
                case KType.KPrimitive.CHAR -> Opcodes.T_CHAR;
                case KType.KPrimitive.FLOAT -> Opcodes.T_FLOAT;
                case KType.KPrimitive.DOUBLE -> Opcodes.T_DOUBLE;
                case KType.KPrimitive.BYTE -> Opcodes.T_BYTE;
                case KType.KPrimitive.SHORT -> Opcodes.T_SHORT;
                case KType.KPrimitive.INT -> Opcodes.T_INT;
                case KType.KPrimitive.LONG -> Opcodes.T_LONG;
            };
        }
        throw new IllegalArgumentException("Unsupported fieldType for NEWARRAY: " + type);
    }


    public static String getDesc(Model model, MethodPointer pointer) {
        return getDesc(model, pointer.erasedParameters(), pointer.erasedReturnType());
    }
    public static String getDesc(Model model, @NotNull Signature signature) {
        return TypeEncoding.getDesc(model, signature.parametersErased(), Types.erase(signature.returnType()));
    }
    private static String getDesc(Model model, List<KType> erasedParams, KType erasedReturnType) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('(');
        for (var erasedParam : erasedParams) {
            stringBuilder.append(getDescriptor(model, erasedParam));
        }
        stringBuilder.append(')');
        stringBuilder.append(getDescriptor(model, erasedReturnType));
        return stringBuilder.toString();
    }

    public static int getReturnInstruction(KType returnType) {
        return getCode(returnType.unpack(), Opcodes.IRETURN);
    }
    public static int getVariableStoreInstruction(KType variableType) {
        return getCode(variableType.unpack(), Opcodes.ISTORE);
    }
    public static int getVariableLoadInstruction(KType variableType) {
        return getCode(variableType.unpack(), Opcodes.ILOAD);
    }
    public static int getArrayStoreInstruction(KType arrayElementType) {
        return getCode(arrayElementType.unpack(), Opcodes.IASTORE);
    }

    public static int getArrayLoadInstruction(KType arrayElementType) {
        return getCode(arrayElementType.unpack(), Opcodes.IALOAD);
    }

    /// From [Type]:
    ///
    /// @param type type to convert the opcode to.
    ///
    /// @param opcode a JVM instruction opcode. This opcode must be one of ILOAD, ISTORE, IALOAD,
    ///        IASTORE, IADD, ISUB, IMUL, IDIV, IREM, INEG, ISHL, ISHR, IUSHR, IAND, IOR, IXOR and
    ///        IRETURN.
    ///
    ///
    ///
    private static int getCode(KType type, int opcode) {
        if (opcode == Opcodes.IALOAD || opcode == Opcodes.IASTORE) {
            return switch (type) {
                case KType.PrimitiveType(var primitive) -> switch (primitive) {
                    case INT -> opcode;
                    case CHAR -> opcode + (Opcodes.CALOAD - Opcodes.IALOAD);
                    case SHORT -> opcode + (Opcodes.SALOAD - Opcodes.IALOAD);
                    case FLOAT -> opcode + (Opcodes.FALOAD - Opcodes.IALOAD);
                    case DOUBLE -> opcode + (Opcodes.DALOAD - Opcodes.IALOAD);
                    case LONG -> opcode + (Opcodes.LALOAD - Opcodes.IALOAD);
                    case BYTE, BOOL -> opcode + (Opcodes.BALOAD - Opcodes.IALOAD);
                };
                case KType.Resolvable resolvable -> {
                    resolvable.collapseToObject();
                    yield opcode + (Opcodes.AALOAD - Opcodes.IALOAD);
                }
                case KType any when Types.hasIdentity(any) ->
                        opcode + (Opcodes.AALOAD - Opcodes.IALOAD);
                default -> {
                    throw new IllegalArgumentException("Unsupported type for: " + type);
                }
            };
        } else {
            return switch (type) {
                case KType.PrimitiveType(var primitive) -> switch (primitive) {
                    case INT, CHAR, BYTE, BOOL, SHORT -> opcode;
                    case FLOAT -> opcode + (Opcodes.FRETURN - Opcodes.IRETURN);
                    case DOUBLE -> opcode + (Opcodes.DRETURN - Opcodes.IRETURN);
                    case LONG -> opcode + (Opcodes.LRETURN - Opcodes.IRETURN);
                };
                case KType.VoidType _ -> {
                    if (opcode != Opcodes.IRETURN) {
                        throw new IllegalArgumentException("Unsupported opcode for void type: " + opcode);
                    }
                    yield Opcodes.RETURN;
                }
                case KType.Resolvable resolvable -> {
                    resolvable.collapseToObject();
                    if (opcode != Opcodes.ILOAD && opcode != Opcodes.ISTORE && opcode != Opcodes.IRETURN) {
                        throw new IllegalArgumentException("Unsupported opcode for objects: " + opcode);
                    }
                    yield opcode + (Opcodes.ARETURN - Opcodes.IRETURN);
                }
                case KType any when Types.hasIdentity(any) -> {
                    if (opcode != Opcodes.ILOAD && opcode != Opcodes.ISTORE && opcode != Opcodes.IRETURN) {
                        throw new IllegalArgumentException("Unsupported opcode for objects: " + opcode);
                    }
                    yield opcode + (Opcodes.ARETURN - Opcodes.IRETURN);
                }
                default -> {
                    throw new IllegalArgumentException("Unsupported type for: " + type);
                }
            };
        }
    }
}
