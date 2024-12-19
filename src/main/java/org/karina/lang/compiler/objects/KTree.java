package org.karina.lang.compiler.objects;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.json.JsonElement;
import org.karina.lang.lsp.EventHandler;

import java.util.ArrayList;
import java.util.List;

public final class KTree {

    @Builder
    public record KPackage(
            @NonNull ObjectPath path,
            @NonNull String name,
            @Singular List<KPackage> subPackages,
            @Singular List<KUnit> units
    ) {

        public KItem findItem(ObjectPath path) {
            if (path.isEmpty()) {
                return null;
            }
            var objectPath = path.everythingButLast();
            var unit = findUnit(objectPath);
            if (unit == null) {
                return null;
            }
            var itemName = path.last();
            return unit.findItem(itemName);
        }

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
            @Singular List<SpanOf<ObjectPath>> importedFunctions
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
            @NonNull SynatxObject.TypeImport importType,
            @NonNull SpanOf<ObjectPath> path
    ) {}

    public sealed interface KItem permits KFunction, KTypeItem {
        Span region();
        List<KAnnotation> annotations();
        SpanOf<String> name();
        ObjectPath path();
        List<KType.GenericType> generics();
    }

    public sealed interface KTypeItem extends KItem permits KInterface, KStruct, KEnum {
    }

    @Builder
    public record KInterface(
            @NonNull Span region,
            @NonNull SpanOf<String> name,
            @NonNull ObjectPath path,
            @Singular List<KType.GenericType> generics,
            @Singular List<KAnnotation> annotations,
            @Singular List<KFunction> functions,
            @Singular List<KType> superTypes
    ) implements KTypeItem {}

    @Builder
    public record KStruct(
            @NonNull Span region,
            @NonNull SpanOf<String> name,
            @NonNull ObjectPath path,
            @Singular List<KType.GenericType> generics,
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
            @Singular List<KType.GenericType> generics,
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
            @Singular List<KType.GenericType> generics,
            @Nullable KExpr expr
    ) implements KItem {}

    @Builder
    public record KImplBlock(@NonNull Span region, @NonNull KType type, @Singular List<KFunction> functions) { }


    public record KField(Span region, SpanOf<String> name, KType type) { }


    @Builder
    public record KEnumEntry(Span region, SpanOf<String> name, @Singular List<KParameter> parameters) { }


    public record KParameter(Span region, SpanOf<String> name, KType type) { }
    public record KAnnotation(Span region, SpanOf<String> name, JsonElement value) { }

}
