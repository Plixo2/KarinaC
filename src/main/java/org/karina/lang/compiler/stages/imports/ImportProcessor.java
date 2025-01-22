package org.karina.lang.compiler.stages.imports;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.Unique;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.jvm.model.JKModelBuilder;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.model.ClassModel;
import org.karina.lang.compiler.model.FieldModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.model.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.TypeImport;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

public class ImportProcessor {

    public JKModel importTree(JKModel model) throws Log.KarinaException {
        var build = new JKModelBuilder();
        try (var collector = new ErrorCollector()){
            for (var userClass : model.getUserClasses()) {
                var importContext = new ImportTable(model);
                //TODO add outer class support for generics and imports
                collector.collect(() -> {
                    var classModel = importClass(model, userClass, importContext);
                    validateClassModel(model, classModel);
                    build.addClass(classModel);
                });
            }
        }
        return build.build();
    }

    //All Types are valid after importing, so all ClassPointers are valid
    private void validateClassModel(JKModel model, KClassModel classModel) {
        validateSuperClassCycle(model, classModel, new HashSet<>());
        validateInterfaceCycle(model, classModel, new HashSet<>());
        validateOuterClassCycle(model, classModel, new HashSet<>());

        //TODO access tests



        if (Modifier.isInterface(classModel.modifiers())) {
            if (classModel.superClass() != null) {
                Log.temp(classModel.region(), "Interface cannot have super class");
                throw new Log.KarinaException();
            } else if (!Modifier.isAbstract(classModel.modifiers())) {
                Log.temp(classModel.region(), "Interface must be abstract");
                throw new Log.KarinaException();
            }
            if (!classModel.fields().isEmpty()) {
                Log.temp(classModel.region(), "Interface cannot have fields");
                throw new Log.KarinaException();
            }
        } else {
            validateConstructor(classModel);

            if (classModel.superClass() == null) {
                Log.temp(classModel.region(), "Class must have a super class");
                throw new Log.KarinaException();
            }
        }

        for (var field : classModel.fields()) {
            if (!field.classPointer().equals(classModel.pointer())) {
                Log.temp(classModel.region(), "Field does not have correct class pointer");
                throw new Log.KarinaException();
            }
        }

        for (var method : classModel.methods()) {
            if (!method.classPointer().equals(classModel.pointer())) {
                Log.temp(classModel.region(), "Method does not have correct class pointer");
                throw new Log.KarinaException();
            }
            if (Modifier.isStatic(method.modifiers())) {
                if (Modifier.isAbstract(method.modifiers())) {
                    Log.temp(method.region(), "Static method must not be abstract");
                    throw new Log.KarinaException();
                }
                if (method.expression() == null) {
                    Log.temp(classModel.region(), "Method must have expression");
                    throw new Log.KarinaException();
                }
            } else {
                if (Modifier.isAbstract(method.modifiers())) {
                    if (!Modifier.isAbstract(classModel.modifiers()) && !Modifier.isInterface(classModel.modifiers())) {
                        Log.temp(method.region(), "Abstract method must be in abstract class or interface");
                        throw new Log.KarinaException();
                    }

                    if (method.expression() != null) {
                        Log.temp(method.region(), "Abstract method must not have an expression");
                        throw new Log.KarinaException();
                    }

                } else if (method.expression() == null) {
                    Log.temp(method.region(), "Method must have an expression: " + method.name());
                    throw new Log.KarinaException();
                }
            }
        }

        if (!classModel.permittedSubclasses().isEmpty()) {
            var duplicateSubClass =
                    Unique.testUnique(classModel.permittedSubclasses(), Function.identity());
            if (duplicateSubClass != null) {
                Log.temp(classModel.region(), "Duplicate permitted subclass");
                throw new Log.KarinaException();
            }
        }

        var superClass = classModel.superClass();
        if (superClass != null) {
            var superClassModel = model.getClass(superClass);
            //TODO temporary fix
            if (Modifier.isPrivate(superClassModel.modifiers())) {
                Log.temp(classModel.region(), "Super class cannot be private");
                throw new Log.KarinaException();
            }

            if (Modifier.isFinal(superClassModel.modifiers())) {
                Log.temp(classModel.region(), "Cannot extend final class");
                throw new Log.KarinaException();
            } else if (Modifier.isInterface(superClassModel.modifiers())) {
                Log.temp(classModel.region(), "Cannot extend interface");
                throw new Log.KarinaException();
            } else if ((superClassModel.modifiers() & Opcodes.ACC_ENUM) != 0) {
                if (!Modifier.isAbstract(classModel.modifiers())) {
                    Log.temp(classModel.region(), "Cannot extend enum class");
                    throw new Log.KarinaException();
                }
            } else if ((superClassModel.modifiers() & Opcodes.ACC_ANNOTATION) != 0) {
                Log.temp(classModel.region(), "Cannot extend annotation class");
                throw new Log.KarinaException();
            } else if ((superClassModel.modifiers() & Opcodes.ACC_RECORD) != 0) {
                Log.temp(classModel.region(), "Cannot extend record class");
                throw new Log.KarinaException();
            }
            var isSealed = !superClassModel.permittedSubclasses().isEmpty();
            if (isSealed) {
                if (!superClassModel.permittedSubclasses().contains(superClass)) {
                    Log.temp(classModel.region(), "Cannot extend sealed class");
                    throw new Log.KarinaException();
                }
            }
        }
        for (var anInterface : classModel.interfaces()) {
            var interfaceModel = model.getClass(anInterface);
            //TODO temporary fix
            if (Modifier.isPrivate(interfaceModel.modifiers())) {
                Log.temp(classModel.region(), "Interface class cannot be private");
                throw new Log.KarinaException();
            }

            if (!Modifier.isInterface(interfaceModel.modifiers()) || Modifier.isFinal(interfaceModel.modifiers())) {
                Log.temp(classModel.region(), "Cannot implement non-interface class");
                throw new Log.KarinaException();
            }
        }

        var duplicateInner = Unique.testUnique(
                classModel.innerClasses().stream().map(model::getClass).toList(),
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
                classModel.interfaces().stream().map(model::getClass).toList(),
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
        for (var innerClass : classModel.innerClasses()) {
            if (!Objects.equals(model.getClass(innerClass).outerClass(), classModel.pointer())) {
                Log.temp(classModel.region(), "Inner class does not have correct outer class");
                throw new Log.KarinaException();
            }
        }
        var outerClass = classModel.outerClass();
        if (outerClass != null) {
            var outerClassModel = model.getClass(outerClass);
            if (!outerClassModel.innerClasses().contains(classModel.pointer())) {
                Log.temp(classModel.region(), "Outer class does not have inner class");
                throw new Log.KarinaException();
            }
        }

    }

    private void validateOuterClassCycle(JKModel model, ClassModel cls, Set<ClassPointer> visited) {
        if (visited.contains(cls.pointer())) {
            Log.temp(cls.region(),"Cycle in outer class hierarchy");
            throw new Log.KarinaException();
        }

        visited.add(cls.pointer());
        var outerClassPointer = cls.outerClass();
        if (outerClassPointer != null) {
            var outerModel = model.getClass(outerClassPointer);
            validateOuterClassCycle(model, outerModel, visited);
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
            var superModel = model.getClass(superClassPointer);
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
            var interfaceModel = model.getClass(interface_);
            validateInterfaceCycle(model, interfaceModel, new HashSet<>(visited));
        }
    }

    private void validateConstructor(KClassModel classModel) {
        for (var method : classModel.methods()) {
            if (method.name().equals("<init>")) {
                return;
            }
        }
        Log.temp(classModel.region(), "Class must have constructor");
        throw new Log.KarinaException();
    }

    private KClassModel importClass(JKModel model, KClassModel classModel, ImportTable ctx) {

        var context = importItemsOfClass(model, classModel, ctx);
        for (var generic : classModel.generics()) {
            context = context.addGeneric(classModel.region(), generic);
        }

        for (var kImport : classModel.imports()) {
            context = addImport(model, kImport, context);
        }

        var fields = ImmutableList.<KFieldModel>builder();
        for (var field : classModel.fields()) {
            fields.add(new KFieldModel(
                    field.name(),
                    context.importType(field.region(), field.type()),
                    field.modifiers(),
                    field.region(),
                    field.classPointer()
            ));
        }

        var methods = ImmutableList.<KMethodModel>builder();
        for (var method : classModel.methods()) {
            var expression = method.expression();
//            if (expression != null) {
//                expression = expression.importExpression(model, context);
//            }
            methods.add(new KMethodModel(
                    method.name(),
                    method.modifiers(),
                    method.signature(),
                    method.parameters(),
                    method.generics(),
                    expression,
                    method.region(),
                    method.classPointer()
            ));
        }

        return new KClassModel(
                classModel.name(),
                classModel.path(),
                classModel.modifiers(),
                classModel.superClass(),
                classModel.outerClass(),
                classModel.interfaces(),
                classModel.innerClasses(),
                fields.build(),
                methods.build(),
                classModel.generics(),
                classModel.imports(),
                classModel.permittedSubclasses(),
                classModel.resource(),
                classModel.region()
        );
    }

    private ImportTable importItemsOfClass(JKModel model, ClassModel classModel, ImportTable ctx) {
        var newCtx = ctx;

        var buckets = new HashMap<String, List<MethodPointer>>();
        for (var method : classModel.methods()) {
            if (!Modifier.isStatic(method.modifiers())) {
                continue;
            }
            var bucket = buckets.computeIfAbsent(method.name(), k -> new ArrayList<>());
            bucket.add(method.pointer());
        }

        for (var stringListEntry : buckets.entrySet()) {
            var name = stringListEntry.getKey();
            var methods = stringListEntry.getValue();
            if (buckets.isEmpty()) {
                continue;
            }
            newCtx = newCtx.addStaticMethod(classModel.region(), name, methods, false);
        }
        for (var field : classModel.fields()) {
            if (!Modifier.isStatic(field.modifiers())) {
                continue;
            }
            newCtx = newCtx.addStaticField(classModel.region(), field.name(), field.pointer(), false);
        }
        for (var classPointer : classModel.innerClasses()) {
            var innerClass = model.getClass(classPointer);
            newCtx = newCtx.addClass(classModel.region(), innerClass.name(), innerClass.pointer(), false);
        }

        return newCtx;
    }

    private ImportTable importItemsOfClassByName(JKModel model, ClassModel classModel, ImportTable ctx, String namePredicate, Region importRegion) {
        var newCtx = ctx;

        boolean added = false;

        var buckets = new HashMap<String, List<MethodPointer>>();
        for (var method : classModel.methods()) {
            if (!Modifier.isStatic(method.modifiers()) || !namePredicate.equals(method.name())) {
                continue;
            }
            added = true;
            var bucket = buckets.computeIfAbsent(method.name(), k -> new ArrayList<>());
            bucket.add(method.pointer());
        }

        for (var stringListEntry : buckets.entrySet()) {
            var name = stringListEntry.getKey();
            var methods = stringListEntry.getValue();
            if (buckets.isEmpty()) {
                continue;
            }
            newCtx = newCtx.addStaticMethod(importRegion, name, methods, true);
        }
        for (var field : classModel.fields()) {
            if (!Modifier.isStatic(field.modifiers()) || !namePredicate.equals(field.name())) {
                continue;
            }
            added = true;
            newCtx = newCtx.addStaticField(importRegion, field.name(), field.pointer(), true);
        }

        if (!added) {
            Log.importError(new ImportError.NoItemFound(importRegion, namePredicate, classModel.pointer().path()));
            throw new Log.KarinaException();
        }

        return newCtx;
    }

    private ImportTable addImport(JKModel model, KTree.KImport kImport, ImportTable ctx) {

        var pointer = model.getClassPointer(kImport.path());
        if (pointer == null) {
            Log.importError(new ImportError.NoUnitFound(kImport.region(), kImport.path()));
            throw new Log.KarinaException();
        }
        var modelClass = model.getClass(pointer);
        ImportTable newCtx = ctx;
        switch (kImport.importType()) {
            case TypeImport.All all -> {
                newCtx = importItemsOfClass(model, modelClass, ctx);
            }
            case TypeImport.Base base -> {
                newCtx = newCtx.addClass(kImport.region(), modelClass.name(), modelClass.pointer(), true);
            }
            case TypeImport.Names names -> {
                for (var name : names.names()) {
                    newCtx = importItemsOfClassByName(model, modelClass, ctx, name, kImport.region());
                    //TODO detect if name is found
                }
            }
        }

        return newCtx;
    }


//    private KTree.KPackage importPackage(KTree.KPackage root, KTree.KPackage pkg) {
//
//        var build = KTree.KPackage.builder();
//        build.name(pkg.name());
//        build.path(pkg.path());
//        try (var collector = new ErrorCollector()) {
//            for (var subPackage : pkg.subPackages()) {
//                collector.collect(() ->
//                        build.subPackage(importPackage(root, subPackage))
//                );
//            }
//            for (var unit : pkg.units()) {
//                collector.collect(() ->
//                        build.unit(importUnit(root, unit))
//                );
//            }
//        }
//        return build.build();
//
//    }
//
//    public KTree.KUnit importUnit(KTree.KPackage root, KTree.KUnit unit) {
//
//        var build = KTree.KUnit.builder();
//        build.region(unit.region());
//        build.name(unit.name());
//        build.path(unit.path());
//        build.kImports(unit.kImports());
//        var contextBuilder = ImportContext.getImports(root, unit);
//        build.unitScopeSymbolTable(contextBuilder.build());
//
//        try (var collector = new ErrorCollector()) {
//            for (var item : unit.items()) {
//                collector.collect(() -> {
//                    switch (item) {
//                        case KTree.KFunction kFunction -> {
//                            build.item(ImportingItem.importFunction(root, contextBuilder.copy(), kFunction));
//                        }
//                        case KTree.KStruct kStruct -> {
//                            build.item(
//                                    ImportingItem.importStruct(root, contextBuilder.copy(), kStruct));
//                        }
//                        case KTree.KEnum kEnum -> {
//                            build.item(ImportingItem.importEnum(root, contextBuilder.copy(), kEnum));
//                        }
//                        case KTree.KInterface kImplBlock -> {
//                            build.item(ImportingItem.importInterface(root, contextBuilder.copy(), kImplBlock));
//                        }
//                    }
//                });
//            }
//        }
//        var duplicate = Unique.testUnique(unit.items(), KTree.KItem::name);
//        if (duplicate != null) {
//            Log.importError(new ImportError.DuplicateItem(
//                    duplicate.first().region(),
//                    duplicate.duplicate().region(),
//                    duplicate.value().value()
//            ));
//            throw new Log.KarinaException();
//        }
//
//        return build.build();
//
//    }


}
