package org.karina.lang.compiler.stages.generate.gen;

import org.karina.lang.compiler.stages.generate.BytecodeProcessor;
import org.karina.lang.compiler.stages.generate.BytecodeContext;
import org.karina.lang.compiler.stages.generate.TypeConversion;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.BinaryOperator;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.symbols.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Objects;

public class ExpressionGen {
    BytecodeProcessor backend;

    public ExpressionGen(BytecodeProcessor backend) {
        this.backend = backend;
    }

    public void addExpression(KExpr expr, BytecodeContext ctx) {
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
                        var type = TypeConversion.getType(arrayElement.elementType());
                        ctx.add(new InsnNode(type.getOpcode(Opcodes.IASTORE)));
                    }
                    case AssignmentSymbol.Field field -> {
                        addExpression(field.object(), ctx);
                        addExpression(assignment.right(), ctx);
                        var owner = TypeConversion.getType(field.fieldOwner());
                        var fieldType = TypeConversion.getType(field.fieldType());
                        ctx.add(new FieldInsnNode(
                                Opcodes.PUTFIELD,
                                owner.getInternalName(),
                                TypeConversion.toJVMName(field.name()),
                                fieldType.getDescriptor()
                        ));
                    }
                    case AssignmentSymbol.LocalVariable localVariable -> {
                        addExpression(assignment.right(), ctx);
                        var index = ctx.getVariableIndex(localVariable.variable());
                        var type = TypeConversion.getType(localVariable.variable().type());
                        ctx.add(new VarInsnNode(type.getOpcode(Opcodes.ISTORE), index));
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
                        binary(doubleOP.operator(), BinaryOperatorSet.DOUBLE, ctx);
                    }
                    case BinOperatorSymbol.FloatOP floatOP -> {
                        binary(floatOP.operator(), BinaryOperatorSet.FLOAT, ctx);
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
                        binary(longOP.operator(), BinaryOperatorSet.LONG, ctx);
                    }
                }

            }
            case KExpr.Block block -> {
                var iterator = block.expressions().iterator();
                while (iterator.hasNext()) {
                    var next = iterator.next();
                    addExpression(next, ctx);
                    var type = next.type();
                    if (!type.isVoid() && iterator.hasNext()) {
                        var size = TypeConversion.jvmSize(TypeConversion.getType(type));
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
                    Log.temp(expr.region(), "Cannot be expressed");
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
                    if (branch.elseArm().shortPattern() != null) {
                        Log.temp(expr.region(), "Cannot be expressed");
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
                assert call.symbol() != null;
                switch (call.symbol()) {
                    case CallSymbol.CallDynamic callDynamic -> {
                        Log.temp(expr.region(), "Cannot be expressed");
                        throw new Log.KarinaException();
                    }
                    case CallSymbol.CallInterface callInterface -> {
                        addExpression(call.left(), ctx);

                        for (var argument : call.arguments()) {
                            addExpression(argument, ctx);
                        }

                        var path = callInterface.path();
                        var owner = Type.getObjectType(TypeConversion.toJVMPath(path.everythingButLast()));
                        var desc = this.backend.getMethodDescriptor(callInterface.argTypeStatic(), callInterface.returnTypeStatic());
                        ctx.add(new MethodInsnNode(
                                Opcodes.INVOKEINTERFACE,
                                owner.getInternalName(),
                                TypeConversion.toJVMName(path.last()),
                                desc,
                                true
                        ));
                    }
                    case CallSymbol.CallStatic callStatic -> {
                        var path = callStatic.path();
                        var owner = Type.getObjectType(TypeConversion.toJVMPath(path.everythingButLast()));
                        var desc = this.backend.getMethodDescriptor(callStatic.argTypeStatic(), callStatic.returnTypeStatic());
                        var name = TypeConversion.toJVMName(path.last());

                        var opcode = Opcodes.INVOKESTATIC;
                        if (name.equals("println")) {
                            ctx.add(new FieldInsnNode(
                                    Opcodes.GETSTATIC,
                                    "java/lang/System",
                                    "out",
                                    "Ljava/io/PrintStream;"
                            ));

                            name = "println";
                            desc = "(Ljava/lang/Object;)V";
                            owner = Type.getObjectType("java/io/PrintStream");
                            opcode = Opcodes.INVOKEVIRTUAL;

                        } else if (name.equals("intToString")) {
                            name = "toString";
                            desc = "(I)Ljava/lang/String;";
                            owner = Type.getObjectType("java/lang/Integer");
                        } else if (name.equals("arrayToString")) {
                            name = "toString";
                            desc = "([Ljava/lang/Object;)Ljava/lang/String;";
                            owner = Type.getObjectType("java/util/Arrays");
                        }


                        for (var argument : call.arguments()) {
                            addExpression(argument, ctx);
                        }


                        ctx.add(new MethodInsnNode(
                                opcode,
                                owner.getInternalName(), name,
                                desc,
                                callStatic.inInterface()
                        ));
                    }
                    case CallSymbol.CallVirtual callVirtual -> {
                        addExpression(call.left(), ctx);

                        for (var argument : call.arguments()) {
                            addExpression(argument, ctx);
                        }

                        var path = callVirtual.path();
                        var owner = Type.getObjectType(TypeConversion.toJVMPath(path.everythingButLast()));
                        var desc = this.backend.getMethodDescriptor(callVirtual.argTypeStatic(), callVirtual.returnTypeStatic());
                        ctx.add(new MethodInsnNode(
                                Opcodes.INVOKEVIRTUAL,
                                owner.getInternalName(),
                                TypeConversion.toJVMName(path.last()),
                                desc,
                                false
                        ));
                    }
                }
            }
            case KExpr.Cast cast -> {
                addExpression(cast.expression(), ctx);
                assert cast.symbol() != null;
                var from = cast.symbol().fromNumeric().primitive();
                var to = cast.symbol().toNumeric().primitive();
                var code = numericCast(from, to);
                if (code != Opcodes.NOP) {
                    ctx.add(new InsnNode(code));
                }
            }
            case KExpr.Closure closure -> {
                Log.temp(expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.Continue aContinue -> {
                assert ctx.getContinueTarget() != null;
                ctx.add(new JumpInsnNode(Opcodes.GOTO, ctx.getContinueTarget()));
            }
            case KExpr.CreateArray createArray -> {
                assert createArray.symbol() != null;
                var elementType = createArray.symbol().elementType();
                var type = TypeConversion.getType(elementType);
                var size = createArray.elements().size();
                ctx.add(new LdcInsnNode(size));

                var storeOp = type.getOpcode(Opcodes.IASTORE);

                if (elementType.isPrimitive()) {
                    var arrayNewType = TypeConversion.getNewArrayConstant(type);
                    ctx.add(new IntInsnNode(Opcodes.NEWARRAY, arrayNewType));
                } else {
                    ctx.add(new TypeInsnNode(Opcodes.ANEWARRAY, type.getInternalName()));
                }
                for (var element : createArray.elements()) {
                    addExpression(element, ctx);
                    ctx.add(new InsnNode(type.getOpcode(storeOp)));
                }

            }
            case KExpr.CreateObject createObject -> {
                assert createObject.symbol() != null;
                var type = TypeConversion.getType(createObject.symbol());
                ctx.add(new TypeInsnNode(Opcodes.NEW, type.getInternalName()));

                ctx.add(new InsnNode(Opcodes.DUP));
                ctx.add(new MethodInsnNode(
                        Opcodes.INVOKESPECIAL,
                        type.getInternalName(),
                        "<init>",
                        "()V",
                        false
                ));

                for (var parameter : createObject.parameters()) {
                    ctx.add(new InsnNode(Opcodes.DUP));
                    addExpression(parameter.expr(), ctx);
                    assert parameter.symbol() != null;
                    var parameterType = TypeConversion.getType(parameter.symbol());
                    ctx.add(new FieldInsnNode(
                            Opcodes.PUTFIELD,
                            type.getInternalName(),
                            TypeConversion.toJVMName(parameter.name().value()),
                            parameterType.getDescriptor()
                    ));
                }

            }
            case KExpr.For aFor -> {
                Log.temp(expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.GetArrayElement getArrayElement -> {
                addExpression(getArrayElement.left(), ctx);
                addExpression(getArrayElement.index(), ctx);
                assert getArrayElement.elementType() != null;
                var type = TypeConversion.getType(getArrayElement.elementType());
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
                        var owner = TypeConversion.getType(fieldSymbol.owner());
                        var fieldType = TypeConversion.getType(fieldSymbol.type());
                        ctx.add(new FieldInsnNode(
                                Opcodes.GETFIELD,
                                owner.getInternalName(),
                                TypeConversion.toJVMName(fieldSymbol.name()),
                                fieldType.getDescriptor()
                        ));
                    }
                    case MemberSymbol.InterfaceFunctionSymbol interfaceFunctionSymbol -> {
                        throw new NullPointerException("Cannot be expressed");
                    }
                    case MemberSymbol.VirtualFunctionSymbol virtualFunctionSymbol -> {
                        throw new NullPointerException("Cannot be expressed");
                    }
                }
            }
            case KExpr.IsInstanceOf isInstanceOf -> {
                Log.temp(expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.Literal literal -> {
                assert literal.symbol() != null;
                switch (literal.symbol()) {
                    case LiteralSymbol.InterfaceReference interfaceReference -> {
                        throw new NullPointerException("Cannot be expressed");
                    }
                    case LiteralSymbol.StaticFunction staticFunction -> {
                        throw new NullPointerException("Cannot be expressed");
                    }
                    case LiteralSymbol.StructReference structReference -> {
                        throw new NullPointerException("Cannot be expressed");
                    }
                    case LiteralSymbol.VariableReference variableReference -> {
                        var variable = variableReference.variable();
                        var index = ctx.getVariableIndex(variable);
                        var type = TypeConversion.getType(variable.type());
                        ctx.add(new VarInsnNode(type.getOpcode(Opcodes.ILOAD), index));
                    }
                }
            }
            case KExpr.Match match -> {
                Log.temp(expr.region(), "Cannot be expressed");
                throw new Log.KarinaException();
            }
            case KExpr.Number number -> {
                Object value;
                switch (Objects.requireNonNull(number.symbol())) {
                    case NumberSymbol.DoubleValue doubleValue -> {
                        value = doubleValue.value();
                    }
                    case NumberSymbol.FloatValue floatValue -> {
                        value = floatValue.value();
                    }
                    case NumberSymbol.IntegerValue integerValue -> {
                        value = integerValue.value();
                    }
                    case NumberSymbol.LongValue longValue -> {
                        value = longValue.value();
                    }
                }
                ctx.add(new LdcInsnNode(value));
            }
            case KExpr.Return aReturn -> {
                if (aReturn.value() != null) {
                    addExpression(aReturn.value(), ctx);
                    assert aReturn.yieldType() != null;
                    var returnCode =
                            TypeConversion.getType(aReturn.yieldType()).getOpcode(Opcodes.IRETURN);
                    ctx.add(new InsnNode(returnCode));
                } else {
                    ctx.add(new InsnNode(Opcodes.RETURN));
                }
            }
            case KExpr.Self self -> {
                ctx.add(new VarInsnNode(Opcodes.ALOAD, 0));
            }
            case KExpr.StringExpr stringExpr -> {
                ctx.add(new LdcInsnNode(stringExpr.value()));
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
                var target = ctx.getVariableIndex(symbol);
                var type = TypeConversion.getType(symbol.type());
                ctx.add(new VarInsnNode(type.getOpcode(Opcodes.ISTORE), target));
            }
            case KExpr.While aWhile -> {

                var startOfLoop = new LabelNode();
                var endOfLoop = new LabelNode();
                ctx.add(startOfLoop);
                addExpression(aWhile.condition(), ctx);
                ctx.add(new JumpInsnNode(Opcodes.IFEQ, endOfLoop));
                ctx.setBreakTarget(endOfLoop);
                ctx.setContinueTarget(startOfLoop);
                //TODO pop value when there is one
                addExpression(aWhile.body(), ctx);
                ctx.add(new JumpInsnNode(Opcodes.GOTO, startOfLoop));
                ctx.add(endOfLoop);

            }
            case KExpr.Super aSuper -> {
                throw new NullPointerException("Cannot be expressed");
            }
        }

    }

    private static int numericCast(KType.KPrimitive from, KType.KPrimitive to) {
        if (from == to) {
            return Opcodes.NOP;
        }
        if (from == KType.KPrimitive.VOID || to == KType.KPrimitive.VOID) {
            throw new NullPointerException("Cannot cast to or from void");
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

    private static void binary(BinaryOperator operator, BinaryOperatorSet set, BytecodeContext ctx) {
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

    private static void addEquals(BytecodeContext ctx, LabelNode falseLabel) {
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
