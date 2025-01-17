package org.karina.lang.compiler.backend.jvm.gen;

import org.karina.lang.compiler.backend.jvm.BytecodeBackend;
import org.karina.lang.compiler.backend.jvm.JarOutput;
import org.karina.lang.compiler.backend.jvm.TypeConversion;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.SpanOf;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

public class InterfaceGen {
    BytecodeBackend backend;

    public InterfaceGen(BytecodeBackend backend) {
        this.backend = backend;
    }

    public JarOutput addInterface(KTree.KInterface kInterface) {
        var classNode = new ClassNode();
        var selfType =
                new KType.ClassType(
                        kInterface.region(), SpanOf.span(kInterface.region(), kInterface.path()),
                        List.of()
                );
        for (var items : kInterface.functions()) {
            if (items instanceof KTree.KFunction function) {
                var method = this.backend.methodGen.createMethod(selfType, true, function);
                classNode.methods.add(method);
            }
        }
        classNode.sourceFile = kInterface.region().source().resource().identifier();
        classNode.access = Opcodes.ACC_PUBLIC  | Opcodes.ACC_INTERFACE | Opcodes.ACC_ABSTRACT;
        classNode.name = TypeConversion.toJVMPath(kInterface.path());
        classNode.version = BytecodeBackend.CLASS_VERSION;
        classNode.superName = "java/lang/Object";
        return BytecodeBackend.getJarOutput(classNode,classNode.name + ".class");
    }

}
