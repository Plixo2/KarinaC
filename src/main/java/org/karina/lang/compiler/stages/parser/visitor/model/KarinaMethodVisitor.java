package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.ImportError;
import org.karina.lang.compiler.model_api.Signature;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KAnnotation;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.parser.RegionContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.Region;

import java.lang.reflect.Modifier;
import java.util.List;

public class KarinaMethodVisitor {
    private final RegionContext context;
    private final KarinaUnitVisitor base;

    public KarinaMethodVisitor(KarinaUnitVisitor base, RegionContext regionContext) {
        this.base = base;
        this.context = regionContext;
    }

    public KMethodModel visit(ClassPointer owningClass,  ImmutableList<KAnnotation> annotations, KarinaParser.FunctionContext function) {
        String name;
        if (function.id() != null) {
            name = this.context.escapeID(function.id());
        } else {
            name = "<init>";
        }

        if (function.OVERRIDE() != null) {
            var region = this.context.toRegion(function.OVERRIDE());
            Log.importError(new ImportError.InvalidName(region, "override", "Override is not allowed here"));
            throw new Log.KarinaException();
        }

        var region = this.context.toRegion(function);

        var generics = ImmutableList.<Generic>of();
        if (function.genericHintDefinition() != null) {
            generics = ImmutableList.copyOf(this.base.visitGenericHintDefinition(function.genericHintDefinition()));
        }
        var isStatic = function.selfParameterList().SELF() == null;

        var parameters = ImmutableList.copyOf(function.selfParameterList().parameter().stream().map(
                KarinaParser.ParameterContext::id).map(this.context::escapeID).toList());
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
                annotations,
                region,
                owningClass,
                List.of()
        );

    }

    private Signature getSignature(KarinaParser.FunctionContext function) {
        KType returnType;

        if (function.type() != null) {
            returnType = this.base.typeVisitor.visitType(function.type());
        } else {
            returnType = KType.NONE;
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
