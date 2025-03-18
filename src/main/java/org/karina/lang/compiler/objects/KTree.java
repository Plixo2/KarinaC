package org.karina.lang.compiler.objects;

import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.SymbolTable;
import org.karina.lang.compiler.utils.Variable;
import org.karina.lang.compiler.utils.*;

import java.util.ArrayList;
import java.util.List;

@Deprecated()
public final class KTree {

    @Deprecated()
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

    @Deprecated()
    public static @Nullable KFunction findAbsoluteVirtualFunction(KPackage root, ObjectPath path) {
        if (path.isEmpty()) {
            return null;
        }
        var functionName = path.last();
        var item = findAbsolutItem(root, path.everythingButLast());
        if (item == null) {
            return null;
        }

        return switch (item) {
            case KFunction kf -> null;
            case KEnum ke -> null;
            case KInterface kInterface -> {
                yield null;
            }
            case KStruct kStruct -> {
                for (var function : kStruct.functions()) {
                    if (function.name().value().equals(functionName) && function.self() != null) {
                        yield function;
                    }
                }
                for (var implBlock : kStruct.implBlocks()) {
                    for (var function : implBlock.functions()) {
                        if (function.name().value().equals(functionName) && function.self() != null) {
                            yield function;
                        }
                    }
                }

                yield null;
            }
        };
    }

    @Deprecated()
    public static @Nullable KFunction findAbsoluteInterfaceFunction(KPackage root, ObjectPath path) {
        if (path.isEmpty()) {
            return null;
        }
        var functionName = path.last();
        var item = findAbsolutItem(root, path.everythingButLast());
        if (item == null) {
            return null;
        }

        return switch (item) {
            case KFunction kf -> null;
            case KEnum ke -> null;
            case KInterface kInterface -> {
                for (var function : kInterface.functions()) {
                    if (function.name().value().equals(functionName) && function.self() != null) {
                        yield function;
                    }
                }
                yield null;
            }
            case KStruct kStruct -> {
                for (var implBlock : kStruct.implBlocks()) {
                    for (var function : implBlock.functions()) {
                        if (function.name().value().equals(functionName) && function.self() != null) {
                            yield function;
                        }
                    }
                }
                yield null;
            }
        };
    }

//    public static @Nullable KField findAbsoluteField(KPackage root, ObjectPath path) {
//        if (path.isEmpty()) {
//            return null;
//        }
//        var fieldName = path.last();
//        var item = findAbsolutItem(root, path.everythingButLast());
//        if (item == null) {
//            return null;
//        }
//
//        return switch (item) {
//            case KFunction kf -> null;
//            case KEnum ke -> null;
//            case KInterface kInterface -> null;
//            case KStruct kStruct -> {
//                for (var field : kStruct.fields()) {
//                    if (field.name().value().equals(fieldName)) {
//                        yield field;
//                    }
//                }
//                yield null;
//            }
//        };
//    }

    @Deprecated()
    public static @Nullable KItem findRelativeItem(KPackage root, ObjectPath relativeToSrc) {
        if (relativeToSrc.isEmpty()) {
            return null;
        }
        var objectPath = relativeToSrc.everythingButLast();
        var unit = root.findUnit(objectPath);
        var last = relativeToSrc.last();
        if (unit == null) {
            var mayStruct = findRelativeItem(root, objectPath);
            if (mayStruct instanceof KStruct struct) {
                for (var function : struct.functions()) {
                    if (function.name().value().equals(last) && function.self() == null) {
                        return function;
                    }
                }
            } else if (mayStruct instanceof KInterface kInterface) {
                for (var function : kInterface.functions()) {
                    if (function.name().value().equals(last) && function.self() == null) {
                        return function;
                    }
                }
            }
            return null;
        }
        var function = unit.findItem(last);
        if (function instanceof KFunction kf) {
            if (kf.self() != null) {
                return null;
            }
        }
        return function;
    }

    @Deprecated()
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

    @Deprecated()
    @Builder
    public record KUnit(
            @NonNull Region region,
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

    @Deprecated()
    public sealed interface KItem permits KFunction, KTypeItem {
        Region region();
        List<KAnnotation> annotations();
        RegionOf<String> name();
        ObjectPath path();
        List<Generic> generics();
    }

    @Deprecated()
    public sealed interface KTypeItem extends KItem permits KInterface, KStruct, KEnum {
    }

    @Deprecated()
    @Builder
    public record KInterface(
            @NonNull Region region,
            @NonNull RegionOf<String> name,
            @NonNull ObjectPath path,
            @Singular List<Generic> generics,
            @Singular List<KAnnotation> annotations,
            @Singular List<KFunction> functions,
            @Singular List<KType> superTypes,
            @Singular List<ObjectPath> permittedStructs
    ) implements KTypeItem {}

    @Deprecated()
    @Builder
    public record KStruct(
            @NonNull Region region,
            @NonNull RegionOf<String> name,
            @NonNull ObjectPath path,
            @NotNull StructModifier modifier,
            @Singular List<Generic> generics,
            @Singular List<KAnnotation> annotations,
            @Singular List<KFunction> functions,
            @Singular List<KField> fields,
            @Singular List<KImplBlock> implBlocks
    ) implements KTypeItem {}

    @Deprecated()
    @Builder
    public record KEnum(
            @NonNull Region region,
            @NonNull RegionOf<String> name,
            @NonNull ObjectPath path,
            @Singular List<Generic> generics,
            @Singular List<KAnnotation> annotations,
            @Singular List<KEnumEntry> entries
    ) implements KTypeItem {}


    @Deprecated()
    @Builder
    public record KFunction(
            @NonNull Region region,
            @NonNull RegionOf<String> name,
            @NonNull ObjectPath path,
            @Nullable Region self,// null if not defines self, otherwise the region of the self definition
            @NotNull FunctionModifier modifier, // unused
            @Singular List<KAnnotation> annotations,
            @Singular List<KParameter> parameters,
            @Nullable KType returnType,
            @Singular List<Generic> generics,
            @Nullable KExpr expr
    ) implements KItem {}

    @Deprecated()
    @Builder
    public record KImplBlock(@NonNull Region region, @NonNull KType type, @Singular List<KFunction> functions) { }

    @Deprecated()
    public record KField(Region region, ObjectPath path, RegionOf<String> name, KType type) { }

    @Deprecated()
    @Builder
    public record KEnumEntry(Region region, RegionOf<String> name, @Singular List<KParameter> parameters) { }

    @Deprecated()
    public record KParameter(Region region, RegionOf<String> name, KType type, @Nullable @Symbol Variable symbol) { }

    @Deprecated()
    public record KAnnotation(Region region, RegionOf<String> name, JsonElement value) { }

}
