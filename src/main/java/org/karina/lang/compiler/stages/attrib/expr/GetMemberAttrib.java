package org.karina.lang.compiler.stages.attrib.expr;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.Span;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.AttribError;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.AttributionExpr;
import org.karina.lang.compiler.stages.attrib.AttributionContext;
import org.karina.lang.compiler.symbols.LiteralSymbol;
import org.karina.lang.compiler.symbols.MemberSymbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GetMemberAttrib extends AttributionExpr {
    public static AttributionExpr attribGetMember(
            @Nullable KType hint, AttributionContext ctx, KExpr.GetMember expr) {

        var left = attribExpr(null, ctx, expr.left()).expr();
        MemberSymbol symbol;

        if (left instanceof KExpr.Literal(var ignored, var name, var symbol1)) {
            Optional<KTree.KFunction> staticFunc = Optional.empty();

            if (symbol1 instanceof LiteralSymbol.StructReference(Span region, ObjectPath path)) {
                var item = KTree.findAbsolutItem(ctx.root(), path);
                if (item instanceof KTree.KStruct struct) {
                    staticFunc = struct.functions().stream()
                                       .filter(ref -> ref.name().equals(expr.name()) && ref.self() == null).findFirst();
                }
            } else if (symbol1 instanceof LiteralSymbol.InterfaceReference(
                    Span region, ObjectPath path
            )) {
                var item = KTree.findAbsolutItem(ctx.root(), path);
                if (item instanceof KTree.KInterface kInterface) {
                    staticFunc = kInterface.functions().stream()
                                           .filter(ref -> ref.name().equals(expr.name()) && ref.self() == null)
                                           .findFirst();

                }
            }

            if (staticFunc.isPresent()) {
                if (staticFunc.get().self() != null) {
                    Log.temp(expr.region(), "Not a Static Function");
                    throw new Log.KarinaException();
                }
                return of(
                        ctx, new KExpr.Literal(
                                expr.region(), expr.name().value(),
                                new LiteralSymbol.StaticFunction(
                                        expr.region(),
                                        staticFunc.get().path()
                                )
                        )
                );
            }
        }
        if (left.type() instanceof KType.ArrayType arrayType) {
            if (expr.name().value().equals("size")) {
                symbol = new MemberSymbol.ArrayLength(expr.region());
                return of(ctx, new KExpr.GetMember(expr.region(), left, expr.name(), symbol));
            } else {
                Log.attribError(new AttribError.UnknownField(expr.name().region(), expr.name().value()));
                throw new Log.KarinaException();
            }
        }

        if (!(left.type() instanceof KType.ClassType classType)) {
            Log.attribError(new AttribError.NotAStruct(expr.left().region(), left.type()));
            throw new Log.KarinaException();
        }

        var item = KTree.findAbsolutItem(ctx.root(), classType.path());
        if (item instanceof KTree.KInterface kInterface) {
            var functionToGet =
                    kInterface.functions().stream().filter(ref -> ref.name().equals(expr.name()) && ref.self() != null)
                          .findFirst();
            if (functionToGet.isPresent()) {
                var function = functionToGet.get();
                symbol = new MemberSymbol.InterfaceFunctionSymbol(
                        expr.region(),
                        classType,
                        function.path()
                );
            } else {
                Log.attribError(
                        new AttribError.UnknownField(expr.name().region(), expr.name().value()));
                throw new Log.KarinaException();
            }
        } else if (item instanceof KTree.KStruct struct) {

            var fieldToGet = struct.fields().stream().filter(ref -> ref.name().equals(expr.name()))
                                   .findFirst();
            var functionToGet =
                    struct.functions().stream().filter(ref -> ref.name().equals(expr.name())  && ref.self() != null)
                          .findFirst();

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

                symbol =
                        new MemberSymbol.FieldSymbol(fieldType, field.path(), field.name().value(), left.type());
            } else if (functionToGet.isPresent()) {
                var function = functionToGet.get();
                symbol = new MemberSymbol.VirtualFunctionSymbol(
                        expr.region(), classType,
                        function.path()
                );
            } else {

                KTree.KFunction interfaceFunctionToGet = null;

                for (var implBlock : struct.implBlocks()) {
                    for (var function : implBlock.functions()) {
                        if (function.name().equals(expr.name()) && function.self() != null) {
                            interfaceFunctionToGet = function;
                            break;
                        }
                    }
                }

                if (interfaceFunctionToGet != null) {
                    symbol = new MemberSymbol.VirtualFunctionSymbol(
                            expr.region(),
                            classType,
                            interfaceFunctionToGet.path()
                    );
                } else {
                    Log.attribError(new AttribError.UnknownField(
                            expr.name().region(),
                            expr.name().value()
                    ));
                    throw new Log.KarinaException();
                }
            }
        } else {
            Log.attribError(new AttribError.NotAStruct(expr.left().region(), left.type()));
            throw new Log.KarinaException();
        }


        return of(ctx, new KExpr.GetMember(expr.region(), left, expr.name(), symbol));
    }
}
