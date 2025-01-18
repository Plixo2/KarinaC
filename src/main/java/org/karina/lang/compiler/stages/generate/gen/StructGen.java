package org.karina.lang.compiler.stages.generate.gen;

import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.stages.generate.BytecodeProcessor;
import org.karina.lang.compiler.stages.generate.JarCompilation;
import org.karina.lang.compiler.stages.generate.TypeConversion;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.utils.SpanOf;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;

public class StructGen {
    BytecodeProcessor backend;

    public StructGen(BytecodeProcessor backend) {
        this.backend = backend;
    }



    public JarCompilation.JarOutput addStruct(KTree.KStruct struct) {
        var classNode = new ClassNode();
        var selfType =
                new KType.ClassType(
                        struct.region(), SpanOf.span(struct.region(), struct.path()),
                        List.of()
                );
        for (var items : struct.functions()) {
            if (items instanceof KTree.KFunction function) {
                var method = this.backend.methodGen.createMethod(selfType, false, function);
                classNode.methods.add(method);
            }
        }
        for (var field : struct.fields()) {
            var type = TypeConversion.getType(field.type());
            var fieldNode = new FieldNode(
                    Opcodes.ACC_PUBLIC,
                    field.name().value(),
                    type.getDescriptor(),
                    null,
                    null
            );
            classNode.fields.add(fieldNode);
        }


        classNode.sourceFile = struct.region().source().resource().identifier();
        classNode.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
        classNode.name = TypeConversion.toJVMPath(struct.path());
        classNode.version = BytecodeProcessor.CLASS_VERSION;
        classNode.superName = "java/lang/Object";

        for (var implBlock : struct.implBlocks()) {
            var type = TypeConversion.getType(implBlock.type());
            classNode.interfaces.add(type.getInternalName());
            for (var function : implBlock.functions()) {
                var method = this.backend.methodGen.createMethod(implBlock.type(), false, function);
                classNode.methods.add(method);
            }
        }

        if (isAnnotation(struct.annotations(), "Throwable")) {
            classNode.superName = "java/lang/Throwable";
        }
        var constructor = createConstructor(selfType, classNode.superName);
        classNode.methods.add(constructor);

        return BytecodeProcessor.getJarOutput(classNode,classNode.name + ".class");
    }

    private static boolean isAnnotation(List<KTree.KAnnotation> annotations, String name) {
        for (var annotation : annotations) {
            if (annotation.name().value().equals(name)) {
                if (annotation.value() instanceof JsonPrimitive primitive) {
                    return primitive.getAsBoolean();
                }
                return true;
            }
        }
        return false;
    }


    private MethodNode createConstructor(@Nullable KType selfType, String superType) {

        var method = new MethodNode();
        method.access = Opcodes.ACC_PUBLIC;
        method.name = "<init>";
        method.desc = "()V";
        method.signature = null;
        method.exceptions = new ArrayList<>();

        method.tryCatchBlocks = new ArrayList<>();
        method.localVariables = new ArrayList<>();

        var instructions = new InsnList();
        method.instructions = instructions;
        var start = new LabelNode();
        var end = new LabelNode();
        instructions.add(start);

        instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
        instructions.add(new MethodInsnNode(
                Opcodes.INVOKESPECIAL,
                superType,
                "<init>",
                "()V",
                false
        ));
        instructions.add(new InsnNode(Opcodes.RETURN));

        instructions.add(end);


        method.localVariables = new ArrayList<>();
        if (selfType != null) {
            var type = TypeConversion.getType(selfType);
            var node =
                    new LocalVariableNode("this", type.getDescriptor(), null, start, end, 0);
            method.localVariables.add(node);
        }
        return method;

    }


}
