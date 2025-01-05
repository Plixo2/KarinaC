package org.karina.lang.compiler.stages;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VariableCollection implements Iterable<Variable> {
    private final List<Variable> variables;

    public VariableCollection() {
        this.variables = List.of();
    }
    private VariableCollection(List<Variable> collection) {
        this.variables = new ArrayList<>(collection);
    }

    public Set<String> names() {
        return new HashSet<>(this.variables.stream().map(Variable::name).toList());
    }

    public VariableCollection add(Variable variable) {
        var collection = new ArrayList<>(this.variables);
        collection.add(variable);
        return new VariableCollection(collection);
    }

    public boolean contains(String name) {
        return get(name) != null;
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
