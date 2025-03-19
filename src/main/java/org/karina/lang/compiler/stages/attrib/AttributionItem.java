package org.karina.lang.compiler.stages.attrib;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.jvm.model.JKModel;
import org.karina.lang.compiler.jvm.model.PhaseDebug;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class AttributionItem {

    public static KClassModel attribClass(JKModel model, KClassModel outerClass, KClassModel classModel) {

        var logName = "class-" + classModel.name();
        Log.beginType(Log.LogTypes.CLASS_NAME, logName);
        var fields = ImmutableList.<KFieldModel>of();

        var methodsToFill = new ArrayList<KMethodModel>();
        var innerToFill = new ArrayList<KClassModel>();
        var classModelNew = new KClassModel(
                PhaseDebug.TYPED,
                classModel.name(),
                classModel.path(),
                classModel.modifiers(),
                classModel.superClass(),
                outerClass,
                classModel.interfaces(),
                innerToFill,
                fields,
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
        //todo inner classes

        for (var kClassModel : classModel.innerClasses()) {
            var inner = attribClass(model, classModelNew, kClassModel);
            innerToFill.add(inner);
        }

        if (classModelNew.symbolTable() == null) {
            Log.temp(classModelNew.region(), "No symbol table was created");
            throw new Log.KarinaException();
        }
        var importTable = StaticImportTable.fromImportTable(classModel.pointer(), model, classModelNew.symbolTable());

        for (var method : classModel.methods()) {
            if (!(method instanceof KMethodModel kMethodModel)) {
                Log.temp(method.region(), "Invalid method");
                throw new Log.KarinaException();
            }
            var attribMethod = attribMethod(model, classModelNew, importTable, kMethodModel);
            methodsToFill.add(attribMethod);
        }
        Log.endType(Log.LogTypes.CLASS_NAME, logName);

        return classModelNew;
    }



    private static KMethodModel attribMethod(JKModel model, KClassModel classModel, StaticImportTable importTable, KMethodModel methodModel) {

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
            Log.temp(methodModel.region(), "Invalid parameters");
            throw new Log.KarinaException();
        }

        var typeChecking = new TypeChecking(model);
        var protection = new ProtectionChecking(model);
        var contextNew = new AttributionContext(
                model,
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
            contextNew = contextNew.addVariable(variable);
        }


        var expression = methodModel.expression();
        if (expression != null) {
            expression = AttributionExpr.attribExpr(returnType, contextNew, expression).expr();

            var isVoid = returnType.isVoid();


            if (!expression.doesReturn()) {
                if (isVoid) {
                    //we dont care about the yield type, if the method is void
                    expression = new KExpr.Return(
                            methodModel.region(),
                            null,
                            KType.VOID
                    );
                } else {
                    expression = contextNew.makeAssignment(methodModel.region(), returnType, expression);

/*                    if (!typeChecking.canAssign(methodModel.region(), returnType, expression.type(), false)) {
                        Log.attribError(new AttribError.TypeMismatch(
                                methodModel.region(),
                                returnType,
                                expression.type()
                        ));
                        throw new Log.KarinaException();
                    }*/

                    expression = new KExpr.Return(
                            expression.region(),
                            expression,
                            expression.type()
                    );
                }
            }
        }


        if (methodModel.name().equals("<init>")) {
            //test
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
                methodModel.classPointer()
                //TODO add variables
        );
    }



}
