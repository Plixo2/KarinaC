package org.karina.lang.compiler.stages.lower;

import org.karina.lang.compiler.objects.KExpr;

public class LowerExpr {


    public static KExpr lower(LoweringContext context, KExpr expr) {
        return switch (expr) {
            case KExpr.Assignment assignment -> null;
            case KExpr.Binary binary -> null;
            case KExpr.Block block -> null;
            case KExpr.Boolean aBoolean -> aBoolean;
            case KExpr.Branch branch -> null;
            case KExpr.Break aBreak -> null;
            case KExpr.Call call -> null;
            case KExpr.Cast cast -> null;
            case KExpr.Closure closure -> null;
            case KExpr.Continue aContinue -> null;
            case KExpr.CreateArray createArray -> null;
            case KExpr.CreateObject createObject -> null;
            case KExpr.For aFor -> null;
            case KExpr.GetArrayElement getArrayElement -> null;
            case KExpr.GetMember getMember -> null;
            case KExpr.IsInstanceOf isInstanceOf -> null;
            case KExpr.Literal literal -> null;
            case KExpr.Match match -> null;
            case KExpr.Number number -> number;
            case KExpr.Return aReturn -> null;
            case KExpr.Self self -> null;
            case KExpr.SpecialCall specialCall -> null;
            case KExpr.StringExpr stringExpr -> stringExpr;
            case KExpr.StringInterpolation stringInterpolation -> null;
            case KExpr.Throw aThrow -> null;
            case KExpr.Unary unary -> null;
            case KExpr.Unwrap unwrap -> null;
            case KExpr.VariableDefinition variableDefinition -> null;
            case KExpr.While aWhile -> null;
            case KExpr.StaticPath staticPath -> null;
        };
    }


    ///
    ///Lower For loop into While Loop
    ///
    ///For can iterate over arrays, ranges, and iterables.
    ///
    ///
    ///For v in A { ... }
    ///Expand to:
    ///
    ///- Iterable:
    ///     ```
    ///     var $iter = A.iterator();
    ///     while ($iter.hasNext()) {
    ///         let v = $iter.next();
    ///         ...
    ///     }
    ///     ```
    /// - Array:
    ///     ```
    ///     var $iter = -1; // Start at -1 to account for the increment at the start of the loop
    ///     var $len = A.length;
    ///     while ({$iter = $iter + 1
    ///             $iter < $len}) {
    ///         var v = A[$iter];
    ///         ...
    ///     }
    ///     ```
    /// - Range:
    ///     Similar to Array
    ///     ```
    ///     var $step = A.step;
    ///     var $iter = A.start - $step;
    ///     var $end = A.end;
    ///     while ({$iter = $iter + step
    ///             $iter < $end}) {
    ///         var v = $iter;
    ///         ...
    ///     }
    ///     ```
    ///
    ///
    ///The field `symbol` is storing the type of iteration and the variable reference used.
    ///
    /// Signature:
    ///     For(Region region, NameAndOptType varPart, KExpr iter, KExpr body, @Nullable @Symbol IteratorTypeSymbol symbol)
    private static KExpr lowerFor(LoweringContext ctx, KExpr.For aFor) {
        var region = aFor.region();



        return null;
    }

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
    ///
    private static KExpr lowerClosure(LoweringContext ctx, KExpr.Closure closure) {
        return null;
    }
}
