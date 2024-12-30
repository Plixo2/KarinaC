package org.karina.lang.compiler.stages.attrib;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.BranchPattern;
import org.karina.lang.compiler.Generic;
import org.karina.lang.compiler.NamedExpression;
import org.karina.lang.compiler.Variable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;

import java.util.*;

public class ExprAttribution {

    static AttribExpr attribExpr(@Nullable KType hint, AttributionContext ctx, KExpr expr) {
        return switch (expr) {
            case KExpr.Assignment assignment -> attribAssignment(hint, ctx, assignment);
            case KExpr.Binary binary -> attribBinary(hint, ctx, binary);
            case KExpr.Block block -> attribBlock(hint, ctx, block);
            case KExpr.Boolean aBoolean -> attribBoolean(hint, ctx, aBoolean);
            case KExpr.Branch branch -> attribBranch(hint, ctx, branch);
            case KExpr.Break aBreak -> attribBreak(hint, ctx, aBreak);
            case KExpr.Call call -> attribCall(hint, ctx, call);
            case KExpr.Cast cast -> attribCast(hint, ctx, cast);
            case KExpr.Closure closure -> attribClosure(hint, ctx, closure);
            case KExpr.Continue aContinue -> attribContinue(hint, ctx, aContinue);
            case KExpr.CreateArray createArray -> attribCreateArray(hint, ctx, createArray);
            case KExpr.CreateObject createObject -> attribCreateObject(hint, ctx, createObject);
            case KExpr.For aFor -> attribFor(hint, ctx, aFor);
            case KExpr.GetArrayElement getArrayElement -> attribGetArrayElement(hint, ctx, getArrayElement);
            case KExpr.GetMember getMember -> attribGetMember(hint, ctx, getMember);
            case KExpr.IsInstanceOf isInstanceOf -> attribInstanceOf(hint, ctx, isInstanceOf);
            case KExpr.Literal literal -> attribLiteral(hint, ctx, literal);
            case KExpr.Match match -> attribMatch(hint, ctx, match);
            case KExpr.Number number -> attribNumber(hint, ctx, number);
            case KExpr.Return aReturn -> attribReturn(hint, ctx, aReturn);
            case KExpr.Self self -> attribSelf(hint, ctx, self);
            case KExpr.StringExpr stringExpr -> attribStringExpr(hint, ctx, stringExpr);
            case KExpr.Unary unary -> attribUnary(hint, ctx, unary);
            case KExpr.VariableDefinition variableDefinition -> attribVariableDefinition(hint, ctx, variableDefinition);
            case KExpr.While aWhile -> attribWhile(hint, ctx, aWhile);
        };
    }

    private static AttribExpr attribAssignment(@Nullable KType hint, AttributionContext ctx, KExpr.Assignment expr) { return of(ctx, expr);}
    private static AttribExpr attribBinary(@Nullable KType hint, AttributionContext ctx, KExpr.Binary expr) { return of(ctx, expr);}
    private static AttribExpr attribBlock(@Nullable KType hint, AttributionContext ctx, KExpr.Block expr) {
        var newExpressions = new ArrayList<KExpr>();
        var expressions = expr.expressions();
        var subCtx = ctx;
        for (var i = 0; i < expressions.size(); i++) {
            var subExpr = expressions.get(i);
            var isLast = i == expressions.size() - 1;
            var hintLine = isLast ? hint : new KType.PrimitiveType.VoidType(subExpr.region());
            var newExpr = attribExpr(hintLine, subCtx, subExpr);
            subCtx = ctx(newExpr);
            newExpressions.add(expr(newExpr));
        }
        KType returningType;
        if (newExpressions.isEmpty()) {
            returningType = new KType.PrimitiveType.VoidType(expr.region());
        } else {
            returningType = newExpressions.getLast().type();
        }
        return of(ctx, new KExpr.Block(
                expr.region(),
                newExpressions,
                returningType
        ));
    }
    private static AttribExpr attribBoolean(@Nullable KType hint, AttributionContext ctx, KExpr.Boolean expr) { return of(ctx, expr);}
    private static AttribExpr attribBranch(@Nullable KType hint, AttributionContext ctx, KExpr.Branch expr) {
        var boolType = new KType.PrimitiveType.BoolType(expr.condition().region());
        var conditionHint =  expr.branchPattern() == null ? boolType : null;
        var condition = attribExpr(conditionHint, ctx, expr.condition()).expr();

        var thenContext = ctx;
        BranchPattern branchPattern;
        switch (expr.branchPattern()) {
            case BranchPattern.Cast cast -> {
                var newSymbol = new Variable(
                        cast.castedName().region(),
                        cast.castedName().value(),
                        cast.type(),
                        false,
                        false
                );
                thenContext = thenContext.addVariable(newSymbol);
                branchPattern = new BranchPattern.Cast(
                        cast.region(),
                        cast.type(),
                        cast.castedName(),
                        newSymbol
                );
            }
            case BranchPattern.Destruct destruct -> {
//                var classObj = destruct.type();
//                ctx.getStruct(destruct.region(), destruct.type());
//
//
//                var newVariables = new ArrayList<NameAndOptType>();
//                for (var variable : destruct.variables()) {
//                    var type = variable.type();
//
//                    newVariables.add(new NameAndOptType(
//                            variable.region(),
//                            variable.name(),
//                            type,
//
//                    ));
//                }
//                branchPattern = new BranchPattern.Destruct(
//                        destruct.region(),
//                        destruct.type(),
//                        newVariables
//                );
                Log.temp(destruct.region(), "Destruct not implemented");
                throw new Log.KarinaException();
            }
            case null -> {
                branchPattern = null;
                ctx.assign(condition.region(), boolType, condition.type());
            }
        }

        var then = attribExpr(hint, thenContext, expr.thenArm()).expr();
        var elseArm = expr.elseArm();

        KType returnType;

        if (elseArm == null) {
            returnType = new KType.PrimitiveType.VoidType(expr.region());
        } else {
            var elseHint = hint;
            if (hint == null) {
                elseHint = then.type();
            }
            elseArm = attribExpr(elseHint, ctx, elseArm).expr();
            returnType = ctx.getSuperType(then.type(), elseArm.type());
            if (returnType == null) {
                returnType = new KType.PrimitiveType.VoidType(expr.region());
            }
        }

        return of(ctx, new KExpr.Branch(
                expr.region(),
                condition,
                then,
                elseArm,
                branchPattern,
                returnType
        ));
    }
    private static AttribExpr attribBreak(@Nullable KType hint, AttributionContext ctx, KExpr.Break expr) { return of(ctx, expr);}
    private static AttribExpr attribCall(@Nullable KType hint, AttributionContext ctx, KExpr.Call expr) { return of(ctx, expr); }
    private static AttribExpr attribCast(@Nullable KType hint, AttributionContext ctx, KExpr.Cast expr) { return of(ctx, expr);}
    private static AttribExpr attribClosure(@Nullable KType hint, AttributionContext ctx, KExpr.Closure expr) { return of(ctx, expr);}
    private static AttribExpr attribContinue(@Nullable KType hint, AttributionContext ctx, KExpr.Continue expr) { return of(ctx, expr);}
    private static AttribExpr attribCreateArray(@Nullable KType hint, AttributionContext ctx, KExpr.CreateArray expr) { return of(ctx, expr);}
    private static AttribExpr attribCreateObject(@Nullable KType hint, AttributionContext ctx, KExpr.CreateObject expr) {

        var struct = ctx.getStruct(expr.createType().region(), expr.createType());
        //casting is ok, ctx.getStruct already checks for this
        var classType = (KType.ClassType) expr.createType();
        var annotatedGenerics = !classType.generics().isEmpty();

        //generate the new generics for the implementation
        List<KType> newGenerics;
        if (annotatedGenerics) {
            //We don't have to test of the length of the generics,
            // this should be already be checked in the import stage
            newGenerics = classType.generics();
        } else {
            var genericCount = struct.generics().size();
            newGenerics = new ArrayList<>(genericCount);
            for (var ignored = 0; ignored < genericCount; ignored++) {
                newGenerics.add(new KType.PrimitiveType.Resolvable(expr.region()));
            }
        }


        //We map all generics here to the new implementation to replace fields in the struct.
        //The previous step should have already ensured that the size of generics is the same.
        Map<Generic, KType> mapped = new HashMap<>();
        for (var i = 0; i < newGenerics.size(); i++) {
            var generic = struct.generics().get(i);
            var type = newGenerics.get(i);
            mapped.put(generic, type);
        }

        //The new type with all the generics replaced
        var newType = new KType.ClassType(
                expr.createType().region(),
                classType.path(),
                newGenerics
        );

        //check all parameters
        var newParameters = new ArrayList<NamedExpression>();

        var openParameters = new ArrayList<>(expr.parameters());
        for (var field : struct.fields()) {
            var fieldType = replaceType(field.type(), mapped);
            var foundParameter = openParameters
                    .stream().filter(ref -> ref.name().equals(field.name())).findFirst();
            if (foundParameter.isEmpty()) {
                Log.attribError(new AttribError.MissingField(
                        expr.region(),
                        field.name().value()
                ));
                throw new Log.KarinaException();
            } else {
                var parameter = foundParameter.get();
                openParameters.remove(parameter);
                var attribField = attribExpr(fieldType, ctx, parameter.expr()).expr();

                ctx.assign(parameter.name().region(), fieldType, attribField.type());

                newParameters.add(new NamedExpression(
                        parameter.region(),
                        parameter.name(),
                        attribField
                ));
            }
        }
        if (!openParameters.isEmpty()) {
            var toMany = openParameters.getFirst();
            Log.attribError(new AttribError.UnknownField(toMany.name().region(), toMany.name().value()));
        }

        return of(ctx, new KExpr.CreateObject(
                expr.region(),
                newType,
                newParameters,
                newType
        ));

    }
    private static AttribExpr attribFor(@Nullable KType hint, AttributionContext ctx, KExpr.For expr) { return of(ctx, expr);}
    private static AttribExpr attribGetArrayElement(@Nullable KType hint, AttributionContext ctx, KExpr.GetArrayElement expr) { return of(ctx, expr);}
    private static AttribExpr attribGetMember(@Nullable KType hint, AttributionContext ctx, KExpr.GetMember expr) { return of(ctx, expr);}
    private static AttribExpr attribInstanceOf(@Nullable KType hint, AttributionContext ctx, KExpr.IsInstanceOf expr) { return of(ctx, expr);}
    private static AttribExpr attribLiteral(@Nullable KType hint, AttributionContext ctx, KExpr.Literal expr) {
        if (ctx.variables().contains(expr.name())) {
            return of(ctx, new KExpr.Literal(
                    expr.region(),
                    expr.name(),
                    ctx.variables().get(expr.name())
            ));
        } else if (ctx.table().getFunction(expr.name()) != null) {
            throw new NullPointerException("Function not implemented");
        } else {
            var variableName = ctx.variables().names();
            var functionNames = ctx.table().availableFunctionNames();
            var available = new HashSet<>(variableName);
            available.addAll(functionNames);
            Log.attribError(new AttribError.UnknownIdentifier(expr.region(), expr.name(), available));
            throw new Log.KarinaException();
        }
    }
    private static AttribExpr attribMatch(@Nullable KType hint, AttributionContext ctx, KExpr.Match expr) { return of(ctx, expr);}
    private static AttribExpr attribNumber(@Nullable KType hint, AttributionContext ctx, KExpr.Number expr) { return of(ctx, expr);}
    private static AttribExpr attribReturn(@Nullable KType hint, AttributionContext ctx, KExpr.Return expr) { return of(ctx, expr);}
    private static AttribExpr attribSelf(@Nullable KType hint, AttributionContext ctx, KExpr.Self expr) {
        if (ctx.selfType() == null) {
            Log.attribError(new AttribError.UnqualifiedSelf(
                    expr.region(), ctx.methodRegion()
            ));
            throw new Log.KarinaException();
        }
        return of(ctx, new KExpr.Self(
                expr.region(),
                ctx.selfType()
        ));
    }
    private static AttribExpr attribStringExpr(@Nullable KType hint, AttributionContext ctx, KExpr.StringExpr expr) { return of(ctx, expr);}
    private static AttribExpr attribUnary(@Nullable KType hint, AttributionContext ctx, KExpr.Unary expr) { return of(ctx, expr);}
    private static AttribExpr attribVariableDefinition(@Nullable KType hint, AttributionContext ctx, KExpr.VariableDefinition expr) {

        var valueExpr = attribExpr(expr.hint(), ctx, expr.value()).expr();

        var varTypeHint = expr.hint();
        if (varTypeHint == null) {
            varTypeHint = valueExpr.type();
        } else {
            ctx.assign(expr.region(), varTypeHint, valueExpr.type());
        }

        var symbol = new Variable(
                expr.name().region(),
                expr.name().value(),
                expr.hint(),
                true,
                false
        );

        return of(ctx.addVariable(symbol), new KExpr.VariableDefinition(
                expr.region(),
                expr.name(),
                varTypeHint,
                valueExpr,
                symbol
        ));

    }
    private static AttribExpr attribWhile(@Nullable KType hint, AttributionContext ctx, KExpr.While expr) {
        var boolType = new KType.PrimitiveType.BoolType(expr.condition().region());
        var condition = attribExpr(boolType, ctx, expr.condition()).expr();
        ctx.assign(expr.condition().region(), boolType, condition.type());
        var body = attribExpr(null, ctx, expr.body()).expr();
        return of(ctx, new KExpr.While(
                expr.region(),
                condition,
                body
        ));
    }


    public record AttribExpr(KExpr expr, AttributionContext ctx) {}
    private static KExpr expr(AttribExpr expr) {
        return expr.expr;
    }
    private static AttributionContext ctx(AttribExpr expr) {
        return expr.ctx;
    }
    private static AttribExpr of(AttributionContext ctx, KExpr expr) {
        return new AttribExpr(expr, ctx);
    }


    private static KType replaceType(KType original, Map<Generic, KType> generics) {
        //TODO replace types
        return original;
    }
}
