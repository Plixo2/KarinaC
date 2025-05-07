package org.karina.lang.compiler.stages.parser.visitor;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.stages.parser.RegionContext;
import org.karina.lang.compiler.stages.parser.visitor.model.KarinaUnitVisitor;

import java.util.List;

/**
 * Used to convert an AST fieldType object to the corresponding {@link KType}.
 */
public class KarinaTypeVisitor {
    private final RegionContext conv;
    private final KarinaUnitVisitor visitor;

    public KarinaTypeVisitor(KarinaUnitVisitor visitor, RegionContext converter) {
        this.conv = converter;
        this.visitor = visitor;
    }

    public KType visitType(KarinaParser.TypeContext ctx) {

        var inner = visitInnerType(ctx.typeInner());

        if (ctx.typePostFix() != null) {
            //we return KType.ROOT. so the identity check can be done
            if (inner.isPrimitive() || inner.isVoid() || inner == KType.ROOT) {
                Log.syntaxError(this.conv.toRegion(ctx), "Invalid optional type");
                throw new Log.KarinaException();
            }
            //link to karina standard library option type
            return KType.KARINA_OPTION(inner);
        }

        return inner;
    }

    public KType visitInnerType(KarinaParser.TypeInnerContext ctx) {

        var region = this.conv.toRegion(ctx);
        if (ctx.VOID() != null) {
            return KType.NONE;
        } else if (ctx.INT() != null) {
            return KType.INT;
        } else if (ctx.DOUBLE() != null) {
            return KType.DOUBLE;
        } else if (ctx.SHORT() != null) {
            return KType.SHORT;
        } else if (ctx.BYTE() != null) {
            return KType.BYTE;
        } else if (ctx.CHAR() != null) {
            return KType.CHAR;
        } else if (ctx.LONG() != null) {
            return KType.LONG;
        } else if (ctx.FLOAT() != null) {
            return KType.FLOAT;
        } else if (ctx.BOOL() != null) {
            return KType.BOOL;
        } else if (ctx.STRING() != null) {
            return KType.STRING;
        } else if (ctx.structType() != null) {
            return visitStructType(ctx.structType());
        } else if (ctx.arrayType() != null) {
            var innerType = ctx.arrayType().type();
            var inner = visitType(innerType);
            if (inner.isVoid()) {
                var innerRegion = this.conv.toRegion(innerType);
                Log.attribError(new AttribError.NotSupportedType(innerRegion, inner));
                throw new Log.KarinaException();
            }
            return new KType.ArrayType(inner);
        } else if (ctx.functionType() != null) {
            return visitFunctionType(ctx.functionType());
        } else if (ctx.type() != null) {
            return visitType(ctx.type());
        } else if (ctx.CHAR_QUESTION() != null) {
            return KType.ROOT;
        }
        else {
            Log.syntaxError(region, "Invalid fieldType");
            throw new Log.KarinaException();
        }
    }

    public KType.UnprocessedType visitStructType(KarinaParser.StructTypeContext ctx) {

        var region = this.conv.toRegion(ctx);
        var path = this.visitor.visitDotWordChain(ctx.dotWordChain());
        var generics =
                ctx.genericHint() != null ? this.visitor.visitGenericHint(ctx.genericHint()) : List.<KType>of();
        return new KType.UnprocessedType(region, path, generics);

    }

    private KType visitFunctionType(KarinaParser.FunctionTypeContext ctx) {

        var interfaces = ctx.interfaceImpl() != null ? visitInterfaceImpl(ctx.interfaceImpl()) : List.<KType>of();
        var args = visitTypeList(ctx.typeList());


        var returnType = ctx.type() != null ? visitType(ctx.type()) : KType.NONE;

        if (returnType != null && returnType.isPrimitive()) {
            Log.attribError(new AttribError.NotSupportedType(this.conv.toRegion(ctx.type()), returnType));
            throw new Log.KarinaException();
        }

        List<KType> interfacesCast = interfaces.stream().map(ref -> (KType)ref).toList();
        return new KType.FunctionType(args, returnType, interfacesCast);

    }

    private List<KType> visitTypeList(KarinaParser.TypeListContext ctx) {

        return ctx.type().stream().map(ref -> {
            var mapped = visitType(ref);
            if (mapped.isVoid() || mapped.isPrimitive()) {
                var innerRegion = this.conv.toRegion(ref);
                Log.attribError(new AttribError.NotSupportedType(innerRegion, mapped));
                throw new Log.KarinaException();
            }
            return mapped;
        }).toList();
    }

    //#region public
    public List<? extends KType> visitInterfaceImpl(KarinaParser.InterfaceImplContext ctx) {
        return ctx.structTypeList().structType().stream().map(this::visitStructType).toList();
    }
    //#endregion

}
