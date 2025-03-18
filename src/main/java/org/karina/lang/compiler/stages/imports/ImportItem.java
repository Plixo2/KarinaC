package org.karina.lang.compiler.stages.imports;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.jvm.model.PhaseDebug;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.Prelude;
import org.karina.lang.compiler.utils.Region;

import java.lang.reflect.Modifier;
import java.util.*;


public class ImportItem {



    public static KClassModel importClass(KClassModel classModel, @Nullable KClassModel outerClass, ImportTable ctx, Prelude prelude) {

        ImportHelper.testName(classModel.region(), classModel.name());

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
        if (Log.LogTypes.IMPORT_STAGES.isVisible()) context.logImport();
        Log.endType(Log.LogTypes.IMPORTS, "inner items");


        Log.beginType(Log.LogTypes.IMPORTS, "generics");
        //add defined generics
        for (var generic : classModel.generics()) {
            context = context.addGeneric(classModel.region(), generic);
        }

        if (Log.LogTypes.IMPORT_STAGES.isVisible())  context.logImport();
        Log.endType(Log.LogTypes.IMPORTS, "generics");

        Log.beginType(Log.LogTypes.IMPORTS, "imports statements");
        //add manual imports
        for (var kImport : classModel.imports()) {
            context = ImportHelper.addImport(classModel.region(), kImport, context);
        }
        if (Log.LogTypes.IMPORT_STAGES.isVisible()) context.logImport();
        Log.endType(Log.LogTypes.IMPORTS, "imports statements");

        if (!Log.LogTypes.IMPORT_STAGES.isVisible())
            context.logImport();

        //now the import table is ready to import types

        //start by importing the super class
        var superClass = classModel.superClass();
        assert superClass != null;
        var superClassImportedAny = context.importType(classModel.region(), superClass);
        if (!(superClassImportedAny instanceof KType.ClassType superClassImported)) {
            Log.temp(classModel.region(), "Invalid Super class");
            throw new Log.KarinaException();
        }

        //importing interfaces
        var interfaces = ImmutableList.<KType.ClassType>builder();
        for (var anInterface : classModel.interfaces()) {
            var imported = context.importType(classModel.region(), anInterface);
            if (!(imported instanceof KType.ClassType interfaceImported)) {
                Log.temp(classModel.region(), "Invalid Interface");
                throw new Log.KarinaException();
            }
            interfaces.add(interfaceImported);
        }

        //importing fields
        var fields = ImmutableList.<KFieldModel>builder();
        for (var field : classModel.fields()) {
            if (!(field instanceof KFieldModel kField)) {
                Log.temp(classModel.region(), "Invalid Field");
                throw new Log.KarinaException();
            }
            fields.add(importField(kField, context));
        }

        //importing methods
        var methodsToBeFilled = new ArrayList<KMethodModel>();
        for (var method : classModel.methods()) {
            if (!(method instanceof KMethodModel kMethod)) {
                Log.temp(classModel.region(), "Invalid Method");
                throw new Log.KarinaException();
            }
            methodsToBeFilled.add(importMethod(kMethod, context));
        }


        var innerClassesToBeFilled = new ArrayList<KClassModel>();

        var newClassModel = new KClassModel(
                PhaseDebug.IMPORTED,
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
            innerClassesToBeFilled.add(importClass(innerClass, newClassModel, context, prelude));
        }

        Log.endType(Log.LogTypes.IMPORTS, logName);
        return newClassModel;
    }

    private static KFieldModel importField(KFieldModel field, ImportTable ctx) {
        ImportHelper.testName(field.region(), field.name());
        var type = ctx.importType(field.region(), field.type());
        if (type.isVoid()) {
            Log.attribError(new AttribError.NotSupportedType(field.region(), field.type()));
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

    private static KMethodModel importMethod(KMethodModel method, ImportTable ctx) {

        ImportHelper.testName(method.region(), method.name());

        var context = ctx;
        //remove generics for static methods from any outer class
        if (Modifier.isStatic(method.modifiers())) {
            context = context.removeGenerics();
        }

        //add defined generics
        for (var generic : method.generics()) {
            context = context.addGeneric(generic.region(), generic);
        }

        //import return type and parameters
        var signature = importSignature(method.region(), method.signature(), context);

        //Now ready to import the expression

        //ImportContext is used for this.
        // We only expose the resolveType method to the expression import
        var importContext = new ImportContext(context);

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
                method.classPointer()
        );
    }


    private static Signature importSignature(Region region, Signature signature, ImportTable ctx) {
        var parameters = ImmutableList.<KType>builder();
        for (var parameter : signature.parameters()) {
            var importedParam = ctx.importType(region, parameter);
            if (importedParam.isVoid()) {
                Log.attribError(new AttribError.NotSupportedType(region, parameter));
                throw new Log.KarinaException();
            }
            parameters.add(importedParam);
        }
        var returnType = ctx.importType(region, signature.returnType());
        return new Signature(parameters.build(), returnType);
    }



}
