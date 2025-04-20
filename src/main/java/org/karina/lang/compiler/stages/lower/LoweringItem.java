package org.karina.lang.compiler.stages.lower;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.mutable.MutableInt;
import org.karina.lang.compiler.jvm.model.ModelBuilder;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.jvm.model.table.ClassLookup;
import org.karina.lang.compiler.jvm.model.table.LinearLookup;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LoweringItem {
    public static KClassModel lowerClass(
            Model model,
            KClassModel outerClass,
            KClassModel classModel,
            ModelBuilder builder
    ) {

        var logName = "class-" + classModel.name();
        Log.beginType(Log.LogTypes.LOWERING, logName);
        var fields = ImmutableList.<KFieldModel>builder();
        for (var field : classModel.fields()) {
            //TODO lower field
            if (!(field instanceof KFieldModel kFieldModel)) {
                Log.temp(field.region(), "Invalid field");
                throw new Log.KarinaException();
            }
            fields.add(kFieldModel);
        }

        var methodsToFill = new ArrayList<KMethodModel>();
        var innerToFill = new ArrayList<KClassModel>();
        var classModelNew = new KClassModel(
                classModel.name(),
                classModel.path(),
                classModel.modifiers(),
                classModel.superClass(),
                outerClass,
                classModel.interfaces(),
                innerToFill,
                fields.build(),
                methodsToFill,
                classModel.generics(),
                classModel.imports(),
                classModel.permittedSubclasses(),
                new ArrayList<>(classModel.nestMembers()),
                classModel.annotations(),
                classModel.resource(),
                classModel.region(),
                classModel.symbolTable()
        );


        for (var kClassModel : classModel.innerClasses()) {
            var inner = lowerClass(model, classModelNew, kClassModel, builder);
            innerToFill.add(inner);
        }

        MutableInt syntheticCounter = new MutableInt(0);
        var newClasses = new LinearLookup();
        for (var method : classModel.methods()) {
            if (!(method instanceof KMethodModel kMethodModel)) {
                Log.temp(method.region(), "Invalid method");
                throw new Log.KarinaException();
            }
            var lowerMethod = lowerMethod(model, classModelNew, kMethodModel, syntheticCounter, newClasses);
            methodsToFill.add(lowerMethod);
        }


        for (var kClassModel : newClasses.userClasses()) {
            builder.addClass(kClassModel);
        }

        builder.addClass(classModelNew);
        Log.endType(Log.LogTypes.LOWERING, logName);
        return classModelNew;
    }

    private static KMethodModel lowerMethod(Model model, KClassModel classModel, KMethodModel methodModel, MutableInt synCounter, ClassLookup newClasses) {

        var ctx = new LoweringContext(
                newClasses,
                synCounter,
                model,
                methodModel,
                methodModel,
                classModel,
                classModel,
                List.of()
        );

        var expression = methodModel.expression();
        if (expression != null) {
            expression = LowerExpr.lower(ctx, expression);
        }

        return new KMethodModel(
                methodModel.name(),
                methodModel.modifiers(),
                methodModel.signature(),
                methodModel.parameters(),
                methodModel.generics(),
                expression,
                methodModel.annotations(),
                methodModel.region(),
                methodModel.classPointer(),
                methodModel.getParamVariables()
        );
    }
}
