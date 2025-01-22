package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.antlr.v4.runtime.tree.ParseTree;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.jvm.model.JKModelBuilder;
import org.karina.lang.compiler.jvm.model.karina.KClassModel;
import org.karina.lang.compiler.jvm.model.karina.KFieldModel;
import org.karina.lang.compiler.jvm.model.karina.KMethodModel;
import org.karina.lang.compiler.model.FieldModel;
import org.karina.lang.compiler.model.MethodModel;
import org.karina.lang.compiler.model.pointer.ClassPointer;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.stages.parser.TextContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.stages.parser.visitor.KarinaExprVisitor;
import org.karina.lang.compiler.stages.parser.visitor.KarinaTypeVisitor;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.RegionOf;
import org.karina.lang.compiler.utils.TypeImport;

import java.lang.reflect.Modifier;
import java.util.List;

public class KarinaUnitVisitor {
    private final String name;
    private final ObjectPath path;
    private final TextContext context;

    KarinaStructVisitor structVisitor;
    KarinaInterfaceVisitor interfaceVisitor;
    KarinaEnumVisitor enumVisitor;
    KarinaMethodVisitor methodVisitor;
    KarinaTypeVisitor typeVisitor;
    KarinaExprVisitor exprVisitor;

    public KarinaUnitVisitor(TextContext textContext, String name, ObjectPath path) {
        this.name = name;
        this.path = path;
        this.context = textContext;

        this.structVisitor = new KarinaStructVisitor(this, textContext);
        this.interfaceVisitor = new KarinaInterfaceVisitor(this, textContext);
        this.enumVisitor = new KarinaEnumVisitor(this, textContext);
        this.methodVisitor = new KarinaMethodVisitor(this, textContext);
        this.typeVisitor = new KarinaTypeVisitor(this, textContext);
        this.exprVisitor = new KarinaExprVisitor(this, this.typeVisitor, textContext);

    }

    public ClassPointer visit(KarinaParser.UnitContext ctx, JKModelBuilder builder) {
        var region = this.context.toRegion(ctx);
        int mods = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

        var imports = ImmutableList.copyOf(ctx.import_().stream().map(this::visitImport).toList());
        var interfaces = ImmutableList.<ClassPointer>of();
        var fields = ImmutableList.<KFieldModel>of();
        var innerClasses = ImmutableList.<ClassPointer>builder();
        var methods = ImmutableList.<KMethodModel>builder();
        var generics = ImmutableList.<Generic>of();

        var currentClass = ClassPointer.of(this.path);
        var constructor =
                KarinaStructVisitor.createConstructor(region, currentClass, List.of(), Modifier.PRIVATE);
        methods.add(constructor);

        for (var itemContext : ctx.item()) {
            if (itemContext.function() != null) {
                var method = this.methodVisitor.visit(currentClass, itemContext.function());
                methods.add(method);
            } else if (itemContext.struct() != null) {
                var struct = this.structVisitor.visit(currentClass, this.path, itemContext.struct(), builder);
                innerClasses.add(struct);
            } else if (itemContext.enum_() != null) {
                var enum_ = this.enumVisitor.visit(currentClass, this.path, itemContext.enum_(), builder);
                innerClasses.add(enum_);
            } else if (itemContext.interface_() != null) {
                var interface_ = this.interfaceVisitor.visit(currentClass, this.path, itemContext.interface_(), builder);
                innerClasses.add(interface_);
            } else {
                Log.syntaxError(this.context.toRegion(itemContext), "Invalid item");
                throw new Log.KarinaException();
            }
        }

        var permittedSubClasses = ImmutableList.<ClassPointer>of();

        var classModel = new KClassModel(
                this.name,
                this.path,
                mods,
                ClassPointer.ROOT,
                null,
                interfaces,
                innerClasses.build(),
                fields,
                methods.build(),
                generics,
                imports,
                permittedSubClasses,
                this.context.source(),
                region
        );

        builder.addClass(classModel);

        return classModel.pointer();
    }

    private KTree.KImport visitImport(KarinaParser.Import_Context ctx) {

        var region = this.context.toRegion(ctx);
        var path = visitDotWordChain(ctx.dotWordChain()).value();
        var importAll = ctx.CHAR_STAR() != null;
        if (importAll) {
            return new KTree.KImport(region, new TypeImport.All(), path);
        }

        var names = ImmutableList.<String>builder();

        if (ctx.ID() != null) {
            names.add(ctx.ID().getText());
        } else if (ctx.commaWordChain() != null) {
            ctx.commaWordChain().ID().forEach(id -> names.add(id.getText()));
        } else {
            return new KTree.KImport(region, new TypeImport.Base(), path);
        }

        return new KTree.KImport(region, new TypeImport.Names(names.build()), path);

    }

    public RegionOf<ObjectPath> visitDotWordChain(KarinaParser.DotWordChainContext ctx) {

        var elements = ctx.ID().stream().map(ParseTree::getText).toList();
        return this.context.region(new ObjectPath(elements), ctx.getSourceInterval());

    }

    public List<KType> visitGenericHint(KarinaParser.GenericHintContext ctx) {
        return ctx.type().stream().map(this.typeVisitor::visitType).toList();
    }

    public List<Generic> visitGenericHintDefinition(KarinaParser.GenericHintDefinitionContext ctx) {
        return ctx.ID().stream().map(ref -> {
            var region = this.context.region(ref);
            return new Generic(region.region(), region.value());
        }).toList();
    }

}
