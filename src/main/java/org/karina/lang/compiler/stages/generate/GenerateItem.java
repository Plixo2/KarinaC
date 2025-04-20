package org.karina.lang.compiler.stages.generate;

import org.karina.lang.compiler.api.CustomClassWriter;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.Region;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.ArrayList;

public class GenerateItem {


    public static JarCompilation.JarOutput compileClass(Model model, KClassModel classModel, int classVersion) {

        var classNode = new ClassNode();

        for (var method : classModel.methods()) {
            var name = "Method " + method.name();
            Log.beginType(Log.LogTypes.GENERATION, name);
            if (method instanceof KMethodModel kMethodModel) {
                classNode.methods.add(compileMethod(kMethodModel));
            }
            Log.endType(Log.LogTypes.GENERATION, name);
        }
        for (var field : classModel.fields()) {
            var name = "Field " + field.name();
            Log.beginType(Log.LogTypes.GENERATION, name);
            if (field instanceof KFieldModel kFieldModel) {
                classNode.fields.add(compileField(kFieldModel));
            }
            Log.endType(Log.LogTypes.GENERATION, "Field " + field.name());
        }

        classNode.signature = GenerateSignature.getClassSignature(classModel);


        classNode.permittedSubclasses = classModel.permittedSubclasses().stream().map(ref -> {
            return TypeConversion.toJVMPath(ref.path());
        }).toList();

        classNode.nestMembers = classModel.nestMembers().stream().map(ref -> {
            return TypeConversion.toJVMPath(ref.path());
        }).toList();


        var outerClass = classModel.outerClass();
        if (outerClass != null) {
            classNode.outerClass = TypeConversion.toJVMPath(outerClass.pointer().path());
        } else {
            classNode.outerClass = null;
        }
        classNode.interfaces = classModel.interfaces().stream().map(ref ->
                TypeConversion.toJVMPath(ref.pointer().path())
        ).toList();
        classNode.sourceFile = classModel.resource().resource().identifier();
        classNode.access = classModel.modifiers();
        classNode.name = TypeConversion.toJVMPath(classModel.path());
        classNode.version = classVersion;
        var superClass = classModel.superClass();
        assert superClass != null;
        classNode.superName = TypeConversion.toJVMPath(superClass.pointer().path());
        return getJarOutput(model, classModel.region(), classNode);
    }

    private static FieldNode compileField(KFieldModel fieldModel) {
        var descriptor = TypeConversion.getType(fieldModel.type()).getDescriptor();
        Log.recordType(Log.LogTypes.GENERATION,"Field: ", fieldModel.name(), descriptor, fieldModel.type());
        var signature = GenerateSignature.fieldSignature(fieldModel.type());

        return new FieldNode(
                fieldModel.modifiers(),
                fieldModel.name(),
                descriptor,
                signature,
                null
        );
    }

    private static MethodNode compileMethod(KMethodModel methodModel) {
        var methodNode = new MethodNode();

        methodNode.access = methodModel.modifiers();
        methodNode.name = methodModel.name();

        methodNode.parameters = new ArrayList<>();
        for (var parameter : methodModel.parameters()) {
            methodNode.parameters.add(new ParameterNode(parameter, 0));
        }
        methodNode.desc = TypeConversion.getDesc(methodModel.signature());

        methodNode.signature = GenerateSignature.methodSignature(methodModel);


        var instructions = new InsnList();
        methodNode.localVariables = new ArrayList<>();
        var context = new GenerationContext(-1, instructions, methodNode.localVariables, 0, null, null);

        for (var paramVariable : methodModel.getParamVariables()) {
            context.putVariable(paramVariable);
        }


        var expression = methodModel.expression();
        if (expression != null) {
            GenerateExpr.addExpression(expression, context);
        }

//        context.getVariables().

        methodNode.instructions = instructions;

        return methodNode;
    }

    public static JarCompilation.JarOutput getJarOutput(Model model, Region region, ClassNode classNode) {
        var cw = new CustomClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS, model, region);
        try {
            classNode.accept(cw);
        } catch(Exception e) {
            Log.internal(e);
            Log.temp(region, "Error while generating class " + classNode.name);

            PrintWriter pw = new PrintWriter(System.out);

            TraceClassVisitor tracer = new TraceClassVisitor(pw);
            classNode.accept(tracer);

            throw new Log.KarinaException();
        }
        var b = cw.toByteArray();
        return new JarCompilation.JarOutput(classNode.name + ".class", b);
    }

}
