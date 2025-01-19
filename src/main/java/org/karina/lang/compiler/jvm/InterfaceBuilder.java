package org.karina.lang.compiler.jvm;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.model.Signature;
import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.jvm.model.jvm.JFieldModel;
import org.karina.lang.compiler.jvm.model.jvm.JMethodModel;
import org.karina.lang.compiler.jvm.model.JModel;
import org.karina.lang.compiler.model.FieldModel;
import org.karina.lang.compiler.model.MethodModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.utils.ObjectPath;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class InterfaceBuilder {
    private static final String text = "resources/jars/commons-cli-1.9.0.jar";
    private static final String jdk = "resources/jars/jdk21.jar";

    private final TypeGeneration typeGen;

    public InterfaceBuilder() {
        this.typeGen = new TypeGeneration();
    }

    public static void main(String[] args) throws IOException {
        var interfaceBuilder = new InterfaceBuilder();
        var parsed = interfaceBuilder.parse(jdk);
        var model = new JModel();
        for (var node : parsed) {
            model.addClass(node);
        }
        for (var pointer : interfaceBuilder.typeGen.pointers) {
            var ignored = model.getClass(pointer);
        }
        for (var value : model.getBytecodeClasses()) {
            for (var innerClass : value.innerClasses()) {
                var ignored = model.getClass(innerClass);
            }
        }
        System.out.println("Cross Validated " + interfaceBuilder.typeGen.pointers.size() + " instances");

    }

    private List<JClassModel> parse(String path) throws IOException {
        List<JClassModel> flatNodes = new ArrayList<>();
        var file = new File(path);
        try (var jarFile = new JarFile(file)) {
            var entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    try (var inputStream = jarFile.getInputStream(entry)) {
                        var reader = new ClassReader(inputStream);
                        var classNode = new ClassNode();
                        reader.accept(classNode, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                        if (classNode.name.equals("module-info")) {
                            continue;
                        }
                        flatNodes.add(buildNode(classNode, file, entry.getRealName()));
                    }
                }
            }
        }
        return flatNodes;
    }


    private JClassModel buildNode(ClassNode node, File file, String fileName) {

        var srcId = "jar:file:///" + file.getAbsolutePath().replace("\\", "/") + "!/" + fileName;
        var source = new TextSource(new JavaResource(srcId), List.of());

        var nameSplit = node.name.split("/");
        var name = nameSplit[nameSplit.length - 1];
        var path = new ObjectPath();
        for (String s : nameSplit) {
            path = path.append(s);
        }
        var modifiers = node.access;
        ClassPointer superPointer = null;

        //java.lang.Object
        if (node.superName != null) {
            superPointer = this.typeGen.internalNameToPointer(node.superName);
        } else {
            if (!node.name.equals("java/lang/Object")) {
                throw new NullPointerException("Superclass is null for " + node.name);
            }
        }

        var interfaces = ImmutableList.<ClassPointer>builder();
        for (var anInterface : node.interfaces) {
            interfaces.add(this.typeGen.internalNameToPointer(anInterface));
        }
        var innerClasses = ImmutableList.<ClassPointer>builder();
        for (var anInner : node.innerClasses) {
            innerClasses.add(this.typeGen.internalNameToPointer(anInner.name));
        }
        var fields = ImmutableList.<FieldModel>builder();
        for (var field : node.fields) {
            fields.add(buildField(field));
        }
        var methods = ImmutableList.<MethodModel>builder();
        for (var method : node.methods) {
            methods.add(buildMethod(method));
        }
        return new JClassModel(
                name, path, modifiers, superPointer, interfaces.build(), innerClasses.build(),
                fields.build(), methods.build(), source
        );
    }

    private FieldModel buildField(FieldNode fieldNode) {
        var name = fieldNode.name;
        var type = this.typeGen.fromType(fieldNode.desc, fieldNode.signature);
        var modifiers = fieldNode.access;

        return new JFieldModel(name, type, modifiers);
    }

    private MethodModel buildMethod(MethodNode methodNode) {
        var nameSplit = methodNode.name.split("/");
        var name = nameSplit[nameSplit.length - 1];
        var modifiers = methodNode.access;

        var returnType = this.typeGen.getReturnType(methodNode.desc, methodNode.signature);
        var parameterTypes = this.typeGen.getParameters(methodNode.desc, methodNode.signature);

        var parameters = ImmutableList.<String>builder();
        for (var i = 0; i < parameterTypes.size(); i++) {
            parameters.add("arg" + i);
        }

        return new JMethodModel(
                name, modifiers, new Signature(parameterTypes, returnType), parameters.build(),
                ImmutableList.of(), null
        );
    }


}
