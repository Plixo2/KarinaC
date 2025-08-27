package org.karina.lang.compiler.stages.generate;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.GenerateError;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Region;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Custom ClassWriter that overrides the {@link #getCommonSuperClass} method for generating stack frames
 */
public class CustomClassWriter extends ClassWriter {

    private final Region region;
    private final Model model;
    private final Context c;
    public CustomClassWriter(Context c, int flags, Model model, Region region) {
        super(null, flags);
        this.c = c;
        this.model = model;
        this.region = region;
    }



    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        var pathA = fromJavaPath(type1);
        var pathB = fromJavaPath(type2);

        var pointerA = this.model.getClassPointer(this.region, pathA);
        var pointerB = this.model.getClassPointer(this.region, pathB);
        if (pointerA == null) {
            Log.warn(this.c, this.region, "Cannot find class while emitting bytecode " + type1 + " " + pathA);
            return "java/lang/Object";
        }
        if (pointerB == null) {
            Log.warn(this.c, this.region, "Cannot find class while emitting bytecode " + type2 + " " + pathB);
            return "java/lang/Object";
        }
        return TypeEncoding.toJVMPath(this.model, getCommonSuperClass(pointerA, pointerB));
    }

    private static final Pattern DOLLAR_SPLIT = Pattern.compile("\\$(?!\\d)");
    private static ObjectPath fromJavaPath(String type) {
        List<String> parts = new ArrayList<>();

        String[] slashParts = type.split("/");

        for (String slashPart : slashParts) {
            // split by $ (only when not followed by a digit)
            String[] dollarParts = DOLLAR_SPLIT.split(slashPart);
            for (String dp : dollarParts) {
                if (!dp.isEmpty()) {
                    parts.add(dp);
                }
            }
        }
        return new ObjectPath(parts);
    }

    private ClassPointer getCommonSuperClass(ClassPointer pointerA, ClassPointer pointerB) {

        var classModelA = this.model.getClass(pointerA);
        var classModelB = this.model.getClass(pointerB);

        if (Modifier.isInterface(classModelA.modifiers())) {
            if (implementsType(classModelB, classModelA)) {
                return pointerA;
            } else {
                return KType.ROOT.pointer();
            }
        }

        if (Modifier.isInterface(classModelB.modifiers())) {
            if (implementsType(classModelA, classModelB)) {
                return pointerB;
            } else {
                return KType.ROOT.pointer();
            }
        }

        var ancestorsA = getAncestorsOf(classModelA);
        var ancestorsB = getAncestorsOf(classModelB);
        int i = ancestorsA.size() - 1;
        int j = ancestorsB.size() - 1;

        var lastCommon = KType.ROOT.pointer();

        while (i >= 0 && j >= 0) {
            var pa = ancestorsA.get(i);
            var pb = ancestorsB.get(j);

            if (pa.equals(pb)) {
                lastCommon = pa;
            } else {
                break;
            }

            i--;
            j--;
        }

        return lastCommon;
    }

    private boolean implementsType(ClassModel classModel, ClassModel interfaceModel) {

        while (classModel != null) {

            var interfaces = classModel.interfaces();
            for (var itf : interfaces) {
                if (itf.pointer().equals(interfaceModel.pointer())) {
                    return true;
                }
            }
            for (var itf : interfaces) {
                var interfaceClassModel = this.model.getClass(itf.pointer());
                if (implementsType(interfaceClassModel, interfaceModel)) {
                    return true;
                }
            }

            var superClass = classModel.superClass();
            if (superClass == null) {
                break;
            }
            classModel = this.model.getClass(superClass.pointer());
        }
        return false;

    }

    private List<ClassPointer> getAncestorsOf(ClassModel classModel) {
        var list = new ArrayList<ClassPointer>();

        while (classModel != null) {

            var superClass = classModel.superClass();
            if (superClass == null) {
                break;
            }
            list.add(superClass.pointer());
            classModel = this.model.getClass(superClass.pointer());
        }

        return list;
    }



   /* @SneakyThrows
    public static void test() {
        var name = "org.objectweb.asm.ClassWriter";

        var reader = new ClassReader(ClassWriter.class.getName());
        var classNode = new ClassNode();
        reader.accept(classNode, 0);
        removeFinalModifiers(classNode);

        var classWriter = new ClassWriter(0);
        classNode.accept(classWriter);

        var bytes = classWriter.toByteArray();

        var loader = new ClassLoader() {
            public Class<?> define(String name, byte[] bytes) {
                return defineClass(name, bytes, 0, bytes.length);
            }
        };
        loader.define(name, bytes);
        Class<?> loadClass = loader.loadClass(name);
        System.out.println("Loaded class: " + loadClass.getName());

    }

    private static void removeFinalModifiers(ClassNode classNode) {
        var method = findMethod(classNode, "visitMethod");
        if (method == null) {
            throw new IllegalStateException("Method 'visitMethod' not found in class " + classNode.name);
        }
        method.access &= ~Modifier.FINAL;


    }

    private static @Nullable MethodNode findMethod(ClassNode classNode, String name) {
        for (var method : classNode.methods) {
            if (method.name.equals(name)) {
                return method;
            }
        }
        return null;
    }*/

    ///
    /// catches errors when generating stack frames
    public static class TracingClassNode extends ClassNode {
        private final Context c;
        public final Map<KMethodModel, MethodNode> methodMap = new HashMap<>();

        public TracingClassNode(Context c) {
            super(Opcodes.ASM9);
            this.c = c;
        }

        public void accept(final ClassVisitor classVisitor) {
            if (!this.methods.isEmpty()) {
                Log.internal(this.c, new IllegalStateException(
                        "ClassNode should not have methods in the 'method' list, " +
                                "use methodMap instead"
                ));
                throw new Log.KarinaException();
            }
            defaultAccept(classVisitor);
            for (var method : this.methodMap.entrySet()) {
                var methodModel = method.getKey();
                var methodNode = method.getValue();
                try {
                    methodNode.accept(classVisitor);
                } catch(Exception e) {
                    Log.internal(this.c, e);

                    try (var boas = new ByteArrayOutputStream()) {
                        PrintWriter pw = new PrintWriter(boas);
                        TraceClassVisitor tracer = new TraceClassVisitor(pw);
                        methodNode.accept(tracer);
                        tracer.visitEnd();
                        Log.generate(this.c, new GenerateError.GenerateMethod(
                                methodModel.region(),
                                methodModel.name(),
                                boas.toString()
                        ));
                    } catch (IOException ex) {
                        Log.temp(
                                this.c,
                                methodModel.region(),
                                "Error while generating error report " + ex
                        );
                    }
                    throw new Log.KarinaException();
                }
            }

            classVisitor.visitEnd();
        }

        /// From ClassNode, without classVisitor.visitEnd()
        private void defaultAccept(ClassVisitor classVisitor) {
            // Visit the header.
            String[] interfacesArray = new String[this.interfaces.size()];
            this.interfaces.toArray(interfacesArray);
            classVisitor.visit(this.version, this.access, this.name, this.signature, this.superName, interfacesArray);
            // Visit the source.
            if (this.sourceFile != null || this.sourceDebug != null) {
                classVisitor.visitSource(this.sourceFile, this.sourceDebug);
            }
            // Visit the module.
            if (this.module != null) {
                this.module.accept(classVisitor);
            }
            // Visit the nest host class.
            if (this.nestHostClass != null) {
                classVisitor.visitNestHost(this.nestHostClass);
            }
            // Visit the outer class.
            if (this.outerClass != null) {
                classVisitor.visitOuterClass(this.outerClass, this.outerMethod,
                        this.outerMethodDesc
                );
            }
            // Visit the annotations.
            if (this.visibleAnnotations != null) {
                for (AnnotationNode annotation : this.visibleAnnotations) {
                    annotation.accept(classVisitor.visitAnnotation(annotation.desc, true));
                }
            }
            if (this.invisibleAnnotations != null) {
                for (AnnotationNode annotation : this.invisibleAnnotations) {
                    annotation.accept(classVisitor.visitAnnotation(annotation.desc, false));
                }
            }
            if (this.visibleTypeAnnotations != null) {
                for (TypeAnnotationNode typeAnnotation : this.visibleTypeAnnotations) {
                    typeAnnotation.accept(classVisitor.visitTypeAnnotation(
                            typeAnnotation.typeRef,
                            typeAnnotation.typePath, typeAnnotation.desc, true
                    ));
                }
            }
            if (this.invisibleTypeAnnotations != null) {
                for (TypeAnnotationNode typeAnnotation : this.invisibleTypeAnnotations) {
                    typeAnnotation.accept(classVisitor.visitTypeAnnotation(
                            typeAnnotation.typeRef,
                            typeAnnotation.typePath, typeAnnotation.desc, false
                    ));
                }
            }
            // Visit the non standard attributes.
            if (this.attrs != null) {
                for (Attribute attr : this.attrs) {
                    classVisitor.visitAttribute(attr);
                }
            }
            // Visit the nest members.
            if (this.nestMembers != null) {
                for (String nestMember : this.nestMembers) {
                    classVisitor.visitNestMember(nestMember);
                }
            }
            // Visit the permitted subclasses.
            if (this.permittedSubclasses != null) {
                for (String permittedSubclass : this.permittedSubclasses) {
                    classVisitor.visitPermittedSubclass(permittedSubclass);
                }
            }
            // Visit the inner classes.
            for (org.objectweb.asm.tree.InnerClassNode innerClass : this.innerClasses) {
                innerClass.accept(classVisitor);
            }
            // Visit the record components.
            if (this.recordComponents != null) {
                for (org.objectweb.asm.tree.RecordComponentNode recordComponent : this.recordComponents) {
                    recordComponent.accept(classVisitor);
                }
            }
            // Visit the fields.
            for (org.objectweb.asm.tree.FieldNode field : this.fields) {
                field.accept(classVisitor);
            }
        }
    }



//    private static class TracingVisitor extends ClassVisitor {
//        private final Context c;
//        public TracingVisitor(Context c, ClassVisitor classVisitor) {
//            super(Opcodes.ASM9, classVisitor);
//            this.c = c;
//        }
//
//
//        @Override
//        public MethodVisitor visitMethod(
//                int access, String name, String descriptor,
//                String signature, String[] exceptions
//        ) {
//            try {
//                var methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
//                return methodVisitor;
//            } catch(Exception e) {
//                Log.internal(this.c, e);
//
//                Log.temp(this.c, region, "Error while generating error report " + ex);
//
//                throw new Log.KarinaException();
//            }
//
//        }
//    }

}
