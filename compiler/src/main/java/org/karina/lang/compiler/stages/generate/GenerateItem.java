package org.karina.lang.compiler.stages.generate;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.GenerateError;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KFieldModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.Region;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GenerateItem {


    public static JarCompilation.JarOutput compileClass(Context c, Model model, KClassModel classModel, int classVersion) {

        var classNode = new ClassNode();

        for (var method : classModel.methods()) {
            var name = "Method " + method.name();
            Log.beginType(Log.LogTypes.GENERATION, name);
            if (method instanceof KMethodModel kMethodModel) {
                classNode.methods.add(compileMethod(c, model, kMethodModel));
            }
            Log.endType(Log.LogTypes.GENERATION, name);
        }
        for (var field : classModel.fields()) {
            var name = "Field " + field.name();
            Log.beginType(Log.LogTypes.GENERATION, name);
            if (field instanceof KFieldModel kFieldModel) {
                classNode.fields.add(compileField(model, kFieldModel));
            }
            Log.endType(Log.LogTypes.GENERATION, "Field " + field.name());
        }

        classNode.signature = GenerateSignature.getClassSignature(classModel);


        classNode.permittedSubclasses = classModel.permittedSubclasses()
                              .stream()
                              .map(ref -> TypeEncoding.toJVMPath(model, ref))
                              .toList();

        classNode.nestMembers = classModel.nestMembers().stream().map(ref -> {
            return TypeEncoding.toJVMPath(model, ref);
        }).toList();


        var outerClass = classModel.outerClass();
        if (outerClass != null) {
            classNode.outerClass = TypeEncoding.toJVMPath(model, outerClass.pointer());
        } else {
            classNode.outerClass = null;
        }
        var nestHost = classModel.nestHost();
        if (nestHost != null) {
            classNode.nestHostClass = TypeEncoding.toJVMPath(model, nestHost);
        } else {
            classNode.nestHostClass = null;
        }

        classNode.interfaces = classModel.interfaces().stream().map(ref ->
                TypeEncoding.toJVMPath(model, ref.pointer())
        ).toList();
        classNode.sourceFile = classModel.resource().resource().identifier();
        classNode.access = classModel.modifiers();
        classNode.name = TypeEncoding.toJVMPath(model, classModel.pointer());
        classNode.version = classVersion;
        var superClass = classModel.superClass();
        assert superClass != null;
        classNode.superName = TypeEncoding.toJVMPath(model, superClass.pointer());
        return getJarOutput(c, model, classModel.region(), classNode);
    }

    private static FieldNode compileField(Model model, KFieldModel fieldModel) {
        var descriptor = TypeEncoding.getDescriptor(model, fieldModel.type());
        var signature = GenerateSignature.fieldSignature(fieldModel.type());

        return new FieldNode(
                fieldModel.modifiers(),
                fieldModel.name(),
                descriptor,
                signature,
                fieldModel.defaultValue()
        );
    }

    private static MethodNode compileMethod(Context c, Model model, KMethodModel methodModel) {
        var methodNode = new MethodNode();

        methodNode.access = methodModel.modifiers();
        methodNode.name = methodModel.name();

        methodNode.parameters = new ArrayList<>();
        for (var parameter : methodModel.parameters()) {
            methodNode.parameters.add(new ParameterNode(parameter, 0));
        }
        methodNode.desc = TypeEncoding.getDesc(model, methodModel.signature());

        methodNode.signature = GenerateSignature.methodSignature(methodModel);


        var instructions = new InsnList();
        methodNode.localVariables = new ArrayList<>();
        var context = new GenerationContext(
                -1,
                instructions,
                methodNode.localVariables,
                c,
                model,
                0,
                null,
                null
        );

        for (var paramVariable : methodModel.getParamVariables()) {
            context.putVariable(paramVariable);
        }


        var expression = methodModel.expression();
        if (expression != null) {
            GenerateExpr.generate(expression, context);
        }


        methodNode.instructions = instructions;

        return methodNode;
    }

    public static JarCompilation.JarOutput getJarOutput(Context c, Model model, Region region, ClassNode classNode) {
        var cw = new CustomClassWriter(c, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS, model, region);
        try {
            classNode.accept(cw);
        } catch(Exception e) {
            Log.internal(c, e);

            try (var boas = new ByteArrayOutputStream()) {
                PrintWriter pw = new PrintWriter(boas);
                TraceClassVisitor tracer = new TraceClassVisitor(pw);
                classNode.accept(tracer);
                Log.generate(c, new GenerateError.NotValidAnymore(region, classNode.name, boas.toString()));
            } catch (IOException ex) {
                Log.temp(c, region, "Error while generating error report " + ex);
            }

            throw new Log.KarinaException();
        }
        var b = cw.toByteArray();
        return new JarCompilation.JarOutput(classNode.name + ".class", b);
    }

}
