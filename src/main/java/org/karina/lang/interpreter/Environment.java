package org.karina.lang.interpreter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.lang.compiler.utils.Variable;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Environment {
    @Getter
    @Accessors(fluent = true)
    private final Object self;
    private final Map<Variable, Object> variables = new HashMap<>();

    public void set(Variable variable, Object value) {
        this.variables.put(variable, value);
    }

    public Object get(Variable variable) {
        return this.variables.get(variable);
    }



}
