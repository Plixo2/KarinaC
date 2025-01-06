package org.karina.lang.compiler.stages;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class VariableCollection implements Iterable<Variable> {
    private final List<Variable> variables;
    private final Set<Variable> markedImmutable;

    public VariableCollection() {
        this.variables = List.of();
        this.markedImmutable = Set.of();
    }

    private VariableCollection(List<Variable> collection, Set<Variable> markedImmutable) {
        this.variables = List.copyOf(collection);
        this.markedImmutable = Set.copyOf(markedImmutable);
    }

    public boolean isFinal(String name) {
        var variable = get(name);
        if (variable == null) {
            return false;
        }
        return this.markedImmutable.contains(variable) || !variable.mutable();
    }

    public Set<String> names() {
        return new HashSet<>(this.variables.stream().map(Variable::name).toList());
    }

    public VariableCollection add(Variable variable) {
        var collection = new ArrayList<>(this.variables);
        collection.add(variable);
        return new VariableCollection(collection, this.markedImmutable);
    }

    public VariableCollection markImmutable(Variable variable) {
        var markedFinal = new HashSet<>(this.markedImmutable);
        markedFinal.add(variable);
        return new VariableCollection(this.variables, markedFinal);
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
