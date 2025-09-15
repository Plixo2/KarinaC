package org.karina.lang.compiler.stages.imports;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KFieldModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.stages.imports.table.ImportTable;
import org.karina.lang.compiler.stages.imports.table.UserImportTable;
import org.karina.lang.compiler.utils.*;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;


public class ImportItem {


    /**
     * @param ctx when null, only validate and apply functional interfaces
     */
    public static KClassModel importClass(Context c, KClassModel classModel, @Nullable KClassModel outerClass, ImportTable ctx, ModelBuilder modelBuilder) {

        ImportHelper.testName(c, classModel.region(), classModel.name());
        var logName = "importing class " + classModel.name();
        Log.beginType(Log.LogTypes.IMPORTS, logName);

        ImportTable importTable;
        if (ctx instanceof UserImportTable userImportTable) {
            importTable = getFullUserImportTable(c, classModel, userImportTable);
        } else {
            importTable = ctx;
        }


        //now the import table is ready to import types

        // import generics
        for (var generic : classModel.generics()) {
            importGeneric(c, generic, importTable);
        }

        //start by importing the super class
        var superClass = classModel.superClass();
        assert superClass != null;
        var superClassImportedAny = importTable.importType(classModel.region(), superClass);
        if (!(superClassImportedAny instanceof KType.ClassType superClassImported)) {
            Log.temp(c, classModel.region(), "Invalid Super class");
            throw new Log.KarinaException();
        }

        //importing interfaces
        var interfaces = ImmutableList.<KType.ClassType>builder();
        for (var anInterface : classModel.interfaces()) {
            var imported = importTable.importType(classModel.region(), anInterface);
            if (!(imported instanceof KType.ClassType interfaceImported)) {
                Log.temp(c, classModel.region(), "Invalid Interface");
                throw new Log.KarinaException();
            }
            interfaces.add(interfaceImported);
        }

        //importing fields
        var fields = ImmutableList.<KFieldModel>builder();
        for (var field : classModel.fields()) {
            if (!(field instanceof KFieldModel kField)) {
                Log.temp(c, classModel.region(), "Invalid Field");
                throw new Log.KarinaException();
            }
            fields.add(importField(c, kField, importTable));
        }

        //importing methods
        var methodsToBeFilled = new ArrayList<KMethodModel>();
        for (var method : classModel.methods()) {
            if (!(method instanceof KMethodModel kMethod)) {
                Log.temp(c, classModel.region(), "Invalid Method");
                throw new Log.KarinaException();
            }
            methodsToBeFilled.add(importMethod(c, kMethod, importTable));
        }


        var innerClassesToBeFilled = new ArrayList<KClassModel>();

        UserImportTable toReference;
        if (importTable instanceof UserImportTable toRef) {
              toReference = toRef;
        } else {
            if (classModel.symbolTable() == null) {
                Log.temp(c, classModel.region(), "No symbol table was created");
                throw new Log.KarinaException();
            }
            toReference = classModel.symbolTable();
        }

        var newClassModel = new KClassModel(
                classModel.name(),
                classModel.path(),
                classModel.modifiers(),
                superClassImported,
                outerClass,
                classModel.nestHost(),
                interfaces.build(),
                innerClassesToBeFilled,
                fields.build(),
                methodsToBeFilled,
                classModel.generics(),
                classModel.imports(),
                classModel.permittedSubclasses(),
                new ArrayList<>(classModel.nestMembers()),
                classModel.annotations(),
                classModel.resource(),
                classModel.region(),
                toReference
        );


        //recursively import inner classes, done after, so we can pass the new class model as the outer class
        for (var innerClass : classModel.innerClasses()) {
            // also propagate the full import table, when ctx is not null
            innerClassesToBeFilled.add(importClass(c, innerClass, newClassModel, importTable, modelBuilder));
        }
        modelBuilder.addClass(c, newClassModel);

        Log.endType(Log.LogTypes.IMPORTS, logName);
        return newClassModel;
    }



    private static KFieldModel importField(Context c, KFieldModel field, ImportTable ctx) {
        ImportHelper.testName(c, field.region(), field.name());
        var type = ctx.importType(field.region(), field.type());
        if (type.isVoid()) {
            Log.error(c, new AttribError.NotSupportedType(field.region(), field.type()));
            throw new Log.KarinaException();
        }

        var defaultValue = field.defaultValue();
        if (!field.type().equals(KType.STRING) && defaultValue instanceof String) {
            // remove default value for non-string fields types
            defaultValue = null;
        }

        return new KFieldModel(
                field.name(),
                type,
                field.modifiers(),
                field.region(),
                field.classPointer(),
                defaultValue
        );
    }

    private static KMethodModel importMethod(Context c, KMethodModel method, ImportTable table) {

        ImportHelper.testName(c, method.region(), method.name());

        if (table instanceof UserImportTable userImportTable) {
            //remove generics for static methods from any outer class
            if (Modifier.isStatic(method.modifiers())) {
                userImportTable = userImportTable.removeGenerics();
            }

            //add defined generics
            for (var generic : method.generics()) {
                userImportTable = userImportTable.addGeneric(generic.region(), generic);
            }
            table = userImportTable;
        }


        //Now ready to import the expression

        // import generics
        for (var generic : method.generics()) {
//            if (!generic.bounds().isEmpty()) {
//                Log.temp(c, generic.region(), "Method generics with bounds are not supported yet");
//                throw new Log.KarinaException();
//            }
            importGeneric(c, generic, table);
        }

        // We only expose the resolveType method to the expression import
        var importContext = new ImportContext(c, table);

        //check if the method has an expression, so not an abstract method
        KExpr expression = method.expression();
        if (expression != null) {
            expression = ImportExpr.importExpr(importContext, expression);
        }

        //import return type and parameters
        var signature = importSignature(c, method.region(), method.signature(), table);

        //dont check for '_', as a parameter name, the parameter may be required for a method
        // that must be implemented, but is not in use



        return new KMethodModel(
                method.name(),
                method.modifiers(),
                signature,
                method.parameters(),
                method.generics(),
                expression,
                method.annotations(),
                method.region(),
                method.classPointer(),
                List.of()
        );
    }


    private static Signature importSignature(Context c, Region region, Signature signature, ImportTable ctx) {
        var parameters = ImmutableList.<KType>builder();
        for (var parameter : signature.parameters()) {
            var importedParam = ctx.importType(region, parameter);
            if (importedParam.isVoid()) {
                Log.error(c, new AttribError.NotSupportedType(region, importedParam));
                throw new Log.KarinaException();
            }
            parameters.add(importedParam);
        }
        var returnType = ctx.importType(region, signature.returnType());
        return new Signature(parameters.build(), returnType);
    }


    private static void importGeneric(Context c, Generic generic, ImportTable ctx) {

        List<KType.ClassType> newBounds = new ArrayList<>();
        for (var bound : generic.bounds()) {
            var importedBound = ctx.importType(generic.region(), bound);
            if (!(importedBound instanceof KType.ClassType classType)) {
                Log.error(c, new AttribError.NotSupportedType(generic.region(), importedBound));
                throw new Log.KarinaException();
            }
            newBounds.add(classType);
        }

        KType.ClassType superClass = null;
        if (generic.superType() != null) {
            var importedSuper = ctx.importType(generic.region(), generic.superType());
            if (!(importedSuper instanceof KType.ClassType classType)) {
                Log.error(c, new AttribError.NotSupportedType(generic.region(), importedSuper));
                throw new Log.KarinaException();
            }
            superClass = classType;
        }
        if (superClass == null && !newBounds.isEmpty()) {
            var maybeAClass = newBounds.getFirst();
            var theMaybeClassModel = ctx.model().getClass(maybeAClass.pointer());
            if (!Modifier.isInterface(theMaybeClassModel.modifiers())) {
                superClass = maybeAClass;
                newBounds = newBounds.subList(1, newBounds.size());
            }
        }

        for (var newBound : newBounds) {
            var interfaceBound = ctx.model().getClass(newBound.pointer());
            if (!Modifier.isInterface(interfaceBound.modifiers())) {
                Log.error(c, new AttribError.NotAInterface(generic.region(), newBound));
                throw new Log.KarinaException();
            }
        }
        if (superClass != null) {
            var superClassModel = ctx.model().getClass(superClass.pointer());
            if (Modifier.isInterface(superClassModel.modifiers())) {
                Log.error(c, new AttribError.NotAClass(generic.region(), superClass));
                throw new Log.KarinaException();
            }
        }


        generic.updateBounds(superClass, newBounds);

    }
    /**
     * Adds user defined imports and generics to the import table.
     */
    private static UserImportTable getFullUserImportTable(Context c, KClassModel classModel, UserImportTable ctx) {
        var context = ctx;
        //remove generics for static inner classes
        if (Modifier.isStatic(classModel.modifiers())) {
            context = context.removeGenerics();
        }

        Log.beginType(Log.LogTypes.IMPORTS, "inner items");
        //import all items of the current class
        context = ImportHelper.importItemsOfClass(classModel, context);
        if (Log.LogTypes.IMPORT_STAGES.isVisible()) context.debugImport();
        Log.endType(Log.LogTypes.IMPORTS, "inner items");


        Log.beginType(Log.LogTypes.IMPORTS, "generics");
        //add defined generics
        for (var generic : classModel.generics()) {
            context = context.addGeneric(classModel.region(), generic);
        }

        if (Log.LogTypes.IMPORT_STAGES.isVisible())  context.debugImport();
        Log.endType(Log.LogTypes.IMPORTS, "generics");

        Log.beginType(Log.LogTypes.IMPORTS, "imports statements");
        //add manual imports

        var importTypeSplit = classModel.imports().stream().collect(Collectors.partitioningBy(
                o -> o.importType() instanceof KImport.TypeImport.BaseAs
        ));
        for (var kImport : importTypeSplit.get(false)) {
            context = ImportHelper.addImport(c, classModel.region(), kImport, context);
        }
        for (var kImport : importTypeSplit.get(true)) {
            context = ImportHelper.addImport(c, classModel.region(), kImport, context);
        }

        if (Log.LogTypes.IMPORT_STAGES.isVisible()) context.debugImport();
        Log.endType(Log.LogTypes.IMPORTS, "imports statements");

        if (!Log.LogTypes.IMPORT_STAGES.isVisible())
            context.debugImport();

        return context;
    }


}
