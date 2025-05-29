package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.ImportError;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.FieldModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.*;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

/**
 * Validates classes after importing. Only checks, does not transform the model.
 *    TODO validation
 *    access modifiers
 *    can extends
 *    can implement
 *    valid field
 */
public class PostImportValidator {

    public static void validateTree(Context c, Model model) {
        Log.begin("validate");

        try(var validateFork = c.fork()) {
            var userClasses = model.getUserClasses();
            for (var kClassModel : userClasses) {
                if (!kClassModel.isTopLevel()) {
                    continue;
                }
                validateFork.collect(subC -> {
                    PostImportValidator.validateClassModel(subC, model, kClassModel);
                    //nothing to yield
                    return null;
                });
            }
            var _ = validateFork.dispatchParallel();
        }

        Log.end("validate");
    }

    //All Types are validated after importing, so all ClassPointers are valid
    private static void validateClassModel(Context c, Model model, KClassModel classModel) {
        validateSuperClassCycle(c, model, classModel, new HashSet<>());
        validateInterfaceCycle(c, model, classModel, new HashSet<>());

        //TODO access tests


        var superClass = classModel.superClass();
        if (Modifier.isInterface(classModel.modifiers())) {

            if (superClass == null) {
                Log.temp(c, classModel.region(), "Interface must have object super class");
                throw new Log.KarinaException();
            } else {
                if (!superClass.pointer().isRoot()) {
                    Log.temp(c, classModel.region(), "Interface must have object super class");
                    throw new Log.KarinaException();
                }
            }
            if (!Modifier.isAbstract(classModel.modifiers())) {
                Log.temp(c, classModel.region(), "Interface must be abstract");
                throw new Log.KarinaException();
            }
            if (!classModel.fields().isEmpty()) {
                Log.temp(c, classModel.region(), "Interface cannot have fields");
                throw new Log.KarinaException();
            }
        } else {
            validateConstructor(c, classModel);

            if (!classModel.permittedSubclasses().isEmpty()) {
                Log.temp(c, classModel.region(), "Class cannot be sealed");
                throw new Log.KarinaException();
            }

            if (superClass == null) {
                Log.temp(c, classModel.region(), "Class must have a super class");
                throw new Log.KarinaException();
            }
        }

        for (var field : classModel.fields()) {
            if (!field.classPointer().equals(classModel.pointer())) {
                Log.temp(c, classModel.region(), "Field does not have correct class methodPointer");
                throw new Log.KarinaException();
            }
        }

        validateFields(c, classModel.fields());
        validateMethodSignatures(c, classModel.methods());

        for (var method : classModel.methods()) {
            if (!method.classPointer().equals(classModel.pointer())) {
                Log.temp(c, classModel.region(), "Method does not have correct class methodPointer");
                throw new Log.KarinaException();
            }
            if (Modifier.isStatic(method.modifiers())) {
                if (Modifier.isAbstract(method.modifiers())) {
                    Log.syntaxError(c, method.region(), "Static method must not be abstract");
                    throw new Log.KarinaException();
                }
                if (method.expression() == null) {
                    Log.syntaxError(c, classModel.region(), "Method must have expression");
                    throw new Log.KarinaException();
                }
            } else {
                if (Modifier.isAbstract(method.modifiers())) {
                    if (!Modifier.isAbstract(classModel.modifiers()) && !Modifier.isInterface(classModel.modifiers())) {
                        Log.syntaxError(c, method.region(), "Abstract method must be in abstract class or interface");
                        throw new Log.KarinaException();
                    }

                    if (method.expression() != null) {
                        Log.syntaxError(c, method.region(), "Abstract method must not have an expression");
                        throw new Log.KarinaException();
                    }

                } else if (method.expression() == null) {
                    Log.syntaxError(c, method.region(), "Method must have an expression: " + method.name());
                    throw new Log.KarinaException();
                }
            }
        }

        if (!classModel.permittedSubclasses().isEmpty()) {
            var duplicateSubClass =
                    Unique.testUnique(classModel.permittedSubclasses(), Function.identity());
            if (duplicateSubClass != null) {
                Log.syntaxError(c, classModel.region(), "Duplicate permitted subclass");
                throw new Log.KarinaException();
            }
        }

        var superClassModel = model.getClass(superClass.pointer());
        //TODO temporary fix
        if (!Modifier.isPublic(superClassModel.modifiers())) {
            Log.syntaxError(c, classModel.region(), "Super class cannot be private");
            throw new Log.KarinaException();
        }

        if (Modifier.isFinal(superClassModel.modifiers())) {
            Log.syntaxError(c, classModel.region(), "Cannot extend final class " + superClassModel.name());
            throw new Log.KarinaException();
        }  else if (Modifier.isInterface(superClassModel.modifiers())) {
            Log.syntaxError(c, classModel.region(), "Cannot extend interface");
            throw new Log.KarinaException();
        } else if ((superClassModel.modifiers() & Opcodes.ACC_ENUM) != 0) {
            if (!Modifier.isAbstract(classModel.modifiers())) {
                Log.syntaxError(c, classModel.region(), "Cannot extend enum class");
                throw new Log.KarinaException();
            }
        } else if ((superClassModel.modifiers() & Opcodes.ACC_ANNOTATION) != 0) {
            Log.syntaxError(c, classModel.region(), "Cannot extend annotation class");
            throw new Log.KarinaException();
        } else if ((superClassModel.modifiers() & Opcodes.ACC_RECORD) != 0) {
            Log.syntaxError(c, classModel.region(), "Cannot extend record class");
            throw new Log.KarinaException();
        }
        var isSealed = !superClassModel.permittedSubclasses().isEmpty();
        if (isSealed) {
            if (!superClassModel.permittedSubclasses().contains(superClass.pointer())) {
                Log.syntaxError(c, classModel.region(), "Cannot extend sealed class");
                throw new Log.KarinaException();
            }
        }

        for (var anInterface : classModel.interfaces()) {
            var interfaceModel = model.getClass(anInterface.pointer());
            //TODO temporary fix
            if (!Modifier.isPublic(interfaceModel.modifiers())) {
                Log.syntaxError(c, classModel.region(), "Interface " + anInterface.pointer() + " class is private");
                throw new Log.KarinaException();
            }

            if (!Modifier.isInterface(interfaceModel.modifiers()) || Modifier.isFinal(interfaceModel.modifiers())) {
                Log.syntaxError(c, classModel.region(), "Cannot implement non-interface class " + interfaceModel.name());
                throw new Log.KarinaException();
            }
        }

        var duplicateInner = Unique.testUnique(
                classModel.innerClasses(),
                ClassModel::name
        );
        if (duplicateInner != null) {
            Log.error(c, new ImportError.DuplicateItem(
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
            Log.error(c, new ImportError.DuplicateItem(
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
            Log.error(c, new ImportError.DuplicateItem(
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
            Log.error(c, new ImportError.DuplicateItem(
                    duplicateGeneric.first().region(),
                    duplicateGeneric.duplicate().region(),
                    duplicateGeneric.value()
            ));
            throw new Log.KarinaException();
        }
        var outerClassModel = classModel.outerClass();
        if (outerClassModel != null) {
            if (!outerClassModel.innerClasses().contains(classModel)) {
                Log.temp(c, classModel.region(), "Outer class does not have inner class");
                throw new Log.KarinaException();
            }
        }
        validateImplementedMethods(c, model, classModel);

        for (var innerClass : classModel.innerClasses()) {
            //we want the correct outer class, by identity
            if (innerClass.outerClass() != classModel) {
                Log.temp(c, classModel.region(), "Inner class does not have correct outer class");
                throw new Log.KarinaException();
            }
            validateClassModel(c, model, innerClass);
        }


    }

    private static void validateSuperClassCycle(Context c, Model model, ClassModel cls, Set<ClassPointer> visited) {
        if (visited.contains(cls.pointer())) {
            Log.temp(c, cls.region(), "Cycle in super class hierarchy");
            throw new Log.KarinaException();
        }

        visited.add(cls.pointer());
        var superClassPointer = cls.superClass();
        if (superClassPointer != null) {
            var superModel = model.getClass(superClassPointer.pointer());
            validateSuperClassCycle(c, model, superModel, visited);
        }
    }

    private static void validateInterfaceCycle(Context c, Model model, ClassModel cls, Set<ClassPointer> visited) {
        if (visited.contains(cls.pointer())) {
            Log.temp(c, cls.region(), "Cycle in interface hierarchy");
            throw new Log.KarinaException();
        }

        visited.add(cls.pointer());
        var interfaces = cls.interfaces();
        for (var interface_ : interfaces) {
            var interfaceModel = model.getClass(interface_.pointer());
            validateInterfaceCycle(c, model, interfaceModel, new HashSet<>(visited));
        }
    }

    private static void validateFields(Context c, List<? extends FieldModel> fieldModels) {
        var stringDuplicate = Unique.testUnique(fieldModels, FieldModel::name);
        if (stringDuplicate != null) {
            Log.error(c, new ImportError.DuplicateItem(
                    stringDuplicate.first().region(),
                    stringDuplicate.duplicate().region(),
                    stringDuplicate.value()
            ));
            throw new Log.KarinaException();
        }
    }

    private static void validateMethodSignatures(Context c, List<? extends MethodModel> methodModels) {
        var buckets = new HashMap<String, List<MethodModel>>();

        for (var methodModel : methodModels) {

            var duplicate = Unique.testUnique(methodModel.parameters());
            if (duplicate != null) {
                //todo fix error region
                Log.error(c, new ImportError.DuplicateItemWithMessage(
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
                    Log.error(c, new ImportError.DuplicateItem(
                            firstWithSignature.model.region(),
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



    private static void validateConstructor(Context c, KClassModel classModel) {
        for (var method : classModel.methods()) {
            if (method.name().equals("<init>")) {
                return;
            }
        }
        Log.temp(c, classModel.region(), "Class must have constructor");
        throw new Log.KarinaException();
    }

    private static void validateImplementedMethods(Context c, Model model, KClassModel classModel) {
        if (Modifier.isAbstract(classModel.modifiers())) {
            return;
        }

        var classType = classModel.getDefaultClassType();
        var toImplement = MethodHelper.getMethodsToImplementForClass(c, model, classType);

        for (var methodToImplement : toImplement) {
            Log.temp(c, classModel.region(),
                    "Method '" + methodToImplement.toReadableString() + "' is not implemented from class " +
                            methodToImplement.originalMethodPointer().classPointer()
            );
        }
        if (!toImplement.isEmpty()) {
            throw new Log.KarinaException();
        }

    }
}
