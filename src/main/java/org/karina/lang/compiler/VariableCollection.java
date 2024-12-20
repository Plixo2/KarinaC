package org.karina.lang.compiler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VariableCollection implements Iterable<Variable> {
    private final List<Variable> variables;

    public VariableCollection() {
        this.variables = List.of();
    }
    private VariableCollection(List<Variable> collection) {
        this.variables = new ArrayList<>(collection);
    }

    public VariableCollection add(Variable variable) {
        var collection = new ArrayList<>(this.variables);
        collection.add(variable);
        return new VariableCollection(collection);
    }

    public boolean contains(String name) {
        return this.variables.stream().anyMatch(v -> v.name().equals(name));
    }

    public @Nullable Variable get(String name) {
        return this.variables.stream().filter(v -> v.name().equals(name)).findFirst().orElse(null);
    }

    @Override
    public @NotNull Iterator<Variable> iterator() {
        return this.variables.iterator();
    }

    public static VariableCollection empty() {
        return new VariableCollection();
    }
}
