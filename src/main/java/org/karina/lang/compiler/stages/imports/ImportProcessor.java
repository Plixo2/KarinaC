package org.karina.lang.compiler.stages.imports;

import com.google.common.base.Strings;
import org.karina.lang.compiler.jvm.model.PhaseDebug;
import org.karina.lang.compiler.logging.ErrorCollector;
import org.karina.lang.compiler.utils.Unique;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.ImportError;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.jvm.model.JKModelBuilder;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.Types;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.Prelude;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

public class ImportProcessor {

    public JKModel importTree(JKModel model) throws Log.KarinaException {
        var build = new JKModelBuilder(PhaseDebug.IMPORTED);
        Log.begin("import");

        var prelude = Prelude.fromModel(model);

        Log.beginType(Log.LogTypes.IMPORT_PRELUDE, "prelude");
        if (Log.LogTypes.IMPORT_PRELUDE.isVisible()) {
            prelude.log();
        }
        Log.endType(Log.LogTypes.IMPORT_PRELUDE, "prelude");

        try (var collector = new ErrorCollector()) {
            for (var kClassModel : model.getUserClasses()) {
                if (!kClassModel.isTopLevel()) {
                    continue;
                }
                var importContext = new ImportTable(model);

                collector.collect(() -> {
                    var withPrelude = ImportHelper.importPrelude(kClassModel, importContext, prelude);
                    var classModel = ImportItem.importClass(kClassModel, null, withPrelude, prelude);
                    build.addClassWithChildren(classModel);
                });
            }
            for (var bytecodeClass : model.getBytecodeClasses()) {
                build.addClass(bytecodeClass);
            }
        }



        Log.end("import");


        var newModel = build.build();
        Log.begin("validate");
        try (var collector = new ErrorCollector()) {
            for (var kClassModel : newModel.getUserClasses()) {
                if (!kClassModel.isTopLevel()) {
                    continue;
                }
                collector.collect(() -> {
                    validateClassModel(newModel, kClassModel);
                });
            }
        }
        Log.end("validate");

        return newModel;
    }



    //All Types are validated after importing, so all ClassPointers are valid
    private void validateClassModel(JKModel model, KClassModel classModel) {
        validateSuperClassCycle(model, classModel, new HashSet<>());
        validateInterfaceCycle(model, classModel, new HashSet<>());

        //TODO access tests


        var superClass = classModel.superClass();
        if (Modifier.isInterface(classModel.modifiers())) {

            if (superClass == null) {
                Log.temp(classModel.region(), "Interface must have object super class");
                throw new Log.KarinaException();
            } else {
                if (!superClass.pointer().isRoot()) {
                    Log.temp(classModel.region(), "Interface must have object super class");
                    throw new Log.KarinaException();
                }
            }
            if (!Modifier.isAbstract(classModel.modifiers())) {
                Log.temp(classModel.region(), "Interface must be abstract");
                throw new Log.KarinaException();
            }
            if (!classModel.fields().isEmpty()) {
                Log.temp(classModel.region(), "Interface cannot have fields");
                throw new Log.KarinaException();
            }
        } else {
            validateConstructor(classModel);

            if (!classModel.permittedSubclasses().isEmpty()) {
                Log.temp(classModel.region(), "Class cannot be sealed");
                throw new Log.KarinaException();
            }

            if (superClass == null) {
                Log.temp(classModel.region(), "Class must have a super class");
                throw new Log.KarinaException();
            }
        }

        for (var field : classModel.fields()) {
            if (!field.classPointer().equals(classModel.pointer())) {
                Log.temp(classModel.region(), "Field does not have correct class methodPointer");
                throw new Log.KarinaException();
            }
        }

        validateFields(classModel.fields());
        validateMethodSignatures(classModel.methods());

        for (var method : classModel.methods()) {
            if (!method.classPointer().equals(classModel.pointer())) {
                Log.temp(classModel.region(), "Method does not have correct class methodPointer");
                throw new Log.KarinaException();
            }
            if (Modifier.isStatic(method.modifiers())) {
                if (Modifier.isAbstract(method.modifiers())) {
                    Log.syntaxError(method.region(), "Static method must not be abstract");
                    throw new Log.KarinaException();
                }
                if (method.expression() == null) {
                    Log.syntaxError(classModel.region(), "Method must have expression");
                    throw new Log.KarinaException();
                }
            } else {
                if (Modifier.isAbstract(method.modifiers())) {
                    if (!Modifier.isAbstract(classModel.modifiers()) && !Modifier.isInterface(classModel.modifiers())) {
                        Log.syntaxError(method.region(), "Abstract method must be in abstract class or interface");
                        throw new Log.KarinaException();
                    }

                    if (method.expression() != null) {
                        Log.syntaxError(method.region(), "Abstract method must not have an expression");
                        throw new Log.KarinaException();
                    }

                } else if (method.expression() == null) {
                    Log.syntaxError(method.region(), "Method must have an expression: " + method.name());
                    throw new Log.KarinaException();
                }
            }
        }

        if (!classModel.permittedSubclasses().isEmpty()) {
            var duplicateSubClass =
                    Unique.testUnique(classModel.permittedSubclasses(), Function.identity());
            if (duplicateSubClass != null) {
                Log.syntaxError(classModel.region(), "Duplicate permitted subclass");
                throw new Log.KarinaException();
            }
        }

        var superClassModel = model.getClass(superClass.pointer());
        //TODO temporary fix
        if (!Modifier.isPublic(superClassModel.modifiers())) {
            Log.syntaxError(classModel.region(), "Super class cannot be private");
            throw new Log.KarinaException();
        }

        if (Modifier.isFinal(superClassModel.modifiers())) {
            Log.syntaxError(classModel.region(), "Cannot extend final class " + superClassModel.name());
            throw new Log.KarinaException();
        } else if (!Modifier.isStatic(superClassModel.modifiers()) && !superClassModel.isTopLevel()) {
            Log.syntaxError(classModel.region(), "Cannot extend non static class " + superClassModel.name());
            throw new Log.KarinaException();
        } else if (Modifier.isInterface(superClassModel.modifiers())) {
            Log.syntaxError(classModel.region(), "Cannot extend interface");
            throw new Log.KarinaException();
        } else if ((superClassModel.modifiers() & Opcodes.ACC_ENUM) != 0) {
            if (!Modifier.isAbstract(classModel.modifiers())) {
                Log.syntaxError(classModel.region(), "Cannot extend enum class");
                throw new Log.KarinaException();
            }
        } else if ((superClassModel.modifiers() & Opcodes.ACC_ANNOTATION) != 0) {
            Log.syntaxError(classModel.region(), "Cannot extend annotation class");
            throw new Log.KarinaException();
        } else if ((superClassModel.modifiers() & Opcodes.ACC_RECORD) != 0) {
            Log.syntaxError(classModel.region(), "Cannot extend record class");
            throw new Log.KarinaException();
        }
        var isSealed = !superClassModel.permittedSubclasses().isEmpty();
        if (isSealed) {
            if (!superClassModel.permittedSubclasses().contains(superClass.pointer())) {
                Log.syntaxError(classModel.region(), "Cannot extend sealed class");
                throw new Log.KarinaException();
            }
        }

        for (var anInterface : classModel.interfaces()) {
            var interfaceModel = model.getClass(anInterface.pointer());
            //TODO temporary fix
            if (!Modifier.isPublic(interfaceModel.modifiers())) {
                Log.syntaxError(classModel.region(), "Interface " + anInterface.pointer() + " class is private");
                throw new Log.KarinaException();
            }

            if (!Modifier.isInterface(interfaceModel.modifiers()) || Modifier.isFinal(interfaceModel.modifiers())) {
                Log.syntaxError(classModel.region(), "Cannot implement non-interface class " + interfaceModel.name());
                throw new Log.KarinaException();
            }
        }

        var duplicateInner = Unique.testUnique(
                classModel.innerClasses(),
                ClassModel::name
        );
        if (duplicateInner != null) {
            Log.importError(new ImportError.DuplicateItem(
                    duplicateInner.first().region(),
                    duplicateInner.duplicate().region(),
                    duplicateInner.value()
            ));
            throw new Log.KarinaException();
        }

        var duplicateField = Unique.testUnique(
                classModel.fields(),
                FieldModel::name
        );
        if (duplicateField != null) {
            Log.importError(new ImportError.DuplicateItem(
                    duplicateField.first().region(),
                    duplicateField.duplicate().region(),
                    duplicateField.value()
            ));
            throw new Log.KarinaException();
        }

        var duplicateInterface = Unique.testUnique(
                classModel.interfaces().stream().map(ref -> model.getClass(ref.pointer())).toList(),
                ClassModel::name
        );
        if (duplicateInterface != null) {
            Log.importError(new ImportError.DuplicateItem(
                    duplicateInterface.first().region(),
                    duplicateInterface.duplicate().region(),
                    duplicateInterface.value()
            ));
            throw new Log.KarinaException();
        }

        var duplicateGeneric = Unique.testUnique(
                classModel.generics(),
                Generic::name
        );
        if (duplicateGeneric != null) {
            Log.importError(new ImportError.DuplicateItem(
                    duplicateGeneric.first().region(),
                    duplicateGeneric.duplicate().region(),
                    duplicateGeneric.value()
            ));
            throw new Log.KarinaException();
        }
        var outerClassModel = classModel.outerClass();
        if (outerClassModel != null) {
            if (!outerClassModel.innerClasses().contains(classModel)) {
                Log.temp(classModel.region(), "Outer class does not have inner class");
                throw new Log.KarinaException();
            }
        }

        for (var innerClass : classModel.innerClasses()) {
            //we want the correct outer class, by identity
            if (innerClass.outerClass() != classModel) {
                Log.temp(classModel.region(), "Inner class does not have correct outer class");
                throw new Log.KarinaException();
            }
            validateClassModel(model, innerClass);
        }

    }

    private void validateSuperClassCycle(JKModel model, ClassModel cls, Set<ClassPointer> visited) {
        if (visited.contains(cls.pointer())) {
            Log.temp(cls.region(), "Cycle in super class hierarchy");
            throw new Log.KarinaException();
        }

        visited.add(cls.pointer());
        var superClassPointer = cls.superClass();
        if (superClassPointer != null) {
            var superModel = model.getClass(superClassPointer.pointer());
            validateSuperClassCycle(model, superModel, visited);
        }
    }

    private void validateInterfaceCycle(JKModel model, ClassModel cls, Set<ClassPointer> visited) {
        if (visited.contains(cls.pointer())) {
            Log.temp(cls.region(), "Cycle in interface hierarchy");
            throw new Log.KarinaException();
        }

        visited.add(cls.pointer());
        var interfaces = cls.interfaces();
        for (var interface_ : interfaces) {
            var interfaceModel = model.getClass(interface_.pointer());
            validateInterfaceCycle(model, interfaceModel, new HashSet<>(visited));
        }
    }

    private void validateFields(List<? extends FieldModel> fieldModels) {
        var stringDuplicate = Unique.testUnique(fieldModels, FieldModel::name);
        if (stringDuplicate != null) {
            Log.importError(new ImportError.DuplicateItem(
                    stringDuplicate.first().region(),
                    stringDuplicate.duplicate().region(),
                    stringDuplicate.value()
            ));
            throw new Log.KarinaException();
        }
    }

    private void validateMethodSignatures(List<? extends MethodModel> methodModels) {
        var buckets = new HashMap<String, List<MethodModel>>();

        for (var methodModel : methodModels) {

            var duplicate = Unique.testUnique(methodModel.parameters());
            if (duplicate != null) {
                //todo fix error region
                Log.importError(new ImportError.DuplicateItemWithMessage(
                        methodModel.region(),
                        methodModel.region(),
                        duplicate.value(),
                        "Duplicate parameter in " + methodModel.name()
                ));
                throw new Log.KarinaException();
            }

            buckets.computeIfAbsent(methodModel.name(), k -> new ArrayList<>())
                   .add(methodModel);

        }

        for (var methodBuckets : buckets.entrySet()) {
            var ignored = methodBuckets.getKey();
            List<NameAndSignature> currentSignatures = new ArrayList<>();

            for (var methodModel : methodBuckets.getValue()) {
                var parameters = methodModel.signature().parameters();
                var erased = parameters.stream().map(Types::erase).toList();

                NameAndSignature firstWithSignature = null;
                for (var currentSignature : currentSignatures) {
                    if (Types.signatureEquals(currentSignature.signature(), erased)) {
                        firstWithSignature = currentSignature;
                        break;
                    }
                }
                if (firstWithSignature != null) {
                    var erasedStr = String.join(",",
                            erased.stream().map(Object::toString).toList()
                    );
                    var equals = String.join(",",
                            firstWithSignature.signature().stream().map(Object::toString).toList()
                    );
                    var duplicate = "fn " + methodModel.name() + "(" + erasedStr + ")";
                    var first = "fn " + methodModel.name() + "(" + equals + ")";
                    Log.importError(new ImportError.DuplicateItem(
                            methodModel.region(),
                            methodModel.region(),
                            duplicate + " / " + first
                    ));
                    throw new Log.KarinaException();
                }

                currentSignatures.add(new NameAndSignature(methodModel, erased));
            }

        }

    }
    private record NameAndSignature(MethodModel model, List<KType> signature) { }



    private void validateConstructor(KClassModel classModel) {
        for (var method : classModel.methods()) {
            if (method.name().equals("<init>")) {
                return;
            }
        }
        Log.temp(classModel.region(), "Class must have constructor");
        throw new Log.KarinaException();
    }

}
