package org.karina.lang.compiler.stages.parser.visitor;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.utils.Unique;
import org.karina.lang.compiler.logging.errors.ImportError;
import org.karina.lang.compiler.objects.KAnnotation;
import org.karina.lang.compiler.objects.annotations.*;
import org.karina.lang.compiler.stages.parser.RegionContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;

import java.util.stream.Collectors;

public class KarinaAnnotationVisitor {
    private final RegionContext conv;
    private final KarinaTypeVisitor typeVisitor;
    private final KarinaExprVisitor exprVisitor;

    public KarinaAnnotationVisitor(
            RegionContext conv, KarinaTypeVisitor typeVisitor, KarinaExprVisitor exprVisitor) {
        this.conv = conv;
        this.typeVisitor = typeVisitor;
        this.exprVisitor = exprVisitor;
    }

    public KAnnotation visit(KarinaParser.AnnotationContext ctx) {
        var region = this.conv.toRegion(ctx);
        var name = this.conv.escapeID(ctx.id());
        AnnotationValue value;
        if (ctx.jsonValue() != null) {
            value = this.visitAnnotationValue(ctx.jsonValue());
        } else {
            value = new AnnotationBool(region, true);
        }
        return new KAnnotation(region, name, value);
    }

    private AnnotationValue visitAnnotationValue(KarinaParser.JsonValueContext ctx) {

        var region = this.conv.toRegion(ctx);
        if (ctx.STRING_LITERAL() != null) {
            var inner = ctx.STRING_LITERAL().getText();
            var text = inner.substring(1, inner.length() - 1);
            return new AnnotationString(region, text);
        } else if (ctx.NUMBER() != null) {
            try {
                var text = ctx.NUMBER().getText();
                var decimal = this.exprVisitor.parseNumber(text);
                return new AnnotationNumber(region, decimal);
            } catch (NumberFormatException e) {
                Log.syntaxError(region, "Invalid number");
                throw new Log.KarinaException();
            }
        } else if (ctx.TRUE() != null) {
            return new AnnotationBool(region, true);
        } else if (ctx.FALSE() != null) {
            return new AnnotationBool(region, false);
        } else if (ctx.NULL() != null) {
            return new AnnotationNull(region);
        } else if (ctx.jsonArray() != null) {
            return this.visitAnnotationArray(ctx.jsonArray());
        } else if (ctx.jsonObj() != null) {
            return this.visitAnnotationObject(ctx.jsonObj());
        } else if (ctx.jsonExpression() != null) {
            return visitExpression(ctx.jsonExpression());
        } else if (ctx.jsonType() != null) {
            return visitType(ctx.jsonType());
        } else {
            Log.temp(region, "Unknown annotation value: " + ctx.getText());
            throw new Log.KarinaException();
        }

    }

    private AnnotationAST visitExpression(KarinaParser.JsonExpressionContext ctx) {
        var region = this.conv.toRegion(ctx);
        var expr = this.exprVisitor.visitBlock(ctx.block());
        return new AnnotationAST.Expression(region, expr);

    }

    private AnnotationAST visitType(KarinaParser.JsonTypeContext ctx) {
        var region = this.conv.toRegion(ctx);
        var type = this.typeVisitor.visitType(ctx.type());
        return new AnnotationAST.Type(region, type);
    }

    private AnnotationObject visitAnnotationObject(KarinaParser.JsonObjContext ctx) {
        var region = this.conv.toRegion(ctx);

        var duplicate = Unique.testUnique(ctx.jsonPair(), this::getKey);
        if (duplicate != null) {
            Log.importError(new ImportError.DuplicateItem(
                    this.conv.toRegion(duplicate.first()),
                    this.conv.toRegion(duplicate.duplicate()),
                    this.getKey(duplicate.first())
            ));
            throw new Log.KarinaException();
        }

        var values = ctx.jsonPair().stream().collect(
                Collectors.toMap(
                        this::getKey,
                        p -> this.visitAnnotationValue(p.jsonValue())
                ));
        return new AnnotationObject(region, values);
    }

    private AnnotationArray visitAnnotationArray(KarinaParser.JsonArrayContext ctx) {
        var region = this.conv.toRegion(ctx);
        var values = ctx.jsonValue().stream().map(this::visitAnnotationValue).toList();
        return new AnnotationArray(region, values);
    }


    private String getKey(KarinaParser.JsonPairContext ctx) {
        if (ctx.STRING_LITERAL() != null) {
            var inner = ctx.STRING_LITERAL().getText();
            return inner.substring(1, inner.length() - 1);
        } else {
            return this.conv.escapeID(ctx.id());
        }
    }
}
