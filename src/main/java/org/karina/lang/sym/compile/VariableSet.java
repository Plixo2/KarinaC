package org.karina.lang.sym.compile;

import lombok.RequiredArgsConstructor;
import org.karina.lang.compiler.utils.Variable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class VariableSet {
    private final int baseVariableOffset;
    private final List<Variable> variables = new ArrayList<>();


    public void putVariable(Variable variable) {
        this.variables.add(variable);
    }

    public int variableIndex(Variable variable) {
        return this.baseVariableOffset + this.variables.indexOf(variable);
    }



}
