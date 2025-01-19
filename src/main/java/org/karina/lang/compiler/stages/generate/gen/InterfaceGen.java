package org.karina.lang.compiler.stages.generate.gen;

import org.karina.lang.compiler.stages.generate.BytecodeProcessor;
import org.karina.lang.compiler.stages.generate.JarCompilation;
import org.karina.lang.compiler.stages.generate.TypeConversion;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.SpanOf;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

public class InterfaceGen {
    BytecodeProcessor backend;

    public InterfaceGen(BytecodeProcessor backend) {
        this.backend = backend;
    }

    public JarCompilation.JarOutput addInterface(KTree.KInterface kInterface) {
        var classNode = new ClassNode();
        var selfType =
                new KType.ClassType(kInterface.path(), List.of());
        for (var items : kInterface.functions()) {
            if (items instanceof KTree.KFunction function) {
                var method = this.backend.methodGen.createMethod(selfType, true, function);
                classNode.methods.add(method);
            }
        }
        classNode.sourceFile = kInterface.region().source().resource().identifier();
        classNode.access = Opcodes.ACC_PUBLIC  | Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT;
        classNode.name = TypeConversion.toJVMPath(kInterface.path());
        classNode.version = BytecodeProcessor.CLASS_VERSION;
        classNode.superName = "java/lang/Object";
        return BytecodeProcessor.getJarOutput(classNode,classNode.name + ".class");
    }

}
