package org.karina.lang.lsp.impl;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.Generic;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KFieldModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.*;
import org.karina.lang.compiler.utils.symbols.*;

import java.math.BigDecimal;
import java.util.List;


@RequiredArgsConstructor
public abstract class ClassVisitor {
    private final boolean visitTypes;
    private final boolean visitExpressions;
    protected final @Nullable Region region;
    private final boolean expressionsRangeCheck;


    //<editor-fold defaultstate="collapsed" desc="Expressions">
    public void visitExpr(KExpr expr) {
        if (!this.visitExpressions) {
            return;
        }
        if (this.region != null && !expr.region().intersects(this.region) &&
                this.expressionsRangeCheck) {
            return;
        }
        visitExprNoCheck(expr);
    }
    protected void visitExprNoCheck(KExpr expr) {
        switch (expr) {
            case KExpr.Block(Region region, List<KExpr> expressions, @Nullable KType symbol, boolean doesReturn) -> {
                for (var expression : expressions) {
                    visitExpr(expression);
                }
            }
            case KExpr.VariableDefinition(Region region, RegionOf<String> name, @Nullable KType varHint, KExpr value, @Nullable Variable symbol) -> {
                visitExpr(value);
                if (varHint != null) {
                    visitType(varHint);
                }
            }
            case KExpr.UsingVariableDefinition(Region region, RegionOf<String> name, @Nullable KType varHint, KExpr value, KExpr block, @Nullable Variable symbol) -> {
                visitExpr(value);
                visitExpr(block);
                if (varHint != null) {
                    visitType(varHint);
                }
            }
            case KExpr.Branch(Region region, KExpr condition, KExpr thenArm, @Nullable ElsePart elseArm, @Nullable BranchPattern branchPattern, @Nullable BranchYieldSymbol symbol) -> {
                visitExpr(condition);
                visitExpr(thenArm);
                if (elseArm != null) {
                    var elsePattern = elseArm.elsePattern();
                    if (elsePattern != null) {
                        visitBranchPattern(elsePattern);
                    }
                    visitExpr(elseArm.expr());
                }
                if (branchPattern != null) {
                    visitBranchPattern(branchPattern);
                }
            }
            case KExpr.While(Region region, KExpr condition, KExpr body) -> {
                visitExpr(condition);
                visitExpr(body);
            }
            case KExpr.For(Region region, NameAndOptType varPart, KExpr iter, KExpr body, @Nullable IteratorTypeSymbol symbol) -> {
                var type = varPart.type();
                if (type != null) {
                    visitType(type);
                }
                visitExpr(iter);
                visitExpr(body);
            }
            case KExpr.Return(Region region, @Nullable KExpr value, @Nullable KType returnType) -> {
                if (value != null) {
                    visitExpr(value);
                }
            }
            case KExpr.Binary(Region region, KExpr left, RegionOf<BinaryOperator> operator, KExpr right, @Nullable BinOperatorSymbol symbol) -> {
                visitExpr(left);
                visitExpr(right);
            }
            case KExpr.Unary(Region region, RegionOf<UnaryOperator> operator, KExpr value, @Nullable UnaryOperatorSymbol symbol) -> {
                visitExpr(value);
            }
            case KExpr.IsInstanceOf(Region region, KExpr left, KType isType) -> {
                visitExpr(left);
                visitType(isType);
            }
            case KExpr.GetMember(Region region, KExpr left, RegionOf<String> name, boolean isNextACall, MemberSymbol symbol) -> {
                visitExpr(left);
            }
            case KExpr.Call(Region region, KExpr left, List<KType> generics, List<KExpr> arguments, @Nullable CallSymbol symbol) -> {

                if (symbol instanceof CallSymbol.CallVirtual virtual
                        && virtual.original() != null
                ) {
                    visitExpr(virtual.original());
                } else {
                    visitExpr(left);
                }
                for (var generic : generics) {
                    visitType(generic);
                }
                for (var argument : arguments) {
                    visitExpr(argument);
                }
            }
            case KExpr.GetArrayElement(Region region, KExpr left, KExpr index, @Nullable KType elementType) -> {
                visitExpr(left);
                visitExpr(index);
            }
            case KExpr.Cast(Region region, KExpr expression, CastTo cast, @Nullable CastSymbol symbol) -> {
                visitExpr(expression);
                if (cast instanceof CastTo.CastToType(KType asType)) {
                    visitType(asType);
                }
            }
            case KExpr.Assignment(Region region, KExpr left, KExpr right, @Nullable AssignmentSymbol symbol) -> {
                visitExpr(left);
                visitExpr(right);
            }
            case KExpr.CreateArray(Region region, @Nullable KType hint, List<KExpr> elements, @Nullable KType.ArrayType symbol) -> {
                if (hint != null) {
                    visitType(hint);
                }
                for (var element : elements) {
                    visitExpr(element);
                }
            }
            case KExpr.SpecialCall(Region region, InvocationType invocationType) -> {
                switch (invocationType) {
                    case InvocationType.NewInit(KType.ClassType classType) -> {
                        visitType(classType);
                    }
                    case InvocationType.SpecialInvoke(String name, KType superType) -> {
                        visitType(superType);
                    }
                }
            }
            case KExpr.StringInterpolation(Region region, ImmutableList<StringComponent> components) -> {
                for (var component : components) {
                    if (component instanceof StringComponent.ExpressionComponent(_, KExpr expression)) {
                        visitExpr(expression);
                    }
                }
            }
            case KExpr.Match(Region region, KExpr value, List<MatchPattern> cases) -> {
                visitExpr(value);
                for (var aCase : cases) {
                    visitExpr(aCase.expr());
                    switch (aCase) {
                        case MatchPattern.Cast cast -> {
                            visitType(cast.type());
                        }
                        case MatchPattern.Destruct destruct -> {
                            visitType(destruct.type());
                            for (var variable : destruct.variables()) {
                                var type = variable.type();
                                if (type != null) {
                                    visitType(type);
                                }
                            }
                        }
                        case MatchPattern.Default aDefault -> {
                            // Nothing to do
                        }
                    }
                }
            }
            case KExpr.CreateObject(Region region, KType createType, List<NamedExpression> parameters) -> {
                visitType(createType);
                for (var parameter : parameters) {
                    visitExpr(parameter.expr());
                }
            }
            case KExpr.Closure(
                    Region region,
                    List<NameAndOptType> args,
                    @Nullable KType returnType,
                    List<? extends KType> interfaces,
                    KExpr body,
                    @Nullable ClosureSymbol symbol
            ) -> {
                for (var arg : args) {
                    var type = arg.type();
                    if (type != null) {
                        visitType(type);
                    }
                }
                if (returnType != null) {
                    visitType(returnType);
                }
                for (var anInterface : interfaces) {
                    visitType(anInterface);
                }
                visitExpr(body);
            }
            case KExpr.Unwrap(Region region, KExpr left, @Nullable UnwrapSymbol symbol) -> {
                visitExpr(left);
            }
            case KExpr.Throw(Region region, KExpr value) -> {
                visitExpr(value);
            }

            case KExpr.Break(Region region) -> {}
            case KExpr.Continue(Region region) -> {}
            case KExpr.Number(Region region, BigDecimal number, boolean decimalAnnotated, @Nullable NumberSymbol symbol) -> {}
            case KExpr.Boolean(Region region, boolean value) -> {}
            case KExpr.Literal(Region region, String name, @Nullable LiteralSymbol symbol) -> {}
            case KExpr.StaticPath(Region region, ImmutableList<Region> individualRegions, ObjectPath path, @Nullable ClassPointer importedPointer) -> {}
            case KExpr.Self(Region region, @Nullable Variable symbol) -> {}
            case KExpr.StringExpr(Region region, String value, boolean isChar) -> {}
        }
    }

    public void visitBranchPattern(BranchPattern branchPattern) {
        visitType(branchPattern.type());
        if (branchPattern instanceof BranchPattern.Destruct destruct) {
            for (var variable : destruct.variables()) {
                var type = variable.type();
                if (type != null) {
                    visitType(type);
                }
            }
        }
    }


    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Items">
    public void visitField(KFieldModel field) {
        if (!this.visitTypes) {
            return;
        }
        visitType(field.type());
    }
    public void visitMethod(KMethodModel methodModel) {
        if (this.region != null && !methodModel.region().intersects(this.region)) {
            return;
        }
        if (this.visitTypes) {
            var signature = methodModel.signature();
            for (var parameter : signature.parameters()) {
                visitType(parameter);
            }
            visitType(signature.returnType());
        }
        for (var generic : methodModel.generics()) {
            visitGenericDef(generic);
        }
        if (this.visitExpressions) {
            var expr = methodModel.expression();
            if (expr != null) {
                visitExpr(expr);
            }
        }
    }

    public void visitClass(KClassModel classModel) {
        if (this.region != null && !classModel.region().intersects(this.region)) {
            return;
        }
        for (var field : classModel.fields()) {
            if (field instanceof KFieldModel fieldModel) {
                visitField(fieldModel);
            }
        }
        for (var method : classModel.methods()) {
            if (method instanceof KMethodModel methodModel) {
                visitMethod(methodModel);
            }
        }
        for (var generic : classModel.generics()) {
            visitGenericDef(generic);
        }
        for (var anImport : classModel.imports()) {
            visitImport(anImport);
        }
        for (var anInterface : classModel.interfaces()) {
            visitType(anInterface);
        }
        var superClass = classModel.superClass();
        if (superClass != null) {
            visitType(superClass);
        }
        for (var innerClass : classModel.innerClasses()) {
            visitInnerClass(innerClass);
        }
    }
    public void visitInnerClass(KClassModel innerClass) {
        visitClass(innerClass);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Type">
    public void visitType(KType type) {
        if (!this.visitTypes) {
            return;
        }
        switch (type) {
            case KType.ArrayType arrayType -> visitArrayType(arrayType);
            case KType.ClassType classType -> visitClassType(classType);
            case KType.FunctionType functionType -> visitFunctionType(functionType);
            case KType.PrimitiveType primitiveType -> visitPrimitiveType(primitiveType.primitive());
            case KType.GenericLink genericLink -> visitGenericType(genericLink);
            case KType.Resolvable resolvable -> visitResolvableType(resolvable);
            case KType.UnprocessedType unprocessedType -> visitUnprocessedType(unprocessedType);
            case KType.VoidType _ -> visitVoid();
        }
    }

    public void visitArrayType(KType.ArrayType arrayType) {
        visitType(arrayType.elementType());
    }
    public void visitClassType(KType.ClassType classType) {
        for (var generic : classType.generics()) {
            visitType(generic);
        }
    }
    public void visitFunctionType(KType.FunctionType functionType) {
        for (var argument : functionType.arguments()) {
            visitType(argument);
        }
        visitType(functionType.returnType());
        for (var anInterface : functionType.interfaces()) {
            visitType(anInterface);
        }
    }

    public void visitResolvableType(KType.Resolvable resolvable) {
        var resolved = resolvable.get();
        if (resolved != null) {
            visitType(resolved);
        }
    }
    public void visitUnprocessedType(KType.UnprocessedType unprocessedType) {
        for (var generic : unprocessedType.generics()) {
            visitType(generic);
        }
    }
    public void visitGenericType(KType.GenericLink genericLink) {}
    public void visitPrimitiveType(KType.KPrimitive primitive) {}
    public void visitVoid() {}
    //</editor-fold>

    public void visitImport(KImport anImport) {}

    public void visitGenericDef(Generic generic) {
        if (!this.visitTypes) {
            return;
        }
        if (this.region != null && !generic.region().intersects(this.region)) {
            return;
        }
        var superType = generic.superType();
        if (superType != null) {
            visitType(superType);
        }
        for (var bound : generic.bounds()) {
            visitType(bound);
        }
    }
}
