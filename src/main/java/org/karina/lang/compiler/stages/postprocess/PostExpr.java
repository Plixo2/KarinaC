package org.karina.lang.compiler.stages.postprocess;

import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.*;
import org.karina.lang.compiler.symbols.CallSymbol;
import org.karina.lang.compiler.utils.*;

import java.util.ArrayList;
import java.util.List;

public class PostExpr {

    public static KExpr rewrite(@NotNull KExpr expr, PostContext context) {
        return switch (expr) {
            case KExpr.Assignment assignment -> {
                var left = rewrite(assignment.left(), context);
                var right = rewrite(assignment.right(), context);
                yield new KExpr.Assignment(
                        assignment.region(),
                        left,
                        right,
                        assignment.symbol()
                );
            }
            case KExpr.Binary binary -> {
                var left = rewrite(binary.left(), context);
                var right = rewrite(binary.right(), context);
                yield new KExpr.Binary(
                        binary.region(),
                        left,
                        binary.operator(),
                        right,
                        binary.symbol()
                );
            }
            case KExpr.Block block -> {
                var items = new ArrayList<KExpr>();
                for (var item : block.expressions()) {
                    items.add(rewrite(item, context));
                }
                yield new KExpr.Block(
                        block.region(),
                        items,
                        block.symbol()
                );
            }
            case KExpr.Boolean aBoolean -> {
                yield aBoolean;
            }
            case KExpr.Branch branch -> {
                var condition = rewrite(branch.condition(), context);
                var thenBranch = rewrite(branch.thenArm(), context);
                KExpr elseBranch = null;
                if (branch.elseArm() != null) {
                    elseBranch = rewrite(branch.elseArm(), context);
                }
                yield new KExpr.Branch(
                        branch.region(),
                        condition,
                        thenBranch,
                        elseBranch,
                        branch.branchPattern(),
                        branch.symbol()
                );
            }
            case KExpr.Break aBreak -> {
                yield aBreak;
            }
            case KExpr.Call call -> {
                var arguments = new ArrayList<KExpr>();
                for (var argument : call.arguments()) {
                    arguments.add(rewrite(argument, context));
                }
                var left = rewrite(call.left(), context);

                CallSymbol symbol = call.symbol();
                if (symbol instanceof CallSymbol.CallDynamic(Span region, KType returnType)) {

                    if (!(call.left().type() instanceof KType.FunctionType functionType)) {
                        Log.temp(call.region(), "Invalid function type");
                        throw new Log.KarinaException();
                    }

                    //static args, aka functions signature
                    var argTypeStatic = new ArrayList<KType>();
                    for (var arg : functionType.arguments()) {
                        argTypeStatic.add(new KType.AnyClass(arg.region()));
                    }

                    KType returnTypeStatic = new KType.AnyClass(region);
                    if (functionType.returnType() == null) {
                        returnTypeStatic = new KType.PrimitiveType.VoidType(region);
                    } else if (functionType.returnType().isVoid()) {
                        returnTypeStatic = new KType.PrimitiveType.VoidType(region);
                    }

                    //interface class type
                    KType.ClassType classType = toClassType(functionType);

                    //path to the interface
                    ObjectPath path = classType.path().value().append("apply");


                    symbol = new CallSymbol.CallInterface(
                            region,
                            classType,
                            path,
                            List.of(), returnType,
                            argTypeStatic,
                            returnTypeStatic
                    );
                }

                //TODO replace dynamic calls with virtual calls
                yield new KExpr.Call(
                        call.region(),
                        left,
                        call.generics(),
                        arguments,
                        symbol
                );
            }
            case KExpr.Cast cast -> {
                var left = rewrite(cast.expression(), context);
                yield new KExpr.Cast(
                        cast.region(),
                        left,
                        cast.asType(),
                        cast.symbol()
                );
            }
            case KExpr.Closure closure -> {
                yield rewriteClosure(closure, context);
            }
            case KExpr.Continue aContinue -> {
                yield aContinue;
            }
            case KExpr.CreateArray createArray -> {
                var elements = new ArrayList<KExpr>();
                for (var item : createArray.elements()) {
                    elements.add(rewrite(item, context));
                }
                yield new KExpr.CreateArray(
                        createArray.region(),
                        createArray.hint(),
                        elements,
                        createArray.symbol()
                );
            }
            case KExpr.CreateObject createObject -> {
                var parameters = new ArrayList<NamedExpression>();
                for (var parameter : createObject.parameters()) {
                    var sExpr = rewrite(parameter.expr(), context);
                    parameters.add(new NamedExpression(
                            parameter.region(),
                            parameter.name(),
                            sExpr,
                            parameter.symbol()
                    ));
                }
                yield new KExpr.CreateObject(
                        createObject.region(),
                        createObject.createType(),
                        parameters,
                        createObject.symbol()
                );
            }
            case KExpr.For aFor -> {
                var iter = rewrite(aFor.iter(), context);
                var body = rewrite(aFor.body(), context);
                yield new KExpr.For(
                        aFor.region(),
                        aFor.name(),
                        iter,
                        body,
                        aFor.symbol()
                );
            }
            case KExpr.GetArrayElement getArrayElement -> {
                var left = rewrite(getArrayElement.left(), context);
                var index = rewrite(getArrayElement.index(), context);
                yield new KExpr.GetArrayElement(
                        getArrayElement.region(),
                        left,
                        index,
                        getArrayElement.elementType()
                );
            }
            case KExpr.GetMember getMember -> {
                var left = rewrite(getMember.left(), context);
                yield new KExpr.GetMember(
                        getMember.region(),
                        left,
                        getMember.name(),
                        getMember.symbol()
                );
            }
            case KExpr.IsInstanceOf isInstanceOf -> {
                var left = rewrite(isInstanceOf.left(), context);
                yield new KExpr.IsInstanceOf(
                        isInstanceOf.region(),
                        left,
                        isInstanceOf.type()
                );
            }
            case KExpr.Literal literal -> {
                yield literal;
            }
            case KExpr.Match match -> {
                throw new NullPointerException("Match not implemented");
            }
            case KExpr.Number number -> {
                yield number;
            }
            case KExpr.Return aReturn -> {
                KExpr value = null;
                if (aReturn.value() != null) {
                    value = rewrite(aReturn.value(), context);
                }
                yield new KExpr.Return(
                        aReturn.region(),
                        value,
                        aReturn.yieldType()
                );
            }
            case KExpr.Self self -> {
                yield self;
            }
            case KExpr.StringExpr stringExpr -> {
                yield stringExpr;
            }
            case KExpr.Throw aThrow -> {
                var value = rewrite(aThrow.value(), context);
                yield new KExpr.Throw(
                        aThrow.region(),
                        value,
                        aThrow.symbol()
                );
            }
            case KExpr.Unary unary -> {
                var left = rewrite(unary.value(), context);
                yield new KExpr.Unary(
                        unary.region(),
                        unary.operator(),
                        left,
                        unary.symbol()
                );
            }
            case KExpr.VariableDefinition variableDefinition -> {
                var left = rewrite(variableDefinition.value(), context);
                yield new KExpr.VariableDefinition(
                        variableDefinition.region(),
                        variableDefinition.name(),
                        variableDefinition.hint(),
                        left,
                        variableDefinition.symbol()
                );
            }
            case KExpr.While aWhile -> {
                var condition = rewrite(aWhile.condition(), context);
                var body = rewrite(aWhile.body(), context);
                yield new KExpr.While(
                        aWhile.region(),
                        condition,
                        body
                );
            }
        };
    }

    public static KType.ClassType toClassType(KType.FunctionType functionType) {
        boolean hasReturn = true;

        if (functionType.returnType() == null) {
            hasReturn = false;
        } else if (functionType.returnType().isVoid()) {
            hasReturn = false;
        }

        var generics = new ArrayList<>(functionType.arguments());

        if (hasReturn) {
            generics.add(functionType.returnType());
        }
        var input = functionType.arguments().size();
        var name = "$FunctionalInterface" + input + "_" + hasReturn;
        var path = new ObjectPath(List.of("src" , "FunctionalInterfaces", name));
        return new KType.ClassType(
                functionType.region(),
                SpanOf.span(functionType.region(), path),
                generics
        );
    }

    private static KExpr rewriteClosure(KExpr.Closure closure, PostContext context) {
        var name = "Closure" + context.getItemCounter();

        var structPath = context.getUnitPath().append(name);

        var fields = new ArrayList<KTree.KField>();
        assert closure.symbol() != null;
        for (var capture : closure.symbol().captures()) {
            fields.add(new KTree.KField(
                    capture.region(),
                    structPath.append(capture.name()),
                    SpanOf.span(capture.region(), capture.name()),
                    capture.type()
            ));
        }
        if (closure.symbol().captureSelf()) {
            assert closure.symbol().self() != null;
            fields.add(new KTree.KField(
                    closure.symbol().self().region(),
                    structPath.append("self"),
                    SpanOf.span(closure.symbol().self().region(), "self"),
                    closure.symbol().self().type()
            ));
        }
        var interfaceToImplement = toClassType(closure.symbol().functionType());

        var params = new ArrayList<KTree.KParameter>();
        var args = closure.symbol().argVariables();
        for (var i = 0; i < args.size(); i++) {
            var argument = args.get(i);
            params.add(new KTree.KParameter(
                    argument.region(),
                    SpanOf.span(argument.region(), "p" + i),
                    argument.type(),
                    argument
            ));
        }

        var returnType = closure.symbol().functionType().returnType();

        var preReplace = new ArrayList<>(context.toReplaceWithSelf);

//        context.toReplaceWithSelf.add()

        var applyFn = new KTree.KFunction(
                closure.region(),
                SpanOf.span(closure.region(), "apply"),
                structPath.append("apply"),
                closure.region(),
                new FunctionModifier(),
                List.of(),
                params,
                returnType,
                List.of(),
                rewrite(closure.body(), context)
        );

        context.toReplaceWithSelf = preReplace;

        var kImplBlock = new KTree.KImplBlock(
                closure.region(),
                interfaceToImplement,
                List.of(applyFn)
        );

        var struct = new KTree.KStruct(
                closure.region(),
                SpanOf.span(closure.region(), name), structPath,
                new StructModifier(),
                List.of(),
                List.of(),
                List.of(),
                fields,
                List.of(kImplBlock)
        );

        context.add(struct);


        return new KExpr.StringExpr(closure.region(), "HI");
    }
}
