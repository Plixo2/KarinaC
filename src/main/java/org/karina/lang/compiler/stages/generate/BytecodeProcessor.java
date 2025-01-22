package org.karina.lang.compiler.stages.generate;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.stages.generate.gen.ExpressionGen;
import org.karina.lang.compiler.stages.generate.gen.InterfaceGen;
import org.karina.lang.compiler.stages.generate.gen.MethodGen;
import org.karina.lang.compiler.stages.generate.gen.StructGen;
import org.karina.lang.compiler.stages.postprocess.PostProcessor;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.*;


public class BytecodeProcessor {
    //Java 17
    public static final int CLASS_VERSION = 61;
    private List<JarCompilation.JarOutput> files = new ArrayList<>();


    public final ExpressionGen expressionGen;
    public final MethodGen methodGen;
    public final StructGen structGen;
    public final InterfaceGen interfaceGen;
    public BytecodeProcessor() {
        this.expressionGen = new ExpressionGen(this);
        this.methodGen = new MethodGen(this);
        this.structGen = new StructGen(this);
        this.interfaceGen = new InterfaceGen(this);
    }


    public JarCompilation accept(KTree.KPackage kPackage, String mainClass) {

        var manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass);

        var units = kPackage.getAllUnitsRecursively();
        for (var unit : units) {
            addUnit(unit);

            for (var item : unit.items()) {
                if (item instanceof KTree.KStruct struct) {
                    var output = this.structGen.addStruct(struct);
                    this.files.add(output);
                } else if (item instanceof KTree.KInterface kInterface) {
                    var output = this.interfaceGen.addInterface(kInterface);
                    this.files.add(output);
                }
            }
        }

        return new JarCompilation(
                this.files,
                manifest
        );
    }

    private void addUnit(KTree.KUnit unit) {
        var classNode = new ClassNode();
        for (var items : unit.items()) {
            if (items instanceof KTree.KFunction function) {
                var method = this.methodGen.createMethod(null, false, function);
                classNode.methods.add(method);
            }
        }
        classNode.sourceFile = unit.region().source().resource().identifier();
        classNode.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
        classNode.name = TypeConversion.toJVMPath(unit.path());
        classNode.version = CLASS_VERSION;
        classNode.superName = "java/lang/Object";
        var out = getJarOutput(classNode,classNode.name + ".class");
        this.files.add(out);
    }



    public String getMethodDescriptor(KTree.KFunction function) {
        Type returnType;
        if (function.returnType() != null) {
            returnType = TypeConversion.getType(function.returnType());
        } else {
            returnType = Type.VOID_TYPE;
        }

        var parameters = function.parameters();
        Type[] array = new Type[parameters.size()];
        for (int i = 0; i < parameters.size(); i++) {
            array[i] = TypeConversion.getType(parameters.get(i).type());
        }


        return Type.getMethodDescriptor(returnType, array);
    }

    public String getMethodDescriptor(List<KType> args, KType returnType) {
        Type returnTpe = TypeConversion.getType(returnType);

        Type[] array = new Type[args.size()];
        for (int i = 0; i < args.size(); i++) {
            array[i] = TypeConversion.getType(args.get(i));
        }

        return Type.getMethodDescriptor(returnTpe, array);
    }




    public static JarCompilation.JarOutput getJarOutput(ClassNode classNode, String name) {
        var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(cw);
        var b = cw.toByteArray();
        return new JarCompilation.JarOutput(name, b);
    }







}
