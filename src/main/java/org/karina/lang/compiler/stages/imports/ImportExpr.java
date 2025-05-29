package org.karina.lang.compiler.stages.imports;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.utils.Unique;
import org.karina.lang.compiler.logging.errors.ImportError;
import org.karina.lang.compiler.utils.*;

import java.util.ArrayList;
import java.util.List;

import static org.karina.lang.compiler.stages.imports.ImportTable.ImportGenericBehavior.INSTANCE_CHECK;
import static org.karina.lang.compiler.stages.imports.ImportTable.ImportGenericBehavior.OBJECT_CREATION;

public class ImportExpr {

    public static KExpr importExpr(ImportContext ctx, KExpr kExpr) {

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
            case KExpr.StringInterpolation stringInterpolation -> importStringInterpolation(ctx, stringInterpolation);
            case KExpr.Unary unary -> importUnary(ctx, unary);
            case KExpr.VariableDefinition variableDefinition -> importVariableDefinition(ctx, variableDefinition);
            case KExpr.While aWhile -> importWhile(ctx, aWhile);
            case KExpr.Throw aThrow -> importThrow(ctx, aThrow);
            case KExpr.SpecialCall aSpecialCall -> importSuper(ctx, aSpecialCall);
            case KExpr.Unwrap unwrap -> importUnwrap(ctx, unwrap);
            case KExpr.StaticPath staticPath -> importPath(ctx, staticPath);
        };
    }

    private static KExpr importPath(ImportContext ctx, KExpr.StaticPath staticPath) {

        var region = staticPath.region();
        var path = staticPath.path();
        var pointer = ctx.table().getClassPointerNullable(region, path);
        if (pointer == null) {
            if (path.size() <= 1) {
                ctx.table().logUnknownPointerError(region, path);
                throw new Log.KarinaException();
            }
            var potentialFunctionName = path.last();
            var potentialClassName = path.everythingButLast();
            var potentialClass = ctx.table().getClassPointer(region, potentialClassName);

            return new KExpr.GetMember(
                    region,
                    new KExpr.StaticPath(region, potentialClassName, potentialClass),
                    RegionOf.region(region, potentialFunctionName),
                    true,
                    null
            );

        } else {
            return new KExpr.StaticPath(region, path, pointer);
        }

    }

    private static KExpr importUnwrap(ImportContext ctx, KExpr.Unwrap unwrap) {
        var left = importExpr(ctx, unwrap.left());

        return new KExpr.Unwrap(unwrap.region(), left, null);
    }

    private static KExpr importSuper(ImportContext ctx, KExpr.SpecialCall expr) {

        InvocationType type = switch (expr.invocationType()) {
            case InvocationType.NewInit newInit -> newInit;
            case InvocationType.SpecialInvoke(var name, var tpe) -> {
                yield new InvocationType.SpecialInvoke(
                        name,  ctx.resolveType(expr.region(), tpe)
                );
            }
        };

        return new KExpr.SpecialCall(
                expr.region(),
                type
        );
    }



    private static KExpr importThrow(ImportContext ctx, KExpr.Throw expr) {
        var value = importExpr(ctx, expr.value());
        return new KExpr.Throw(expr.region(), value);
    }

    private static KExpr importAssignment(ImportContext ctx, KExpr.Assignment expr) {
        var left = importExpr(ctx, expr.left());
        var right = importExpr(ctx, expr.right());
        return new KExpr.Assignment(expr.region(), left, right, null);
    }

    private static KExpr importBinary(ImportContext ctx, KExpr.Binary expr) {
        var left = importExpr(ctx, expr.left());
        var right = importExpr(ctx, expr.right());
        return new KExpr.Binary(expr.region(), left, expr.operator(), right, null);
    }

    private static KExpr importBlock(ImportContext ctx, KExpr.Block expr) {
        List<KExpr> expressions;
        try (var fork = ctx.intoContext().<KExpr>fork()) {
            for (var sub : expr.expressions()) {
                fork.collect(subC -> {
                    return importExpr(ctx.withNewContext(subC), sub);
                });
            }
            expressions = fork.dispatch();
        }
        return new KExpr.Block(expr.region(), expressions, null, false);
    }

    private static KExpr importBoolean(ImportContext ctx, KExpr.Boolean expr) {
        return expr;
    }

    private static KExpr importBranch(ImportContext ctx, KExpr.Branch expr) {
        var condition = importExpr(ctx, expr.condition());
        var thenArm = importExpr(ctx, expr.thenArm());
        ElsePart elseArm;
        if (expr.elseArm() == null) {
            elseArm = null;
        } else {
            var elseExpr = importExpr(ctx, expr.elseArm().expr());
            BranchPattern falseBranchPattern;
            if (expr.elseArm().elsePattern() == null) {
                falseBranchPattern = null;
            } else {
                if (expr.branchPattern() == null) {
                    Log.temp(ctx, expr.condition().region(), "Missing primary branch pattern");
                    throw new Log.KarinaException();
                }

                falseBranchPattern = importBranchPatterns(ctx, expr.elseArm().elsePattern());
            }
            elseArm = new ElsePart(elseExpr, falseBranchPattern);
        }

        BranchPattern trueBranchPattern;
        if (expr.branchPattern() == null) {
            trueBranchPattern = null;
        } else {
            trueBranchPattern = importBranchPatterns(ctx, expr.branchPattern());
        }

        return new KExpr.Branch(expr.region(), condition, thenArm, elseArm, trueBranchPattern, null);
    }

    private static BranchPattern importBranchPatterns(ImportContext ctx, BranchPattern branchPattern) {
        return switch (branchPattern) {
            case BranchPattern.Cast(var region, KType type, RegionOf<String> castedName, var ignored) -> {
                var resolved = ctx.resolveType(region, type, INSTANCE_CHECK);
                yield new BranchPattern.Cast(region, resolved, castedName, null);
            }
            case BranchPattern.Destruct destruct -> {
                var variables = new ArrayList<NameAndOptType>();
                for (var variable : destruct.variables()) {
                    KType variableType;
                    if (variable.type() == null) {
                        variableType = null;
                    } else {
                        variableType = ctx.resolveType(destruct.region(), variable.type());
                    }
                    var optType = new NameAndOptType(variable.region(), variable.name(), variableType, null);
                    variables.add(optType);
                }
                var uniqueVariables = Unique.testUnique(variables, NameAndOptType::name);
                if (uniqueVariables != null) {
                    Log.error(ctx, new ImportError.DuplicateItem(
                            uniqueVariables.first().name().region(),
                            uniqueVariables.duplicate().name().region(),
                            uniqueVariables.value().value()
                    ));
                    throw new Log.KarinaException();
                }

                var resolved = ctx.resolveType(destruct.region(), destruct.type(), INSTANCE_CHECK);
                yield new BranchPattern.Destruct(destruct.region(), resolved, variables);
            }
            case BranchPattern.JustType justType -> {
                var resolved = ctx.resolveType(justType.region(), justType.type(), INSTANCE_CHECK);
                yield new BranchPattern.JustType(justType.region(), resolved);
            }
        };
    }

    private static KExpr importBreak(ImportContext ctx, KExpr.Break expr) {
        return expr;
    }

    private static KExpr importCall(ImportContext ctx, KExpr.Call expr) {
        var left = importExpr(ctx, expr.left());
        var generics = new ArrayList<KType>();
        List<KExpr> arguments;
        try (var fork = ctx.intoContext().<KExpr>fork()) {
            for (var argument : expr.arguments()) {
                fork.collect(subC -> {
                    return importExpr(ctx.withNewContext(subC), argument);
                });
            }
            arguments = fork.dispatch();
        }
        for (var generic : expr.generics()) {
            generics.add(ctx.resolveType(expr.region(), generic));
        }

        return new KExpr.Call(expr.region(), left, generics, arguments, null);
    }

    private static KExpr importCast(ImportContext ctx, KExpr.Cast expr) {
        var expression = importExpr(ctx, expr.expression());
        var cast = switch (expr.cast()) {
            case CastTo.AutoCast c -> c;
            case CastTo.CastToType(var to) -> {
                //dont use INSTANCE_CHECK as hint, we only allow casting to a super type, not a subtype
                //or for primitive types
                var type = ctx.resolveType(expr.region(), to);
                yield new CastTo.CastToType(type);
            }
        };
        return new KExpr.Cast(expr.region(), expression, cast, null);
    }

    private static KExpr importClosure(ImportContext ctx, KExpr.Closure expr) {
        List<NameAndOptType> args;
        List<KType> interfaces;

        try (var fork = ctx.intoContext().<KType>fork()) {
            args = ImportHelper.importNameAndOptTypeList(ctx, expr.args());
            for (var kType : expr.interfaces()) {
                fork.collect(subC -> {
                    return ctx.withNewContext(subC).resolveType(expr.region(), kType);
                });
            }
            interfaces = fork.dispatch();
        }
        KType returnType = null;
        if (expr.returnType() != null) {
            returnType = ctx.resolveType(expr.region(), expr.returnType());
        }
        var body = importExpr(ctx, expr.body());

        return new KExpr.Closure(expr.region(), args, returnType, interfaces, body, null);
    }


    private static KExpr importContinue(ImportContext ctx, KExpr.Continue expr) {
        return expr;
    }

    private static KExpr importCreateArray(ImportContext ctx, KExpr.CreateArray expr) {
        List<KExpr> elements;
        try (var fork = ctx.intoContext().<KExpr>fork()) {
            for (var element : expr.elements()) {
                fork.collect(subC -> {
                    return importExpr(ctx.withNewContext(subC), element);
                });
            }
            elements = fork.dispatch();
        }
        KType hint;
        if (expr.hint() == null) {
            hint = null;
        } else {
            hint = ctx.resolveType(expr.region(), expr.hint());
        }
        return new KExpr.CreateArray(expr.region(), hint, elements, null);
    }

    private static KExpr importCreateObject(ImportContext ctx, KExpr.CreateObject expr) {
        var createdType = ctx.resolveType(expr.region(), expr.createType(), OBJECT_CREATION);
        List<NamedExpression> parameters;
        try (var collector = ctx.intoContext().<NamedExpression>fork()){
            for (var parameter : expr.parameters()) {
                collector.collect(subC -> {
                    var name = parameter.name();
                    var value = importExpr(ctx.withNewContext(subC), parameter.expr());
                    return new NamedExpression(
                            parameter.region(),
                            name,
                            value,
                            null
                    );
                });
            }
            parameters = collector.dispatch();
        }
        return new KExpr.CreateObject(expr.region(), createdType, parameters);
    }

    private static KExpr importFor(ImportContext ctx, KExpr.For expr) {
        var iter = importExpr(ctx, expr.iter());
        var body = importExpr(ctx, expr.body());
        var varPart = expr.varPart();

        if (varPart.type() != null) {
            var importedType = ctx.resolveType(varPart.region(), varPart.type());
            varPart = new NameAndOptType(varPart.region(), varPart.name(), importedType, null);
        }

        return new KExpr.For(expr.region(), varPart, iter, body, null);
    }

    private static KExpr importLiteral(ImportContext ctx, KExpr.Literal expr) {
        return expr;
    }

    private static KExpr importMatch(ImportContext ctx, KExpr.Match expr) {
        var value = importExpr(ctx, expr.value());
        List<MatchPattern> cases;
        try (var collector = ctx.intoContext().<MatchPattern>fork()) {
            for (var aCase : expr.cases()) {
                collector.collect(subC -> {
                    var newContext = ctx.withNewContext(subC);
                    return switch (aCase) {
                        case MatchPattern.Cast cast -> {
                            var type = newContext.resolveType(cast.region(), cast.type(), INSTANCE_CHECK);
                            var newName = cast.name();
                            var body = importExpr(newContext, cast.expr());
                            yield new MatchPattern.Cast(
                                    cast.region(),
                                    type,
                                    newName,
                                    body
                            );
                        }
                        case MatchPattern.Destruct destruct -> {
                            var body = importExpr(newContext, destruct.expr());
                            var type = newContext.resolveType(destruct.region(), destruct.type(), INSTANCE_CHECK);
                            var args = new ArrayList<NameAndOptType>();
                            var uniqueArgs = Unique.testUnique(args, NameAndOptType::name);
                            if (uniqueArgs != null) {
                                Log.error(newContext, new ImportError.DuplicateItem(
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
                            var body = importExpr(newContext, aDefault.expr());
                            yield new MatchPattern.Default(
                                    aDefault.region(),
                                    body
                            );
                        }
                    };
                });
            }
            cases = collector.dispatch();
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
        return new KExpr.Return(expr.region(), value, null);
    }

    private static KExpr importStringExpr(ImportContext ctx, KExpr.StringExpr expr) {
        return expr;
    }

    private static KExpr importStringInterpolation(ImportContext ctx, KExpr.StringInterpolation expr) {
        return expr;
    }


    private static KExpr importUnary(ImportContext ctx, KExpr.Unary expr) {
        var value = importExpr(ctx, expr.value());
        return new KExpr.Unary(expr.region(), expr.operator(), value, null);
    }

    private static KExpr importVariableDefinition(ImportContext ctx, KExpr.VariableDefinition expr) {
        var value = importExpr(ctx, expr.value());
        KType hint;
        if (expr.varHint() == null) {
            hint = null;
        } else {
            hint = ctx.resolveType(expr.region(), expr.varHint());
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
        return new KExpr.GetMember(expr.region(), left, expr.name(), expr.isNextACall(), null);
    }

    private static KExpr importInstanceOf(ImportContext ctx, KExpr.IsInstanceOf expr) {
        var left = importExpr(ctx, expr.left());
        var type = ctx.resolveType(expr.region(), expr.isType(), INSTANCE_CHECK);
        return new KExpr.IsInstanceOf(expr.region(), left, type);
    }

    private static KExpr importSelf(ImportContext ctx, KExpr.Self expr) {
        return expr;
    }

}
