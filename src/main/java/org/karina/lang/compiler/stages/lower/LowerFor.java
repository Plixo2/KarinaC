package org.karina.lang.compiler.stages.lower;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.model_api.pointer.MethodPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.symbols.CallSymbol;
import org.karina.lang.compiler.symbols.IteratorTypeSymbol;
import org.karina.lang.compiler.symbols.LiteralSymbol;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.utils.Variable;

import java.util.List;

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
        var interVariableName = "$iter" + ctx.syntheticCounter().getAndIncrement();
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
