package org.karina.lang.compiler.stages.attrib.expr;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.objects.Types;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.symbols.CallSymbol;
import org.karina.lang.compiler.symbols.LiteralSymbol;
import org.karina.lang.compiler.symbols.MemberSymbol;

import java.lang.reflect.Modifier;
import java.util.*;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class CallAttrib  {
    public static AttributionExpr attribCall(
            @Nullable KType hint, AttributionContext ctx, KExpr.Call expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();
        var genericsAnnotated = !expr.generics().isEmpty();
        List<KExpr> newArguments = new ArrayList<>();
        CallSymbol symbol;
        //OMG PATTERNS
        if (left instanceof KExpr.Literal(var ignored, var ignored2, LiteralSymbol.StaticMethodReference(var ignored3, MethodCollection collection))) {
            symbol = getStatic(ctx, expr, collection, genericsAnnotated, newArguments);
        } else if (left instanceof KExpr.GetMember(var ignored, var object, var name, MemberSymbol.VirtualFunctionSymbol sym)) {
            symbol = getVirtual(ctx, expr, name, sym.classType(), sym.collection(), genericsAnnotated, newArguments);
            left = object;
        } else if (left.type() instanceof KType.FunctionType functionType) {
            symbol = getFunctionalType(ctx, expr, functionType, newArguments);
        } else if (left instanceof KExpr.SpecialCall specialCallExpr) {
            symbol = getSuper(ctx, expr, specialCallExpr.invocationType(), genericsAnnotated, newArguments);
        } else {
            Log.temp(expr.region(), "Invalid call onto " + left.getClass().getSimpleName());
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

    private static @NotNull CallSymbol getStatic(
            AttributionContext ctx,
            KExpr.Call expr,
            MethodCollection collection,
            boolean genericsAnnotated,
            List<KExpr> newArguments
    ) {
        HashMap<Generic, KType> mapped = new HashMap<>();
        var methodTypedReturn = matchCollection(ctx, collection, expr, mapped, genericsAnnotated);
        newArguments.addAll(methodTypedReturn.newArguments);
        Log.recordType(Log.LogTypes.CALLS, "returning of static method", methodTypedReturn.pointer, methodTypedReturn.returnType);

        return new CallSymbol.CallStatic(
                methodTypedReturn.pointer,
                methodTypedReturn.generics,
                methodTypedReturn.returnType
        );
    }

    private static @NotNull CallSymbol getSuper(
            AttributionContext ctx,
            KExpr.Call expr,
            InvocationType invocationType,
            boolean genericsAnnotated,
            List<KExpr> newArguments
    ) {
        var name = switch (invocationType) {
            case InvocationType.NewInit newInit -> "<init>";
            case InvocationType.SpecialInvoke specialInvoke -> specialInvoke.name();
            case InvocationType.Unknown unknown -> null;
        };
        if (name == null) {
            Log.temp(expr.region(), "Invalid invocation type");
            throw new Log.KarinaException();
        }
        var superType = switch (invocationType) {
            case InvocationType.NewInit newInit -> newInit.classType();
            case InvocationType.SpecialInvoke specialInvoke -> {
                if (!(specialInvoke.superType() instanceof KType.ClassType classType)) {
                    Log.attribError(
                            new AttribError.NotAStruct(
                                    expr.region(),
                                    specialInvoke.superType()
                            )
                    );
                    throw new Log.KarinaException();
                }
                yield classType;
            }
            default -> throw new IllegalStateException("Unexpected value: " + invocationType);
        };

        //TODO when calling onto super, invoke the super constructor,
        // validate, that this is only called in a constructor
        Log.recordType(Log.LogTypes.CALLS, "Calling", name, "of object", superType);

        var superClassModel = ctx.model().getClass(superType.pointer());
        var collection = superClassModel.getMethodCollection(name);
        Log.beginType(Log.LogTypes.CALLS, "raw pointers");

        for (var pointer : collection) {
            Log.recordType(Log.LogTypes.CALLS, "Method ", pointer);
        }

        Log.endType(Log.LogTypes.CALLS, "raw pointers");


        collection = collection.filter(ref -> {
            var modifiers = ctx.model().getMethod(ref).modifiers();
            return !Modifier.isStatic(modifiers) &&
                    ctx.protection().canReference(ctx.owningClass(), ref.classPointer(), modifiers);
        });

        Log.beginType(Log.LogTypes.CALLS, "filtered pointers");

        for (var pointer : collection) {
            Log.recordType(Log.LogTypes.CALLS, "Method", pointer);
        }

        Log.endType(Log.LogTypes.CALLS, "filtered pointers");

        var mapped = new HashMap<Generic, KType>();
        if (superClassModel.generics().size() != superType.generics().size()) {
            Log.temp(expr.region(), "Class generic count mismatch");
            throw new Log.KarinaException();
        }
        //this maps the class fieldType generics
        for (var i = 0; i < superClassModel.generics().size(); i++) {
            var generic = superClassModel.generics().get(i);
            var type = superType.generics().get(i);
            mapped.put(generic, type);
        }

        var methodTypedReturn = matchCollection(ctx, collection, expr, mapped, genericsAnnotated);
        newArguments.addAll(methodTypedReturn.newArguments);

        Log.recordType(Log.LogTypes.CALLS, "returning of super method", methodTypedReturn.pointer, methodTypedReturn.returnType);

        var yielding = switch (invocationType) {
            case InvocationType.NewInit newInit -> newInit.classType();
            default -> methodTypedReturn.returnType;
        };
        Log.recordType(Log.LogTypes.CALLS, "yielding ", yielding);

        return new CallSymbol.CallSuper(
                methodTypedReturn.pointer,
                methodTypedReturn.generics,
                yielding
        );
    }

    private static @NotNull CallSymbol getVirtual(
            AttributionContext ctx,
            KExpr.Call expr,
            RegionOf<String> name,
            KType.ClassType classType,
            MethodCollection collection,
            boolean genericsAnnotated,
            List<KExpr> newArguments
    ) {

        var mapped = new HashMap<Generic, KType>();
        var classModel = ctx.model().getClass(classType.pointer());
        if (classModel.generics().size() != classType.generics().size()) {
            Log.temp(expr.region(), "Class generic count mismatch");
            throw new Log.KarinaException();
        }
        //this maps the class fieldType generics
        for (var i = 0; i < classModel.generics().size(); i++) {
            var generic = classModel.generics().get(i);
            var type = classType.generics().get(i);
            mapped.put(generic, type);
        }

        var methodTypedReturn = matchCollection(ctx, collection, expr, mapped, genericsAnnotated);
        newArguments.addAll(methodTypedReturn.newArguments);
        Log.recordType(Log.LogTypes.CALLS, "returning of virtual method", methodTypedReturn.pointer, methodTypedReturn.returnType);


        return new CallSymbol.CallVirtual(
                methodTypedReturn.pointer,
                methodTypedReturn.generics,
                methodTypedReturn.returnType
        );
    }


    private static @NotNull CallSymbol getFunctionalType(
            AttributionContext ctx,
            KExpr.Call expr,
            KType.FunctionType functionType,
            List<KExpr> newArguments
    ) {

        CallSymbol symbol;
        var expected = functionType.arguments().size();
        var found = expr.arguments().size();
        if (found != expected) {
            var toMany = expr.arguments().get((found - expected) - 1);
            Log.attribError(new AttribError.ParameterCountMismatch(toMany.region(), expected));
            throw new Log.KarinaException();
        }

        for (var i = 0; i < expected; i++) {
            var parameter = functionType.arguments().get(i);
            var argument = expr.arguments().get(i);
            var newArgument = attribExpr(parameter, ctx, argument).expr();
            var newArgumentAssign = ctx.makeAssignment(argument.region(), parameter, newArgument);
            newArguments.add(newArgumentAssign);
        }

        var returnType = functionType.returnType();

        //todo infer return fieldType...
        Log.recordType(Log.LogTypes.CALLS, "returning of function", returnType);

        symbol = new CallSymbol.CallDynamic(expr.region(), returnType);
        return symbol;
    }

    private static MethodTypedReturn matchCollection(AttributionContext ctx, MethodCollection collection, KExpr.Call expr, Map<Generic, KType> mapped, boolean genericsAnnotated) {
        var pointers = matchCollectionWithArgs(ctx, collection, expr, mapped, genericsAnnotated);
        if (pointers.size() > 1) {
            var logName = "ambiguous-call-" + collection.name();
            Log.recordType(Log.LogTypes.AMBIGUOUS, logName);

            //TODO hot fix, analyze what method is the best
            pointers = List.of(pointers.getFirst());
        }


        if (pointers.size() == 1) {
            var first = pointers.getFirst();
            //solve for generics
            for (var i = 0; i < first.newArguments.size(); i++) {
                var parameter = first.methodParams.get(i);
                var argument = first.newArguments.get(i);
                var newArg = ctx.makeAssignment(
                        argument.region(),
                        parameter,
                        argument
                );
                first.newArguments.set(i, newArg);
            }

            return first;
        }

        var foundTypes = expr.arguments().stream().map(ref ->
                attribExpr(null, ctx, ref).expr().type()
        ).toList();

        var availableSignatures = new ArrayList<RegionOf<List<KType>>>();

        for (var method : collection) {
            var methodModel = ctx.model().getMethod(method);
            availableSignatures.add(RegionOf.region(method.region(), methodModel.signature().parameters()));
        }

        Log.attribError(new AttribError.SignatureMismatch(
                expr.region(),
                collection.name(),
                foundTypes,
                availableSignatures
        ));


        throw new Log.KarinaException();
    }

    private static List<MethodTypedReturn> matchCollectionWithArgs(
            AttributionContext ctx,
            MethodCollection collection,
            KExpr.Call expr,
            Map<Generic, KType> mapped,
            boolean genericsAnnotated
    ) {
        Log.beginType(Log.LogTypes.CALLS, collection.name());

        var available = new ArrayList<PotentialMethodPointer>();
        for (var method : collection) {
            var mapping = new HashMap<>(mapped);

            var methodModel = ctx.model().getMethod(method);

            var parameters = methodModel.signature().parameters();

            if (expr.arguments().size() != parameters.size()) {
                continue;
            }

            if (!putMappedGenerics(expr, genericsAnnotated, methodModel, mapping)) {
                continue;
            }
            available.add(new PotentialMethodPointer(
                    method,
                    mapping,
                    methodModel
            ));

        }

        var arguments = new ArrayList<PrecomputedExpr>();
        for (var argument : expr.arguments()) {
            arguments.add(new PrecomputedExpr(argument, ctx));
        }

        for (var method : available) {

            var methodPointer = method.pointer();
            var methodModel = method.methodModel();
            var mappedCopy = method.genericMapping();
            var returnType = Types.projectGenerics(methodModel.signature().returnType(), mappedCopy);

            var unmappedParameters = methodModel.signature().parameters();
            var mappedParameters = unmappedParameters.stream().map(ref ->
                    Types.projectGenerics(ref, mappedCopy)
            ).toList();


            var canAssign = true;
            var i = 0;
            for (i = 0; i < mappedParameters.size(); i++) {
                var mappedParameter = mappedParameters.get(i);

                var mutable = available.size() == 1;

                var argument = arguments.get(i).getNew(mappedParameter);

                var canConvert = ctx.getConversion(argument.region(), mappedParameter, argument, mutable) != null;
                if (!canConvert) {
                    Log.recordType(Log.LogTypes.CALLS, "Cannot assign " + mappedParameter + " cannot be assigned from " + argument.type());
                    canAssign = false;
                    break;
                }
            }
            if (canAssign) {
                Log.endType(Log.LogTypes.CALLS, collection.name(), "Matched signature", mappedParameters);
                var newArgs = arguments.stream().map(ref -> ref.get(null)).toList();
                return List.of(new MethodTypedReturn(
                        returnType,
                        methodPointer,
                        new ArrayList<>(newArgs),
                        List.copyOf(mappedCopy.values()),
                        mappedParameters
                ));
            } else {
                Log.recordType(Log.LogTypes.CALLS, "Skipping signature ", mappedParameters, " for element " + i);
            }

        }
        Log.endType(Log.LogTypes.CALLS, collection.name());

        return List.of();
    }

    record MethodTypedReturn(KType returnType, MethodPointer pointer, List<KExpr> newArguments, List<KType> generics, List<KType> methodParams) {}

    record PotentialMethodPointer(MethodPointer pointer, HashMap<Generic, KType> genericMapping, MethodModel methodModel) {}

    private static boolean putMappedGenerics(
            KExpr.Call expr,
            boolean genericsAnnotated,
            MethodModel method,
            HashMap<Generic, KType> mapped
    ) {

        var methodGenerics = method.generics();
        if (genericsAnnotated) {
            var expressionGenerics = expr.generics();
            if (expressionGenerics.size() != methodGenerics.size()) {
                return false;
            }

            for (var i = 0; i < methodGenerics.size(); i++) {
                var generic = methodGenerics.get(i);
                var type = expressionGenerics.get(i);
                mapped.put(generic, type);
            }
        } else {
            for (var i = 0; i < methodGenerics.size(); i++) {
                var generic = methodGenerics.get(i);
                var type = new KType.Resolvable();
                mapped.put(generic, type);
            }
        }
        return true;
    }

    @RequiredArgsConstructor
    static class PrecomputedExpr {
        private final KExpr previous;
        private final AttributionContext ctx;

        private KExpr inner = null;

        public KExpr getNew(KType kType) {
            //dont remember
            this.inner = attribExpr(kType, this.ctx, this.previous).expr();
            return this.inner;
        }

        public KExpr get(@Nullable KType kType) {
            if (this.inner == null) {
                this.inner = attribExpr(kType, this.ctx, this.previous).expr();
            }
            return this.inner;
        }

    }
}
