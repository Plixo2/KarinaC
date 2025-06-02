package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.utils.KExpr;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.utils.symbols.AssignmentSymbol;
import org.karina.lang.compiler.utils.symbols.LiteralSymbol;
import org.karina.lang.compiler.utils.symbols.MemberSymbol;

import java.lang.reflect.Modifier;

import static org.karina.lang.compiler.stages.attrib.AttributionExpr.*;

public class AssignmentAttrib {

    public static AttributionExpr attribAssignment(@Nullable KType hint, AttributionContext ctx, KExpr.Assignment expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();
        var right = attribExpr(left.type(), ctx, expr.right()).expr();

        var symbol = switch (left) {
            case KExpr.Literal(
                    _, _, var literalSymbol) -> {

                if (literalSymbol instanceof LiteralSymbol.VariableReference(_, Variable variable)) {
                    if (!ctx.isMutable(variable)) {
                        //if (ctx.canEscapeMutable(variable)) {
                        //variable.markEscapeWrapped();
                        //} else {
                        Log.error(ctx,
                                new AttribError.FinalAssignment(
                                        expr.region(), variable.region(), variable.name()));
                        throw new Log.KarinaException();
                        //}
                    }
                    yield new AssignmentSymbol.LocalVariable(variable);
                } else if (literalSymbol instanceof LiteralSymbol.StaticFieldReference(var innerRegion, var fieldPointer, _)) {
                    var fieldModel = ctx.model().getField(fieldPointer);
                    if (Modifier.isFinal(fieldModel.modifiers())) {
                        if (ctx.owningMethod() == null || !ctx.owningMethod().isStaticConstructor()) {
                            Log.error(ctx,
                                    new AttribError.FinalAssignment(
                                            expr.region(), innerRegion,
                                            fieldPointer.name()
                                    )
                            );
                            throw new Log.KarinaException();
                        }
                    }
                    var fieldType = fieldModel.type(); //no need for replacement
                    yield new AssignmentSymbol.StaticField(fieldPointer, fieldType);
                } else {
                    Log.error(ctx, new AttribError.NotSupportedExpression(left.region(), "Unknown assignment symbol on the left side"));
                    throw new Log.KarinaException();
                }
            }
            case KExpr.GetMember(
                    var lhsRegion, var object, _, _, MemberSymbol.FieldSymbol(var pointer, var type, var owner)) -> {

                var fieldModel = ctx.model().getField(pointer);
                if (Modifier.isFinal(fieldModel.modifiers())) {
                    if (ctx.owningMethod() == null || !ctx.owningMethod().isConstructor()) {
                        Log.error(ctx, new AttribError.FinalAssignment(
                                        expr.region(),
                                        fieldModel.region(),
                                        pointer.name()
                        ));
                        throw new Log.KarinaException();
                    }
                }

                yield new AssignmentSymbol.Field(object, pointer, type, owner);
            }
            case KExpr.GetArrayElement getArrayElement -> {
                    yield new AssignmentSymbol.ArrayElement(
                            getArrayElement.left(), getArrayElement.index(),
                            getArrayElement.elementType()
                    );
            }
            default -> {
                Log.error(ctx, new AttribError.NotSupportedExpression(left.region(), "Unknown assignment symbol on the left side"));
                throw new Log.KarinaException();
            }
        };
        right = ctx.makeAssignment(right.region(), left.type(), right);

        return of(ctx, new KExpr.Assignment(
                expr.region(),
                left,
                right,
                symbol
        ));
    }

}
