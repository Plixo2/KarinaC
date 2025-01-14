package org.karina.lang.compiler.stages.parser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.json.*;
import org.karina.lang.compiler.objects.*;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Transforms the ANTLR AST into a Karina parse Tree.
 * Uses the {@link KarinaTypeVisitor} and {@link KarinaExprVisitor} to convert types and expressions.
 *
 * @see KarinaParser
 * @see KTree
 *
 */
public class KarinaVisitor {
    private final RegionConverter conv;

    /**
     * Absolute path to the unit, already including the unit path
     */
    private final ObjectPath path;
    /**
     * The path of the unit being visited
     */
    private final String unitName;

    private final KarinaTypeVisitor typeVisitor;
    private final KarinaExprVisitor exprVisitor;

    public KarinaVisitor(RegionConverter converter, ObjectPath path, String unitName) {
        this.typeVisitor = new KarinaTypeVisitor(this,converter);
        this.exprVisitor = new KarinaExprVisitor(this, this.typeVisitor,converter);
        this.conv = converter;
        this.path = path;
        this.unitName = unitName;
    }

    public KTree.KUnit visitUnit(KarinaParser.UnitContext ctx) {

        var imports = ctx.import_().stream().map(this::visitImport).toList();
        var region = this.conv.toRegion(ctx.getSourceInterval());
        var items = ctx.item().stream().map(this::visitItem).toList();
        return new KTree.KUnit(region, this.unitName, this.path, imports, items, null);

    }

    //#region items
    private KTree.KItem visitItem(KarinaParser.ItemContext ctx) {

        var annotations = ctx.annotation().stream().map(this::visitAnnotation).toList();
        var function = ctx.function();
        var struct = ctx.struct();
        var enum_ = ctx.enum_();
        var interface_ = ctx.interface_();
        if (function != null) {
            return visitFunction(function, this.path, annotations, false);
        } else if (struct != null) {
            return this.visitStruct(struct, annotations);
        } else if (enum_ != null) {
            return this.visitEnum(enum_, annotations);
        } else if (interface_ != null) {
            return this.visitInterface(interface_, annotations);
        } else {
            Log.syntaxError(this.conv.toRegion(ctx.getSourceInterval()), "Invalid item");
            throw new Log.KarinaException();
        }

    }

    private KTree.KFunction visitFunction(
            KarinaParser.FunctionContext ctx,
            ObjectPath path,
            List<KTree.KAnnotation> annotations,
            boolean isInterface
    ) {

        var region = this.conv.toRegion(ctx);
        var name = this.conv.span(ctx.ID());
        var generics = ctx.genericHintDefinition() == null ? List.<Generic>of() :
                this.visitGenericHintDefinition(ctx.genericHintDefinition());
        Span definesSelf = null;
        if (ctx.selfParameterList().SELF() != null) {
            definesSelf = this.conv.toRegion(ctx.selfParameterList().SELF());
        }

        var parameters = visitParametersSelf(ctx.selfParameterList());
        if (!isInterface) {
            if (ctx.expression() == null && ctx.block() == null) {
                Log.syntaxError(region, "Function must have a body");
                throw new Log.KarinaException();
            }
        }
        var returnType = ctx.type() == null ? null : this.typeVisitor.visitType(ctx.type());
        KExpr expression = null;
        if (ctx.expression() != null) {
            expression = this.exprVisitor.visitExpression(ctx.expression());
        } else if (ctx.block() != null) {
            expression = this.exprVisitor.visitBlock(ctx.block());
        }
        var objectPath = path.append(name.value());
        return new KTree.KFunction(
                region,
                name,
                objectPath,
                definesSelf,
                new FunctionModifier(),
                annotations,
                parameters,
                returnType,
                generics,
                expression
        );

    }

    private KTree.KStruct visitStruct(
            KarinaParser.StructContext ctx,
            List<KTree.KAnnotation> annotations
    ) {

        var region = this.conv.toRegion(ctx);
        var name = this.conv.span(ctx.ID());
        var path = this.path.append(name.value());
        var generics = ctx.genericHintDefinition() == null ?
                List.<Generic>of():
                visitGenericHintDefinition(ctx.genericHintDefinition());
        var fields = ctx.field().stream().map(ref -> visitField(ref, path)).toList();
        var functions = ctx.function()
                           .stream()
                           .map(
                                (var ref) -> visitFunction(ref, path, List.of(), false)
                           ).toList();
        var implBlocks = ctx.implementation().stream().map(ref -> {
            var implRegion = this.conv.toRegion(ref);
            var kType = this.typeVisitor.visitStructType(ref.structType());
            var implFunctions = ref.function()
                                   .stream()
                                   .map(
                                        (var fRef) -> visitFunction(fRef, path, List.of(), false)
                                   ).toList();
            return new KTree.KImplBlock(implRegion, kType, implFunctions);
        }).toList();
        return new KTree.KStruct(
                region,
                name,
                path,
                new StructModifier(),
                generics,
                annotations,
                functions,
                fields,
                implBlocks
        );

    }

    private KTree.KEnum visitEnum(KarinaParser.EnumContext ctx, List<KTree.KAnnotation> annotations) {

        var name = this.conv.span(ctx.ID());
        var generics = ctx.genericHintDefinition() == null ?
                        List.<Generic>of():
                        visitGenericHintDefinition(ctx.genericHintDefinition());
        var region = this.conv.toRegion(ctx);
        var path = this.path.append(name.value());
        var entries = ctx.enumMember().stream().map(this::visitEnumEntries).toList();
        return new KTree.KEnum(
                region,
                name,
                path,
                generics,
                annotations,
                entries
        );

    }

    private KTree.KInterface visitInterface(KarinaParser.InterfaceContext ctx, List<KTree.KAnnotation> annotations) {

        var name = this.conv.span(ctx.ID());
        var generics = ctx.genericHintDefinition() == null ? List.<Generic>of() :
                visitGenericHintDefinition(ctx.genericHintDefinition());
        var path = this.path.append(name.value());
        var region = this.conv.toRegion(ctx);
        var superTypes = ctx.interfaceExtension()
                            .stream()
                            .map(
                                    (var ref) -> this.typeVisitor.visitStructType(ref.structType())
                            ).toList();
        var functions = ctx.function()
                           .stream()
                           .map(
                                   (var ref) -> visitFunction(ref, path, List.of(), true)
                           ).toList();
        return new KTree.KInterface(
                region,
                name,
                path,
                generics,
                annotations,
                functions,
                superTypes,
                List.of()
        );

    }
    //#endregion

    //#region other
    private KTree.KEnumEntry visitEnumEntries(KarinaParser.EnumMemberContext ctx) {

        var region = this.conv.toRegion(ctx);
        var name = this.conv.span(ctx.ID());
        var parameters = ctx.parameterList() == null ? List.<KTree.KParameter>of() :
                visitParameters(ctx.parameterList());
        return new KTree.KEnumEntry(region, name, parameters);

    }

    private List<KTree.KParameter> visitParameters(KarinaParser.ParameterListContext ctx) {

        return ctx.parameter().stream().map(ref -> {
            var region = this.conv.toRegion(ref);
            var name = this.conv.span(ref.ID());
            var type = this.typeVisitor.visitType(ref.type());
            return new KTree.KParameter(region, name, type, null);
        }).toList();

    }

    private List<KTree.KParameter> visitParametersSelf(KarinaParser.SelfParameterListContext ctx) {

        return ctx.parameter().stream().map(ref -> {
            var region = this.conv.toRegion(ref);
            var name = this.conv.span(ref.ID());
            var type = this.typeVisitor.visitType(ref.type());
            return new KTree.KParameter(region, name, type, null);
        }).toList();

    }

    private KTree.KAnnotation visitAnnotation(KarinaParser.AnnotationContext ctx) {

        var region = this.conv.toRegion(ctx);
        var span = this.conv.span(ctx.ID());
        JsonElement element;
        if (ctx.jsonValue() == null) {
            element = new JsonBoolean(region, true);
        } else {
            element = visitJsonValue(ctx.jsonValue());
        }
        return new KTree.KAnnotation(region, span, element);

    }

    private JsonElement visitJsonValue(KarinaParser.JsonValueContext ctx) {

        var region = this.conv.toRegion(ctx);
        if (ctx.jsonObj() != null) {
            return visitJsonObject(ctx.jsonObj());
        } else if (ctx.jsonArray() != null) {
            return visitJsonArray(ctx.jsonArray());
        } else if (ctx.TRUE() != null) {
            return new JsonBoolean(region, true);
        } else if (ctx.FALSE() != null) {
            return new JsonBoolean(region, false);
        } else if (ctx.NULL() != null) {
            return new JsonNull(region);
        } else if (ctx.NUMBER() != null) {
           return visitJsonNumber(ctx.NUMBER());
        } else if (ctx.STRING_LITERAL() != null) {
            var string = visitString(ctx.STRING_LITERAL()).value();
            return new JsonString(region, string);
        } else {
            Log.syntaxError(region, "Invalid JSON value");
            throw new Log.KarinaException();
        }

    }

    private JsonNumber visitJsonNumber(TerminalNode ctx) {

        var region = this.conv.toRegion(ctx);
        try {
            return new JsonNumber(region, parseNumber(ctx.getText()));
        } catch (NumberFormatException e) {
            Log.syntaxError(region, "Invalid number format");
            throw new Log.KarinaException();
        }

    }

    private BigDecimal parseNumber(String text) throws NumberFormatException {

        text = text.replace("_", "");
        if (text.startsWith("0x")) {
            var numberStr = text.substring(2);
            return new BigDecimal(new BigInteger(numberStr, 16));
        } else if (text.startsWith("0b")) {
            var numberStr = text.substring(2);
            return new BigDecimal(new BigInteger(numberStr, 2));
        } else {
            return new BigDecimal(text);
        }

    }

    private JsonObject visitJsonObject(KarinaParser.JsonObjContext ctx) {

        var members = ctx.jsonPair().stream().map(ref -> {
            var name = ref.ID() == null ? visitString(ref.STRING_LITERAL()) : this.conv.span(ref.ID());
            var value = visitJsonValue(ref.jsonValue());
            return new JsonObject.JsonMember(name.region(), name.value(), value);
        }).toList();
        return new JsonObject(this.conv.toRegion(ctx), members);

    }

    private JsonArray visitJsonArray(KarinaParser.JsonArrayContext ctx) {

        return new JsonArray(
                this.conv.toRegion(ctx),
                ctx.jsonValue().stream().map(this::visitJsonValue).toList()
        );

    }

    private KTree.KImport visitImport(KarinaParser.Import_Context ctx) {

        var region = this.conv.toRegion(ctx);
        var importAll = ctx.CHAR_STAR() != null;
        var nameRegion = ctx.ID() == null ? null : this.conv.span(ctx.ID());
        var importJava = ctx.JAVA_IMPORT() != null;
        var path = visitDotWordChain(ctx.dotWordChain());
        TypeImport typeImport;
        if (importJava) {
            if (importAll) {
                Log.syntaxError(region, "Invalid import statement");
                throw new Log.KarinaException();
            } else {
                if (nameRegion == null) {
                    typeImport = new TypeImport.JavaClass(path.region());
                } else {
                    typeImport = new TypeImport.JavaAlias(nameRegion);
                }
            }
        } else {
            if (importAll) {
                typeImport = new TypeImport.All(this.conv.span(ctx.CHAR_STAR()).region());
            } else if (nameRegion != null) {
                typeImport = new TypeImport.Single(nameRegion);
            } else {
                Log.syntaxError(region, "Invalid import statement");
                throw new Log.KarinaException();
            }
        }
        return new KTree.KImport(region, typeImport, path);

    }

    private List<Generic> visitGenericHintDefinition(KarinaParser.GenericHintDefinitionContext ctx) {
        return ctx.ID().stream().map(ref -> {
            var region = KarinaVisitor.this.conv.span(ref);
            return new Generic(region.region(), region.value());
        }).toList();
    }

    private SpanOf<String> visitString(TerminalNode ctx) {

        var text = ctx.getText();
        var substring = text.substring(1, text.length() - 1);
        return this.conv.span(substring, ctx.getSourceInterval());

    }

    private KTree.KField visitField(KarinaParser.FieldContext ctx, ObjectPath path) {

        var region = this.conv.toRegion(ctx);
        var name = this.conv.span(ctx.ID());
        var type = this.typeVisitor.visitType(ctx.type());
        var newPath = path.append(name.value());
        return new KTree.KField(region, newPath, name, type);

    }
    //#endregion

    //#region public
    public SpanOf<ObjectPath> visitDotWordChain(KarinaParser.DotWordChainContext ctx) {

        var elements = ctx.ID().stream().map(ParseTree::getText).toList();
        return this.conv.span(new ObjectPath(elements), ctx.getSourceInterval());

    }

    public List<KType> visitGenericHint(KarinaParser.GenericHintContext ctx) {
        return ctx.type().stream().map(this.typeVisitor::visitType).toList();
    }
    //#endregion
}
