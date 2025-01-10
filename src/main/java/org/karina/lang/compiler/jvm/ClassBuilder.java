package org.karina.lang.compiler.jvm;

import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.api.TextSource;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.StructModifier;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.List;

public record ClassBuilder(Span span) {


    public KTree.KStruct build(ClassNode classNode) {
        var path = ObjectPath.fromString(classNode.name, "/");
        var builder = KTree.KStruct.builder();
        builder.region(this.span);
        builder.name(SpanOf.span(this.span, path.last()));
        builder.path(path);
        var isStatic = Modifier.isStatic(classNode.access);
        var isPrivate = !Modifier.isPublic(classNode.access);
        builder.modifier(new StructModifier(isPrivate, isStatic));

        //Empty for now
        builder.generics(List.of());

        //We dont care about annotations
        builder.annotations(List.of());

        for (var field : classNode.fields) {
            builder.field(buildField(field, path));
        }

        return builder.build();
    }

    private KTree.KFunction buildFunction(MethodNode node) {

        throw new NullPointerException("");
    }

    private KTree.KField buildField(FieldNode node, ObjectPath path) {

        var subPath = path.append(node.name);
        var name = SpanOf.span(this.span, node.name);
        var type = buildType(this.span, Type.getType(node.desc));
        return new KTree.KField(this.span, subPath, name, type);

    }

    private static KType buildType(Span region, Type type) {

        return switch (type.getSort()) {
            case Type.VOID -> new KType.PrimitiveType.VoidType(region);
            case Type.BOOLEAN -> new KType.PrimitiveType.BoolType(region);
            case Type.CHAR -> new KType.PrimitiveType.CharType(region);
            case Type.BYTE -> new KType.PrimitiveType.ByteType(region);
            case Type.SHORT -> new KType.PrimitiveType.ShortType(region);
            case Type.INT -> new KType.PrimitiveType.IntType(region);
            case Type.FLOAT -> new KType.PrimitiveType.FloatType(region);
            case Type.LONG -> new KType.PrimitiveType.LongType(region);
            case Type.DOUBLE -> new KType.PrimitiveType.DoubleType(region);
            case Type.ARRAY -> new KType.ArrayType(region, buildType(region, type.getElementType()));
            case Type.OBJECT -> {
                var className = type.getClassName();
                var path = ObjectPath.fromString(className, "/");
                yield new KType.ClassType(region, SpanOf.span(region, path), List.of());
            }
            default -> throw new IllegalStateException("Unexpected value: " + type.getSort());
        };

    }

    public static Span span(JavaResource resource) {
        var source = new TextSource(resource, List.of("<java>"));
        var start = new Span.Position(0, 0);
        var end = new Span.Position(0, 0);
        return new Span(source, start, end);
    }
}
