package org.karina.lang.compiler.objects;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.*;
import org.karina.lang.compiler.json.JsonElement;
import org.karina.lang.compiler.stages.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public final class KTree {

    public static @Nullable KItem findAbsolutItem(KPackage root, ObjectPath absolut) {
        if (absolut.isEmpty()) {
            return null;
        }
        var first = absolut.first();
        if (!first.equals(root.name)) {
            return null;
        }
        return findRelativeItem(root, absolut.tail());
    }

    public static @Nullable KItem findRelativeItem(KPackage root, ObjectPath relativeToSrc) {
        if (relativeToSrc.isEmpty()) {
            return null;
        }
        var objectPath = relativeToSrc.everythingButLast();
        var unit = root.findUnit(objectPath);
        if (unit == null) {
            return null;
        }
        var itemName = relativeToSrc.last();
        return unit.findItem(itemName);
    }

    @Builder
    public record KPackage(
            @NonNull ObjectPath path,
            @NonNull String name,
            @Singular List<KPackage> subPackages,
            @Singular List<KUnit> units
    ) {
        public @Nullable KUnit findUnit(ObjectPath path) {

            if (path.elements().isEmpty()) {
                return null;
            }
            var head = path.first();
            var tail = path.tail();
            if (tail.isEmpty()) {
                for (var unit : this.units) {
                    if (unit.name().equals(head)) {
                        return unit;
                    }
                }
            } else {
                for (var subPackage : this.subPackages) {
                    if (subPackage.name().equals(head)) {
                        return subPackage.findUnit(tail);
                    }
                }
            }
            return null;
        }

        public List<KUnit> getAllUnitsRecursively() {
            var units = new ArrayList<>(this.units);
            for (var subPackage : this.subPackages) {
                units.addAll(subPackage.getAllUnitsRecursively());
            }
            return units;
        }

    }

    @Builder
    public record KUnit(
            @NonNull Span region,
            @NonNull String name,
            @NonNull ObjectPath path,
            @Singular List<KImport> kImports,
            @Singular List<KItem> items,
            @Nullable SymbolTable unitScopeSymbolTable
    ) {

        public @Nullable KItem findItem(String name) {

            for (var item : this.items) {
                if (item.name().value().equals(name)) {
                    return item;
                }
            }
            return null;

        }

    }

    @Builder
    public record KImport(
            @NonNull Span region,
            @NonNull TypeImport importType,
            @NonNull SpanOf<ObjectPath> path
    ) {}

    public sealed interface KItem permits KFunction, KTypeItem {
        Span region();
        List<KAnnotation> annotations();
        SpanOf<String> name();
        ObjectPath path();
        List<Generic> generics();
    }

    public sealed interface KTypeItem extends KItem permits KInterface, KStruct, KEnum {
    }

    @Builder
    public record KInterface(
            @NonNull Span region,
            @NonNull SpanOf<String> name,
            @NonNull ObjectPath path,
            @Singular List<Generic> generics,
            @Singular List<KAnnotation> annotations,
            @Singular List<KFunction> functions,
            @Singular List<KType> superTypes
    ) implements KTypeItem {}

    @Builder
    public record KStruct(
            @NonNull Span region,
            @NonNull SpanOf<String> name,
            @NonNull ObjectPath path,
            @Singular List<Generic> generics,
            @Singular List<KAnnotation> annotations,
            @Singular List<KFunction> functions,
            @Singular List<KField> fields,
            @Singular List<KImplBlock> implBlocks
    ) implements KTypeItem {}

    @Builder
    public record KEnum(
            @NonNull Span region,
            @NonNull SpanOf<String> name,
            @NonNull ObjectPath path,
            @Singular List<Generic> generics,
            @Singular List<KAnnotation> annotations,
            @Singular List<KEnumEntry> entries
    ) implements KTypeItem {}


    @Builder
    public record KFunction(
            @NonNull Span region,
            @NonNull SpanOf<String> name,
            @NonNull ObjectPath path,
            @Singular List<KAnnotation> annotations,
            @Singular List<KParameter> parameters,
            @Nullable KType returnType,
            @Singular List<Generic> generics,
            @Nullable KExpr expr
    ) implements KItem {}

    @Builder
    public record KImplBlock(@NonNull Span region, @NonNull KType type, @Singular List<KFunction> functions) { }


    public record KField(Span region, SpanOf<String> name, KType type) { }


    @Builder
    public record KEnumEntry(Span region, SpanOf<String> name, @Singular List<KParameter> parameters) { }


    public record KParameter(Span region, SpanOf<String> name, KType type, @Nullable Variable symbol) { }
    public record KAnnotation(Span region, SpanOf<String> name, JsonElement value) { }

}
