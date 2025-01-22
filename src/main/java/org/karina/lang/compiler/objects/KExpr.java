package org.karina.lang.compiler.objects;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.symbols.*;
import org.karina.lang.compiler.utils.*;

import java.math.BigDecimal;
import java.util.List;

public sealed interface KExpr {
    Region region();

    default KType type() {
        var type = getType(this);
        if (type == null) {
            Log.temp(region(), "Type is null for " + this.getClass().getSimpleName());
            throw new Log.KarinaException();
        }
        return type.unpack();
    }

    private static KType getType(KExpr expr) {
        switch (expr) {
            case Assignment assignment -> {
                return new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
            case Binary binary -> {
                if (binary.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return binary.symbol().type();
            }
            case Block block -> {
                return block.symbol();
            }
            case Boolean aBoolean -> {
                return new KType.PrimitiveType(KType.KPrimitive.BOOL);
            }
            case Branch branch -> {
                return branch.symbol();
            }
            case Break aBreak -> {
                return new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
            case Call call -> {
                if (call.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return call.symbol().returnType();
            }
            case Cast cast -> {
                if (cast.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return cast.symbol().type();
            }
            case Closure closure -> {
                if (closure.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return closure.symbol().type();
            }
            case Continue aContinue -> {
                return new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
            case CreateArray createArray -> {
                return createArray.symbol();
            }
            case CreateObject createObject -> {
                return createObject.symbol();
            }
            case For aFor -> {
                return new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
            case GetArrayElement getArrayElement -> {
                return getArrayElement.elementType();
            }
            case GetMember getMember -> {
                if (getMember.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return getMember.symbol().type();
            }
            case IsInstanceOf isInstanceOf -> {
                return new KType.PrimitiveType(KType.KPrimitive.BOOL);
            }
            case Literal literal -> {
                if (literal.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return literal.symbol().type();
            }
            case Match match -> {

            }
            case Number number -> {
                if (number.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return number.symbol().type();
            }
            case Return aReturn -> {
                return new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
            case Self self -> {
                if (self.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return self.symbol().type();
            }
            case StringExpr stringExpr -> {
                var path = new ObjectPath("java", "lang", "String");
                return new KType.ClassType(
                        //TODO not ok, fix with validated path
                        ClassPointer.of(path),
                        List.of()
                );
            }
            case Unary unary -> {
                if (unary.symbol() == null) {
                    Log.temp(expr.region(), "Symbol is null");
                    return null;
                }
                return unary.symbol().type();
            }
            case VariableDefinition variableDefinition -> {
                return new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
            case While aWhile -> {
                return new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
            case Throw aThrow -> {
                return new KType.PrimitiveType(KType.KPrimitive.VOID);
            }
            case Super aSuper -> {

            }
        }
        Log.temp(expr.region(), "Unimplemented type for " + expr.getClass().getSimpleName());
        throw new Log.KarinaException();
    }

    record Block(Region region, List<KExpr> expressions, @Nullable @Symbol KType symbol) implements KExpr {}
    record VariableDefinition(Region region, RegionOf<String> name, @Nullable KType hint, KExpr value, @Nullable @Symbol Variable symbol) implements KExpr {}
    record Branch(Region region, KExpr condition, KExpr thenArm, @Nullable ElsePart elseArm, @Nullable BranchPattern branchPattern, @Nullable @Symbol KType symbol) implements KExpr {}
    record While(Region region, KExpr condition, KExpr body) implements KExpr {}
    record For(Region region, RegionOf<String> name , KExpr iter, KExpr body, @Nullable @Symbol Variable symbol) implements KExpr {}
    record Return(Region region, @Nullable KExpr value, @Nullable @Symbol KType yieldType) implements KExpr {}
    record Break(Region region) implements KExpr {}
    record Continue(Region region) implements KExpr {}
    record Binary(Region region, KExpr left, RegionOf<BinaryOperator> operator, KExpr right, @Nullable @Symbol BinOperatorSymbol symbol) implements KExpr {}
    record Unary(Region region, RegionOf<UnaryOperator> operator, KExpr value, @Nullable @Symbol UnaryOperatorSymbol symbol) implements KExpr {}
    record IsInstanceOf(Region region, KExpr left, KType isType) implements KExpr {}
    record GetMember(Region region, KExpr left, RegionOf<String> name, @Nullable @Symbol MemberSymbol symbol) implements KExpr {}
    record Call(Region region, KExpr left, List<KType> generics, List<KExpr> arguments, @Nullable @Symbol CallSymbol symbol) implements KExpr {}
    record GetArrayElement(Region region, KExpr left, KExpr index, @Nullable @Symbol KType elementType) implements KExpr {}
    record Cast(Region region, KExpr expression, KType asType, @Nullable @Symbol CastSymbol symbol) implements KExpr {}
    record Assignment(Region region, KExpr left, KExpr right, @Nullable @Symbol AssignmentSymbol symbol) implements KExpr {}
    record CreateArray(Region region, @Nullable KType hint, List<KExpr> elements, @Nullable @Symbol KType.ArrayType symbol) implements KExpr {}
    record Number(Region region, BigDecimal number, boolean decimalAnnotated, @Nullable @Symbol NumberSymbol symbol) implements KExpr {}
    record Boolean(Region region, boolean value) implements KExpr {}
    record Literal(Region region, String name, @Nullable @Symbol LiteralSymbol symbol) implements KExpr {}
    record Self(Region region, @Nullable @Symbol Variable symbol) implements KExpr {}
    record Super(Region region) implements KExpr {}
    record StringExpr(Region region, String value) implements KExpr {}
    record Match(Region region, KExpr value, List<MatchPattern> cases) implements KExpr {}
    record CreateObject(Region region, KType createType, List<NamedExpression> parameters, @Nullable @Symbol KType.ClassType symbol) implements KExpr {}
    record Closure(Region region, List<NameAndOptType> args, @Nullable KType returnType, List<KType> interfaces, KExpr body, @Nullable @Symbol ClosureSymbol symbol) implements KExpr {}
    record Throw(Region region, KExpr value, @Nullable @Symbol KType.ClassType symbol) implements KExpr {}


}
