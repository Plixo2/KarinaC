package org.karina.lang.compiler;

import org.jetbrains.annotations.NotNull;

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

    @Override
    public @NotNull Iterator<Variable> iterator() {
        return this.variables.iterator();
    }

    public static VariableCollection empty() {
        return new VariableCollection();
    }
}
