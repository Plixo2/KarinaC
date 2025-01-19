package org.karina.lang.compiler.stages.parser.visitor;

import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.stages.parser.TextContext;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.SpanOf;

import java.util.List;

/**
 * Used to convert an AST type object to the corresponding {@link KType}.
 */
public class KarinaTypeVisitor {
    private final TextContext conv;
    private final KarinaVisitor visitor;

    public KarinaTypeVisitor(KarinaVisitor visitor, TextContext converter) {
        this.conv = converter;
        this.visitor = visitor;
    }

    public KType visitType(KarinaParser.TypeContext ctx) {

        var region = this.conv.toRegion(ctx);
        if (ctx.VOID() != null) {
            return new KType.PrimitiveType(KType.KPrimitive.VOID);
        } else if (ctx.INT() != null) {
            return new KType.PrimitiveType(KType.KPrimitive.INT);
        } else if (ctx.DOUBLE() != null) {
            return new KType.PrimitiveType(KType.KPrimitive.DOUBLE);
        } else if (ctx.SHORT() != null) {
            return new KType.PrimitiveType(KType.KPrimitive.SHORT);
        } else if (ctx.BYTE() != null) {
            return new KType.PrimitiveType(KType.KPrimitive.BYTE);
        } else if (ctx.CHAR() != null) {
            return new KType.PrimitiveType(KType.KPrimitive.CHAR);
        } else if (ctx.LONG() != null) {
            return new KType.PrimitiveType(KType.KPrimitive.LONG);
        } else if (ctx.FLOAT() != null) {
            return new KType.PrimitiveType(KType.KPrimitive.FLOAT);
        } else if (ctx.BOOL() != null) {
            return new KType.PrimitiveType(KType.KPrimitive.BOOL);
        } else if (ctx.STRING() != null) {
            var path = new ObjectPath("java", "lang", "String");
            var span = this.conv.span(ctx.STRING()).region();
            return new KType.UnprocessedType(
                    span,
                    SpanOf.span(span, path),
                    List.of()
            );
        } else if (ctx.structType() != null) {
            return visitStructType(ctx.structType());
        } else if (ctx.arrayType() != null) {
            return new KType.ArrayType(visitType(ctx.arrayType().type()));
        } else if (ctx.functionType() != null) {
            return visitFunctionType(ctx.functionType());
        } else if (ctx.type() != null) {
            return visitType(ctx.type());
        } else if (ctx.CHAR_QUESTION() != null) {
            return new KType.AnyClass();
        }
        else {
            Log.syntaxError(region, "Invalid type");
            throw new Log.KarinaException();
        }
    }

    public KType visitStructType(KarinaParser.StructTypeContext ctx) {

        var region = this.conv.toRegion(ctx);
        var path = this.visitor.visitDotWordChain(ctx.dotWordChain());
        var generics =
                ctx.genericHint() != null ? this.visitor.visitGenericHint(ctx.genericHint()) : List.<KType>of();
        return new KType.UnprocessedType(region, path, generics);

    }

    private KType visitFunctionType(KarinaParser.FunctionTypeContext ctx) {

        var interfaces = ctx.interfaceImpl() != null ? visitInterfaceImpl(ctx.interfaceImpl()) :
                List.<KType>of();
        var args = visitTypeList(ctx.typeList());
        var returnType = ctx.type() != null ? visitType(ctx.type()) : null;
        return new KType.FunctionType(args, returnType, interfaces);

    }

    private List<KType> visitTypeList(KarinaParser.TypeListContext ctx) {
        return ctx.type().stream().map(this::visitType).toList();
    }

    //#region public
    public List<KType> visitInterfaceImpl(KarinaParser.InterfaceImplContext ctx) {
        return ctx.structTypeList().structType().stream().map(this::visitStructType).toList();
    }
    //#endregion

}
