package org.karina.lang.compiler.stages.lower;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KFieldModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.model_api.impl.table.ClassLookup;
import org.karina.lang.compiler.model_api.impl.table.LinearLookup;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Types;
import org.karina.lang.compiler.utils.logging.Logging;
import org.karina.lang.compiler.utils.symbols.CallSymbol;
import org.karina.lang.compiler.utils.symbols.CastSymbol;
import org.karina.lang.compiler.utils.symbols.LiteralSymbol;
import org.karina.lang.compiler.utils.*;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class LoweringItem {
    public static KClassModel lowerClass(
            Context c,
            Model model,
            KClassModel outerClass,
            KClassModel classModel,
            ModelBuilder builder
    ) {
        try(var _ = c.section(Logging.Lowering.Classes.class, classModel.name())) {
            var fields = ImmutableList.<KFieldModel>builder();
            for (var field : classModel.fields()) {
                //TODO lower field
                if (!(field instanceof KFieldModel kFieldModel)) {
                    Log.temp(c, field.region(), "Invalid field");
                    throw new Log.KarinaException();
                }
                fields.add(kFieldModel);
            }

            var methodsToFill = new ArrayList<KMethodModel>();
            var innerToFill = new ArrayList<KClassModel>();
            var classModelNew = new KClassModel(
                    classModel.name(), classModel.path(), classModel.modifiers(),
                    classModel.superClass(), outerClass, classModel.nestHost(),
                    classModel.interfaces(), innerToFill, fields.build(), methodsToFill,
                    classModel.generics(), classModel.imports(), classModel.permittedSubclasses(),
                    classModel.nestMembersMutable(), classModel.annotations(),
                    classModel.resource(), classModel.region(), classModel.symbolTable()
            );


            for (var kClassModel : classModel.innerClasses()) {
                var inner = lowerClass(c, model, classModelNew, kClassModel, builder);
                innerToFill.add(inner);
            }

            MutableInt syntheticCounter = new MutableInt(0);
            var newClasses = new LinearLookup();
            for (var method : classModel.methods()) {
                if (!(method instanceof KMethodModel kMethodModel)) {
                    Log.temp(c, method.region(), "Invalid method");
                    throw new Log.KarinaException();
                }
                var lowerMethod =
                        lowerMethod(
                                c, model, classModelNew, kMethodModel, syntheticCounter, newClasses,
                                List.of()
                        );
                methodsToFill.add(lowerMethod);
            }
            methodsToFill.addAll(createBridgeMethods(c, model, classModelNew));

            for (var kClassModel : newClasses.userClasses()) {
                builder.addClass(c, kClassModel);
            }

            builder.addClass(c, classModelNew);


            return classModelNew;
        }
    }

    public static KMethodModel lowerMethod(
            Context c,
            Model model,
            KClassModel classModel,
            KMethodModel methodModel,
            MutableInt synCounter,
            ClassLookup newClasses,
            List<LoweringContext.ClosureReplacement> replacements
    ) {

        var ctx = new LoweringContext(
                newClasses,
                synCounter,
                model,
                c,
                methodModel,
                methodModel,
                classModel,
                classModel,
                replacements
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


    public static List<KMethodModel> createBridgeMethods(Context c, Model model, KClassModel classModel) {

        if (Modifier.isAbstract(classModel.modifiers())) {
            return List.of();
        }

        var classType = classModel.getDefaultClassType();

        //TODO final test if everything is implemented, (mainly test for bugs in the lower stage, can be removed later)
        {
            var notImplemented = MethodHelper.getMethodsToImplementForClass(c, model, classType);
            for (var methodToImplement : notImplemented) {
                Log.temp(c,
                        classModel.region(), "Method '" + methodToImplement.toReadableString() +
                                "' is not implemented from class " +
                                methodToImplement.originalMethodPointer().classPointer()
                );
            }
            if (!notImplemented.isEmpty()) {
                throw new Log.KarinaException();
            }
        }

        var toImplement = MethodHelper.getMethodForBridgeConstruction(c, model, classType);
        if (!toImplement.isEmpty()) {

            var methods = new ArrayList<KMethodModel>();
            for (var methodToImplement : toImplement) {
                var methodModel = model.getMethod(methodToImplement.originalMethodPointer());

                var paramsErased = methodModel.signature().parametersErased();
                var returnTypeErased = Types.erase(methodModel.signature().returnType());

                var foundMethod =
                        isImplemented(methodToImplement.name(), paramsErased, returnTypeErased, classModel.methods());
                if (foundMethod != null) {
                    if (methodToImplement.implementing() == null) {
                        Log.temp(c, methodModel.region(),
                                "Internal error, missing information for implementing method " + methodToImplement.name()
                        );
                        throw new Log.KarinaException();
                    }
                    if (!methodToImplement.implementing().originalMethodPointer().equals(foundMethod.pointer())) {
                        Log.temp(c, foundMethod.region(),
                                "Cannot create bridge method, method " + foundMethod.name() +
                                        " already exists with the same signature"
                        );
                        throw new Log.KarinaException();
                    } else {
                        continue;
                    }
                }


                var bridgeMethod =
                        createBridgeMethod(
                                c, model, classModel, methodToImplement, paramsErased,
                                returnTypeErased
                        );
                if (!doesMethodWithSameSignatureExist(bridgeMethod, methods)) {
                    methods.add(bridgeMethod);
                }
            }
            return methods;

        }

        return List.of();

    }

    public static boolean doesMethodWithSameSignatureExist(MethodModel methodModel, List<? extends MethodModel> existing) {

        var selfErased = methodModel.erasedParameters();
        for (var existingMethodModel : existing) {
            if (!existingMethodModel.name().equals(methodModel.name())) {
                continue;
            }
            var erased = existingMethodModel.erasedParameters();
            if (Types.signatureEquals(selfErased, erased)) {
                var returnErased = Types.erase(existingMethodModel.signature().returnType());
                var existingReturnErased = Types.erase(methodModel.signature().returnType());
                if (Types.erasedEquals(returnErased, existingReturnErased)) {
                    return true;
                }
            }
        }
        return false;

    }

    private static KMethodModel createBridgeMethod(
            Context c,
            Model model,
            KClassModel currentClass,
            MethodHelper.MethodToImplement implement,
            List<KType> params,
            KType returnType
    ) {

        if (implement.implementing() == null) {
            Log.temp(c, currentClass.region(), "Internal error, missing information for implementing method " + implement.name());
            throw new Log.KarinaException();
        }
        var reference = model.getMethod(implement.implementing().originalMethodPointer());

        var flags = Opcodes.ACC_PUBLIC | Opcodes.ACC_BRIDGE | Opcodes.ACC_SYNTHETIC;


        List<Variable> variables = new ArrayList<>();

        var classType = currentClass.getDefaultClassType();
        Variable self;
        variables.add(self = new Variable(
                reference.region(),
                "self", classType, false, true
        ));

        for (var i = 0; i < reference.parameters().size(); i++) {
            var name = reference.parameters().get(i);
            var type = params.get(i);
            variables.add(new Variable(
                    reference.region(),
                    name,
                    type,
                    false,
                    true
            ));
        }



        List<KExpr> callArgs = new ArrayList<>();
        for (var i = 0; i < reference.parameters().size(); i++) {
            var name = reference.parameters().get(i);
            var variable = variables.get(1 + i);
            var expectedType = reference.signature().parameters().get(i);
            var currentArg = variable.type();

            KExpr expr = new KExpr.Literal(reference.region(), name,
                    new LiteralSymbol.VariableReference(
                            variable.region(), variable
                    )
            );
            if (!Types.erasedEquals(currentArg, expectedType)) {
                expr = new KExpr.Cast(
                        reference.region(),
                        expr,
                        new CastTo.CastToType(expectedType),
                        new CastSymbol.PrimitiveCast.UpCast(
                                KType.ROOT,
                                expectedType
                        )
                );
            }
            callArgs.add(expr);
        }


        var callSymbol = new CallSymbol.CallVirtual(
                reference.pointer(),
                List.of(),
                reference.signature().returnType(),
                Modifier.isInterface(model.getClass(reference.classPointer()).modifiers())
        );



        KExpr expression = new KExpr.Call(
                reference.region(),
                new KExpr.Self(reference.region(), self),
                List.of(),
                callArgs,
                callSymbol
        );
        if (reference.signature().returnType().isVoid()) {
            expression = new KExpr.Block(
                    reference.region(),
                    List.of(
                            expression,
                            new KExpr.Return(reference.region(), null, KType.NONE)
                    ),
                    KType.NONE,
                    true
            );
        } else {
            expression = new KExpr.Return(reference.region(), expression, reference.signature().returnType());
        }


        return new KMethodModel(
                implement.name(),
                flags,
                new Signature(ImmutableList.copyOf(params), returnType),
                reference.parameters(),
                reference.generics(),
                expression,
                ImmutableList.of(),
                reference.region(),
                currentClass.pointer(),
                variables
        );
    }

    // returns the return type when found so it can be checked if its the same
    private static @Nullable MethodModel isImplemented(String name, List<KType> params, KType returnType, List<? extends MethodModel> methodModels) {

        for (var methodModel : methodModels) {
            if (!methodModel.name().equals(name)) {
                continue;
            }

            var erasedReturnType = Types.erase(methodModel.signature().returnType());

            if (!Types.erasedEquals(erasedReturnType, returnType)) {
                continue;
            }

            if (Types.signatureEquals(methodModel.signature().parametersErased(), params)) {
                return methodModel;
            }
        }

        return null;

    }

}
