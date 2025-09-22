package org.karina.lang.compiler.jvm_loading.loading;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.jvm_loading.signature.FieldSignatureBuilder;
import org.karina.lang.compiler.jvm_loading.signature.MethodSignatureBuilder;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.jvm_loading.TypeDecoding;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.model_api.impl.jvm.JFieldModel;
import org.karina.lang.compiler.model_api.impl.jvm.JMethodModel;
import org.karina.lang.compiler.jvm_loading.signature.ClassSignatureBuilder;
import org.karina.lang.compiler.jvm_loading.signature.SignatureVisitor;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

//TODO rework java loading
public class InterfaceLinker {

    private final TypeDecoding typeGen;

    public InterfaceLinker() {
        this.typeGen = new TypeDecoding();
    }

    public JClassModel createClass(IntoContext c, @Nullable JClassModel outerClassModel, OpenSet.LoadedClass cls, OpenSet openSet, Set<String> visited, ModelBuilder modelBuilder, @Nullable String innerName) {
        var node = cls.node();

        var source = cls.getSource();
        var region = source.emptyRegion();

        if (visited.contains(node.name)) {
            Log.bytecode(c, source, node.name, "Cycle detected");
            throw new Log.KarinaException();
        }
        visited.add(node.name);

        var pointer = TypeDecoding.internalNameToPointer(region, node.name);
        var name = pointer.path().last();
        var path = pointer.path();
        if (innerName != null) {
            name = innerName;
        }


        var modifiers = node.access;
        var generics = ImmutableList.<Generic>of();
        var interfaceBuilder = ImmutableList.<KType.ClassType>builder();


        for (var anInterface : node.interfaces) {
            var interFacePointer = TypeDecoding.internalNameToPointer(region, anInterface);

            interfaceBuilder.add(interFacePointer.implement(List.of()));
        }
        var interfaces = interfaceBuilder.build();

        KType.ClassType superType = null;


        if (node.signature != null) {
            var signatureParser = new SignatureVisitor(region, node.signature);
            var signature = signatureParser.parseClassSignature();
            var builder = new ClassSignatureBuilder(c.intoContext(), node.name, region, signature, outerClassModel);
            generics = builder.generics();
            superType = builder.superClass();
            if (interfaces.size() != builder.interfaces().size()) {
                Log.temp(c, region, "Mismatch in interfaces");
                throw new Log.KarinaException();
            }
            interfaces = builder.interfaces();
        } else if (node.superName != null) {
            var superPointer = TypeDecoding.internalNameToPointer(region, node.superName);
            superType = superPointer.implement(List.of());
        } else if (!node.name.equals("java/lang/Object")) {
            Log.bytecode(c, region, node.name, "No super class found");
            throw new Log.KarinaException();
        }



        var permittedSubclasses = ImmutableList.<ClassPointer>builder();
        if (node.permittedSubclasses != null) {
            for (var permittedSubclass : node.permittedSubclasses) {
                var interfacePointer = TypeDecoding.internalNameToPointer(region, permittedSubclass);
                permittedSubclasses.add(interfacePointer);
            }
        }

        var nestHost = node.nestHostClass == null ? null : TypeDecoding.internalNameToPointer(region, node.nestHostClass);

        var version = node.version;
        var nestMembers = ImmutableList.<ClassPointer>of();

        var innerClassesToFill = new ArrayList<JClassModel>();
        var methodsToFill = new ArrayList<JMethodModel>();
        var fieldsToFill = new ArrayList<JFieldModel>();
        var classModel = new JClassModel(
                name,
                path,
                version,
                modifiers,
                superType,
                outerClassModel,
                nestHost,
                interfaces,
                innerClassesToFill,
                fieldsToFill,
                methodsToFill,
                generics,
                permittedSubclasses.build(),
                nestMembers,
                source,
                region
        );
        modelBuilder.addClass(c, classModel);

        for (var field : node.fields) {
            fieldsToFill.add(buildField(c, classModel, region, field));
        }
        for (var method : node.methods) {
            methodsToFill.add(buildMethod(c, classModel, region, method));
        }
        if (node.innerClasses != null) {
            for (var innerClass : node.innerClasses) {
                if (innerClass.outerName == null) {
                    var _ = openSet.removeByName(innerClass.name);
                    continue;
                } else if (!Objects.equals(innerClass.outerName, node.name)) {
                    continue;
                }
                var loadedClass = openSet.removeByName(innerClass.name);
                if (loadedClass == null) {
                    Log.bytecode(c, region, node.name, "Cannot load inner class: " + innerClass.name);
                    throw new Log.KarinaException();
                }
                var aClass = createClass(c, classModel, loadedClass, openSet, visited, modelBuilder, innerClass.innerName);
                innerClassesToFill.add(aClass);
            }
        }


        return classModel;

    }

    private JFieldModel buildField(IntoContext c, JClassModel owner, Region region, FieldNode fieldNode) {
        var name = fieldNode.name;
        var type = this.typeGen.fromType(region, fieldNode.desc);

        if (fieldNode.signature != null) {
            var signatureParser = new SignatureVisitor(region, fieldNode.signature);
            var signature = signatureParser.parseFieldSignature();
            var builder = new FieldSignatureBuilder(c.intoContext(), name, region, signature, owner);
            type = builder.type();
        }
        var modifiers = fieldNode.access;
        return new JFieldModel(name, type, modifiers, region, owner.pointer());
    }

    private JMethodModel buildMethod(IntoContext c, JClassModel owner, Region region, MethodNode methodNode) {

        var nameSplit = methodNode.name.split("/");
        var name = nameSplit[nameSplit.length - 1];
        var modifiers = methodNode.access;

        var returnType = this.typeGen.getReturnType(region, methodNode.desc);
        var parameterTypes = ImmutableList.copyOf(this.typeGen.getParameters(region, methodNode.desc));

        var parameterNames = getMethodParameterNames(owner, methodNode, parameterTypes);

        var generics = ImmutableList.<Generic>of();
        var isSynthetic = (modifiers & Opcodes.ACC_SYNTHETIC) != 0;
        if (methodNode.signature != null && !isSynthetic) {
            var signatureParser = new SignatureVisitor(region, methodNode.signature);
            var signature = signatureParser.parseMethodSignature();
            var builder = new MethodSignatureBuilder(c.intoContext(), owner.name(), region, signature, owner);
            generics = builder.generics();
            parameterTypes = builder.parameters();
            returnType = builder.returnType();
        }

        if (Modifier.isStatic(modifiers) && !parameterTypes.isEmpty()) {
            if (methodNode.invisibleAnnotations != null) {
                for (var invisibleAnnotation : methodNode.invisibleAnnotations) {
                    if (invisibleAnnotation.desc.equals("L" + ClassPointer.EXTENSION_PATH.mkString("/") + ";")) {
                        modifiers |= ClassModel.EXTENSION_MODIFIER;
                        break;
                    }
                }
            }
        }

        var signature = new Signature(parameterTypes, returnType);
        return new JMethodModel(
                name,
                modifiers,
                signature,
                parameterNames,
                generics,
                null,
                region,
                owner.pointer()
        );
    }

    private static ImmutableList<String> getMethodParameterNames(
            JClassModel owner,
            MethodNode methodNode,
            ImmutableList<KType> parameterTypes
    ) {

        var parameterNames = ImmutableList.<String>builder();
        if (methodNode.parameters != null) {
            var counter = 0;
            for (var i = 0; i < methodNode.parameters.size(); i++) {
                var parameter = methodNode.parameters.get(i);
                if (parameter == null || (parameter.access & Opcodes.ACC_SYNTHETIC) != 0) {
                    continue;
                }

                if (parameter.name != null) {
                    parameterNames.add(parameter.name);
                } else {
                    parameterNames.add("argC" + counter);
                }
                counter += 1;
            }
        } else {

            //skip 'this'
            var add = 1;
            if (Modifier.isStatic(methodNode.access)) {
                add = 0;
            }
            //try to recover parameter names from the first n local variables
            if (methodNode.localVariables != null && methodNode.localVariables.size() >= parameterTypes.size() + add) {
                for (var i = 0; i < parameterTypes.size(); i++) {
                    var parameter = methodNode.localVariables.get(i + add);
                    if (parameter.name != null) {
                        parameterNames.add(parameter.name);
                    } else {
                        parameterNames.add("argN" + i);
                    }
                }
            } else {
                for (var i = 0; i < parameterTypes.size(); i++) {
                    parameterNames.add("arg" + i);
                }
            }
        }
        return parameterNames.build();

    }
}
