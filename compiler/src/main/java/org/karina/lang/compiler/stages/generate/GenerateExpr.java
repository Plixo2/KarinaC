package org.karina.lang.compiler.stages.generate;

import org.apache.commons.text.StringEscapeUtils;
import org.karina.lang.compiler.jvm_loading.TypeDecoding;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.symbols.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Objects;

public class GenerateExpr implements Opcodes {


    public static void generate(KExpr expr, GenerationContext ctx) {

        var region = expr.region();
        var currentLine = region.start().line() + 1;
        if (currentLine != ctx.getLastLineNumber()) {
            var label = new LabelNode();
            ctx.add(label);
            ctx.add(new LineNumberNode(currentLine, label));
            ctx.setLastLineNumber(currentLine);
        }

        switch (expr) {
            case KExpr.Assignment assignment -> {
                assert assignment.symbol() != null;
                switch (assignment.symbol()) {
                    case AssignmentSymbol.ArrayElement arrayElement -> {
                        generate(arrayElement.array(), ctx);
                        generate(arrayElement.index(), ctx);
                        generate(assignment.right(), ctx);
                        var instruction = TypeEncoding.getArrayStoreInstruction(arrayElement.elementType());
                        ctx.add(new InsnNode(instruction));
                    }
                    case AssignmentSymbol.Field field -> {
                        generate(field.object(), ctx);
                        generate(assignment.right(), ctx);
                        var owner = TypeEncoding.toJVMPath(ctx.model(), field.pointer().classPointer());
                        var fieldType = TypeEncoding.getDescriptor(ctx.model(), field.fieldType());
                        ctx.add(new FieldInsnNode(
                                PUTFIELD,
                                owner,
                                field.pointer().name(),
                                fieldType
                        ));
                    }
                    case AssignmentSymbol.LocalVariable localVariable -> {
                        generate(assignment.right(), ctx);
                        var index = ctx.getVariableIndex(assignment.region(), localVariable.variable());
                        var instruction = TypeEncoding.getVariableStoreInstruction(localVariable.variable().type());
                        ctx.add(new VarInsnNode(instruction, index));
                    }
                    case AssignmentSymbol.StaticField staticField -> {
                        generate(assignment.right(), ctx);
                        var owner = TypeEncoding.toJVMPath(ctx.model(), staticField.pointer().classPointer());
                        var fieldType = TypeEncoding.getDescriptor(ctx.model(), staticField.fieldType());
                        ctx.add(new FieldInsnNode(
                                PUTSTATIC,
                                owner,
                                staticField.pointer().name(),
                                fieldType
                        ));
                    }
                }

            }
            case KExpr.Binary binary -> {

                generate(binary.left(), ctx);
                generate(binary.right(), ctx);
                assert binary.symbol() != null;
                switch (binary.symbol()) {
                    case BinOperatorSymbol.BoolOP boolOP -> {
                        switch (boolOP) {
                            case BinOperatorSymbol.BoolOP.And and -> {
                                ctx.add(new InsnNode(IAND));
                            }
                            case BinOperatorSymbol.BoolOP.Equal equal -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(IF_ICMPNE, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.BoolOP.NotEqual notEqual -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(IF_ICMPEQ, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.BoolOP.Or or -> {
                                ctx.add(new InsnNode(IOR));
                            }
                        }
                    }
                    case BinOperatorSymbol.DoubleOP doubleOP -> {
                        binary(expr.region(), binary.operator().value(), BinaryOperatorSet.DOUBLE, ctx);
                    }
                    case BinOperatorSymbol.FloatOP floatOP -> {
                        binary(expr.region(), binary.operator().value(), BinaryOperatorSet.FLOAT, ctx);
                    }
                    case BinOperatorSymbol.IntOP intOP -> {
                        switch (intOP) {
                            case BinOperatorSymbol.IntOP.Add add -> {
                                ctx.add(new InsnNode(IADD));
                            }
                            case BinOperatorSymbol.IntOP.Divide divide -> {
                                ctx.add(new InsnNode(IDIV));
                            }
                            case BinOperatorSymbol.IntOP.Equal equal -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(IF_ICMPNE, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.GreaterThan greaterThan -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(IF_ICMPLE, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.GreaterThanOrEqual greaterThanOrEqual -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(IF_ICMPLT, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.LessThan lessThan -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(IF_ICMPGE, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.LessThanOrEqual lessThanOrEqual -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(IF_ICMPGT, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.Modulus modulus -> {
                                ctx.add(new InsnNode(IREM));
                            }
                            case BinOperatorSymbol.IntOP.Multiply multiply -> {
                                ctx.add(new InsnNode(IMUL));
                            }
                            case BinOperatorSymbol.IntOP.NotEqual notEqual -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(IF_ICMPEQ, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.Subtract subtract -> {
                                ctx.add(new InsnNode(ISUB));
                            }
                        }
                    }
                    case BinOperatorSymbol.LongOP longOP -> {
                        binary(expr.region(), binary.operator().value(), BinaryOperatorSet.LONG, ctx);
                    }
                    case BinOperatorSymbol.ObjectEquals objectEquals -> {
                        switch (objectEquals) {
                            case BinOperatorSymbol.ObjectEquals.Equal _, BinOperatorSymbol.ObjectEquals.NotEqual _ -> {
                                var owner = "java/util/Objects";
                                var argType = Type.getType(Object.class);
                                var desc = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, argType, argType);
                                var name = "equals";

                                var opcode = INVOKESTATIC;
                                ctx.add(new MethodInsnNode(
                                        opcode,
                                        owner,
                                        name,
                                        desc,
                                        false
                                ));
                                if (objectEquals instanceof BinOperatorSymbol.ObjectEquals.NotEqual) {
                                    ctx.add(new InsnNode(ICONST_1));
                                    ctx.add(new InsnNode(IXOR));
                                }
                            }
                            case BinOperatorSymbol.ObjectEquals.StrictEqual _, BinOperatorSymbol.ObjectEquals.StrictNotEqual _ -> {
                                int opcode = IF_ACMPEQ;
                                if (objectEquals instanceof BinOperatorSymbol.ObjectEquals.StrictNotEqual) {
                                    opcode = IF_ACMPNE;
                                }

                                var trueLabel = new LabelNode();
                                var endLabel = new LabelNode();
                                ctx.add(new JumpInsnNode(opcode, trueLabel));
                                ctx.add(new InsnNode(ICONST_0));
                                ctx.add(new JumpInsnNode(GOTO, endLabel));
                                ctx.add(trueLabel);
                                ctx.add(new InsnNode(ICONST_1));
                                ctx.add(endLabel);


                            }
                        }

                    }
                }

            }
            case KExpr.Block block -> {
                var iterator = block.expressions().iterator();
                assert block.symbol() != null;
                while (iterator.hasNext()) {
                    var next = iterator.next();
                    generate(next, ctx);
                    var type = next.type();
                    if (!type.isVoid() && (iterator.hasNext() || block.symbol().isVoid())) {
                        popValues(ctx, type);
                    }
                }
            }
            case KExpr.Boolean aBoolean -> {
                ctx.add(new LdcInsnNode(aBoolean.value()));
            }
            case KExpr.Branch branch -> {
                if (branch.branchPattern() != null) {
                    Log.temp(ctx, expr.region(), "Cannot be expressed");
                    throw new Log.KarinaException();
                }

                assert branch.symbol() != null;
                var removeValues = branch.symbol().type().isVoid();

                generate(branch.condition(), ctx);
                var falseTarget = new LabelNode();
                ctx.add(new JumpInsnNode(IFEQ, falseTarget));
                generate(branch.thenArm(), ctx);

                var thenArmReturnType = branch.thenArm().type();
                if (!thenArmReturnType.isVoid() && removeValues) {
                    popValues(ctx, thenArmReturnType);
                }

                var endTarget = new LabelNode();
                if (branch.elseArm() != null) {
                    ctx.add(new JumpInsnNode(GOTO, endTarget));
                }
                ctx.add(falseTarget);

                if (branch.elseArm() != null) {
                    if (branch.elseArm().elsePattern() != null) {
                        Log.temp(ctx, expr.region(), "Cannot be expressed");
                        throw new Log.KarinaException();
                    }

                    generate(branch.elseArm().expr(), ctx);
                    var elseArmReturnType = branch.elseArm().expr().type();
                    if (!elseArmReturnType.isVoid() && removeValues) {
                        popValues(ctx, elseArmReturnType);
                    }
                    ctx.add(endTarget);
                }

            }
            case KExpr.Break aBreak -> {
                assert ctx.getBreakTarget() != null;
                ctx.add(new JumpInsnNode(GOTO, ctx.getBreakTarget()));
            }
            case KExpr.Call call -> {
                if (call.symbol() == null) {
                    Log.temp(ctx, expr.region(), "No given symbol");
                    throw new Log.KarinaException();
                }
                switch (call.symbol()) {
                    case CallSymbol.CallDynamic callDynamic -> {
                        Log.temp(ctx, expr.region(), "Cannot be expressed");
                        throw new Log.KarinaException();
                    }
                    case CallSymbol.CallStatic callStatic -> {
                        var owner = TypeEncoding.toJVMPath(ctx.model(), callStatic.pointer().classPointer());
                        var desc = TypeEncoding.getDesc(ctx.model(), callStatic.pointer());
                        var name = callStatic.pointer().name();

                        for (var argument : call.arguments()) {
                            generate(argument, ctx);
                        }

                        ctx.add(new MethodInsnNode(
                                INVOKESTATIC,
                                owner,
                                name,
                                desc,
                                callStatic.onInterface()
                        ));
                        applyCorrectReturnType(ctx, callStatic.pointer(), callStatic.returnType());
                    }
                    case CallSymbol.CallVirtual callVirtual -> {
                        generate(call.left(), ctx);
                        var owner = TypeEncoding.toJVMPath(ctx.model(), callVirtual.pointer().classPointer());
                        var desc = TypeEncoding.getDesc(ctx.model(), callVirtual.pointer());
                        var name = callVirtual.pointer().name();

                        for (var argument : call.arguments()) {
                            generate(argument, ctx);
                        }
                        if ( callVirtual.onInterface()) {
                            ctx.add(new MethodInsnNode(
                                    INVOKEINTERFACE,
                                    owner,
                                    name,
                                    desc,
                                    true
                            ));
                        } else {
                            ctx.add(new MethodInsnNode(
                                    INVOKEVIRTUAL,
                                    owner,
                                    name,
                                    desc,
                                    false
                            ));
                        }
                        applyCorrectReturnType(ctx, callVirtual.pointer(), callVirtual.returnType());
                    }
                    case CallSymbol.CallSuper callSuper -> {
                        switch (callSuper.invocationType()) {
                            case InvocationType.NewInit newInit -> {
                                var internalName = TypeEncoding.getInternalName(ctx.model(), newInit.classType());
                                var desc = TypeEncoding.getDesc(ctx.model(), callSuper.pointer());
                                ctx.add(new TypeInsnNode(NEW, internalName));

                                ctx.add(new InsnNode(DUP));

                                for (var argument : call.arguments()) {
                                    generate(argument, ctx);
                                }

                                ctx.add(new MethodInsnNode(
                                        INVOKESPECIAL,
                                        internalName,
                                        "<init>",
                                        desc,
                                        false
                                ));
                            }
                            case InvocationType.SpecialInvoke specialInvoke -> {
                                //Load 'this'
                                ctx.add(new VarInsnNode(ALOAD, 0));

                                if (!(specialInvoke.superType() instanceof KType.ClassType classType)) {
                                    Log.temp(ctx, expr.region(), "Cannot be expressed");
                                    throw new Log.KarinaException();
                                }

                                var internalName = TypeEncoding.getInternalName(ctx.model(), classType);
                                var desc = TypeEncoding.getDesc(ctx.model(), callSuper.pointer());

                                for (var argument : call.arguments()) {
                                    generate(argument, ctx);
                                }

                                ctx.add(new MethodInsnNode(
                                        INVOKESPECIAL,
                                        internalName,
                                        specialInvoke.name(),
                                        desc,
                                        false
                                ));
                                applyCorrectReturnType(ctx, callSuper.pointer(), callSuper.returnType());
                            }
                        }
                    }
                }
            }
            case KExpr.Cast cast -> {
                generate(cast.expression(), ctx);
                assert cast.symbol() != null;
                switch (cast.symbol()) {
                    case CastSymbol.PrimitiveCast primitiveCast -> {
                        var from = primitiveCast.fromNumeric();
                        var to = primitiveCast.toNumeric();
                        var code = numericCast(from, to);
                        if (code != NOP) {
                            ctx.add(new InsnNode(code));
                        }
                    }
                    case CastSymbol.UpCast upCast -> {
                        var type = TypeEncoding.getInternalName(ctx.model(), upCast.toType());
                        ctx.add(new TypeInsnNode(CHECKCAST, type));
                    }
                }
            }
            case KExpr.Closure closure -> {
                Log.temp(ctx, expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.Continue aContinue -> {
                assert ctx.getContinueTarget() != null;
                ctx.add(new JumpInsnNode(GOTO, ctx.getContinueTarget()));
            }
            case KExpr.CreateArray createArray -> {
                assert createArray.symbol() != null;
                var elementType = createArray.symbol().elementType();
                var size = createArray.elements().size();
                ctx.add(new LdcInsnNode(size));

                var storeOp = TypeEncoding.getArrayStoreInstruction(elementType);

                if (elementType.isPrimitive()) {
                    var arrayNewType = TypeEncoding.getNewPrimitiveArrayConstant(elementType);
                    ctx.add(new IntInsnNode(NEWARRAY, arrayNewType));
                } else {
                    ctx.add(new TypeInsnNode(ANEWARRAY, TypeEncoding.getInternalName(ctx.model(), elementType)));
                }

                var index = 0;
                for (var element : createArray.elements()) {
                    ctx.add(new InsnNode(DUP));
                    ctx.add(new LdcInsnNode(index));
                    generate(element, ctx);
                    ctx.add(new InsnNode(storeOp));
                    index += 1;
                }

            }
            case KExpr.CreateObject createObject -> {
                Log.temp(ctx, expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.For aFor -> {
                Log.temp(ctx, expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.GetArrayElement getArrayElement -> {
                generate(getArrayElement.left(), ctx);
                generate(getArrayElement.index(), ctx);
                assert getArrayElement.elementType() != null;
                var instruction = TypeEncoding.getArrayLoadInstruction(getArrayElement.elementType());
                ctx.add(new InsnNode(instruction));
            }
            case KExpr.GetMember getMember -> {
                assert getMember.symbol() != null;
                switch (getMember.symbol()) {
                    case MemberSymbol.ArrayLength arrayLength -> {
                        generate(getMember.left(), ctx);
                        ctx.add(new InsnNode(ARRAYLENGTH));
                    }
                    case MemberSymbol.FieldSymbol fieldSymbol -> {
                        generate(getMember.left(), ctx);
                        var owner = TypeEncoding.toJVMPath(ctx.model(), fieldSymbol.pointer().classPointer());
                        var fieldType = TypeEncoding.getDescriptor(ctx.model(), fieldSymbol.type());
                        var name = fieldSymbol.pointer().name();
                        ctx.add(new FieldInsnNode(
                                GETFIELD,
                                owner,
                                name,
                                fieldType
                        ));
                    }
                    case MemberSymbol.VirtualFunctionSymbol virtualFunctionSymbol -> {
                        Log.temp(ctx, expr.region(), "Cannot be expressed");
                        throw new Log.KarinaException();
                    }
                }
            }
            case KExpr.IsInstanceOf isInstanceOf -> {
                generate(isInstanceOf.left(), ctx);
                var type = TypeEncoding.getInternalName(ctx.model(), isInstanceOf.isType());
                ctx.add(new TypeInsnNode(INSTANCEOF, type));
            }
            case KExpr.Literal literal -> {
                assert literal.symbol() != null;
                switch (literal.symbol()) {
                    case LiteralSymbol.StaticMethodReference staticMethodReference -> {
                        Log.temp(ctx, expr.region(), "Cannot be expressed");
                        throw new Log.KarinaException();
                    }
                    case LiteralSymbol.StaticClassReference staticClassReference -> {
                        // convert to .class
                        var type = Type.getObjectType(TypeEncoding.toJVMPath(ctx.model(), staticClassReference.classPointer()));
                        ctx.add(new LdcInsnNode(type));
                    }
                    case LiteralSymbol.VariableReference variableReference -> {
                        var variable = variableReference.variable();
                        var index = ctx.getVariableIndex(literal.region(), variable);
                        Log.recordType(Log.LogTypes.GENERATION_VAR, "variable " + variable.name() + " at index " + index);
                        var instruction = TypeEncoding.getVariableLoadInstruction(variable.type());
                        ctx.add(new VarInsnNode(instruction, index));
                    }
                    case LiteralSymbol.StaticFieldReference staticFieldReference -> {
                        var owner = TypeEncoding.toJVMPath(ctx.model(), staticFieldReference.fieldPointer().classPointer());
                        var fieldType = TypeEncoding.getDescriptor(ctx.model(), staticFieldReference.fieldType());
                        ctx.add(new FieldInsnNode(
                                GETSTATIC,
                                owner,
                                staticFieldReference.fieldPointer().name(),
                                fieldType
                        ));
                    }
                    case LiteralSymbol.Null aNull -> {
                        ctx.add(new InsnNode(ACONST_NULL));
                    }
                }
            }
            case KExpr.Match match -> {
                Log.temp(ctx, expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.Number number -> {
                switch (Objects.requireNonNull(number.symbol())) {
                    case NumberSymbol.DoubleValue doubleValue -> {
                        ctx.add(new LdcInsnNode(doubleValue.value()));
                    }
                    case NumberSymbol.FloatValue floatValue -> {
                        ctx.add(new LdcInsnNode(floatValue.value()));
                    }
                    case NumberSymbol.IntegerValue integerValue -> {
                        ctx.add(new LdcInsnNode(integerValue.value()));
                    }
                    case NumberSymbol.LongValue longValue -> {
                        ctx.add(new LdcInsnNode(longValue.value()));
                    }
                }
            }
            case KExpr.Return aReturn -> {
                if (aReturn.value() != null) {
                    generate(aReturn.value(), ctx);
                    assert aReturn.returnType() != null;
                    var returnCode = TypeEncoding.getReturnInstruction(aReturn.returnType());
                    ctx.add(new InsnNode(returnCode));
                } else {
                    ctx.add(new InsnNode(RETURN));
                }
            }
            case KExpr.Self self -> {
                ctx.add(new VarInsnNode(ALOAD, 0));
            }
            case KExpr.StringExpr stringExpr -> {
                var content = StringEscapeUtils.unescapeJava(stringExpr.value());
                if (stringExpr.isChar()) {
                    var literalAsInt = (int) content.charAt(0);
                    ctx.add(new LdcInsnNode(literalAsInt));
                    ctx.add(new InsnNode(I2C));
                } else {
                    ctx.add(new LdcInsnNode(content));
                }
            }
            case KExpr.Throw aThrow -> {
                generate(aThrow.value(), ctx);
                ctx.add(new InsnNode(ATHROW));
            }
            case KExpr.Unary unary -> {
                generate(unary.value(), ctx);
                assert unary.symbol() != null;
                switch (unary.symbol()) {
                    case UnaryOperatorSymbol.NegateOP neg -> {
                        switch (neg) {
                            case UnaryOperatorSymbol.NegateOP.DoubleOP doubleOP -> {
                                ctx.add(new InsnNode(DNEG));
                            }
                            case UnaryOperatorSymbol.NegateOP.FloatOP floatOP -> {
                                ctx.add(new InsnNode(FNEG));
                            }
                            case UnaryOperatorSymbol.NegateOP.IntOP intOP -> {
                                ctx.add(new InsnNode(INEG));
                            }
                            case UnaryOperatorSymbol.NegateOP.LongOP longOP -> {
                                ctx.add(new InsnNode(LNEG));
                            }
                        }
                    }
                    case UnaryOperatorSymbol.NotOP not -> {
                        ctx.add(new InsnNode(ICONST_1));
                        ctx.add(new InsnNode(IXOR));
                    }
                }
            }
            case KExpr.VariableDefinition variableDefinition -> {
                var symbol = variableDefinition.symbol();
                assert symbol != null;
                ctx.putVariable(symbol);
                generate(variableDefinition.value(), ctx);
                var target = ctx.getVariableIndex(variableDefinition.region(), symbol);
                var type = TypeEncoding.getDescriptor(ctx.model(), symbol.type());

                LabelNode start = new LabelNode();
                ctx.add(start);
                LabelNode end = new LabelNode();



                LocalVariableNode localVariableNode = new LocalVariableNode(
                        symbol.name(),
                        type,
                        null,
                        start,
                        end,
                        target
                );
                ctx.getLocalVariables().add(localVariableNode);
                var instruction = TypeEncoding.getVariableStoreInstruction(symbol.type());
                ctx.add(new VarInsnNode(instruction, target));
                ctx.add(end);
            }
            case KExpr.While aWhile -> {

                var startOfLoop = new LabelNode();
                var endOfLoop = new LabelNode();
                ctx.add(startOfLoop);
                generate(aWhile.condition(), ctx);
                ctx.add(new JumpInsnNode(IFEQ, endOfLoop));
                ctx.setBreakTarget(endOfLoop);
                ctx.setContinueTarget(startOfLoop);

                generate(aWhile.body(), ctx);

                //pop value when there is one
                var yieldType = aWhile.body().type();
                if (!yieldType.isVoid() && !aWhile.body().doesReturn()) {
                    popValues(ctx, yieldType);
                }

                ctx.add(new JumpInsnNode(GOTO, startOfLoop));
                ctx.add(endOfLoop);

            }
            case KExpr.SpecialCall specialCall -> {
                Log.temp(ctx, expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.StaticPath staticPath -> {
                Log.temp(ctx, expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.StringInterpolation stringInterpolation -> {
                Log.temp(ctx, expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.Unwrap unwrap -> {
                Log.temp(ctx, expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
        }

    }

    private static void popValues(GenerationContext ctx, KType type) {
        var size = TypeEncoding.jvmSize(type);
        if (size == 2) {
            ctx.add(new InsnNode(POP2));
        } else {
            for (int i = 0; i < size; i++) {
                ctx.add(new InsnNode(POP));
            }
        }
    }

    private static void applyCorrectReturnType(GenerationContext ctx, MethodPointer originalPointer, KType returnType) {
        if (!originalPointer.erasedReturnType().isPrimitive() && !originalPointer.erasedReturnType().isVoid()) {
            var original = TypeEncoding.getInternalName(ctx.model(), originalPointer.erasedReturnType());
            var type = TypeEncoding.getInternalName(ctx.model(), returnType);
            if (!original.equals(type)) {
                ctx.add(new TypeInsnNode(CHECKCAST, type));
            }
        }
    }

    private static int numericCast(KType.KPrimitive from, KType.KPrimitive to) {
        if (from == to) {
            return NOP;
        }
        switch (from) {
            case INT, CHAR, BOOL, BYTE, SHORT -> {
                switch (to) {
                    case DOUBLE -> {
                        return I2D;
                    }
                    case LONG -> {
                        return I2L;
                    }
                    case FLOAT -> {
                        return I2F;
                    }
                }
            }
            case FLOAT -> {
                switch (to) {
                    case INT, CHAR, BOOL, BYTE, SHORT -> {
                        return F2I;
                    }
                    case DOUBLE -> {
                        return F2D;
                    }
                    case LONG -> {
                        return F2L;
                    }
                }
            }
            case DOUBLE -> {
                switch (to) {
                    case INT, CHAR, BOOL, BYTE, SHORT -> {
                        return D2I;
                    }
                    case LONG -> {
                        return D2L;
                    }
                    case FLOAT -> {
                        return D2F;
                    }
                }

            }
            case LONG -> {
                switch (to) {
                    case INT, CHAR, BOOL, BYTE, SHORT -> {
                        return L2I;
                    }
                    case DOUBLE -> {
                        return L2D;
                    }
                    case FLOAT -> {
                        return L2F;
                    }
                }
            }
        }
        return NOP;
    }

    private static void binary(Region region, BinaryOperator operator, BinaryOperatorSet set, GenerationContext ctx) {
        switch (operator) {
            case ADD -> {
                ctx.add(new InsnNode(set.add()));
            }
            case SUBTRACT -> {
                ctx.add(new InsnNode(set.subtract()));
            }
            case MULTIPLY -> {
                ctx.add(new InsnNode(set.multiply()));
            }
            case DIVIDE -> {
                ctx.add(new InsnNode(set.divide()));
            }
            case MODULUS -> {
                ctx.add(new InsnNode(set.modulus()));
            }
            case EQUAL -> {
                var target = new LabelNode();
                ctx.add(new InsnNode(set.compare()));
                ctx.add(new JumpInsnNode(IFNE, target));
                addEquals(ctx, target);
            }
            case NOT_EQUAL -> {
                var target = new LabelNode();
                ctx.add(new InsnNode(set.compare()));
                ctx.add(new JumpInsnNode(IFEQ, target));
                addEquals(ctx, target);
            }
            case LESS_THAN -> {
                var target = new LabelNode();
                ctx.add(new InsnNode(set.compare()));
                ctx.add(new JumpInsnNode(IFGE, target));
                addEquals(ctx, target);
            }
            case LESS_THAN_OR_EQUAL -> {
                var target = new LabelNode();
                ctx.add(new InsnNode(set.compare()));
                ctx.add(new JumpInsnNode(IFGT, target));
                addEquals(ctx, target);
            }
            case GREATER_THAN -> {
                var target = new LabelNode();
                ctx.add(new InsnNode(set.compare()));
                ctx.add(new JumpInsnNode(IFLE, target));
                addEquals(ctx, target);
            }
            case GREATER_THAN_OR_EQUAL -> {
                var target = new LabelNode();
                ctx.add(new InsnNode(set.compare()));
                ctx.add(new JumpInsnNode(IFLT, target));
                addEquals(ctx, target);
            }
            case AND, OR, CONCAT  -> {
                Log.temp(ctx, region, "Cannot be expressed");
                throw new Log.KarinaException();
            }
        }
    }

    private static void addEquals(GenerationContext ctx, LabelNode falseLabel) {
        var end = new LabelNode();
        ctx.add(new LdcInsnNode(1));
        ctx.add(new JumpInsnNode(GOTO, end));
        ctx.add(falseLabel);
        ctx.add(new LdcInsnNode(0));
        ctx.add(end);
    }

    public record BinaryOperatorSet(
            int add,
            int subtract,
            int multiply,
            int divide,
            int modulus,
            int compare
    ) {
        private static BinaryOperatorSet DOUBLE = new BinaryOperatorSet(
                DADD,
                DSUB,
                DMUL,
                DDIV,
                DREM,
                DCMPL
        );
        private static BinaryOperatorSet FLOAT = new BinaryOperatorSet(
                FADD,
                FSUB,
                FMUL,
                FDIV,
                FREM,
                FCMPL
        );

        private static BinaryOperatorSet LONG = new BinaryOperatorSet(
                LADD,
                LSUB,
                LMUL,
                LDIV,
                LREM,
                LCMP
        );
    }
}
