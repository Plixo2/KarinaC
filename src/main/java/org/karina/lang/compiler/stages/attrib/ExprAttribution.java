package org.karina.lang.compiler.stages.attrib;

import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.*;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.objects.*;
import org.karina.lang.compiler.stages.Variable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.stages.symbols.*;

import java.util.*;

public class ExprAttribution {

    static AttribExpr attribExpr(@Nullable KType hint, AttributionContext ctx, KExpr expr) {
        if (hint != null) {
            hint = hint.unpack();
        }
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

    private static AttribExpr attribBinary(@Nullable KType hint, AttributionContext ctx, KExpr.Binary expr) {
        var left = attribExpr(hint, ctx, expr.left()).expr();
        var right = attribExpr(left.type(), ctx, expr.right()).expr();

        var leftType = left.type();
        var rightType = right.type();

        if (!(leftType instanceof KType.PrimitiveType leftPrimitive)) {
            Log.attribError(new AttribError.NotSupportedType(left.region(), leftType));
            throw new Log.KarinaException();
        }
        if (!(rightType instanceof KType.PrimitiveType rightPrimitive)) {
            Log.attribError(new AttribError.NotSupportedType(right.region(), rightType));
            throw new Log.KarinaException();
        }
        if (leftPrimitive.primitive() != rightPrimitive.primitive()) {
            Log.attribError(new AttribError.TypeMismatch(expr.region(), leftType, rightType));
            throw new Log.KarinaException();
        }
        val op = switch (leftPrimitive.primitive()) {
            case INT -> BinOperatorSymbol.IntOP.fromOperator(expr.operator());
            case FLOAT -> BinOperatorSymbol.FloatOP.fromOperator(expr.operator());
            case BOOL -> BinOperatorSymbol.BoolOP.fromOperator(expr.operator());
            case DOUBLE -> BinOperatorSymbol.DoubleOP.fromOperator(expr.operator());
            case LONG -> BinOperatorSymbol.LongOP.fromOperator(expr.operator());
            case VOID -> {
                Log.attribError(new AttribError.NotSupportedType(left.region(), leftType));
                throw new Log.KarinaException();
            }
            case STRING, CHAR, SHORT, BYTE -> {
                Log.temp(expr.operator().region(), "Binary for " + leftPrimitive.primitive() + " not implemented");
                throw new Log.KarinaException();
            }
        };
        if (op == null) {
            Log.attribError(new AttribError.NotSupportedType(left.region(), leftType));
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.Binary(
                expr.region(),
                left,
                expr.operator(),
                right,
                op
        ));
    }

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

    private static AttribExpr attribCall(@Nullable KType hint, AttributionContext ctx, KExpr.Call expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();
        var genericsAnnotated = !expr.generics().isEmpty();
        List<KExpr> newArguments;
        CallSymbol symbol;
        //OMG PATTERNS
        if (left instanceof KExpr.Literal(var ignored, var ignored2, LiteralSymbol.StaticFunction staticFunction)) {
            var referencedFunction = KTree.findAbsolutItem(ctx.root(), staticFunction.path());
            if (!(referencedFunction instanceof KTree.KFunction function)) {
                Log.temp(expr.region(), "Function not found");
                throw new Log.KarinaException();
            }
            HashMap<Generic, KType> mapped = new HashMap<>();
            if (genericsAnnotated) {
                if (expr.generics().size() != function.generics().size()) {
                    Log.importError(
                            new ImportError.GenericCountMismatch(
                                    expr.region(), function.name().value(),
                                    function.generics().size(), expr.generics().size()
                            ));
                    throw new Log.KarinaException();
                }

                for (var i = 0; i < function.generics().size(); i++) {
                    var generic = function.generics().get(i);
                    var type = expr.generics().get(i);
                    mapped.put(generic, type);
                }
            } else {
                for (var i = 0; i < function.generics().size(); i++) {
                    var generic = function.generics().get(i);
                    var type = new KType.PrimitiveType.Resolvable(expr.region());
                    mapped.put(generic, type);
                }
            }
            var staticReturnType = function.returnType();
            if (staticReturnType == null) {
                staticReturnType = new KType.PrimitiveType.VoidType(expr.region());
            }
            var returnType = replaceType(staticReturnType, mapped);

            newArguments = new ArrayList<>();
            var expected = function.parameters().size();
            var found = expr.arguments().size();
            if (found > expected) {
                var toMany = expr.arguments().get((found - expected) - 1);
                Log.attribError(new AttribError.ParameterCountMismatch(toMany.region(), expected));
                throw new Log.KarinaException();
            } else if (expected > found) {
                var missing = function.parameters().get((expected - found) - 1);
                Log.attribError(
                        new AttribError.MissingField(expr.region(), missing.name().value()));
                throw new Log.KarinaException();
            }

            for (var i = 0; i < expected; i++) {
                var parameter = function.parameters().get(i);
                var argument = expr.arguments().get(i);
                var argumentType = replaceType(parameter.type(), mapped);
                var newArgument = attribExpr(argumentType, ctx, argument).expr();
                ctx.assign(argument.region(), argumentType, newArgument.type());
                newArguments.add(newArgument);
            }

            symbol = new CallSymbol.CallStatic(
                    staticFunction.path(),
                    List.copyOf(mapped.values()),
                    returnType
            );


        } else if (left instanceof KExpr.GetMember(var ignored, var ignored1, var ignored2, MemberSymbol.VirtualFunctionSymbol sym)) {
            var referencedFunction = KTree.findAbsoluteVirtualFunction(ctx.root(), sym.path());
            var referencedClass = KTree.findAbsolutItem(ctx.root(), sym.classType().path().value());
            if (!(referencedFunction instanceof KTree.KFunction function) || !(referencedClass instanceof KTree.KStruct struct)) {
                Log.temp(expr.region(), "Function not found");
                throw new Log.KarinaException();
            }
            var mapped = new HashMap<Generic, KType>();

            if (genericsAnnotated) {
                if (expr.generics().size() != function.generics().size()) {
                    Log.importError(
                            new ImportError.GenericCountMismatch(
                                    expr.region(), function.name().value(),
                                    function.generics().size(), expr.generics().size()
                            ));
                    throw new Log.KarinaException();
                }

                for (var i = 0; i < function.generics().size(); i++) {
                    var generic = function.generics().get(i);
                    var type = expr.generics().get(i);
                    mapped.put(generic, type);
                }
            } else {
                for (var i = 0; i < function.generics().size(); i++) {
                    var generic = function.generics().get(i);
                    var type = new KType.PrimitiveType.Resolvable(expr.region());
                    mapped.put(generic, type);
                }
            }
            for (var i = 0; i < sym.classType().generics().size(); i++) {
                var generic = struct.generics().get(i);
                var type = sym.classType().generics().get(i);
                mapped.put(generic, type);
            }


            var vreturnType = function.returnType();
            if (vreturnType == null) {
                vreturnType = new KType.PrimitiveType.VoidType(expr.region());
            }
            var returnType = replaceType(vreturnType, mapped);

            newArguments = new ArrayList<>();
            var expected = function.parameters().size();
            var found = expr.arguments().size();
            if (found > expected) {
                var toMany = expr.arguments().get((found - expected) - 1);
                Log.attribError(new AttribError.ParameterCountMismatch(toMany.region(), expected));
                throw new Log.KarinaException();
            } else if (expected > found) {
                var missing = function.parameters().get((expected - found) - 1);
                Log.attribError(
                        new AttribError.MissingField(expr.region(), missing.name().value()));
                throw new Log.KarinaException();
            }

            for (var i = 0; i < expected; i++) {
                var parameter = function.parameters().get(i);
                var argument = expr.arguments().get(i);
                var argumentType = replaceType(parameter.type(), mapped);
                var newArgument = attribExpr(argumentType, ctx, argument).expr();
                ctx.assign(argument.region(), argumentType, newArgument.type());
                newArguments.add(newArgument);
            }

            symbol = new CallSymbol.CallVirtual(
                    sym.classType(),
                    sym.path(),
                    List.copyOf(mapped.values()),
                    returnType
            );

        }
        else {
            Log.temp(expr.region(), "Non Statics not Supported");
            throw new Log.KarinaException();
        }


        return of(ctx, new KExpr.Call(
                expr.region(),
                left,
                expr.generics(),
                newArguments,
                symbol
        ));
    }

    private static AttribExpr attribCast(@Nullable KType hint, AttributionContext ctx, KExpr.Cast expr) {
        var left = attribExpr(null, ctx, expr.expression()).expr();
        var type = left.type();
        var toType = expr.asType();

        if (!(type instanceof KType.PrimitiveType fromPrimitive)) {
            Log.temp(left.region(), "Non Numeric Cast");
            throw new Log.KarinaException();
        }
        if (!(toType instanceof KType.PrimitiveType toPrimitive)) {
            Log.temp(expr.region(), "Non Numeric Cast");
            throw new Log.KarinaException();
        }
        if (fromPrimitive.primitive() == KType.KPrimitive.VOID) {
            Log.attribError(new AttribError.NotSupportedType(left.region(), type));
            throw new Log.KarinaException();
        }
        if (toPrimitive.primitive() == KType.KPrimitive.VOID) {
            Log.attribError(new AttribError.NotSupportedType(expr.region(), toType));
            throw new Log.KarinaException();
        }
        if (!fromPrimitive.isNumeric() || !toPrimitive.isNumeric()) {
            Log.temp(expr.region(), "Non Numeric Cast");
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.Cast(
                expr.region(),
                left,
                toType,
                new CastSymbol(fromPrimitive, toPrimitive)
        ));
    }

    private static AttribExpr attribClosure(@Nullable KType hint, AttributionContext ctx, KExpr.Closure expr) { return of(ctx, expr);}

    private static AttribExpr attribContinue(@Nullable KType hint, AttributionContext ctx, KExpr.Continue expr) { return of(ctx, expr);}

    private static AttribExpr attribCreateArray(@Nullable KType hint, AttributionContext ctx, KExpr.CreateArray expr) {

        KType elementType;
        if (expr.hint() != null) {
            elementType = expr.hint();
        } else {
            if (hint instanceof KType.ArrayType arrayType) {
                elementType = arrayType.elementType();
            } else {
                elementType = new KType.PrimitiveType.Resolvable(expr.region());
            }
        }
        var newElements = new ArrayList<KExpr>();
        for (var element : expr.elements()) {
            var newElement = attribExpr(elementType, ctx, element).expr();
            ctx.assign(newElement.region(), elementType, newElement.type());
            newElements.add(newElement);
        }

        return of(ctx, new KExpr.CreateArray(
                expr.region(),
                elementType,
                newElements,
                new KType.ArrayType(
                        expr.region(),
                        elementType
                )
        ));

    }

    private static AttribExpr attribCreateObject(@Nullable KType hint, AttributionContext ctx, KExpr.CreateObject expr) {

        var struct = ctx.getStruct(expr.createType().region(), expr.createType());
        //casting is ok, ctx.getStruct already checks for this
        var classType = (KType.ClassType) expr.createType();
        var annotatedGenerics = !classType.generics().isEmpty();

        //generate the new generics for the implementation
        List<KType> newGenerics;
        if (annotatedGenerics) {
            //We don't have to test for the length of the generics,
            // this should already be checked in the import stage
            newGenerics = classType.generics();
        } else {
            var genericCount = struct.generics().size();
            newGenerics = new ArrayList<>(genericCount);
            for (var ignored = 0; ignored < genericCount; ignored++) {
                newGenerics.add(new KType.PrimitiveType.Resolvable(expr.region()));
            }
        }

        if (newGenerics.size() != struct.generics().size()) {
            Log.temp(expr.region(), "Generics count mismatch, this is a bug");
            throw new Log.KarinaException();
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

        //check all Parameters,
        //check if all names are correct,
        //and also check the type with the replaced Type (implemented Generics)
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

    private static AttribExpr attribGetArrayElement(@Nullable KType hint, AttributionContext ctx, KExpr.GetArrayElement expr) {

        KType arrayHint;
        arrayHint = Objects.requireNonNullElse(
                hint,
                new KType.PrimitiveType.Resolvable(expr.region())
        );
        arrayHint = new KType.ArrayType(
                expr.left().region(),
                arrayHint
        );

        var left = attribExpr(arrayHint, ctx, expr.left()).expr();
        if (!(left.type() instanceof KType.ArrayType arrayType)) {
            Log.attribError(new AttribError.NotAArray(left.region(), arrayHint));
            throw new Log.KarinaException();
        }

        var index = attribExpr(new KType.PrimitiveType.IntType(expr.index().region()), ctx, expr.index()).expr();
        ctx.assign(index.region(), new KType.PrimitiveType.IntType(expr.index().region()), index.type());

        return of(ctx, new KExpr.GetArrayElement(
                expr.region(),
                left,
                index,
                arrayType.elementType()
        ));
    }

    private static AttribExpr attribGetMember(@Nullable KType hint, AttributionContext ctx, KExpr.GetMember expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();

        var struct = ctx.getStruct(expr.left().region(), left.type());
        //casting is ok, ctx.getStruct already checks for this
        var classType = (KType.ClassType) left.type();

        MemberSymbol symbol;
        var fieldToGet =
                struct.fields().stream().filter(ref -> ref.name().equals(expr.name())).findFirst();
        var functionToGet =
                struct.functions().stream().filter(ref -> ref.name().equals(expr.name())).findFirst();

        if (classType.generics().size() != struct.generics().size()) {
            Log.temp(expr.region(), "Generics count mismatch, this is a bug");
            throw new Log.KarinaException();
        }

        if (fieldToGet.isPresent()) {
            var field = fieldToGet.get();

            Map<Generic, KType> mapped = new HashMap<>();
            for (var i = 0; i < classType.generics().size(); i++) {
                var generic = struct.generics().get(i);
                var type = classType.generics().get(i);
                mapped.put(generic, type);
            }
            var fieldType = replaceType(field.type(), mapped);

            symbol = new MemberSymbol.FieldSymbol(
                    fieldType,
                    field.path()
            );
        } else if (functionToGet.isPresent()) {
            var function = functionToGet.get();
            symbol = new MemberSymbol.VirtualFunctionSymbol(
                    expr.region(),
                    classType,
                    function.path()
            );
        } else {
            Log.attribError(new AttribError.UnknownField(expr.name().region(), expr.name().value()));
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.GetMember(
                expr.region(),
                left,
                expr.name(),
                symbol
        ));
    }

    private static AttribExpr attribInstanceOf(@Nullable KType hint, AttributionContext ctx, KExpr.IsInstanceOf expr) { return of(ctx, expr);}

    private static AttribExpr attribLiteral(@Nullable KType hint, AttributionContext ctx, KExpr.Literal expr) {

        LiteralSymbol symbol;
        if (ctx.variables().contains(expr.name())) {
            symbol = new LiteralSymbol.VariableReference(expr.region(), ctx.variables().get(expr.name()));
        } else {
            var function = ctx.table().getFunction(expr.name());
            if (function != null) {
                symbol = new LiteralSymbol.StaticFunction(expr.region(), function.path());
            } else {
                var variableName = ctx.variables().names();
                var functionNames = ctx.table().availableFunctionNames();
                var available = new HashSet<>(variableName);
                available.addAll(functionNames);
                Log.attribError(new AttribError.UnknownIdentifier(expr.region(), expr.name(), available));
                throw new Log.KarinaException();
            }
        }
        return of(ctx, new KExpr.Literal(
                expr.region(),
                expr.name(),
                symbol
        ));

    }

    private static AttribExpr attribMatch(@Nullable KType hint, AttributionContext ctx, KExpr.Match expr) { return of(ctx, expr);}

    private static AttribExpr attribNumber(@Nullable KType hint, AttributionContext ctx, KExpr.Number expr) {

        var number = expr.number();
        var hasFraction = number.stripTrailingZeros().scale() > 0 || expr.decimalAnnotated();
        NumberSymbol symbol;
        if (hasFraction) {
            if (ctx.isPrimitive(hint, KType.KPrimitive.DOUBLE)) {
                symbol = new NumberSymbol.DoubleValue(expr.region(), number.doubleValue());
            } else {
                symbol = new NumberSymbol.FloatValue(expr.region(), number.floatValue());
            }
        } else {
            if (ctx.isPrimitive(hint, KType.KPrimitive.DOUBLE)) {
                symbol = new NumberSymbol.DoubleValue(expr.region(), number.longValue());
            } else if (ctx.isPrimitive(hint, KType.KPrimitive.FLOAT)) {
                symbol = new NumberSymbol.FloatValue(expr.region(), number.floatValue());
            } else if (ctx.isPrimitive(hint, KType.KPrimitive.LONG)) {
                try {
                    symbol = new NumberSymbol.LongValue(expr.region(), number.longValueExact());
                } catch(ArithmeticException e1) {
                    Log.syntaxError(expr.region(), "Number too large for long");
                    throw new Log.KarinaException();
                }
            } else {
                try {
                    symbol = new NumberSymbol.IntegerValue(expr.region(), number.intValueExact());
                } catch(ArithmeticException e1) {
                    try {
                        symbol = new NumberSymbol.LongValue(expr.region(), number.longValueExact());
                    } catch(ArithmeticException e2) {
                        Log.syntaxError(expr.region(), "Number too large for long");
                        throw new Log.KarinaException();
                    }
                }
            }
        }
        return of(ctx, new KExpr.Number(
                expr.region(),
                number,
                expr.decimalAnnotated(),
                symbol
        ));

    }

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

    private static AttribExpr attribUnary(@Nullable KType hint, AttributionContext ctx, KExpr.Unary expr) {

        var value = attribExpr(hint, ctx, expr.value()).expr();
        if (!(value.type() instanceof KType.PrimitiveType primitive)) {
            Log.attribError(new AttribError.NotSupportedType(value.region(), value.type()));
            throw new Log.KarinaException();
        }
        var symbol = UnaryOperatorSymbol.fromOperator(primitive.primitive(), expr.operator());
        if (symbol == null) {
            Log.attribError(new AttribError.NotSupportedType(value.region(), value.type()));
            throw new Log.KarinaException();
        }

        return of(ctx, new KExpr.Unary(
                expr.region(),
                expr.operator(),
                value,
                symbol
        ));
    }

    private static AttribExpr attribVariableDefinition(@Nullable KType hint, AttributionContext ctx, KExpr.VariableDefinition expr) {

        var valueExpr = attribExpr(expr.hint(), ctx, expr.value()).expr();

        var varTypeHint = expr.hint();
        if (varTypeHint == null) {
            varTypeHint = valueExpr.type();
        } else {
            ctx.assign(valueExpr.region(), varTypeHint, valueExpr.type());
        }

        var symbol = new Variable(
                expr.name().region(),
                expr.name().value(),
                varTypeHint,
                true,
                false
        );

        return of(ctx.addVariable(symbol), new KExpr.VariableDefinition(
                expr.region(),
                expr.name(),
                expr.hint(),
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
        return switch (original) {
            case KType.ArrayType(var region, var element) -> {
                var newElement = replaceType(element, generics);
                yield new KType.ArrayType(region, newElement);
            }
            case KType.ClassType(var region, var path, var classGenerics) -> {
                var newGenerics = new ArrayList<KType>();
                for (var generic : classGenerics) {
                    var newType = replaceType(generic, generics);
                    newGenerics.add(newType);
                }
                yield new KType.ClassType(region, path, newGenerics);
            }
            case KType.FunctionType functionType -> {
                //TODO implement this
                yield functionType;
            }
            case KType.GenericLink genericLink -> {
                var link = genericLink.link();
                var newType = generics.get(link);
                if (newType != null) {
                    yield newType;
                } else {
                    yield genericLink;
                }
            }
            case KType.PrimitiveType primitiveType -> {
                yield primitiveType;
            }
            case KType.Resolvable resolvable -> {
                var resolved = resolvable.get();
                if (resolved == null) {
                    yield resolvable;
                } else {
                    yield replaceType(resolved, generics);
                }
            }
            case KType.UnprocessedType unprocessedType -> {
                yield unprocessedType;
            }
        };
    }
}
