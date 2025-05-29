package org.karina.lang.compiler.stages.generate;

import org.apache.commons.text.StringEscapeUtils;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.BinaryOperator;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.InvocationType;
import org.karina.lang.compiler.utils.symbols.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Objects;

public class GenerateExpr {


    public static void addExpression(KExpr expr, GenerationContext ctx) {

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
                        addExpression(arrayElement.array(), ctx);
                        addExpression(arrayElement.index(), ctx);
                        addExpression(assignment.right(), ctx);
                        var type = TypeEncoding.getType(arrayElement.elementType());
                        ctx.add(new InsnNode(type.getOpcode(Opcodes.IASTORE)));
                    }
                    case AssignmentSymbol.Field field -> {
                        addExpression(field.object(), ctx);
                        addExpression(assignment.right(), ctx);
                        var owner = TypeEncoding.getType(field.fieldOwner());
                        var fieldType = TypeEncoding.getType(field.fieldType());
                        ctx.add(new FieldInsnNode(
                                Opcodes.PUTFIELD,
                                owner.getInternalName(),
                                field.pointer().name(),
                                fieldType.getDescriptor()
                        ));
                    }
                    case AssignmentSymbol.LocalVariable localVariable -> {
                        addExpression(assignment.right(), ctx);
                        var index = ctx.getVariableIndex(assignment.region(), localVariable.variable());
                        var type = TypeEncoding.getType(localVariable.variable().type());
                        ctx.add(new VarInsnNode(type.getOpcode(Opcodes.ISTORE), index));
                    }
                    case AssignmentSymbol.StaticField staticField -> {
                        addExpression(assignment.right(), ctx);
                        var owner = TypeEncoding.getType(staticField.pointer().classPointer());
                        var fieldType = TypeEncoding.getType(staticField.fieldType());
                        ctx.add(new FieldInsnNode(
                                Opcodes.PUTSTATIC,
                                owner.getInternalName(),
                                staticField.pointer().name(),
                                fieldType.getDescriptor()
                        ));
                    }
                }

            }
            case KExpr.Binary binary -> {

                addExpression(binary.left(), ctx);
                addExpression(binary.right(), ctx);
                assert binary.symbol() != null;
                switch (binary.symbol()) {
                    case BinOperatorSymbol.BoolOP boolOP -> {
                        switch (boolOP) {
                            case BinOperatorSymbol.BoolOP.And and -> {
                                ctx.add(new InsnNode(Opcodes.IAND));
                            }
                            case BinOperatorSymbol.BoolOP.Equal equal -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(Opcodes.IF_ICMPNE, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.BoolOP.NotEqual notEqual -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.BoolOP.Or or -> {
                                ctx.add(new InsnNode(Opcodes.IOR));
                            }
                        }
                    }
                    case BinOperatorSymbol.DoubleOP doubleOP -> {
                        binary(binary.operator().value(), BinaryOperatorSet.DOUBLE, ctx);
                    }
                    case BinOperatorSymbol.FloatOP floatOP -> {
                        binary(binary.operator().value(), BinaryOperatorSet.FLOAT, ctx);
                    }
                    case BinOperatorSymbol.IntOP intOP -> {
                        switch (intOP) {
                            case BinOperatorSymbol.IntOP.Add add -> {
                                ctx.add(new InsnNode(Opcodes.IADD));
                            }
                            case BinOperatorSymbol.IntOP.Divide divide -> {
                                ctx.add(new InsnNode(Opcodes.IDIV));
                            }
                            case BinOperatorSymbol.IntOP.Equal equal -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(Opcodes.IF_ICMPNE, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.GreaterThan greaterThan -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(Opcodes.IF_ICMPLE, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.GreaterThanOrEqual greaterThanOrEqual -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(Opcodes.IF_ICMPLT, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.LessThan lessThan -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(Opcodes.IF_ICMPGE, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.LessThanOrEqual lessThanOrEqual -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(Opcodes.IF_ICMPGT, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.Modulus modulus -> {
                                ctx.add(new InsnNode(Opcodes.IREM));
                            }
                            case BinOperatorSymbol.IntOP.Multiply multiply -> {
                                ctx.add(new InsnNode(Opcodes.IMUL));
                            }
                            case BinOperatorSymbol.IntOP.NotEqual notEqual -> {
                                var target = new LabelNode();
                                ctx.add(new JumpInsnNode(Opcodes.IF_ICMPEQ, target));
                                addEquals(ctx, target);
                            }
                            case BinOperatorSymbol.IntOP.Subtract subtract -> {
                                ctx.add(new InsnNode(Opcodes.ISUB));
                            }
                        }
                    }
                    case BinOperatorSymbol.LongOP longOP -> {
                        binary(binary.operator().value(), BinaryOperatorSet.LONG, ctx);
                    }
                    case BinOperatorSymbol.ObjectEquals objectEquals -> {
                        switch (objectEquals) {
                            case BinOperatorSymbol.ObjectEquals.Equal _, BinOperatorSymbol.ObjectEquals.NotEqual _ -> {
                                var owner = "java/util/Objects";
                                var argType = Type.getType(Object.class);
                                var desc = Type.getMethodDescriptor(Type.BOOLEAN_TYPE, argType, argType);
                                var name = "equals";

                                var opcode = Opcodes.INVOKESTATIC;
                                ctx.add(new MethodInsnNode(
                                        opcode,
                                        owner,
                                        name,
                                        desc,
                                        false
                                ));
                                if (objectEquals instanceof BinOperatorSymbol.ObjectEquals.NotEqual) {
                                    ctx.add(new InsnNode(Opcodes.ICONST_1));
                                    ctx.add(new InsnNode(Opcodes.IXOR));
                                }
                            }
                            case BinOperatorSymbol.ObjectEquals.StrictEqual _, BinOperatorSymbol.ObjectEquals.StrictNotEqual _ -> {
                                int opcode = Opcodes.IF_ACMPEQ;
                                if (objectEquals instanceof BinOperatorSymbol.ObjectEquals.StrictNotEqual) {
                                    opcode = Opcodes.IF_ACMPNE;
                                }

                                var trueLabel = new LabelNode();
                                var endLabel = new LabelNode();
                                ctx.add(new JumpInsnNode(opcode, trueLabel));
                                ctx.add(new InsnNode(Opcodes.ICONST_0));
                                ctx.add(new JumpInsnNode(Opcodes.GOTO, endLabel));
                                ctx.add(trueLabel);
                                ctx.add(new InsnNode(Opcodes.ICONST_1));
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
                    addExpression(next, ctx);
                    var type = next.type();
                    if (!type.isVoid() && (iterator.hasNext() || block.symbol().isVoid())) {
                        var size = TypeEncoding.jvmSize(TypeEncoding.getType(type));
                        if (size == 2) {;
                            ctx.add(new InsnNode(Opcodes.POP2));
                        } else if (size == 1) {
                            ctx.add(new InsnNode(Opcodes.POP));
                        } else {
                            for (int i = 0; i < size; i++) {
                                ctx.add(new InsnNode(Opcodes.POP));
                            }
                        }
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

                addExpression(branch.condition(), ctx);
                var falseTarget = new LabelNode();
                ctx.add(new JumpInsnNode(Opcodes.IFEQ, falseTarget));
                addExpression(branch.thenArm(), ctx);
                var endTarget = new LabelNode();
                if (branch.elseArm() != null) {
                    ctx.add(new JumpInsnNode(Opcodes.GOTO, endTarget));
                }
                ctx.add(falseTarget);

                if (branch.elseArm() != null) {
                    if (branch.elseArm().elsePattern() != null) {
                        Log.temp(ctx, expr.region(), "Cannot be expressed");
                        throw new Log.KarinaException();
                    }

                    addExpression(branch.elseArm().expr(), ctx);
                    ctx.add(endTarget);
                }

            }
            case KExpr.Break aBreak -> {
                assert ctx.getBreakTarget() != null;
                ctx.add(new JumpInsnNode(Opcodes.GOTO, ctx.getBreakTarget()));
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
                        var path = callStatic.pointer().classPointer().path().append(callStatic.pointer().name());
                        var owner = Type.getObjectType(TypeEncoding.toJVMPath(path.everythingButLast()));
                        var desc = TypeEncoding.getDesc(callStatic.pointer(), callStatic.pointer().returnType());
                        var name = callStatic.pointer().name();

                        var opcode = Opcodes.INVOKESTATIC;

                        for (var argument : call.arguments()) {
                            addExpression(argument, ctx);
                        }

                        ctx.add(new MethodInsnNode(
                                opcode,
                                owner.getInternalName(),
                                name,
                                desc,
                                callStatic.onInterface()
                        ));
                        applyCorrectReturnType(ctx, callStatic.pointer(), callStatic.returnType());
                    }
                    case CallSymbol.CallVirtual callVirtual -> {
                        addExpression(call.left(), ctx);

                        var path = callVirtual.pointer().classPointer().path().append(callVirtual.pointer().name());
                        var owner = Type.getObjectType(TypeEncoding.toJVMPath(path.everythingButLast()));
                        var desc = TypeEncoding.getDesc(callVirtual.pointer(), callVirtual.pointer().returnType());
                        var name = callVirtual.pointer().name();

//                        var isInterface = ctx.

                        for (var argument : call.arguments()) {
                            addExpression(argument, ctx);
                        }
                        if ( callVirtual.onInterface()) {
                            ctx.add(new MethodInsnNode(
                                    Opcodes.INVOKEINTERFACE,
                                    owner.getInternalName(),
                                    name,
                                    desc,
                                    true
                            ));
                        } else {
                            ctx.add(new MethodInsnNode(
                                    Opcodes.INVOKEVIRTUAL,
                                    owner.getInternalName(),
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

                                var internalName = TypeEncoding.getType(newInit.classType()).getInternalName();
                                var desc = TypeEncoding.getDesc(callSuper.pointer(), KType.NONE);
                                ctx.add(new TypeInsnNode(Opcodes.NEW, internalName));

                                ctx.add(new InsnNode(Opcodes.DUP));

                                for (var argument : call.arguments()) {
                                    addExpression(argument, ctx);
                                }

                                ctx.add(new MethodInsnNode(
                                        Opcodes.INVOKESPECIAL,
                                        internalName,
                                        "<init>",
                                        desc,
                                        false
                                ));
                            }
                            case InvocationType.SpecialInvoke specialInvoke -> {
                                //Load 'this'
                                ctx.add(new VarInsnNode(Opcodes.ALOAD, 0));
//                                addExpression(call.left(), ctx);

                                var internalName = TypeEncoding.getType(specialInvoke.superType()).getInternalName();
                                var desc = TypeEncoding.getDesc(callSuper.pointer(), callSuper.pointer().returnType());

                                for (var argument : call.arguments()) {
                                    addExpression(argument, ctx);
                                }

                                ctx.add(new MethodInsnNode(
                                        Opcodes.INVOKESPECIAL,
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
                addExpression(cast.expression(), ctx);
                assert cast.symbol() != null;
                switch (cast.symbol()) {
                    case CastSymbol.PrimitiveCast primitiveCast -> {
                        var from = primitiveCast.fromNumeric();
                        var to = primitiveCast.toNumeric();
                        var code = numericCast(from, to);
                        if (code != Opcodes.NOP) {
                            ctx.add(new InsnNode(code));
                        }
                    }
                    case CastSymbol.UpCast upCast -> {
                        var type = TypeEncoding.getType(upCast.toType());
                        ctx.add(new TypeInsnNode(Opcodes.CHECKCAST, type.getInternalName()));
                    }
                }
            }
            case KExpr.Closure closure -> {
                Log.temp(ctx, expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.Continue aContinue -> {
                assert ctx.getContinueTarget() != null;
                ctx.add(new JumpInsnNode(Opcodes.GOTO, ctx.getContinueTarget()));
            }
            case KExpr.CreateArray createArray -> {
                assert createArray.symbol() != null;
                var elementType = createArray.symbol().elementType();
                var type = TypeEncoding.getType(elementType);
                var size = createArray.elements().size();
                ctx.add(new LdcInsnNode(size));

                var storeOp = type.getOpcode(Opcodes.IASTORE);

                if (elementType.isPrimitive()) {
                    var arrayNewType = TypeEncoding.getNewArrayConstant(type);
                    ctx.add(new IntInsnNode(Opcodes.NEWARRAY, arrayNewType));
                } else {
                    ctx.add(new TypeInsnNode(Opcodes.ANEWARRAY, type.getInternalName()));
                }

                var index = 0;
                for (var element : createArray.elements()) {
                    ctx.add(new InsnNode(Opcodes.DUP));
                    ctx.add(new LdcInsnNode(index));
                    addExpression(element, ctx);
                    ctx.add(new InsnNode(storeOp));
                    index += 1;
                }

            }
            case KExpr.CreateObject createObject -> {
                Log.temp(ctx, expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
//                assert createObject.symbol() != null;
//                var type = TypeConversion.getType(createObject.symbol());
//                ctx.add(new TypeInsnNode(Opcodes.NEW, type.getInternalName()));
//
//                ctx.add(new InsnNode(Opcodes.DUP));
//                ctx.add(new MethodInsnNode(
//                        Opcodes.INVOKESPECIAL,
//                        type.getInternalName(),
//                        "<init>",
//                        "()V",
//                        false
//                ));
//
//                for (var parameter : createObject.parameters()) {
//                    ctx.add(new InsnNode(Opcodes.DUP));
//                    addExpression(parameter.expr(), ctx);
//                    assert parameter.symbol() != null;
//                    var parameterType = TypeConversion.getType(parameter.symbol());
//                    ctx.add(new FieldInsnNode(
//                            Opcodes.PUTFIELD,
//                            type.getInternalName(),
//                            TypeConversion.toJVMName(parameter.name().value()),
//                            parameterType.getDescriptor()
//                    ));
//                }

            }
            case KExpr.For aFor -> {
                Log.temp(ctx, expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.GetArrayElement getArrayElement -> {
                addExpression(getArrayElement.left(), ctx);
                addExpression(getArrayElement.index(), ctx);
                assert getArrayElement.elementType() != null;
                var type = TypeEncoding.getType(getArrayElement.elementType());
                ctx.add(new InsnNode(type.getOpcode(Opcodes.IALOAD)));
            }
            case KExpr.GetMember getMember -> {
                assert getMember.symbol() != null;
                switch (getMember.symbol()) {
                    case MemberSymbol.ArrayLength arrayLength -> {
                        addExpression(getMember.left(), ctx);
                        ctx.add(new InsnNode(Opcodes.ARRAYLENGTH));
                    }
                    case MemberSymbol.FieldSymbol fieldSymbol -> {
                        addExpression(getMember.left(), ctx);
                        var owner = TypeEncoding.getType(fieldSymbol.pointer().classPointer());
                        var fieldType = TypeEncoding.getType(fieldSymbol.type());
                        var name = fieldSymbol.pointer().name();
                        ctx.add(new FieldInsnNode(
                                Opcodes.GETFIELD,
                                owner.getInternalName(),
                                name,
                                fieldType.getDescriptor()
                        ));
                    }
                    case MemberSymbol.VirtualFunctionSymbol virtualFunctionSymbol -> {
                        throw new NullPointerException("Cannot be expressed");
                    }
                }
            }
            case KExpr.IsInstanceOf isInstanceOf -> {
                addExpression(isInstanceOf.left(), ctx);
                var type = TypeEncoding.getType(isInstanceOf.isType());
                ctx.add(new TypeInsnNode(Opcodes.INSTANCEOF, type.getInternalName()));
            }
            case KExpr.Literal literal -> {
                assert literal.symbol() != null;
                switch (literal.symbol()) {
                    case LiteralSymbol.StaticMethodReference staticMethodReference -> {
                        throw new NullPointerException("Cannot be expressed");
                    }
                    case LiteralSymbol.StaticClassReference staticClassReference -> {
                        // convert to .class
                        var type = TypeEncoding.getType(staticClassReference.classPointer());
                        ctx.add(new LdcInsnNode(type));
                    }
                    case LiteralSymbol.VariableReference variableReference -> {
                        var variable = variableReference.variable();
                        var index = ctx.getVariableIndex(literal.region(), variable);
                        Log.recordType(Log.LogTypes.GENERATION_VAR, "variable " + variable.name() + " at index " + index);
                        var type = TypeEncoding.getType(variable.type());
                        ctx.add(new VarInsnNode(type.getOpcode(Opcodes.ILOAD), index));
                    }
                    case LiteralSymbol.StaticFieldReference staticFieldReference -> {
                        var owner = TypeEncoding.getType(staticFieldReference.fieldPointer().classPointer());
                        var fieldType = TypeEncoding.getType(staticFieldReference.fieldType());
                        ctx.add(new FieldInsnNode(
                                Opcodes.GETSTATIC,
                                owner.getInternalName(),
                                staticFieldReference.fieldPointer().name(),
                                fieldType.getDescriptor()
                        ));
                    }
                    case LiteralSymbol.Null aNull -> {
                        ctx.add(new InsnNode(Opcodes.ACONST_NULL));
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
                    addExpression(aReturn.value(), ctx);
                    assert aReturn.returnType() != null;
                    var returnCode = TypeEncoding.getType(aReturn.returnType()).getOpcode(Opcodes.IRETURN);
                    ctx.add(new InsnNode(returnCode));
                } else {
                    ctx.add(new InsnNode(Opcodes.RETURN));
                }
            }
            case KExpr.Self self -> {
                ctx.add(new VarInsnNode(Opcodes.ALOAD, 0));
            }
            case KExpr.StringExpr stringExpr -> {
                var content = StringEscapeUtils.unescapeJava(stringExpr.value());
                if (stringExpr.isChar()) {
                    var literalAsInt = (int) content.charAt(0);
                    ctx.add(new LdcInsnNode(literalAsInt));
                    ctx.add(new InsnNode(Opcodes.I2C));
                } else {
                    ctx.add(new LdcInsnNode(content));
                }
            }
            case KExpr.Throw aThrow -> {
                addExpression(aThrow.value(), ctx);
                ctx.add(new InsnNode(Opcodes.ATHROW));
            }
            case KExpr.Unary unary -> {
                addExpression(unary.value(), ctx);
                assert unary.symbol() != null;
                switch (unary.symbol()) {
                    case UnaryOperatorSymbol.NegateOP neg -> {
                        switch (neg) {
                            case UnaryOperatorSymbol.NegateOP.DoubleOP doubleOP -> {
                                ctx.add(new InsnNode(Opcodes.DNEG));
                            }
                            case UnaryOperatorSymbol.NegateOP.FloatOP floatOP -> {
                                ctx.add(new InsnNode(Opcodes.FNEG));
                            }
                            case UnaryOperatorSymbol.NegateOP.IntOP intOP -> {
                                ctx.add(new InsnNode(Opcodes.INEG));
                            }
                            case UnaryOperatorSymbol.NegateOP.LongOP longOP -> {
                                ctx.add(new InsnNode(Opcodes.LNEG));
                            }
                        }
                    }
                    case UnaryOperatorSymbol.NotOP not -> {
                        ctx.add(new InsnNode(Opcodes.ICONST_1));
                        ctx.add(new InsnNode(Opcodes.IXOR));
                    }
                }
            }
            case KExpr.VariableDefinition variableDefinition -> {
                var symbol = variableDefinition.symbol();
                assert symbol != null;
                ctx.putVariable(symbol);
                addExpression(variableDefinition.value(), ctx);
                var target = ctx.getVariableIndex(variableDefinition.region(), symbol);
                var type = TypeEncoding.getType(symbol.type());

                LabelNode start = new LabelNode();
                ctx.add(start);
                LabelNode end = new LabelNode();



                LocalVariableNode localVariableNode = new LocalVariableNode(
                        symbol.name(),
                        type.getDescriptor(),
                        null,
                        start,
                        end,
                        target
                );
                ctx.getLocalVariables().add(localVariableNode);

                ctx.add(new VarInsnNode(type.getOpcode(Opcodes.ISTORE), target));
                ctx.add(end);
            }
            case KExpr.While aWhile -> {

                var startOfLoop = new LabelNode();
                var endOfLoop = new LabelNode();
                ctx.add(startOfLoop);
                addExpression(aWhile.condition(), ctx);
                ctx.add(new JumpInsnNode(Opcodes.IFEQ, endOfLoop));
                ctx.setBreakTarget(endOfLoop);
                ctx.setContinueTarget(startOfLoop);

                addExpression(aWhile.body(), ctx);

                //pop value when there is one
                var yieldType = aWhile.body().type();
                if (!yieldType.isVoid() && !aWhile.body().doesReturn()) {
                    var type = TypeEncoding.getType(yieldType);
                    var size = TypeEncoding.jvmSize(type);
                    if (size == 2) {;
                        ctx.add(new InsnNode(Opcodes.POP2));
                    } else if (size == 1) {
                        ctx.add(new InsnNode(Opcodes.POP));
                    } else {
                        for (int i = 0; i < size; i++) {
                            ctx.add(new InsnNode(Opcodes.POP));
                        }
                    }
                }

                ctx.add(new JumpInsnNode(Opcodes.GOTO, startOfLoop));
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

    private static void applyCorrectReturnType(GenerationContext ctx, MethodPointer originalPointer, KType returnType) {
        if (!originalPointer.returnType().isPrimitive() && !originalPointer.returnType().isVoid()) {
            var original = TypeEncoding.getType(originalPointer.returnType());
            var type = TypeEncoding.getType(returnType);
            if (!original.equals(type)) {
                ctx.add(new TypeInsnNode(Opcodes.CHECKCAST, type.getInternalName()));
            }
        }
    }

    private static int numericCast(KType.KPrimitive from, KType.KPrimitive to) {
        if (from == to) {
            return Opcodes.NOP;
        }
        switch (from) {
            case INT,CHAR,BOOL, BYTE, SHORT -> {
                switch (to) {
                    case DOUBLE -> {
                        return Opcodes.I2D;
                    }
                    case LONG -> {
                        return Opcodes.I2L;
                    }
                    case FLOAT -> {
                        return Opcodes.I2F;
                    }
                }
            }
            case FLOAT -> {
                switch (to) {
                    case INT, CHAR, BOOL, BYTE, SHORT -> {
                        return Opcodes.F2I;
                    }
                    case DOUBLE -> {
                        return Opcodes.F2D;
                    }
                    case LONG -> {
                        return Opcodes.F2L;
                    }
                }
            }
            case DOUBLE -> {

                switch (to) {
                    case INT, CHAR, BOOL, BYTE, SHORT -> {
                        return Opcodes.D2I;
                    }
                    case LONG -> {
                        return Opcodes.D2L;
                    }
                    case FLOAT -> {
                        return Opcodes.D2F;
                    }
                }

            }
            case LONG -> {

                switch (to) {
                    case INT, CHAR, BOOL, BYTE, SHORT -> {
                        return Opcodes.L2I;
                    }
                    case DOUBLE -> {
                        return Opcodes.L2D;
                    }
                    case FLOAT -> {
                        return Opcodes.L2F;
                    }
                }
            }
        }
        return Opcodes.NOP;
    }

    private static void binary(BinaryOperator operator, BinaryOperatorSet set, GenerationContext ctx) {
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
                ctx.add(new JumpInsnNode(Opcodes.IFNE, target));
                addEquals(ctx, target);
            }
            case NOT_EQUAL -> {
                var target = new LabelNode();
                ctx.add(new InsnNode(set.compare()));
                ctx.add(new JumpInsnNode(Opcodes.IFEQ, target));
                addEquals(ctx, target);
            }
            case LESS_THAN -> {
                var target = new LabelNode();
                ctx.add(new InsnNode(set.compare()));
                ctx.add(new JumpInsnNode(Opcodes.IFGE, target));
                addEquals(ctx, target);
            }
            case LESS_THAN_OR_EQUAL -> {
                var target = new LabelNode();
                ctx.add(new InsnNode(set.compare()));
                ctx.add(new JumpInsnNode(Opcodes.IFGT, target));
                addEquals(ctx, target);
            }
            case GREATER_THAN -> {
                var target = new LabelNode();
                ctx.add(new InsnNode(set.compare()));
                ctx.add(new JumpInsnNode(Opcodes.IFLE, target));
                addEquals(ctx, target);
            }
            case GREATER_THAN_OR_EQUAL -> {
                var target = new LabelNode();
                ctx.add(new InsnNode(set.compare()));
                ctx.add(new JumpInsnNode(Opcodes.IFLT, target));
                addEquals(ctx, target);
            }
            case AND, OR, CONCAT  -> {
                throw new NullPointerException("Cannot be expressed");
            }
        }
    }

    private static void addEquals(GenerationContext ctx, LabelNode falseLabel) {
        var end = new LabelNode();
        ctx.add(new LdcInsnNode(1));
        ctx.add(new JumpInsnNode(Opcodes.GOTO, end));
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
                Opcodes.DADD,
                Opcodes.DSUB,
                Opcodes.DMUL,
                Opcodes.DDIV,
                Opcodes.DREM,
                Opcodes.DCMPL
        );
        private static BinaryOperatorSet FLOAT = new BinaryOperatorSet(
                Opcodes.FADD,
                Opcodes.FSUB,
                Opcodes.FMUL,
                Opcodes.FDIV,
                Opcodes.FREM,
                Opcodes.FCMPL
        );

        private static BinaryOperatorSet LONG = new BinaryOperatorSet(
                Opcodes.LADD,
                Opcodes.LSUB,
                Opcodes.LMUL,
                Opcodes.LDIV,
                Opcodes.LREM,
                Opcodes.LCMP
        );
    }
}
