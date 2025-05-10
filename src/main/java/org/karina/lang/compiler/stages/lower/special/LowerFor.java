package org.karina.lang.compiler.stages.lower.special;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.stages.lower.LowerExpr;
import org.karina.lang.compiler.stages.lower.LoweringContext;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.symbols.CallSymbol;
import org.karina.lang.compiler.utils.symbols.IteratorTypeSymbol;
import org.karina.lang.compiler.utils.symbols.LiteralSymbol;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.utils.Variable;

import java.util.List;

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
///     `For(Region region, NameAndOptType varPart, KExpr iter, KExpr body, @Nullable @Symbol IteratorTypeSymbol symbol)`
public class LowerFor {
    private final KExpr.For aFor;

    public LowerFor(KExpr.For aFor) {
        this.aFor = aFor;
    }

    public KExpr lower(LoweringContext ctx) {
        assert this.aFor.symbol() != null;
        switch (this.aFor.symbol()) {
            case IteratorTypeSymbol.ForArray _ -> {
                Log.temp(this.aFor.region(), "ForArray is not supported yet");
                throw new Log.KarinaException();
            }
            case IteratorTypeSymbol.ForIterable _ -> {
                return lowerIterator(ctx);
            }
            case IteratorTypeSymbol.ForRange _ -> {
                Log.temp(this.aFor.region(), "ForRange is not supported yet");
                throw new Log.KarinaException();
            }
        }
    }

    private KExpr lowerIterator(LoweringContext ctx) {
        var interVariableName = "$iter" + ctx.syntheticCounter().incrementAndGet();
        var region = this.aFor.region();
        assert this.aFor.symbol() != null;


        var iterType = this.aFor.symbol().variable().type();
        var iteratorClassType = KType.ITERATOR(iterType);
        Variable iterVariable = new Variable(
                region,
                interVariableName,
                iteratorClassType,
                false,
                false,
                true
        );

        var iterableToIteratorMethod = MethodPointer.of(
                region,
                ClassPointer.of(region, ClassPointer.ITERABLE_PATH),
                "iterator",
                Signature.emptyArgs(KType.ITERATOR(KType.ROOT))
        );
        var iterCallSymbol = new CallSymbol.CallVirtual(
                iterableToIteratorMethod,
                List.of(),
                iteratorClassType,
                true
        );

        var iterValue = new KExpr.Call(
                region,
                this.aFor.iter(),
                ImmutableList.of(),
                ImmutableList.of(),
                iterCallSymbol
        );

        KExpr varLet = new KExpr.VariableDefinition(
                region,
                RegionOf.region(this.aFor.varPart().region(), interVariableName),
                null,
                iterValue,
                iterVariable
        );

        KExpr condition = new KExpr.Call(
                region,
                new KExpr.Literal(region, interVariableName, new LiteralSymbol.VariableReference(region, iterVariable)),
                List.of(),
                List.of(),
                new CallSymbol.CallVirtual(
                        MethodPointer.of(
                                region,
                                iteratorClassType.pointer(),
                                "hasNext",
                                Signature.emptyArgs(KType.BOOL)
                        ),
                        List.of(),
                        KType.BOOL,
                        true
                )
        );

        var nextCall = new KExpr.Call(
                region,
                new KExpr.Literal(region, interVariableName, new LiteralSymbol.VariableReference(region, iterVariable)),
                List.of(),
                List.of(),
                new CallSymbol.CallVirtual(
                        MethodPointer.of(
                                region,
                                iteratorClassType.pointer(),
                                "next",
                                Signature.emptyArgs(KType.ROOT)
                        ),
                        List.of(),
                        iterType,
                        true
                )
        );

        KExpr newBody = new KExpr.Block(
                region,
                List.of(
                        new KExpr.VariableDefinition(
                                region,
                                RegionOf.region(region, interVariableName),
                                null,
                                nextCall,
                                this.aFor.symbol().variable()
                        ),
                        LowerExpr.lower(ctx, this.aFor.body())
                ),
                KType.NONE,
                false
        );

        KExpr whileLoop = new KExpr.While(
                region,
                condition,
                newBody
        );
        return new KExpr.Block(
                region,
                List.of(
                        varLet,
                        whileLoop
                ),
                KType.NONE,
                false
        );
    }
}
