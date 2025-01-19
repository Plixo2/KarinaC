package org.karina.lang.compiler.stages.attrib;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.attrib.expr.*;

import java.util.ArrayList;
import java.util.Map;

@Getter
@Accessors(fluent = true)
public class AttributionExpr {

    KExpr expr;
    AttributionContext ctx;

    protected static AttributionExpr attribExpr(@Nullable KType hint, AttributionContext ctx, KExpr expr) {
        if (hint != null) {
            hint = hint.unpack();
        }
        return switch (expr) {
            case KExpr.Assignment assignment -> AssignmentAttrib.attribAssignment(hint, ctx, assignment);
            case KExpr.Binary binary -> BinaryAttrib.attribBinary(hint, ctx, binary);
            case KExpr.Block block -> BlockAttrib.attribBlock(hint, ctx, block);
            case KExpr.Boolean aBoolean -> BooleanAttrib.attribBoolean(hint, ctx, aBoolean);
            case KExpr.Branch branch -> BranchAttrib.attribBranch(hint, ctx, branch);
            case KExpr.Break aBreak -> BreakAttrib.attribBreak(hint, ctx, aBreak);
            case KExpr.Call call -> CallAttrib.attribCall(hint, ctx, call);
            case KExpr.Cast cast -> CastAttrib.attribCast(hint, ctx, cast);
            case KExpr.Closure closure -> ClosureAttrib.attribClosure(hint, ctx, closure);
            case KExpr.Continue aContinue -> ContinueAttrib.attribContinue(hint, ctx, aContinue);
            case KExpr.CreateArray createArray -> CreateArrayAttrib.attribCreateArray(hint, ctx, createArray);
            case KExpr.CreateObject createObject -> CreateObjectAttrib.attribCreateObject(hint, ctx, createObject);
            case KExpr.For aFor -> ForAttrib.attribFor(hint, ctx, aFor);
            case KExpr.GetArrayElement getArrayElement -> GetArrayElementAttrib.attribGetArrayElement(hint, ctx, getArrayElement);
            case KExpr.GetMember getMember -> GetMemberAttrib.attribGetMember(hint, ctx, getMember);
            case KExpr.IsInstanceOf isInstanceOf -> InstanceOfAttrib.attribInstanceOf(hint, ctx, isInstanceOf);
            case KExpr.Literal literal -> LiteralAttrib.attribLiteral(hint, ctx, literal);
            case KExpr.Match match -> MatchAttrib.attribMatch(hint, ctx, match);
            case KExpr.Number number -> NumberAttrib.attribNumber(hint, ctx, number);
            case KExpr.Return aReturn -> ReturnAttrib.attribReturn(hint, ctx, aReturn);
            case KExpr.Self self -> SelfAttrib.attribSelf(hint, ctx, self);
            case KExpr.StringExpr stringExpr -> StringAttrib.attribStringExpr(hint, ctx, stringExpr);
            case KExpr.Unary unary -> UnaryAttrib.attribUnary(hint, ctx, unary);
            case KExpr.VariableDefinition variableDefinition -> VariableDefinitionAttrib.attribVariableDefinition(hint, ctx, variableDefinition);
            case KExpr.While aWhile -> WhileAttrib.attribWhile(hint, ctx, aWhile);
            case KExpr.Throw aThrow -> ThrowAttrib.attribThrow(hint, ctx, aThrow);
        };
    }

    public static AttributionExpr of(AttributionContext ctx, KExpr expr) {
        var attribExpr = new AttributionExpr();
        attribExpr.expr = expr;
        attribExpr.ctx = ctx;
        return attribExpr;
    }

    public static KType replaceType(KType original, Map<Generic, KType> generics) {
        return switch (original) {
            case KType.ArrayType(var element) -> {
                var newElement = replaceType(element, generics);
                yield new KType.ArrayType(newElement);
            }
            case KType.ClassType(var path, var classGenerics) -> {
                var newGenerics = new ArrayList<KType>();
                for (var generic : classGenerics) {
                    var newType = replaceType(generic, generics);
                    newGenerics.add(newType);
                }
                yield new KType.ClassType(path, newGenerics);
            }
            case KType.FunctionType functionType -> {

                var returnType = functionType.returnType();
                if (returnType != null) {
                    returnType = replaceType(returnType, generics);
                }
                var newParameters = new ArrayList<KType>();
                for (var parameter : functionType.arguments()) {
                    var newType = replaceType(parameter, generics);
                    newParameters.add(newType);
                }
                var newInternalGenerics = new ArrayList<KType>();
                for (var kInterface : functionType.interfaces()) {
                    var newType = replaceType(kInterface, generics);
                    newInternalGenerics.add(newType);
                }

                yield new KType.FunctionType(
                        newParameters,
                        returnType,
                        newInternalGenerics
                );
            }
            case KType.GenericLink genericLink -> {
                var link = genericLink.link();
                var newType = generics.get(link);
                if (newType != null) {
                    yield newType;
                } else {
                    yield genericLink;
                }
            }
            case KType.PrimitiveType primitiveType -> {
                yield primitiveType;
            }
            case KType.Resolvable resolvable -> {
                var resolved = resolvable.get();
                if (resolved == null) {
                    yield resolvable;
                } else {
                    yield replaceType(resolved, generics);
                }
            }
            case KType.UnprocessedType unprocessedType -> {
                yield unprocessedType;
            }
            case KType.AnyClass anyClass -> anyClass;
        };
    }

    public static boolean doesReturn(KExpr expr) {
        return switch (expr) {
            case KExpr.Block block -> {
                if (block.expressions().isEmpty()) {
                    yield false;
                } else {
                    yield doesReturn(block.expressions().getLast());
                }
            }
            case KExpr.Branch branch -> {
                if (branch.elseArm() == null) {
                    yield false;
                } else {
                    yield doesReturn(branch.thenArm()) && doesReturn(branch.elseArm());
                }
            }

            case KExpr.Return aReturn -> true;
            //Also include loop control
            case KExpr.Continue aContinue -> true;
            case KExpr.Break aBreak -> true;
            case KExpr.Throw aThrow -> true;

            case KExpr.Boolean aBoolean -> false;
            case KExpr.Assignment assignment -> false;
            case KExpr.Binary binary -> false;
            case KExpr.Call call -> false;
            case KExpr.Cast cast -> false;
            case KExpr.Closure closure -> false;
            case KExpr.CreateArray createArray -> false;
            case KExpr.CreateObject createObject -> false;
            case KExpr.For aFor -> false;
            case KExpr.GetArrayElement getArrayElement -> false;
            case KExpr.GetMember getMember -> false;
            case KExpr.IsInstanceOf isInstanceOf -> false;
            case KExpr.Literal literal -> false;
            case KExpr.Match match -> false;
            case KExpr.Number number -> false;
            case KExpr.Self self -> false;
            case KExpr.StringExpr stringExpr ->false;
            case KExpr.Unary unary -> false;
            case KExpr.VariableDefinition variableDefinition ->false;
            case KExpr.While aWhile -> false;
        };
    }

}
