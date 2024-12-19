package org.karina.lang.compiler.parser;

import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.parser.gen.KarinaParser;

import java.util.List;

/**
 * Used to convert AST type object to the corresponding {@link KType}.
 */
public class KarinaTypeVisitor {
    private final RegionConverter conv;
    private final KarinaVisitor visitor;

    public KarinaTypeVisitor(KarinaVisitor visitor, RegionConverter converter) {
        this.conv = converter;
        this.visitor = visitor;
    }

    public KType visitType(KarinaParser.TypeContext ctx) {

        var region = this.conv.toRegion(ctx);
        if (ctx.VOID() != null) {
            return new KType.PrimitiveType.VoidType(region);
        } else if (ctx.INT() != null) {
            return new KType.PrimitiveType.IntType(region);
        } else if (ctx.DOUBLE() != null) {
            return new KType.PrimitiveType.DoubleType(region);
        } else if (ctx.SHORT() != null) {
            return new KType.PrimitiveType.ShortType(region);
        } else if (ctx.BYTE() != null) {
            return new KType.PrimitiveType.ByteType(region);
        } else if (ctx.CHAR() != null) {
            return new KType.PrimitiveType.CharType(region);
        } else if (ctx.LONG() != null) {
            return new KType.PrimitiveType.LongType(region);
        } else if (ctx.FLOAT() != null) {
            return new KType.PrimitiveType.FloatType(region);
        } else if (ctx.BOOL() != null) {
            return new KType.PrimitiveType.BoolType(region);
        } else if (ctx.STRING() != null) {
            return new KType.PrimitiveType.StringType(region);
        } else if (ctx.structType() != null) {
            return visitStructType(ctx.structType());
        } else if (ctx.arrayType() != null) {
            return new KType.ArrayType(region, visitType(ctx.arrayType().type()));
        } else if (ctx.functionType() != null) {
            return visitFunctionType(ctx.functionType());
        } else if (ctx.type() != null) {
            return visitType(ctx.type());
        }   else {
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

        var region = this.conv.toRegion(ctx);
        var interfaces = ctx.interfaceImpl() != null ? visitInterfaceImpl(ctx.interfaceImpl()) :
                List.<KType>of();
        var args = visitTypeList(ctx.typeList());
        var returnType = ctx.type() != null ? visitType(ctx.type()) : null;
        return new KType.FunctionType(region, args, returnType, interfaces);

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
