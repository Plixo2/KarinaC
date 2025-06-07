package org.karina.lang.compiler.stages.attrib;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KFieldModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.utils.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class AttributionItem {


    public static KClassModel attribClass(Context c, Model model, KClassModel outerClass, KClassModel classModel, ModelBuilder modelBuilder) {

        var logName = "class-" + classModel.name();
        Log.beginType(Log.LogTypes.CLASS_NAME, logName);
        var fields = ImmutableList.<KFieldModel>builder();
        for (var field : classModel.fields()) {
            if (field instanceof KFieldModel fieldModel) {
                fields.add(fieldModel);
            }
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
            var inner = attribClass(c, model, classModelNew, kClassModel, modelBuilder);
            innerToFill.add(inner);
        }

        if (classModelNew.symbolTable() == null) {
            Log.temp(c, classModelNew.region(), "No symbol table was created");
            throw new Log.KarinaException();
        }
        var importTable = StaticImportTable.fromImportTable(classModel.pointer(), model, classModelNew.symbolTable());


        try (var fork = c.<KMethodModel>fork()){
            for (var method : classModel.methods()) {
                if (!(method instanceof KMethodModel kMethodModel)) {
                    Log.temp(c, method.region(), "Invalid method");
                    throw new Log.KarinaException();
                }
                fork.collect(subC -> attribMethod(subC, model, classModelNew, importTable, kMethodModel));
            }
            methodsToFill.addAll(fork.dispatch());
        }
        Log.endType(Log.LogTypes.CLASS_NAME, logName);

        modelBuilder.addClass(c, classModelNew);
        return classModelNew;
    }

    public static KMethodModel attribMethod(Context c, Model model, KClassModel classModel, StaticImportTable importTable, KMethodModel methodModel) {

        var logName = "method-" + methodModel.name() + "-" + methodModel.signature().toString() + " in " + classModel.name();
        Log.beginType(Log.LogTypes.METHOD_NAME, logName);
        Log.recordType(Log.LogTypes.METHOD_NAME, "defined here", methodModel.region());

        Variable self = null;
        if (!Modifier.isStatic(methodModel.modifiers())) {
            var classType = classModel.getDefaultClassType();
            self = new Variable(classModel.region(), "<self>", classType, false, true);
        }
        var returnType = methodModel.signature().returnType();

        var parameters = methodModel.signature().parameters();
        var names = methodModel.parameters();
        if (parameters.size() != names.size()) {
            Log.temp(c, methodModel.region(), "Invalid parameters");
            throw new Log.KarinaException();
        }

        var typeChecking = new TypeChecking(model);
        var protection = new ProtectionChecking(model);
        var contextNew = new AttributionContext(
                model,
                c,
                self,
                false,
                methodModel,
                classModel.pointer(),
                returnType,
                new VariableCollection(),
                importTable,
                typeChecking,
                protection
        );

        var variables = new ArrayList<Variable>();
        if (self != null) {
            variables.add(self);
        }

        for (int i = 0; i < parameters.size(); i++) {
            var parameter = parameters.get(i);
            var name = names.get(i);
            var variable = new Variable(
                    methodModel.region(),
                    name,
                    parameter,
                    false,
                    true
            );
            if (name.equals("_")) {
                continue;
            }
            variables.add(variable);
            contextNew = contextNew.addVariable(variable);
        }


        var expression = methodModel.expression();
        if (expression != null) {
            expression = AttributionExpr.attribExpr(returnType, contextNew, expression).expr();
            expression = MethodHelper.createRetuningExpression(expression, returnType, contextNew);
        }

        //TODO this should not be here. Validate before this stage
        if (methodModel.isConstructor()) {
            if (Modifier.isStatic(methodModel.modifiers())) {
                Log.error(c, new AttribError.NotSupportedExpression(
                        methodModel.region(),
                        "Constructor cannot be static"
                ));
                throw new Log.KarinaException();
            } else if (!returnType.isVoid()) {
                Log.error(c, new AttribError.NotSupportedExpression(
                        methodModel.region(),
                        "Constructor must return void"
                ));
                throw new Log.KarinaException();
            }
        }
        Log.endType(Log.LogTypes.METHOD_NAME, logName);

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
                variables
        );
    }


}
