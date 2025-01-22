package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.tree.ParseTree;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.model.Signature;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.parser.TextContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.Generic;

import java.lang.reflect.Modifier;

public class KarinaMethodVisitor {
    private final TextContext context;
    private final KarinaUnitVisitor base;

    public KarinaMethodVisitor(KarinaUnitVisitor base, TextContext textContext) {
        this.base = base;
        this.context = textContext;
    }

    public KMethodModel visit(ClassPointer owningClass, KarinaParser.FunctionContext function) {
        var name = function.ID().getText();
        var region = this.context.toRegion(function);

        var generics = ImmutableList.<Generic>of();
        if (function.genericHintDefinition() != null) {
            generics = ImmutableList.copyOf(this.base.visitGenericHintDefinition(function.genericHintDefinition()));
        }
        var isStatic = function.selfParameterList().SELF() == null;

        var parameters = ImmutableList.copyOf(function.selfParameterList().parameter().stream().map(
                KarinaParser.ParameterContext::ID).map(ParseTree::getText).toList());
        var signature = this.getSignature(function);

        KExpr expr;
        if (function.expression() != null) {
            expr = this.base.exprVisitor.visitExpression(function.expression());
        } else if (function.block() != null) {
            expr = this.base.exprVisitor.visitBlock(function.block());
        } else {
            expr = null;
        }
        var isAbstract = expr == null;
        var mods = Modifier.PUBLIC | (isStatic ? Modifier.STATIC : 0) | (isAbstract ? Modifier.ABSTRACT : 0);

        return new KMethodModel(
                name,
                mods,
                signature,
                parameters,
                generics,
                expr,
                region,
                owningClass
        );

    }

    private Signature getSignature(KarinaParser.FunctionContext function) {
        KType returnType;

        if (function.type() != null) {
            returnType = this.base.typeVisitor.visitType(function.type());
        } else {
            returnType = new KType.PrimitiveType(KType.KPrimitive.VOID);
        }

        var parameters = ImmutableList.<KType>builder();
        for (var parameterContext : function.selfParameterList().parameter()) {
            var type = this.base.typeVisitor.visitType(parameterContext.type());
            parameters.add(type);
        }

        return new Signature(
                parameters.build(),
                returnType
        );
    }
}
