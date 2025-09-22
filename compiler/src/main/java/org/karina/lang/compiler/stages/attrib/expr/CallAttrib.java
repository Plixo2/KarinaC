package org.karina.lang.compiler.stages.attrib.expr;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.utils.Types;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.logging.Log;
import org.karina.lang.compiler.utils.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.logging.errors.ImportError;
import org.karina.lang.compiler.utils.symbols.CallSymbol;
import org.karina.lang.compiler.utils.symbols.LiteralSymbol;
import org.karina.lang.compiler.utils.symbols.MemberSymbol;

import java.lang.reflect.Modifier;
import java.util.*;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class CallAttrib  {
    public static AttributionExpr attribCall(
            @Nullable KType hint,
            AttributionContext ctx,
            KExpr.Call expr
    ) {

        var left = attribExpr(null, ctx, expr.left()).expr();
        var genericsAnnotated = !expr.generics().isEmpty();

        var owningClass = ctx.model().getClass(ctx.owningClass());

        for (var generic : expr.generics()) {
            if (!Types.isTypeAccessible(ctx.protection(), owningClass, generic)) {
                Log.error(ctx, new ImportError.AccessViolation(
                        expr.region(),
                        owningClass.name(),
                        null,
                        generic
                ));
                throw new Log.KarinaException();
            }
        }


        List<KExpr> newArguments = new ArrayList<>();
        CallSymbol symbol;

        var region = expr.region();
        //OMG PATTERNS
        if (left instanceof KExpr.Literal(
                var ignored, var ignored2,
                LiteralSymbol.StaticMethodReference(
                    var ignored3,
                    MethodCollection collection,
                    @Nullable KExpr firstArg
                )
        )) {
            symbol = getStatic(ctx, expr, collection, genericsAnnotated, newArguments, firstArg);
        } else if (left instanceof KExpr.GetMember(var ignored, var object, var name, _, MemberSymbol.VirtualFunctionSymbol sym)) {
            symbol = getVirtual(ctx, expr, left, name, sym.classType(), sym.collection(), genericsAnnotated, newArguments);
            left = object;
        } else if (left.type() instanceof KType.FunctionType functionType) {
            symbol = getFunctionalType(ctx, expr, functionType, newArguments);
        } else if (left instanceof KExpr.SpecialCall specialCallExpr) {
            symbol = getSuper(ctx, expr, specialCallExpr.invocationType(), genericsAnnotated, newArguments);
        } else {
            Log.temp(ctx, region, "Invalid call onto " + left.getClass().getSimpleName() + " with type " + left.type());
            throw new Log.KarinaException();
        }
        region = region.merge(left.region());

        return of(ctx, new KExpr.Call(
                region,
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
            List<KExpr> newArguments,
            @Nullable KExpr firstArg
    ) {
        HashMap<Generic, KType> mapped = new HashMap<>();
        var arguments = new ArrayList<KExpr>();
        if (firstArg != null) {
            arguments.add(firstArg);
        }
        arguments.addAll(expr.arguments());
        var methodTypedReturn = matchCollection(ctx, collection, expr, arguments, mapped, genericsAnnotated);
        newArguments.addAll(methodTypedReturn.newArguments);

        var isInterface = Modifier.isInterface(
                ctx.model().getClass(methodTypedReturn.pointer.classPointer()).modifiers()
        );

        return new CallSymbol.CallStatic(
                methodTypedReturn.pointer,
                methodTypedReturn.generics,
                methodTypedReturn.returnType,
                isInterface
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
        };
        var superType = switch (invocationType) {
            case InvocationType.NewInit newInit -> {
                yield newInit.classType();
            }
            case InvocationType.SpecialInvoke specialInvoke -> {
                if (!(specialInvoke.superType() instanceof KType.ClassType classType)) {
                    //check if supertype
                    Log.error(ctx,
                            new AttribError.NotAClass(
                                    expr.region(),
                                    specialInvoke.superType()
                            )
                    );
                    throw new Log.KarinaException();
                }
                yield classType;
            }
        };

        //TODO when calling onto super, invoke the super constructor,
        // validate, that this is only called in a constructor

        var superClassModel = ctx.model().getClass(superType.pointer());
        var collection = superClassModel.getMethodCollectionShallow(name);



        collection = collection.filter(ref -> {
            var modifiers = ctx.model().getMethod(ref).modifiers();
            return !Modifier.isStatic(modifiers) &&
                    ctx.protection().isMethodAccessible(ctx.model().getClass(ctx.owningClass()), ref);
        });



        var mapped = new HashMap<Generic, KType>();
        if (superClassModel.generics().size() != superType.generics().size()) {
            Log.temp(ctx, expr.region(), "Class generic count mismatch");
            throw new Log.KarinaException();
        }
        //this maps the class fieldType generics
        for (var i = 0; i < superClassModel.generics().size(); i++) {
            var generic = superClassModel.generics().get(i);
            var type = superType.generics().get(i);
            mapped.put(generic, type);
        }

        var methodTypedReturn = matchCollection(ctx, collection, expr, expr.arguments(), mapped, genericsAnnotated);
        newArguments.addAll(methodTypedReturn.newArguments);

        var yielding = switch (invocationType) {
            case InvocationType.NewInit newInit -> newInit.classType();
            default -> methodTypedReturn.returnType;
        };

        return new CallSymbol.CallSuper(
                methodTypedReturn.pointer,
                methodTypedReturn.generics,
                yielding,
                invocationType
        );
    }

    private static @NotNull CallSymbol getVirtual(
            AttributionContext ctx,
            KExpr.Call expr,
            KExpr left,
            RegionOf<String> name,
            KType.ClassType classType,
            MethodCollection collection,
            boolean genericsAnnotated,
            List<KExpr> newArguments
    ) {

        var mapped = new HashMap<Generic, KType>();
        var classModel = ctx.model().getClass(classType.pointer());
        if (classModel.generics().size() != classType.generics().size()) {
            Log.temp(ctx, expr.region(), "Class generic count mismatch");
            throw new Log.KarinaException();
        }
        //find all generic mappings for calling for application later
        // including super classes and interfaces
        putRecursiveGenerics(
                ctx,
                classType,
                mapped,
                new HashSet<>()
        );

        var methodTypedReturn = matchCollection(ctx, collection, expr, expr.arguments(), mapped, genericsAnnotated);
        newArguments.addAll(methodTypedReturn.newArguments);
        Log.recordType(Log.LogTypes.CALLS, "returning of virtual method", methodTypedReturn.pointer, methodTypedReturn.returnType);

        var isInterface = Modifier.isInterface(
                ctx.model().getClass(methodTypedReturn.pointer.classPointer()).modifiers()
        );

        return new CallSymbol.CallVirtual(
                methodTypedReturn.pointer,
                left,
                methodTypedReturn.generics,
                methodTypedReturn.returnType,
                isInterface
        );
    }

    private static void putRecursiveGenerics(
            AttributionContext ctx,
            KType.ClassType classType,
            HashMap<Generic, KType> mapped,
            Set<ClassPointer> visited
    ) {
        if (visited.contains(classType.pointer())) {
            return;
        }

        var classModel = ctx.model().getClass(classType.pointer());
        for (var i = 0; i < classModel.generics().size(); i++) {
            var generic = classModel.generics().get(i);
            var type = classType.generics().get(i);
            mapped.put(generic, type);
        }


        var superType = Types.getSuperType(ctx, ctx.model(), classType);
        if (superType != null) {
            putRecursiveGenerics(ctx, superType, mapped,  visited);
        }

        var interfaces = Types.getInterfaces(ctx, ctx.model(), classType);
        for (var interfaceType : interfaces) {
            putRecursiveGenerics(ctx, interfaceType, mapped, visited);
        }

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
            Log.error(ctx, new AttribError.ParameterCountMismatch(toMany.region(), expected));
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

    private static MethodTypedReturn matchCollection(AttributionContext ctx, MethodCollection collection, KExpr.Call expr, List<KExpr> arguments, Map<Generic, KType> mapped, boolean genericsAnnotated) {
        var pointers = matchCollectionWithArgs(ctx, collection, expr, arguments, mapped, genericsAnnotated);
        if (pointers.size() > 1) {
            Log.warn(ctx, expr.region(), "Ambiguous call to '" + collection.name() + "'");

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

        var foundTypes = arguments.stream().map(ref ->
                attribExpr(null, ctx, ref).expr().type()
        ).toList();

        var availableSignatures = new ArrayList<RegionOf<List<KType>>>();

        for (var method : collection) {
            var methodModel = ctx.model().getMethod(method);
            availableSignatures.add(RegionOf.region(method.region(), methodModel.signature().parameters()));
        }

        Log.error(ctx, new AttribError.SignatureMismatch(
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
            List<KExpr> arguments,
            Map<Generic, KType> mapped,
            boolean genericsAnnotated
    ) {

        var available = new ArrayList<PotentialMethodPointer>();
        for (var method : collection) {


            var methodModel = ctx.model().getMethod(method);

            var parameters = methodModel.signature().parameters();

            if (arguments.size() != parameters.size()) {
                continue;
            }

            var mapping = new HashMap<>(mapped);
            if (!putMappedGenerics(ctx, expr, genericsAnnotated, methodModel, mapping)) {
                continue;
            }
            available.add(new PotentialMethodPointer(
                    method,
                    mapping,
                    methodModel
            ));

        }

        var argumentsP = new ArrayList<PrecomputedExpr>();
        for (var argument : arguments) {
            argumentsP.add(new PrecomputedExpr(argument, ctx));
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

                var argument = argumentsP.get(i).getNew(mappedParameter);

                var canConvert = ctx.getConversion(argument.region(), mappedParameter, argument, mutable, false) != null;
                if (!canConvert) {
                    canAssign = false;
                    break;
                }
            }
            if (canAssign) {
                var newArgs = argumentsP.stream().map(ref -> ref.get(null)).toList();
                return List.of(new MethodTypedReturn(
                        returnType,
                        methodPointer,
                        new ArrayList<>(newArgs),
                        List.copyOf(mappedCopy.values()),
                        mappedParameters
                ));
            }

        }

        return List.of();
    }

    record MethodTypedReturn(KType returnType, MethodPointer pointer, List<KExpr> newArguments, List<KType> generics, List<KType> methodParams) {}

    record PotentialMethodPointer(MethodPointer pointer, HashMap<Generic, KType> genericMapping, MethodModel methodModel) {}

    @Contract(mutates = "param5")
    private static boolean putMappedGenerics(
            AttributionContext ctx,
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

                var dummyForGeneric = KType.Resolvable.newInstanceFromGeneric(generic);

                if (!ctx.checking().canAssign(ctx, expr.region(), dummyForGeneric, type, true)) {
                    return false;
                }

                mapped.put(generic, type);
            }
        } else {
            for (var generic : methodGenerics) {
                var type = KType.Resolvable.newInstanceFromGeneric(generic);
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
