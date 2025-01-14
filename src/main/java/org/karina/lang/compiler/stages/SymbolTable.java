package org.karina.lang.compiler.stages;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.Generic;
import org.karina.lang.compiler.Pair;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.ImportError;
import org.karina.lang.compiler.objects.KTree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Symbol table to store top level items, functions, and generics
 * This is used mainly for imports, but also for type checking
 */
public record SymbolTable(
        Map<String, KTree.KTypeItem> items,
        Map<String, KTree.KFunction> functions,
        Map<String, Generic> scopeGenerics
) {

    public SymbolTable {
        // Copy item to prevent modification
        items = new HashMap<>(items);
        functions = new HashMap<>(functions);
        scopeGenerics = new HashMap<>(scopeGenerics);
    }


    public @Nullable KTree.KTypeItem getItem(String name) {
        return this.items.get(name);
    }

    public @Nullable KTree.KFunction getFunction(String name) {
        return this.functions.get(name);
    }

    public @Nullable Generic getGeneric(String name) {
        return this.scopeGenerics.get(name);
    }

    public Set<String> availableFunctionNames() {
        return this.functions.keySet();
    }

    public Set<String> availableTypeNames() {
        var keys = new HashSet<>(this.items.keySet());
        keys.addAll(this.scopeGenerics.keySet());
        return keys;
    }
    public static SymbolTableBuilder builder() {
        return new SymbolTableBuilder();
    }

    public static class SymbolTableBuilder {
        private final Map<String, Pair<SpanOf<KTree.KTypeItem>, SymbolLocation>> items = new HashMap<>();
        private final Map<String, Pair<SpanOf<KTree.KFunction>, SymbolLocation>> functions = new HashMap<>();
        private final Map<String, Generic> scopeGenerics = new HashMap<>();

        public void addItem(SymbolLocation bucket, String name, SpanOf<KTree.KTypeItem> item) {
            if (this.items.containsKey(name)) {
                var existingSymbol = this.items.get(name);
                var inBucket = existingSymbol.second();
                if (inBucket == bucket) {
                    Log.importError(new ImportError.DuplicateItem(
                            existingSymbol.first().region(),
                            item.region(), name
                    ));
                    throw new Log.KarinaException();
                } else if (bucket.shouldOverwrite(inBucket)) {
                    this.items.put(name, Pair.of(item, bucket));
                }
            } else {
                this.items.put(name, Pair.of(item, bucket));
            }
        }

        public void addFunction(SymbolLocation bucket, String name, SpanOf<KTree.KFunction> item) {
            if (this.functions.containsKey(name)) {
                var existingSymbol = this.functions.get(name);
                var inBucket = existingSymbol.second();
                if (inBucket == bucket) {
                    Log.importError(new ImportError.DuplicateItem(
                            existingSymbol.first().region(),
                            item.region(), name
                    ));
                    throw new Log.KarinaException();
                } else if (bucket.shouldOverwrite(inBucket)) {
                    this.functions.put(name, Pair.of(item, bucket));
                }
            } else {
                this.functions.put(name, Pair.of(item, bucket));
            }
        }

        public void addGeneric(String name, Generic generic) {
            if (this.scopeGenerics.containsKey(name)) {
                var existingSymbol = this.scopeGenerics.get(name);
                Log.importError(new ImportError.DuplicateItem(
                        existingSymbol.region(),
                        generic.region(),
                        name
                ));
                throw new Log.KarinaException();
            } else {
                this.scopeGenerics.put(name, generic);
            }
        }

        public void clearGenerics() {
            this.scopeGenerics.clear();
        }


        public SymbolTable build() {
            var items = new HashMap<String, KTree.KTypeItem>();
            var functions = new HashMap<String, KTree.KFunction>();

            for (var entry : this.items.entrySet()) {
                var name = entry.getKey();
                var item = entry.getValue().first().value();
                items.put(name, item);
            }

            for (var entry : this.functions.entrySet()) {
                var name = entry.getKey();
                var item = entry.getValue().first().value();
                functions.put(name, item);
            }

            return new SymbolTable(items, functions, this.scopeGenerics);
        }

        public SymbolTableBuilder copy() {
            var builder = new SymbolTableBuilder();
            builder.items.putAll(this.items);
            builder.functions.putAll(this.functions);
            builder.scopeGenerics.putAll(this.scopeGenerics);
            return builder;
        }
    }


    public enum SymbolLocation {
        PRELUDE,
        NON_LOCAL,
        LOCAL;

        public boolean shouldOverwrite(SymbolLocation other) {
            return this == SymbolLocation.LOCAL || other == SymbolLocation.NON_LOCAL ||
                    (this == SymbolLocation.NON_LOCAL && other == SymbolLocation.PRELUDE);
        }
    }
}
