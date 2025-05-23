package org.karina.lang.compiler.stages.parser.visitor.model;

import com.google.common.collect.ImmutableList;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.model_api.impl.ModelBuilder;
import org.karina.lang.compiler.model_api.impl.karina.KClassModel;
import org.karina.lang.compiler.model_api.impl.karina.KFieldModel;
import org.karina.lang.compiler.model_api.impl.karina.KMethodModel;
import org.karina.lang.compiler.logging.errors.AttribError;
import org.karina.lang.compiler.model_api.ClassModel;
import org.karina.lang.compiler.model_api.pointer.ClassPointer;
import org.karina.lang.compiler.utils.KAnnotation;
import org.karina.lang.compiler.utils.KImport;
import org.karina.lang.compiler.utils.KType;
import org.karina.lang.compiler.stages.parser.RegionContext;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.stages.parser.visitor.KarinaAnnotationVisitor;
import org.karina.lang.compiler.stages.parser.visitor.KarinaExprVisitor;
import org.karina.lang.compiler.stages.parser.visitor.KarinaTypeVisitor;
import org.karina.lang.compiler.utils.Generic;
import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.utils.RegionOf;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class KarinaUnitVisitor {
    private final String name;
    private final ObjectPath path;
    private final RegionContext conv;

    KarinaStructVisitor structVisitor;
    KarinaInterfaceVisitor interfaceVisitor;
    KarinaEnumVisitor enumVisitor;
    KarinaMethodVisitor methodVisitor;
    KarinaTypeVisitor typeVisitor;
    KarinaExprVisitor exprVisitor;
    KarinaAnnotationVisitor annotationVisitor;

    public KarinaUnitVisitor(RegionContext regionContext, String name, ObjectPath path) {
        this.name = name;
        this.path = path;
        this.conv = regionContext;

        this.typeVisitor = new KarinaTypeVisitor(this, regionContext);
        this.exprVisitor = new KarinaExprVisitor(this, this.typeVisitor, regionContext);
        this.annotationVisitor = new KarinaAnnotationVisitor(regionContext, this.typeVisitor, this.exprVisitor);
        this.structVisitor = new KarinaStructVisitor(this, regionContext);
        this.interfaceVisitor = new KarinaInterfaceVisitor(this, regionContext);
        this.enumVisitor = new KarinaEnumVisitor(this, regionContext);
        this.methodVisitor = new KarinaMethodVisitor(this, regionContext);

    }

    public ClassPointer visit(KarinaParser.UnitContext ctx, ModelBuilder builder) {
        var region = this.conv.toRegion(ctx);
        int mods = Modifier.PUBLIC | Modifier.FINAL;

        var imports = ImmutableList.copyOf(ctx.import_().stream().map(this::visitImport).toList());
        var interfaces = ImmutableList.<KType.ClassType>of();
        var fields = ImmutableList.<KFieldModel>of();

        var methods = ImmutableList.<KMethodModel>builder();
        var generics = ImmutableList.<Generic>of();
        var superClass = KType.ROOT;
        var currentClass = ClassPointer.of(region, this.path);
        var constructor =
                KarinaStructVisitor.createDefaultConstructor(region, currentClass, List.of(), Modifier.PRIVATE, superClass);
        methods.add(constructor);
        for (var itemContext : ctx.item()) {
            if (itemContext.function() != null) {
                var annotationsInner = ImmutableList.<KAnnotation>builder();

                if (itemContext.annotation() != null) {
                    for (var annotationContext : itemContext.annotation()) {
                        var annotation = this.annotationVisitor.visit(annotationContext);
                        annotationsInner.add(annotation);
                    }
                }
                var kAnnotations = annotationsInner.build();

                var method = this.methodVisitor.visit(currentClass, kAnnotations, itemContext.function());
                methods.add(method);
            }
        }

            var permittedSubClasses = ImmutableList.<ClassPointer>of();


        var innerClassesToFill = new ArrayList<KClassModel>();
        var annotations = ImmutableList.<KAnnotation>of();

        var nestMembersToFill = new ArrayList<ClassPointer>();
        var classModel = new KClassModel(
                this.name,
                this.path,
                mods,
                superClass,
                null,
                interfaces,
                innerClassesToFill,
                fields,
                methods.build(),
                generics,
                imports,
                permittedSubClasses,
                nestMembersToFill,
                annotations,
                this.conv.source(),
                region,
                null
        );



        for (var itemContext : ctx.item()) {
            var annotationsInner = ImmutableList.<KAnnotation>builder();

            if (itemContext.annotation() != null) {
                for (var annotationContext : itemContext.annotation()) {
                    var annotation = this.annotationVisitor.visit(annotationContext);
                    annotationsInner.add(annotation);
                }
            }
            var kAnnotations = annotationsInner.build();
            if (itemContext.struct() != null) {
                var struct = this.structVisitor.visit(classModel, this.path, kAnnotations, itemContext.struct(), builder);
                innerClassesToFill.add(struct);
            } else if (itemContext.enum_() != null) {
                var enum_ = this.enumVisitor.visit(classModel, this.path, kAnnotations , itemContext.enum_(), builder);
                innerClassesToFill.add(enum_);
            } else if (itemContext.interface_() != null) {
                var interface_ = this.interfaceVisitor.visit(classModel, this.path, kAnnotations, itemContext.interface_(), builder);
                innerClassesToFill.add(interface_);
            } else if (itemContext.function() == null) {
                Log.syntaxError(this.conv.toRegion(itemContext), "Invalid item");
                throw new Log.KarinaException();
            }
        }
        //get all inner classes and add them as nest members, so they have access to stuff on the top level
        putNestMembers(innerClassesToFill, nestMembersToFill);

        //same classes as nestMembersToFill
        var allInnerClasses = new ArrayList<KClassModel>();
        putAllClassesDeep(innerClassesToFill, allInnerClasses);

        //we update nest members for all inner classes, so they have access to each other
        for (var deepInnerClass : allInnerClasses) {
            deepInnerClass.updateNestMembers(nestMembersToFill);
        }

        builder.addClass(classModel);

        return classModel.pointer();
    }


    private void putNestMembers(List<? extends ClassModel> children, List<ClassPointer> collection) {
        for (var child : children) {
            collection.add(child.pointer());
            putNestMembers(child.innerClasses(), collection);
        }
    }

    /**
     * Technically, the same as putNestMembers
     */
    private void putAllClassesDeep(List<? extends ClassModel> children, List<KClassModel> collection) {
        for (var child : children) {
            if (child instanceof KClassModel kChild) {
                collection.add(kChild);
            }
            putAllClassesDeep(child.innerClasses(), collection);
        }
    }

    private KImport visitImport(KarinaParser.Import_Context ctx) {

        var region = this.conv.toRegion(ctx);
        var path = visitDotWordChain(ctx.dotWordChain()).value();
        var importAll = ctx.CHAR_STAR() != null;
        if (importAll) {
            return new KImport(region, new KImport.TypeImport.All(), path);
        }

        var names = ImmutableList.<String>builder();

        if (ctx.id() != null) {
            if (ctx.AS() != null) {
                var aliasRegion = this.conv.region(ctx.id());
                return new KImport(
                        region,
                        new KImport.TypeImport.BaseAs(aliasRegion.region(), aliasRegion.value()),
                        path
                );
            } else {
                names.add(this.conv.escapeID(ctx.id()));
            }
        } else if (ctx.commaWordChain() != null) {
            ctx.commaWordChain().id().forEach(id -> names.add(this.conv.escapeID(id)));
        } else {
            return new KImport(region, new KImport.TypeImport.Base(), path);
        }

        return new KImport(region, new KImport.TypeImport.Names(names.build()), path);

    }

    public RegionOf<ObjectPath> visitDotWordChain(KarinaParser.DotWordChainContext ctx) {

        var elements = ctx.id().stream().map(this.conv::escapeID).toList();
        return this.conv.region(new ObjectPath(elements), ctx.getSourceInterval());

    }

    public List<KType> visitGenericHint(KarinaParser.GenericHintContext ctx) {

        return ctx.type().stream().map(ref -> {
            var mapped = this.typeVisitor.visitType(ref);
            if (mapped.isVoid() || mapped.isPrimitive()) {
                var innerRegion = this.conv.toRegion(ref);
                Log.attribError(new AttribError.NotSupportedType(innerRegion, mapped));
                throw new Log.KarinaException();
            }
            return mapped;
        }).toList();
    }

    public List<Generic> visitGenericHintDefinition(KarinaParser.GenericHintDefinitionContext ctx) {
        return ctx.id().stream().map(ref -> {
            var region = this.conv.region(ref);
            var generic = new Generic(region.region(), region.value());
            generic.updateBounds(KType.ROOT, List.of());
            return generic;
        }).toList();
    }

}
