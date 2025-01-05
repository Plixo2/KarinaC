package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.*;
import org.karina.lang.compiler.errors.ErrorCollector;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.Unique;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.objects.*;

import java.util.ArrayList;
import java.util.List;

public class ExprImporting {

    static KExpr importExpr(ImportContext ctx, KExpr kExpr) {
        return switch (kExpr) {
            case KExpr.Assignment assignment -> importAssignment(ctx, assignment);
            case KExpr.Binary binary -> importBinary(ctx, binary);
            case KExpr.Block block -> importBlock(ctx, block);
            case KExpr.Boolean aBoolean -> importBoolean(ctx, aBoolean);
            case KExpr.Branch branch -> importBranch(ctx, branch);
            case KExpr.Break aBreak -> importBreak(ctx, aBreak);
            case KExpr.Call call -> importCall(ctx, call);
            case KExpr.Cast cast -> importCast(ctx, cast);
            case KExpr.Closure closure -> importClosure(ctx, closure);
            case KExpr.Continue aContinue -> importContinue(ctx, aContinue);
            case KExpr.CreateArray createArray -> importCreateArray(ctx, createArray);
            case KExpr.CreateObject createObject -> importCreateObject(ctx, createObject);
            case KExpr.For aFor -> importFor(ctx, aFor);
            case KExpr.GetArrayElement getArrayElement -> importGetArrayElement(ctx, getArrayElement);
            case KExpr.GetMember getMember -> importGetMember(ctx, getMember);
            case KExpr.IsInstanceOf isInstanceOf -> importInstanceOf(ctx, isInstanceOf);
            case KExpr.Literal literal -> importLiteral(ctx, literal);
            case KExpr.Match match -> importMatch(ctx, match);
            case KExpr.Number number -> importNumber(ctx, number);
            case KExpr.Return aReturn -> importReturn(ctx, aReturn);
            case KExpr.Self self -> importSelf(ctx, self);
            case KExpr.StringExpr stringExpr -> importStringExpr(ctx, stringExpr);
            case KExpr.Unary unary -> importUnary(ctx, unary);
            case KExpr.VariableDefinition variableDefinition -> importVariableDefinition(ctx, variableDefinition);
            case KExpr.While aWhile -> importWhile(ctx, aWhile);
        };
    }

    private static KExpr importAssignment(ImportContext ctx, KExpr.Assignment expr) {
        var left = importExpr(ctx, expr.left());
        var right = importExpr(ctx, expr.right());
        return new KExpr.Assignment(expr.region(), left, right);
    }

    private static KExpr importBinary(ImportContext ctx, KExpr.Binary expr) {
        var left = importExpr(ctx, expr.left());
        var right = importExpr(ctx, expr.right());
        return new KExpr.Binary(expr.region(), left, expr.operator(), right, null);
    }

    private static KExpr importBlock(ImportContext ctx, KExpr.Block expr) {
        var expressions = new ArrayList<KExpr>();
        try (var collector = new ErrorCollector()) {
            for (var sub : expr.expressions()) {
                collector.collect(() -> {
                    expressions.add(importExpr(ctx, sub));
                });
            }
        }
        return new KExpr.Block(expr.region(), expressions, null);
    }

    private static KExpr importBoolean(ImportContext ctx, KExpr.Boolean expr) {
        return expr;
    }

    private static KExpr importBranch(ImportContext ctx, KExpr.Branch expr) {
        var condition = importExpr(ctx, expr.condition());
        var thenArm = importExpr(ctx, expr.thenArm());
        KExpr elseArm;
        if (expr.elseArm() == null) {
            elseArm = null;
        } else {
            elseArm = importExpr(ctx, expr.elseArm());
        }
        BranchPattern branchPattern;
        if (expr.branchPattern() == null) {
            branchPattern = null;
        } else {
            branchPattern = switch (expr.branchPattern()) {
                case BranchPattern.Cast(var region, KType type, SpanOf<String> castedName, var ignored) -> {
                    var resolved = ctx.resolveType(type, true);
                    yield new BranchPattern.Cast(region, resolved, castedName, null);
                }
                case BranchPattern.Destruct destruct -> {
                    var variables = new ArrayList<NameAndOptType>();
                    for (var variable : destruct.variables()) {
                        KType variableType;
                        if (variable.type() == null) {
                            variableType = null;
                        } else {
                            variableType = ctx.resolveType(variable.type());
                        }
                        variables.add(new NameAndOptType(
                                variable.region(),
                                variable.name(),
                                variableType,
                                null
                        ));
                    }
                    var uniqueVariables = Unique.testUnique(variables, NameAndOptType::name);
                    if (uniqueVariables != null) {
                        Log.importError(new ImportError.DuplicateItem(
                                uniqueVariables.first().name().region(),
                                uniqueVariables.duplicate().name().region(),
                                uniqueVariables.value().value()
                        ));
                        throw new Log.KarinaException();
                    }

                    var resolved = ctx.resolveType(destruct.type(), true);
                    yield new BranchPattern.Destruct(destruct.region(), resolved, variables);
                }
            };
        }
        return new KExpr.Branch(expr.region(), condition, thenArm, elseArm, branchPattern, null);
    }

    private static KExpr importBreak(ImportContext ctx, KExpr.Break expr) {
        return expr;
    }

    private static KExpr importCall(ImportContext ctx, KExpr.Call expr) {
        var left = importExpr(ctx, expr.left());
        var generics = new ArrayList<KType>();
        var arguments = new ArrayList<KExpr>();
        try (var collector = new ErrorCollector()) {
            for (var argument : expr.arguments()) {
                collector.collect(() -> {
                    arguments.add(importExpr(ctx, argument));
                });
            }
            for (var generic : expr.generics()) {
                collector.collect(() -> {
                    generics.add(ctx.resolveType(generic));
                });
            }
        }

        return new KExpr.Call(expr.region(), left, generics, arguments, null);
    }

    private static KExpr importCast(ImportContext ctx, KExpr.Cast expr) {
        var expression = importExpr(ctx, expr.expression());
        var type = ctx.resolveType(expr.asType());
        return new KExpr.Cast(expr.region(), expression, type, null);
    }

    private static KExpr importClosure(ImportContext ctx, KExpr.Closure expr) {
        List<NameAndOptType> args;
        var interfaces = new ArrayList<KType>();
        var returnType = Reference.<KType>of();
        var body = Reference.<KExpr>of();
        try (var collector = new ErrorCollector()) {
            args = ItemImporting.importNameAndOptTypeList(ctx, expr.args(), collector);
            for (var kType : expr.interfaces()) {
                collector.collect(() -> {
                    interfaces.add(ctx.resolveType(kType));
                });
            }

            collector.collect(() -> {
                if (expr.returnType() == null) {
                    returnType.set(null);
                } else {
                    returnType.set(ctx.resolveType(expr.returnType()));
                }
            });

            collector.collect(() -> {
                body.set(importExpr(ctx, expr.body()));
            });
        }

        return new KExpr.Closure(expr.region(), args, returnType.value, interfaces,body.value);
    }

    private static KExpr importContinue(ImportContext ctx, KExpr.Continue expr) {
        return expr;
    }

    private static KExpr importCreateArray(ImportContext ctx, KExpr.CreateArray expr) {
        var elements = new ArrayList<KExpr>();
        try (var collector = new ErrorCollector()) {
            for (var element : expr.elements()) {
                collector.collect(() -> {
                    elements.add(importExpr(ctx, element));
                });
            }
        }
        KType hint;
        if (expr.hint() == null) {
            hint = null;
        } else {
            hint = ctx.resolveType(expr.hint());
        }
        return new KExpr.CreateArray(expr.region(), hint, elements, null);
    }

    private static KExpr importCreateObject(ImportContext ctx, KExpr.CreateObject expr) {
        var createdType = ctx.resolveType(expr.createType(), true);
        var parameters = new ArrayList<NamedExpression>();
        try (var collector = new ErrorCollector()){
            for (var parameter : expr.parameters()) {
                collector.collect(() -> {
                    var name = parameter.name();
                    var value = importExpr(ctx, parameter.expr());
                    parameters.add(new NamedExpression(
                            parameter.region(),
                            name,
                            value
                    ));
                });
            }
        }
        return new KExpr.CreateObject(expr.region(), createdType, parameters, null);
    }

    private static KExpr importFor(ImportContext ctx, KExpr.For expr) {
        var iter = importExpr(ctx, expr.iter());
        var body = importExpr(ctx, expr.body());
        var name = expr.name();
        return new KExpr.For(expr.region(), name, iter, body);
    }

    private static KExpr importLiteral(ImportContext ctx, KExpr.Literal expr) {
        return expr;
    }

    private static KExpr importMatch(ImportContext ctx, KExpr.Match expr) {
        var value = importExpr(ctx, expr.value());
        var cases = new ArrayList<MatchPattern>();
        try (var collector = new ErrorCollector()) {
            for (var aCase : expr.cases()) {
                collector.collect(() -> {
                    var newCase = switch (aCase) {
                        case MatchPattern.Cast cast -> {
                            var type = ctx.resolveType(cast.type(), true);
                            var newName = cast.name();
                            var body = importExpr(ctx, cast.expr());
                            yield new MatchPattern.Cast(
                                    cast.region(),
                                    type,
                                    newName,
                                    body
                            );
                        }
                        case MatchPattern.Destruct destruct -> {
                            var body = importExpr(ctx, destruct.expr());
                            var type = ctx.resolveType(destruct.type(), true);
                            var args = ItemImporting.importNameAndOptTypeList(ctx, destruct.variables(), collector);
                            var uniqueArgs = Unique.testUnique(args, NameAndOptType::name);
                            if (uniqueArgs != null) {
                                Log.importError(new ImportError.DuplicateItem(
                                        uniqueArgs.first().name().region(),
                                        uniqueArgs.duplicate().name().region(),
                                        uniqueArgs.value().value()
                                ));
                                throw new Log.KarinaException();
                            }
                            yield new MatchPattern.Destruct(
                                    destruct.region(),
                                    type,
                                    args,
                                    body);
                        }
                        case MatchPattern.Default aDefault -> {
                            var body = importExpr(ctx, aDefault.expr());
                            yield new MatchPattern.Default(
                                    aDefault.region(),
                                    body
                            );
                        }
                    };
                    cases.add(newCase);
                });
            }
        }
        return new KExpr.Match(expr.region(), value, cases);
    }

    private static KExpr importNumber(ImportContext ctx, KExpr.Number expr) {
        return expr;
    }

    private static KExpr importReturn(ImportContext ctx, KExpr.Return expr) {
        KExpr value;
        if (expr.value() == null) {
            value = null;
        } else {
            value = importExpr(ctx, expr.value());
        }
        return new KExpr.Return(expr.region(), value);
    }

    private static KExpr importStringExpr(ImportContext ctx, KExpr.StringExpr expr) {
        return expr;
    }

    private static KExpr importUnary(ImportContext ctx, KExpr.Unary expr) {
        var value = importExpr(ctx, expr.value());
        return new KExpr.Unary(expr.region(), expr.operator(), value, null);
    }

    private static KExpr importVariableDefinition(ImportContext ctx, KExpr.VariableDefinition expr) {
        var value = importExpr(ctx, expr.value());
        KType hint;
        if (expr.hint() == null) {
            hint = null;
        } else {
            hint = ctx.resolveType(expr.hint());
        }
        var name = expr.name();
        return new KExpr.VariableDefinition(expr.region(), name, hint, value, null);
    }

    private static KExpr importWhile(ImportContext ctx, KExpr.While expr) {
        var condition = importExpr(ctx, expr.condition());
        var body = importExpr(ctx, expr.body());
        return new KExpr.While(expr.region(), condition, body);
    }

    private static KExpr importGetArrayElement(ImportContext ctx, KExpr.GetArrayElement expr) {
        var left = importExpr(ctx, expr.left());
        var index = importExpr(ctx, expr.index());
        return new KExpr.GetArrayElement(expr.region(), left, index, null);
    }

    private static KExpr importGetMember(ImportContext ctx, KExpr.GetMember expr) {
        var left = importExpr(ctx, expr.left());
        return new KExpr.GetMember(expr.region(), left, expr.name(), null);
    }

    private static KExpr importInstanceOf(ImportContext ctx, KExpr.IsInstanceOf expr) {
        var left = importExpr(ctx, expr.left());
        var type = ctx.resolveType(expr.isType(), true);
        return new KExpr.IsInstanceOf(expr.region(), left, type);
    }

    private static KExpr importSelf(ImportContext ctx, KExpr.Self expr) {
        return expr;
    }

}
