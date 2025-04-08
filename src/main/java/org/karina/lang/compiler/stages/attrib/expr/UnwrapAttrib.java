package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.Types;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.symbols.UnwrapSymbol;
import org.karina.lang.compiler.utils.Region;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.attribExpr;
import static org.karina.lang.compiler.stages.attrib.AttributionExpr.of;

public class UnwrapAttrib {
    public static AttributionExpr attribUnwrap(
            @Nullable KType hint, AttributionContext ctx, KExpr.Unwrap expr) {

        //todo make unwrap error on result compatible with return type via makeAssignment

        var hintInner = getHintType(hint, expr);

        var left = attribExpr(hintInner, ctx, expr.left()).expr();

        var leftType = left.type();
        var region = expr.region();
        if (!(leftType instanceof KType.ClassType classType)) {
            Log.attribError(new AttribError.NotAClass(region, leftType));
            throw new Log.KarinaException();
        }

        if (!(ctx.returnType() instanceof KType.ClassType classTypeReturn)) {
            Log.attribError(new AttribError.ControlFlow(region, "Return type not compatible with ? operator"));
            throw new Log.KarinaException();
        }

        UnwrapSymbol symbol;
        if (isOptionIndirect(region, ctx, classType)) {
            if (isOption(region, classTypeReturn)) {
                //none can always be returned
                symbol = new UnwrapSymbol.UnwrapOptional(classType.generics().getFirst());
            } else {
                Log.attribError(new AttribError.TypeMismatch(region, classType, classTypeReturn));
                throw new Log.KarinaException();
            }
        } else if (isResultIndirect(region, ctx, classType)) {
            var errorType = classType.generics().get(1);
            var okType = classType.generics().getFirst();
            if (isResult(region, classTypeReturn)) {
                //check if second generic is compatible with return type
                var expectedReturn = classTypeReturn.generics().get(1);

                if (!ctx.checking().canAssign(region, expectedReturn, errorType, true)) {
                    Log.attribError(new AttribError.TypeMismatch(region, expectedReturn, errorType));
                    throw new Log.KarinaException();
                }

                symbol = new UnwrapSymbol.UnwrapResult(okType, errorType);
            } else {
                Log.attribError(new AttribError.TypeMismatch(region, classType, classTypeReturn));
                throw new Log.KarinaException();
            }

        } else {
            Log.attribError(new AttribError.NotSupportedType(region, leftType));
            throw new Log.KarinaException();
        }


        return of(
                ctx,
                new KExpr.Unwrap(
                        region,
                        left,
                        symbol
                )
        );
    }

    private static @Nullable KType getHintType(@Nullable KType hint, KExpr.Unwrap expr) {
        if (hint instanceof KType.ClassType classType) {
            if (isOption(expr.region(), classType) || isResult(expr.region(), classType)) {
                return classType.generics().getFirst();
            }
        }
        return null;
    }


    private static boolean isOption(Region region, KType.ClassType classType) {
        if (classType.pointer().path().equals(ClassPointer.OPTION_PATH)) {
            if (classType.generics().size() != 1) {
                Log.temp(region, "Option type must have 1 generics");
                throw new Log.KarinaException();
            }
            return true;
        }
        return false;
    }

    private static boolean isResult(Region region, KType.ClassType classType) {
        if (classType.pointer().path().equals(ClassPointer.RESULT_PATH)) {
            if (classType.generics().size() != 2) {
                Log.temp(region, "Result type must have 2 generics");
                throw new Log.KarinaException();
            }
            return true;
        }
        return false;
    }

    // dont have to map generics to upper type, generics in inner type and in outer interface are the same

    private static boolean isResultIndirect(Region region, AttributionContext ctx, KType.ClassType classType) {
        if (isResult(region, classType)) {
            return true;
        }
        var interfaces = Types.getInterfaces(ctx.model(), classType);
        if (interfaces.size() == 1) {
            return isResult(region, interfaces.getFirst());
        }
        return false;
    }

    private static boolean isOptionIndirect(Region region, AttributionContext ctx, KType.ClassType classType) {
        if (isOption(region, classType)) {
            return true;
        }
        var interfaces = Types.getInterfaces(ctx.model(), classType);
        if (interfaces.size() == 1) {
            return isOption(region, interfaces.getFirst());
        }
        return false;
    }
}
