package org.karina.lang.compiler.stages.lower;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.karina.lang.compiler.jvm.model.MutableModel;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.MethodModel;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.Types;
import org.karina.lang.compiler.stages.attrib.AttributionItem;
import org.karina.lang.compiler.symbols.CallSymbol;
import org.karina.lang.compiler.symbols.LiteralSymbol;
import org.karina.lang.compiler.utils.*;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.util.*;

///
/// Lower a closure into a new class that implements all given interfaces.
///
/// This method has the capture variable defined outside the
/// closure as a field in the new class, initialized in the constructor.
///
/// This includes all variables and any reference to self.
///
/// Since the closure is a new class, but can reference private fields of the parent class,
/// the new synthetic class has to be inserted as a nestHostMember of the parent class and as a inner class
/// The Synthetic class should also link to the parent class as the outer class.
///
/// The code of the closure is moved to the method defined by the primary interface method that needs to be implemented.
///
/// The primary interface is the first interface in the list of interfaces.
/// The primary interface is also used for converting the
/// function type in fields and method signatures into a class type.
///
/// Every other method from the other interfaces are linked to the primary method.
///
/// Example:
/// ```
/// // org.example
///
/// struct Main {
///     private x: float
///     fn main(self, v: float) {
///         fn (x: string) impl java::lang::util::CustomFunction<String, String> -> String {
///             var selfX = self.x;
///             println('$x $selfX $v');
///             return "Hello";
///         }
///     }
/// }
///
/// ```
///
/// will get compiled to the following class:
///
/// ```
/// class Main$1 implements lang.karina.internal.functions.Function1_1<String, String>, java.lang.util.CustomFunction<String, String> {
///
///     private final Main selfRef;
///     private final float v;
///
///     public Main$1(Main selfRef, float v) {
///         this.selfRef = selfRef;
///         this.v = v;
///     }
///
///     \@Override
///     public String apply(String x) {
///         java.lang.System.out.println(x + " " + this.selfRef.x + " " + self.v);
///         return "Hello";
///     }
///
///     \@Override
///     public String invoke(String x) {
///         return apply(x);
///     }
/// }
///
/// ```
///
/// Signature:
///     `Closure(Region region, List<NameAndOptType> args, @Nullable KType returnType, List<? extends KType> interfaces, KExpr body, @Nullable @Symbol ClosureSymbol symbol)`
///
/// ClosureSymbol:
///     `ClosureSymbol(KType.FunctionType functionType, List<Variable> captures, boolean captureSelf, @Nullable Variable self, List<Variable> argVariables)`
///
public class LowerClosure {
    Region region;
    List<String> names;
    List<KType> argTypes;
    KType returnType;
    List<Variable> captures;
    List<Variable> argumentVariable;
    List<? extends KType> interfaces;
    KExpr body;

    public LowerClosure(KExpr.Closure closure) {
        this.region = closure.region();
        this.names = closure.args().stream().map(ref -> ref.name().value()).toList();
        this.body = closure.body();

        var symbol = closure.symbol();
        assert symbol != null;
        this.returnType = symbol.functionType().returnType();
        this.argTypes = symbol.functionType().arguments();
        this.interfaces = symbol.functionType().interfaces();

        this.captures = new ArrayList<>(symbol.captures());
        this.argumentVariable = symbol.argVariables();

        var captureSelf = symbol.captureSelf();
        var self = symbol.self();
        assert !captureSelf || self != null;
        if (self != null) {
            this.captures.add(self);
        }
        if (this.interfaces.isEmpty()) {
            Log.temp(this.region, "Closure has no primary interface");
            throw new Log.KarinaException();
        }

    }

    private ClassModel createClassModel(LoweringContext ctx) {
        var primary = this.interfaces.getFirst();
        var name = ctx.definitionClass().name() + "$" + ctx.definitionMethod().name() + "$" + ctx.syntheticCounter().incrementAndGet();
        var path = ctx.definitionClass().path().append(name);
        var classPointer = ClassPointer.of(this.region, path);
        if (!(ctx.owningClass() instanceof KClassModel outerClass)) {
            Log.temp(this.region, "Closure outer class is not a class model");
            throw new Log.KarinaException();
        }

        var interfacesAsClasses = new ArrayList<KType.ClassType>();
        //TODO replace List.of(primary) with this.interfaces, but check for duplicates or should this be done in the attrib stage
        for (var anInterface : List.of(primary)) {
            if (anInterface instanceof KType.ClassType classType) {
                interfacesAsClasses.add(classType);
            } else {
                Log.temp(this.region, "Closure interface is not a class type");
                throw new Log.KarinaException();
            }
        }

        var fields = new ArrayList<KFieldModel>();

        for (Variable fieldName : this.captures) {
            var field = new KFieldModel(
                    fieldName.name(),
                    fieldName.type(),
                    Modifier.PRIVATE | Modifier.FINAL,
                    this.region,
                    classPointer
            );
            fields.add(field);
        }

        var methods = new ArrayList<KMethodModel>();

        var classModel = new KClassModel(
                name,
                path,
                Modifier.PUBLIC | Modifier.FINAL | Opcodes.ACC_SYNTHETIC,
                KType.ROOT,
                outerClass,
                ImmutableList.copyOf(interfacesAsClasses),
                List.of(),
                ImmutableList.copyOf(fields),
                methods,
                ImmutableList.of(),
                ImmutableList.of(),
                ImmutableList.of(),
                new ArrayList<>(),
                ImmutableList.of(),
                this.region.source(),
                this.region,
                null
        );


        var prev = ctx.newClasses().insert(classModel.path(), classModel);

        if (prev != null) {
            Log.temp(this.region, "Closure class already exists");
            throw new Log.KarinaException();
        }

        for (var interfaceAsClass : interfacesAsClasses) {
            methods.add(createMethodModel(ctx, interfaceAsClass, classPointer, classModel));
        }
        var constructor = createDefaultConstructor(classPointer, fields, Modifier.PUBLIC);
       // DebugWriter.write(constructor, "resources/closureConstructorA.txt");
        var newModel = new MutableModel(ctx.model(), ctx.newClasses());
        var attribConstructor = AttributionItem.attribMethod(newModel, classModel, StaticImportTable.EMPTY, constructor);
      //  DebugWriter.write(attribConstructor, "resources/closureConstructorB.txt");
        methods.add(attribConstructor);

        return classModel;
    }

    public static MethodModel getMethodToImplement(Region region, ClassModel classModel) {
        for (var method : classModel.methods()) {
            if (Modifier.isAbstract(method.modifiers())) {
                //there should only be one abstract method
                return method;
            }
        }
        Log.temp(region, "Closure class has no method to implement, this should not happen");
        throw new Log.KarinaException();
    }

    private KMethodModel createMethodModel(LoweringContext ctx, KType.ClassType currentInterfaceToImplement, ClassPointer outer, ClassModel outerClass) {
        var classModel = ctx.model().getClass(currentInterfaceToImplement.pointer());
        var toImplement = getMethodToImplement(this.region, classModel);
        var name = toImplement.name();


        var mapped = getGenericMapping(ctx, currentInterfaceToImplement);
        var methodReturnType = Types.projectGenerics(toImplement.signature().returnType(), mapped);

        var mappedParameters = toImplement.signature().parameters().stream().map(ref ->
                Types.projectGenerics(ref, mapped)
        ).toList();

        var parameters = ImmutableList.copyOf(mappedParameters);
        var signature = new Signature(parameters, methodReturnType);

//        Log.record("Returning method: " + methodReturnType);

        // we generics, we implement for one type only
        var outerClassType = new KType.ClassType(outerClass.pointer(), List.of());
        var self = new Variable(classModel.region(), "<self>", outerClassType, false, true);

        var variables = new ArrayList<>(this.argumentVariable);
        variables.add(self);

        //TODO map correct generics
        var methodModel = new KMethodModel(
                name,
                Modifier.PUBLIC,
                signature,
                ImmutableList.copyOf(this.names),
                toImplement.generics(),
                null,
                ImmutableList.of(),
                this.region,
                outer,
                variables
        );





        var replacement = new LoweringContext.ClosureReplacement(
                outerClassType,
                self,
                List.copyOf(this.captures)
        );


        var currentReplacement = new ArrayList<>(ctx.toReplace());
        currentReplacement.add(replacement);

        var newCtx =  new LoweringContext(
                ctx.newClasses(),
                ctx.syntheticCounter(),
                ctx.model(),
                ctx.definitionMethod(),
                methodModel,
                ctx.definitionClass(),
                outerClass,
                currentReplacement
        );


        var bodyExpr = LowerExpr.lower(newCtx, this.body);
        methodModel.setExpression(bodyExpr);

        return methodModel;
    }

    private static @NotNull HashMap<Generic, KType> getGenericMapping(LoweringContext ctx, KType.ClassType type) {
        var classModel = ctx.model().getClass(type.pointer());
        var mapped = new HashMap<Generic, KType>();
        for (var i = 0; i < classModel.generics().size(); i++) {
            var generic = classModel.generics().get(i);
            var genmap = type.generics().get(i);
            mapped.put(generic, genmap);
        }
        return mapped;
    }


    //TODO attribute this
    public KMethodModel createDefaultConstructor(
            ClassPointer classPointer,
            List<KFieldModel> fields,
            int mods
    ) {
        var name = "<init>";

        var paramTypes = ImmutableList.copyOf(fields.stream().map(KFieldModel::type).toList());
        var paramNames = ImmutableList.copyOf(fields.stream().map(KFieldModel::name).toList());
        var signature = new Signature(paramTypes, KType.NONE);
        var generics = ImmutableList.<Generic>of();

        var expressions = new ArrayList<KExpr>();

        var superLiteral = new KExpr.SpecialCall(
                this.region,
                new InvocationType.SpecialInvoke(
                        "<init>",
                        KType.ROOT
                )
        );
        var superCall = new KExpr.Call(this.region, superLiteral, List.of(), List.of(), null);
        expressions.add(superCall);

        for (var field : fields) {
            var self = new KExpr.Self(this.region, null);
            var fieldName = RegionOf.region(this.region, field.name());
            var rhs = new KExpr.Literal(this.region, field.name(), null);
            var lhs = new KExpr.GetMember(this.region, self, fieldName, false, null);
            var assign = new KExpr.Assignment(this.region, lhs, rhs, null);
            expressions.add(assign);
        }




        var expression = new KExpr.Block(this.region, expressions, null, false);

        return new KMethodModel(
                name,
                mods,
                signature,
                paramNames,
                generics,
                expression,
                ImmutableList.of(),
                this.region,
                classPointer,
                List.of()
        );
    }

    private KExpr createInstance(LoweringContext ctx) {
        var classModel = createClassModel(ctx);

        var allClasses = new HashSet<KClassModel>();

        var outest = ctx.definitionClass();
        var currentOutest = outest;
        while (currentOutest != null) {
            outest = currentOutest;
            currentOutest = outest.outerClass();
        }
        assert outest != null;
        putAllClassesDeep(List.of(outest), allClasses);

        var nestMemberToAdd = List.of(classModel.pointer());
        for (var possibleAccess : allClasses) {
            possibleAccess.updateNestMembers(nestMemberToAdd);
        }


        if (!classModel.generics().isEmpty()) {
            Log.temp(this.region, "Closure class has generics");
            throw new Log.KarinaException();
        }

        var classType = new KType.ClassType(classModel.pointer(), List.of());
        var superLiteral = new KExpr.SpecialCall(
                this.region,
                new InvocationType.NewInit(classType)
        );
        var inits = classModel.getMethodCollectionShallow("<init>");
        if (inits.isEmpty()) {
            Log.temp(this.region, "Closure class has no inits");
            throw new Log.KarinaException();
        }
        var symbol = new CallSymbol.CallSuper(
                inits.methods().getFirst(),
                List.of(),
                classType,
                superLiteral.invocationType()
        );
        var args = getArgs();
        return new KExpr.Call(this.region, superLiteral, List.of(), args, symbol);
    }

    private List<KExpr> getArgs() {
        var exprs = new ArrayList<KExpr>();
        for (var capture : this.captures) {
            var symbol = new LiteralSymbol.VariableReference(
                    this.region,
                    capture
            );
            var literal = new KExpr.Literal(this.region, capture.name(), symbol);
            exprs.add(literal);
        }
        return exprs;
    }

    public KExpr getExpression(LoweringContext ctx) {
        return createInstance(ctx);
    }

    private void putAllClassesDeep(List<? extends ClassModel> children, Set<KClassModel> collection) {
        for (var child : children) {
            if (child instanceof KClassModel kChild) {
                collection.add(kChild);
            }
            putAllClassesDeep(child.innerClasses(), collection);
        }
    }
}
