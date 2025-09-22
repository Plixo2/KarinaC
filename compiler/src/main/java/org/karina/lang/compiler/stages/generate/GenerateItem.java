package org.karina.lang.compiler.stages.generate;

import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.Logging;
import org.karina.lang.compiler.utils.logging.errors.GenerateError;
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
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class GenerateItem {


    public static JarCompilation.JarOutput compileClass(Context c, Model model, KClassModel classModel, int classVersion) {

        var classNode = new CustomClassWriter.TracingClassNode(c);
        classNode.name = TypeEncoding.toJVMPath(model, classModel.pointer());

        try(var _ = c.section(Logging.Generation.Classes.class, classNode.name)) {
            for (var method : classModel.methods()) {
                if (method instanceof KMethodModel kMethodModel) {
                    var node = compileMethod(c, model, kMethodModel);
                    classNode.methodMap.put(kMethodModel, node);
                }
            }
            for (var field : classModel.fields()) {
                if (field instanceof KFieldModel kFieldModel) {
                    classNode.fields.add(compileField(model, kFieldModel));
                }
            }

            classNode.signature = GenerateSignature.getClassSignature(model, classModel);

            if (ClassModel.isSealed(classModel.modifiers())) {
                classNode.permittedSubclasses = classModel.permittedSubclasses()
                                  .stream()
                                  .map(ref -> TypeEncoding.toJVMPath(model, ref))
                                  .toList();
            } else {
                classNode.permittedSubclasses = null;
            }

            classNode.nestMembers = classModel.nestMembers().stream().map(ref -> TypeEncoding.toJVMPath(model, ref)).toList();


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
            classNode.access = classModel.modifiers() & 0xFFFF; // remove non-standard modifiers
            classNode.version = classVersion;
            var superClass = classModel.superClass();
            assert superClass != null;
            classNode.superName = TypeEncoding.toJVMPath(model, superClass.pointer());

            classNode.innerClasses = new ArrayList<>();



            for (var innerClass : classModel.innerClasses()) {
                var name = TypeEncoding.toJVMPath(model, innerClass.pointer());
                var innerClassNode = new InnerClassNode(
                        name,
                        classNode.name,
                        innerClass.name(),
                        innerClass.modifiers() & 0xFFFF
                );
                classNode.innerClasses.add(innerClassNode);

                if (c.log(Logging.Generation.InnerClassesNodes.class)) {
                    var bob = new StringBuilder();
                    bob.append("{");
                    bob.append("name: ").append(innerClassNode.name).append(", ");
                    bob.append("outerName: ").append(innerClassNode.outerName).append(", ");
                    bob.append("innerName: ").append(innerClassNode.innerName).append(", ");
                    bob.append("access: ").append(Modifier.toString(innerClassNode.access));
                    bob.append("}");

                    c.tag(bob.toString());
                }
            }
            if (outerClass != null) {
                var outerName = TypeEncoding.toJVMPath(model, outerClass.pointer());
                var innerClassNode = new InnerClassNode(
                        classNode.name,
                        outerName,
                        classModel.name(),
                        classNode.access
                );
                classNode.innerClasses.add(innerClassNode);

                if (c.log(Logging.Generation.InnerClassesNodes.class)) {
                    var bob = new StringBuilder();
                    bob.append("{");
                    bob.append("name: ").append(innerClassNode.name).append(", ");
                    bob.append("outerName: ").append(innerClassNode.outerName).append(", ");
                    bob.append("innerName: ").append(innerClassNode.innerName).append(", ");
                    bob.append("access: ").append(Modifier.toString(innerClassNode.access));
                    bob.append("}");

                    c.tag(bob.toString());
                }
            }
            return getJarOutput(c, model, classModel.region(), classNode);
        }
    }

    private static FieldNode compileField(Model model, KFieldModel fieldModel) {
        var descriptor = TypeEncoding.getDescriptor(model, fieldModel.type());
        var signature = GenerateSignature.fieldSignature(model, fieldModel.type());

        return new FieldNode(
                fieldModel.modifiers() & 0xFFFF, // remove non-standard modifiers
                fieldModel.name(),
                descriptor,
                signature,
                fieldModel.defaultValue()
        );
    }

    private static MethodNode compileMethod(Context c, Model model, KMethodModel methodModel) {
        var methodNode = new MethodNode();

        methodNode.access = methodModel.modifiers() & 0xFFFF; // remove non-standard modifiers
        methodNode.name = methodModel.name();

        methodNode.parameters = new ArrayList<>();
        for (var parameter : methodModel.parameters()) {
            methodNode.parameters.add(new ParameterNode(parameter, 0));
        }
        methodNode.desc = TypeEncoding.getDesc(model, methodModel.signature());

        methodNode.signature = GenerateSignature.methodSignature(model, methodModel);

        var expression = methodModel.expression();

        var instructions = new InsnList();
        methodNode.localVariables = new ArrayList<>();
        methodNode.tryCatchBlocks = new ArrayList<>();

        var context = new GenerationContext(
                -1,
                instructions,
                methodNode.localVariables,
                methodNode.tryCatchBlocks,
                c,
                model,
                0,
                null,
                null
        );


        var methodStart = new LabelNode();
        var methodEnd = new LabelNode();

        for (var paramVariable : methodModel.getParamVariables()) {
            context.putVariable(paramVariable);

            if (expression != null) {
                var index = context.getVariableIndex(methodModel.region(), paramVariable);
                var localVariableNode = new LocalVariableNode(
                        paramVariable.name(),
                        TypeEncoding.getDescriptor(model, paramVariable.type()),
                        GenerateSignature.fieldSignature(model, paramVariable.type()),
                        methodStart,
                        methodEnd,
                        index
                );
                context.getLocalVariables().add(localVariableNode);
            }
        }


        if (expression != null) {
            context.add(methodStart);
            GenerateExpr.generate(expression, context);
            context.add(methodEnd);
        }

        methodNode.instructions = instructions;

        return methodNode;
    }

    public static JarCompilation.JarOutput getJarOutput(Context c, Model model, Region region, ClassNode classNode) {
        var cw = new CustomClassWriter(c, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS, model, region);
        try {
            classNode.accept(cw);
        } catch(Exception e) {
            if (e instanceof Log.KarinaException) {
                throw e;
            }
            Log.internal(c, e);

            try (var boas = new ByteArrayOutputStream()) {
                PrintWriter pw = new PrintWriter(boas);
                TraceClassVisitor tracer = new TraceClassVisitor(pw);
                classNode.accept(tracer);
                Log.generate(c, new GenerateError.GenerateClass(region, classNode.name, boas.toString()));
            } catch (IOException ex) {
                Log.temp(c, region, "Error while generating error report " + ex);
            }

            throw new Log.KarinaException();
        }
        var b = cw.toByteArray();
        return new JarCompilation.JarOutput(classNode.name + ".class", b);
    }

}
