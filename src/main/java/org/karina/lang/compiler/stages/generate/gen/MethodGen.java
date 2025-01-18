package org.karina.lang.compiler.stages.generate.gen;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.stages.generate.BytecodeProcessor;
import org.karina.lang.compiler.stages.generate.BytecodeContext;
import org.karina.lang.compiler.stages.generate.TypeConversion;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.Variable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MethodGen {
    BytecodeProcessor backend;

    public MethodGen(BytecodeProcessor backend) {
        this.backend = backend;
    }


    public MethodNode createMethod(@Nullable KType selfType, boolean isInterface, KTree.KFunction function) {
        var method = new MethodNode();
        method.access = Opcodes.ACC_PUBLIC;
        KType self;
        if (function.self() == null) {
            self = null;
            method.access = method.access | Opcodes.ACC_STATIC;
        } else {
            self = selfType;
            if (isInterface) {
                method.access = method.access | Opcodes.ACC_ABSTRACT;
            }
        }
        method.name = TypeConversion.toJVMName(function.name().value());
        method.desc = this.backend.getMethodDescriptor(function);
        method.signature = null;
        method.exceptions = new ArrayList<>();

        method.tryCatchBlocks = new ArrayList<>();
        method.localVariables = new ArrayList<>();

        int startIndex = 0;
        if (function.self() != null) {
            startIndex = 1;
        }

        var ctx = new BytecodeContext(0,new InsnList(), method, startIndex, null, null);

        LabelNode start;
        ctx.add(start = new LabelNode());

        if (function.expr() != null) {

            for (var parameter : function.parameters()) {
                ctx.putVariable(parameter.symbol());
            }

            this.backend.expressionGen.addExpression(function.expr(), ctx);

            var returnedType = function.returnType();
            if (returnedType == null) {
                returnedType = new KType.PrimitiveType.VoidType(function.region());
            }
            if (AttributionExpr.doesReturn(function.expr())) {
                // ok
            } else {
                if (returnedType.isVoid()) {

                    var yieldType = function.expr().type();
                    if (!yieldType.isVoid()) {
                        var type = TypeConversion.getType(yieldType);
                        var size = TypeConversion.jvmSize(type);
                        if (size == 1) {
                            ctx.add(new InsnNode(Opcodes.POP));
                        } else if (size == 2) {
                            ctx.add(new InsnNode(Opcodes.POP2));
                        } else {
                            for (int i = 0; i < size; i++) {
                                ctx.add(new InsnNode(Opcodes.POP));
                            }
                        }
                    }
                    ctx.add(new InsnNode(Opcodes.RETURN));
                } else {
                    var type = TypeConversion.getType(returnedType);
                    ctx.add(new InsnNode(type.getOpcode(Opcodes.IRETURN)));
                }
            }


        }

        LabelNode end;
        ctx.add(end = new LabelNode());

        method.instructions = ctx.getInstructions();
        method.localVariables = getLocalVariableNodes(ctx, start, end);

        if (function.self() != null) {
            assert selfType != null;
            var type = TypeConversion.getType(selfType);
            var node =
                    new LocalVariableNode("this", type.getDescriptor(), null, start, end, 0);
            method.localVariables.add(node);
        }

        return method;
    }

    public List<LocalVariableNode> getLocalVariableNodes(BytecodeContext context, LabelNode start, LabelNode end) {
        var variables = new ArrayList<LocalVariableNode>();
        for (Map.Entry<Variable, Integer> variable : context.getVariables()) {
            var index = variable.getValue();
            var variableKey = variable.getKey();
            var name = variableKey.name();
            var descriptor = TypeConversion.getType(variableKey.type()).getDescriptor();
            var node = new LocalVariableNode( TypeConversion.toJVMName(name), descriptor, null, start, end, index);
            variables.add(node);
        }
        return variables;
    }

}
