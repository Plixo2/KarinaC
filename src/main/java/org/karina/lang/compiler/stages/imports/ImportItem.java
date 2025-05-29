package org.karina.lang.compiler.stages.imports;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KFieldModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.utils.*;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;


public class ImportItem {



    public static KClassModel importClass(Context c, KClassModel classModel, @Nullable KClassModel outerClass, ImportTable ctx, ModelBuilder modelBuilder) {

        ImportHelper.testName(c, classModel.region(), classModel.name());

        var context = ctx;
        //remove generics for static inner classes
        if (Modifier.isStatic(classModel.modifiers())) {
            context = context.removeGenerics();
        }

        var logName = "importing class " + classModel.name();
        Log.beginType(Log.LogTypes.IMPORTS, logName);


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

        //now the import table is ready to import types

        //start by importing the super class
        var superClass = classModel.superClass();
        assert superClass != null;
        var superClassImportedAny = context.importType(classModel.region(), superClass);
        if (!(superClassImportedAny instanceof KType.ClassType superClassImported)) {
            Log.temp(c, classModel.region(), "Invalid Super class");
            throw new Log.KarinaException();
        }

        //importing interfaces
        var interfaces = ImmutableList.<KType.ClassType>builder();
        for (var anInterface : classModel.interfaces()) {
            var imported = context.importType(classModel.region(), anInterface);
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
            fields.add(importField(c, kField, context));
        }

        //importing methods
        var methodsToBeFilled = new ArrayList<KMethodModel>();
        for (var method : classModel.methods()) {
            if (!(method instanceof KMethodModel kMethod)) {
                Log.temp(c, classModel.region(), "Invalid Method");
                throw new Log.KarinaException();
            }
            methodsToBeFilled.add(importMethod(c, kMethod, context));
        }


        var innerClassesToBeFilled = new ArrayList<KClassModel>();

        var newClassModel = new KClassModel(
                classModel.name(),
                classModel.path(),
                classModel.modifiers(),
                superClassImported,
                outerClass,
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
                context
        );


        //recursively import inner classes, done after, so we can pass the new class model as the outer class
        for (var innerClass : classModel.innerClasses()) {
            innerClassesToBeFilled.add(importClass(c, innerClass, newClassModel, context, modelBuilder));
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
        return new KFieldModel(
                field.name(),
                type,
                field.modifiers(),
                field.region(),
                field.classPointer()
        );
    }

    private static KMethodModel importMethod(Context c, KMethodModel method, ImportTable table) {

        ImportHelper.testName(c, method.region(), method.name());

        //remove generics for static methods from any outer class
        if (Modifier.isStatic(method.modifiers())) {
            table = table.removeGenerics();
        }

        //add defined generics
        for (var generic : method.generics()) {
            table = table.addGeneric(generic.region(), generic);
        }

        //import return type and parameters
        var signature = importSignature(c, method.region(), method.signature(), table);

        //Now ready to import the expression

        //ImportContext is used for this.
        // We only expose the resolveType method to the expression import
        var importContext = new ImportContext(c, table);

        //check if the method has an expression, so not an abstract method
        KExpr expression = method.expression();
        if (expression != null) {
            expression = ImportExpr.importExpr(importContext, expression);
        }

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
                Log.error(c, new AttribError.NotSupportedType(region, parameter));
                throw new Log.KarinaException();
            }
            parameters.add(importedParam);
        }
        var returnType = ctx.importType(region, signature.returnType());
        return new Signature(parameters.build(), returnType);
    }



}
