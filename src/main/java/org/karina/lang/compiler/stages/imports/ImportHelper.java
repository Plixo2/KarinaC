package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.logging.ErrorCollector;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.Unique;
import org.karina.lang.compiler.logging.errors.ImportError;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.KImport;
import org.karina.lang.compiler.utils.NameAndOptType;
import org.karina.lang.compiler.utils.Prelude;
import org.karina.lang.compiler.utils.Region;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class ImportHelper {

    //TODO rename methods
    /**
     * Import all static items of a class, so inner classes and static fields and methods
     * @param classModel the class to import
     * @param ctx previous import table to build upon
     * @return the new import table with the items added
     */
    public static ImportTable importItemsOfClass(ClassModel classModel, ImportTable ctx) {
        var newCtx = ctx;

        //create 'buckets' for each method name, so they can be added all at once
        var buckets = buckets(classModel.methods(), _ -> true);

        //add all method buckets to the import table
        for (var methodBucket : buckets.entrySet()) {
            var name = methodBucket.getKey();
            var methods = methodBucket.getValue();
            if (methods.isEmpty()) {
                continue;
            }
            newCtx = newCtx.addStaticMethod(classModel.region(), name, classModel.pointer(), false, false);
        }

        //add all static fields
        for (var field : classModel.fields()) {
            if (!Modifier.isStatic(field.modifiers())) {
                continue;
            }
            newCtx = newCtx.addStaticField(classModel.region(), field.name(), field.pointer(), false, false);
        }

        //add all inner classes
        for (var classPointer : classModel.innerClasses()) {
            newCtx = newCtx.addClass(classModel.region(), classPointer.name(), classPointer.pointer(), false, false);
        }

        return newCtx;
    }

    /**
     * Import all items of the prelude
     * @param owner the class that is currently importing the prelude,
     *              since we want to point to the start of the current file, if a error occurs
     */
    public static ImportTable importPrelude(ClassModel owner, ImportTable ctx, Prelude prelude) {
        Log.recordType(Log.LogTypes.IMPORT_PRELUDE, "importing prelude with ");
        Log.recordType(Log.LogTypes.IMPORT_PRELUDE, prelude.classes().size() + " classes");
        Log.recordType(Log.LogTypes.IMPORT_PRELUDE, prelude.staticFields().size() + " static fields");
        Log.recordType(Log.LogTypes.IMPORT_PRELUDE, prelude.staticMethods().size() + " static methods");
        Log.recordType(Log.LogTypes.IMPORT_PRELUDE, (prelude.classes().size() + prelude.staticFields().size() + prelude.staticMethods().size()) + " items");

        var newCtx = ctx;
        for (var classPointer : prelude.classes()) {
            var classModel = ctx.model().getClass(classPointer);
            newCtx = newCtx.addClass(owner.region(), classModel.name(), classPointer, false, true);
        }
        for (var fieldPointer : prelude.staticFields()) {
            var fieldModel = ctx.model().getField(fieldPointer);
            newCtx = newCtx.addStaticField(owner.region(), fieldModel.name(), fieldPointer, false, true);
        }

        var buckets = buckets(prelude.staticMethods().stream().map(ref -> ctx.model().getMethod(ref)).toList(), _ -> true);
        for (var stringListEntry : buckets.entrySet()) {
            var name = stringListEntry.getKey();
            var methods = stringListEntry.getValue();
            if (methods.isEmpty()) {
                continue;
            }
            newCtx = newCtx.addPreludeMethods(owner.region(), name, methods);
        }

        return newCtx;
    }

    private static ImportTable importItemsOfClassByName(ClassModel classModel, ImportTable ctx, String namePredicate, Region importRegion) {
        var newCtx = ctx;

        boolean added = false;


        var method =
                classModel.methods().stream().anyMatch(ref -> ref.name().equals(namePredicate));
        if (method) {
            newCtx = newCtx.addStaticMethod(importRegion, namePredicate, classModel.pointer(), true, false);
            added = true;
        }

        for (var field : classModel.fields()) {
            if (!Modifier.isStatic(field.modifiers()) || !namePredicate.equals(field.name())) {
                continue;
            }
            added = true;
            newCtx = newCtx.addStaticField(importRegion, field.name(), field.pointer(), true, false);
        }

        //TODO should we allow this?
        for (var inner : classModel.innerClasses()) {
            if (inner.name().equals(namePredicate)) {
                newCtx = newCtx.addClass(classModel.region(), inner.name(), inner.pointer(), true, false);
                added = true;
                break;
            }
        }


        if (!added) {
            Log.importError(new ImportError.NoItemFound(importRegion, namePredicate, classModel.pointer().path()));
            throw new Log.KarinaException();
        }

        return newCtx;
    }



    public static ImportTable addImport(Region region, KImport kImport, ImportTable ctx) {

        var pointer = ctx.model().getClassPointer(region, kImport.path());
        if (pointer == null) {
            //TODO add suggestions
            Log.importError(new ImportError.NoClassFound(kImport.region(), kImport.path()));
            throw new Log.KarinaException();
        }
        var modelClass = ctx.model().getClass(pointer);
        ImportTable newCtx = ctx;
        switch (kImport.importType()) {
            case KImport.TypeImport.All all -> {
                //inner classes and static fields and methods
                newCtx = importItemsOfClass(modelClass, ctx);
            }
            case KImport.TypeImport.Base base -> {
                //only the class itself
                newCtx = newCtx.addClass(kImport.region(), modelClass.name(), modelClass.pointer(), true, false);
            }
            case KImport.TypeImport.Names names -> {
                for (var name : names.names()) {
                    //all items by name
                    newCtx = importItemsOfClassByName(modelClass, newCtx, name, kImport.region());
                }
            }
            case KImport.TypeImport.BaseAs baseAs -> {
                var name = modelClass.name();
                if (!baseAs.alias().toLowerCase().contains(name.toLowerCase())) {
                    Log.importError(new ImportError.InvalidAlias(
                            baseAs.region(),
                            baseAs.alias(),
                            modelClass.name()
                    ));
                    throw new Log.KarinaException();
                }
                if (!newCtx.classes().containsKey(name)) {
                    Log.importError(new ImportError.UnnecessaryAlias(
                            baseAs.region(),
                            baseAs.alias()
                    ));
                    throw new Log.KarinaException();
                }

                newCtx = newCtx.addClass(kImport.region(),baseAs.alias(), modelClass.pointer(), true, false);
            }
        }

        return newCtx;
    }



    public static List<NameAndOptType> importNameAndOptTypeList(ImportContext ctx, List<NameAndOptType> list, ErrorCollector collector) {
        var result = new ArrayList<NameAndOptType>();
        for (var item : list) {
            collector.collect(() -> {
                KType type;
                if (item.type() == null) {
                    type = null;
                } else {
                    type = ctx.resolveType(item.region(), item.type());
                }
                result.add(new NameAndOptType(
                        item.region(),
                        item.name(),
                        type,
                        null
                ));
            });
        }
        collector.collect(() -> {
            var uniqueArgs = Unique.testUnique(result, NameAndOptType::name);
            if (uniqueArgs != null) {
                Log.importError(new ImportError.DuplicateItem(
                        uniqueArgs.first().name().region(),
                        uniqueArgs.duplicate().name().region(),
                        uniqueArgs.value().value()
                ));
                throw new Log.KarinaException();
            }
        });

        return result;

    }

    public static HashMap<String, List<MethodPointer>> buckets(
            List<? extends MethodModel> methods, Predicate<String> predicate) {
        var buckets = new HashMap<String, List<MethodPointer>>();
        for (var method : methods) {
            if (!Modifier.isStatic(method.modifiers()) || !predicate.test(method.name())) {
                continue;
            }
            var bucket = buckets.computeIfAbsent(method.name(), k -> new ArrayList<>());
            bucket.add(method.pointer());
        }
        return buckets;
    }

    public static void testName(Region region, String name) {
        if (name.equals("_")) {
            Log.invalidName(region, name);
            throw new Log.KarinaException();
        }
    }

    public static void logFullModel(Model model) {
        if (!Log.LogTypes.LOADED_CLASSES.isVisible()) {
            return;
        }

        Log.begin("full-model");

        Log.begin("JVM Classes");
        var jvmClasses = model.getBinaryClasses();
        for (var userClass : jvmClasses) {
            Log.record(userClass.pointer());
        }
        Log.end("JVM Classes", jvmClasses.size());

        Log.begin("User Classes");
        var userClasses = model.getUserClasses();
        for (var userClass : userClasses) {
            Log.record(userClass.pointer());
        }
        Log.end("User Classes", userClasses.size());


        Log.end("full-model", model.getClassCount());
    }

}
