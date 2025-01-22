package org.karina.lang.compiler.jvm;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.FileLoadError;
import org.karina.lang.compiler.jvm.binary.in.ModelReader;
import org.karina.lang.compiler.jvm.binary.out.ModelWriter;
import org.karina.lang.compiler.model.Signature;
import org.karina.lang.compiler.jvm.model.jvm.JClassModel;
import org.karina.lang.compiler.jvm.model.jvm.JFieldModel;
import org.karina.lang.compiler.jvm.model.jvm.JMethodModel;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class InterfaceBuilder {

    private final TypeGeneration typeGen;

    public InterfaceBuilder() {
        this.typeGen = new TypeGeneration();
    }

    public List<JClassModel> loadJarFile(File file) throws IOException {
        List<JClassModel> flatNodes = new ArrayList<>();
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

        var srcId = "jar:///" + fileName;
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

        ClassPointer outerClass = null;
        if (node.outerClass != null) {
            outerClass = this.typeGen.internalNameToPointer(node.outerClass);
        }

        var interfaces = ImmutableList.<ClassPointer>builder();
        for (var anInterface : node.interfaces) {
            interfaces.add(this.typeGen.internalNameToPointer(anInterface));
        }
        var innerClasses = ImmutableList.<ClassPointer>builder();
        for (var anInner : node.innerClasses) {
            innerClasses.add(this.typeGen.internalNameToPointer(anInner.name));
        }
        var fields = ImmutableList.<JFieldModel>builder();
        var currentClass = ClassPointer.of(path);
        for (var field : node.fields) {
            fields.add(buildField(currentClass, source, field));
        }
        var methods = ImmutableList.<JMethodModel>builder();
        for (var method : node.methods) {
            methods.add(buildMethod(currentClass, source, method));
        }
        var generics = ImmutableList.<Generic>of();

        var region = source.emptyRegion();

        var permittedSubclasses = ImmutableList.<ClassPointer>builder();
        if (node.permittedSubclasses != null) {
            for (var permittedSubclass : node.permittedSubclasses) {
                permittedSubclasses.add(this.typeGen.internalNameToPointer(permittedSubclass));
            }
        }

        var version = node.version;

        return new JClassModel(
                name,
                path,
                version,
                modifiers,
                superPointer,
                outerClass,
                interfaces.build(),
                innerClasses.build(),
                fields.build(),
                methods.build(),
                generics,
                permittedSubclasses.build(),
                source,
                region
        );
    }

    private JFieldModel buildField(ClassPointer owningClass, TextSource source, FieldNode fieldNode) {
        var name = fieldNode.name;
        var type = this.typeGen.fromType(fieldNode.desc, fieldNode.signature);
        var modifiers = fieldNode.access;
        var region = source.emptyRegion();
        return new JFieldModel(name, type, modifiers, region, owningClass);
    }

    private JMethodModel buildMethod(ClassPointer owningClass, TextSource source, MethodNode methodNode) {
        var nameSplit = methodNode.name.split("/");
        var name = nameSplit[nameSplit.length - 1];
        var modifiers = methodNode.access;

        var returnType = this.typeGen.getReturnType(methodNode.desc, methodNode.signature);
        var parameterTypes = ImmutableList.copyOf(this.typeGen.getParameters(methodNode.desc, methodNode.signature));

        var parameters = ImmutableList.<String>builder();
        for (var i = 0; i < parameterTypes.size(); i++) {
            parameters.add("arg" + i);
        }
        var region = source.emptyRegion();
        return new JMethodModel(
                name, modifiers, new Signature(parameterTypes, returnType), parameters.build(),
                ImmutableList.of(), null, region, owningClass
        );
    }


}
