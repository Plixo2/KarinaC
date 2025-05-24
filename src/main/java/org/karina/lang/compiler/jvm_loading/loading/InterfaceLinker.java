package org.karina.lang.compiler.jvm_loading.loading;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.jvm_loading.signature.FieldSignatureBuilder;
import org.karina.lang.compiler.jvm_loading.signature.MethodSignatureBuilder;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.jvm_loading.TypeDecoding;
import org.karina.lang.compiler.model_api.impl.jvm.JClassModel;
import org.karina.lang.compiler.model_api.impl.jvm.JFieldModel;
import org.karina.lang.compiler.model_api.impl.jvm.JMethodModel;
import org.karina.lang.compiler.jvm_loading.signature.ClassSignatureBuilder;
import org.karina.lang.compiler.jvm_loading.signature.SignatureVisitor;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.Region;
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

    public JClassModel createClass(@Nullable JClassModel outerClassModel, OpenSet.LoadedClass cls, OpenSet openSet, Set<String> visited, ModelBuilder modelBuilder) {
        var node = cls.node();

        Log.beginType(Log.LogTypes.JVM_CLASS_LOADING, "Loading class: " + node.name);

        var source = cls.getSource();
        var region = source.emptyRegion();

        if (visited.contains(node.name)) {
            Log.bytecode(source, node.name, "Cycle detected");
            throw new Log.KarinaException();
        }
        visited.add(node.name);

        var pointer = TypeDecoding.internalNameToPointer(region, node.name);
        var name = pointer.path().last();
        var path = pointer.path();


        var modifiers = node.access;
        var generics = ImmutableList.<Generic>of();
        var interfaceBuilder = ImmutableList.<KType.ClassType>builder();


        for (var anInterface : node.interfaces) {
            var interFacePointer = TypeDecoding.internalNameToPointer(region, anInterface);
            Log.recordType(Log.LogTypes.JVM_CLASS_LOADING, " with interface: " + interFacePointer);

            interfaceBuilder.add(new KType.ClassType(interFacePointer, List.of()));
        }
        var interfaces = interfaceBuilder.build();

        KType.ClassType superType = null;


        if (node.signature != null) {
            var signatureParser = new SignatureVisitor(region, node.signature);
            var signature = signatureParser.parseClassSignature();
            var builder = new ClassSignatureBuilder(node.name, region, signature, outerClassModel);
            generics = builder.generics();
            superType = builder.superClass();
            if (interfaces.size() != builder.interfaces().size()) {
                Log.temp(region, "Mismatch in interfaces");
                throw new Log.KarinaException();
            }
            interfaces = builder.interfaces();
        } else if (node.superName != null) {
            var superPointer = TypeDecoding.internalNameToPointer(region, node.superName);
            superType = new KType.ClassType(superPointer, List.of());
        } else if (!node.name.equals("java/lang/Object")) {
            Log.bytecode(region, node.name, "No super class found");
            throw new Log.KarinaException();
        }



        var permittedSubclasses = ImmutableList.<ClassPointer>builder();
        if (node.permittedSubclasses != null) {
            for (var permittedSubclass : node.permittedSubclasses) {
                var interfacePointer = TypeDecoding.internalNameToPointer(region, permittedSubclass);
                permittedSubclasses.add(interfacePointer);
            }
        }

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
        modelBuilder.addClass(classModel);

        for (var field : node.fields) {
            fieldsToFill.add(buildField(classModel, region, field));
        }
        for (var method : node.methods) {
            methodsToFill.add(buildMethod(classModel, region, method));
        }
        if (node.innerClasses != null) {
            for (var innerClass : node.innerClasses) {
                var args = new Object[]{
                        innerClass.name,
                        "outerName",
                        innerClass.outerName,
                        "innerName",
                        innerClass.innerName,
                        "access",
                        innerClass.access,
                        Modifier.toString(innerClass.access),
                };
                Log.recordType(Log.LogTypes.JVM_CLASS_LOADING, "inner class " + innerClass.name, args);
            }
            for (var innerClass : node.innerClasses) {
                if (innerClass.outerName == null) {
                    var _ = openSet.removeByName(innerClass.name);
                    continue;
                } else if (!Objects.equals(innerClass.outerName, node.name)) {
                    continue;
                }
                var loadedClass = openSet.removeByName(innerClass.name);
                if (loadedClass == null) {
                    Log.bytecode(region, node.name, "Cannot load inner class: " + innerClass.name);
                    throw new Log.KarinaException();
                }
                var aClass = createClass(classModel, loadedClass, openSet, visited, modelBuilder);
                innerClassesToFill.add(aClass);
            }
        }


        Log.endType(Log.LogTypes.JVM_CLASS_LOADING, node.name, " with " + fieldsToFill.size() + " fields and " + methodsToFill.size() + " methods and " + innerClassesToFill.size() + " inner classes");
        return classModel;

    }

    private JFieldModel buildField(JClassModel owner, Region region, FieldNode fieldNode) {
        var name = fieldNode.name;
        var type = this.typeGen.fromType(region, fieldNode.desc);

        if (fieldNode.signature != null) {
            var signatureParser = new SignatureVisitor(region, fieldNode.signature);
            var signature = signatureParser.parseFieldSignature();
            var builder = new FieldSignatureBuilder(name, region, signature, owner);
            type = builder.type();
        }
        var modifiers = fieldNode.access;
        return new JFieldModel(name, type, modifiers, region, owner.pointer());
    }

    private JMethodModel buildMethod(JClassModel owner, Region region, MethodNode methodNode) {
        Log.beginType(Log.LogTypes.JVM_CLASS_LOADING, "Loading method: " + methodNode.name + " of " + owner.path());
        Log.recordType(Log.LogTypes.JVM_CLASS_LOADING, "mods: " + Modifier.toString(methodNode.access));

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
            var builder = new MethodSignatureBuilder(owner.name(), region, signature, owner);
            generics = builder.generics();
            var parametersFromSignature = builder.parameters();
            /*
            if (parameterNames.size() != parametersFromSignature.size()) {
                var msg = "Method " + methodNode.name + " Mismatch in parameters size in signature";
                msg = msg + " Expected: " + parameterNames.size() + " but found: " + parametersFromSignature.size();


                Log.bytecode(region, "Method " + methodNode.name , msg);
                Log.warn(methodNode.parameters);
                if (methodNode.parameters != null) {
                    for (var parameter : methodNode.parameters) {
                        Log.warn("access: ", parameter.access);
                        Log.warn("access: ", (parameter.access & Opcodes.ACC_SYNTHETIC) != 0);
                        Log.warn("name: ", parameter.name);
                    }
                }
                Log.warn(
                        "parameterTypes:",
                        parameterTypes
                );
                Log.warn(
                        "names: ",
                        parameterNames

                );
                Log.warn(
                        "found: ",
                        parametersFromSignature
                );
                Log.warn(
                        "signature: ",
                        signature
                );
                Log.warn(
                        "from string: ",
                        methodNode.signature
                );
                Log.warn(
                        "flags: ",
                        Modifier.toString(methodNode.access)
                );
                Log.warn(
                        "flags raw: ",
                        methodNode.access
                );
                Log.warn(
                        "name: ",
                        methodNode.name
                );
                Log.warn(
                        "modifiers of owner: ",
                        owner.modifiers()
                );
                Log.warn(
                        "mods toString: ",
                        Modifier.toString( owner.modifiers())
                );
                Log.warn(
                        " is : ",
                        (owner.modifiers() & Opcodes.ACC_SYNTHETIC) != 0,
                        (owner.modifiers() & Opcodes.ACC_BRIDGE) != 0
                );

                throw new Log.KarinaException();
            }
            */
            parameterTypes = parametersFromSignature;
            returnType = builder.returnType();
        }
        Log.endType(Log.LogTypes.JVM_CLASS_LOADING, "Loading method: " + methodNode.name);
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
                Log.recordType(Log.LogTypes.JVM_CLASS_LOADING, "Using locals as parameter names");
                for (var i = 0; i < parameterTypes.size(); i++) {
                    var parameter = methodNode.localVariables.get(i + add);
                    if (parameter.name != null) {
                        parameterNames.add(parameter.name);
                    } else {
                        parameterNames.add("argN" + i);
                    }
                }
            } else {
                Log.recordType(Log.LogTypes.JVM_CLASS_LOADING, "Method " + methodNode.name + " has no parameter names");
                for (var i = 0; i < parameterTypes.size(); i++) {
                    parameterNames.add("arg" + i);
                }
            }
        }
        Log.recordType(Log.LogTypes.JVM_CLASS_LOADING, "params",  parameterTypes.size());
        Log.recordType(Log.LogTypes.JVM_CLASS_LOADING, "vars", methodNode.localVariables);
        if (methodNode.localVariables != null) {
            Log.recordType(Log.LogTypes.JVM_CLASS_LOADING, "of length", methodNode.localVariables.size());
        }
        var build = parameterNames.build();
        for (var s : build) {
            Log.recordType(Log.LogTypes.JVM_CLASS_LOADING, "Parameter: " + s);
        }

        return build;

    }
}
