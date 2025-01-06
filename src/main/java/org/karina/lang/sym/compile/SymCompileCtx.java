package org.karina.lang.sym.compile;

import lombok.RequiredArgsConstructor;
import org.karina.lang.compiler.stages.Variable;
import org.karina.lang.sym.Instruction;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SymCompileCtx {
    private final VariableSet variableSet;

    private final List<Instruction> instructions = new ArrayList<>();

    public void putVariable(Variable variable) {
        this.variableSet.putVariable(variable);
    }

    public int variableIndex(Variable variable) {
        return this.variableSet.variableIndex(variable);
    }


    public void add(Instruction instruction) {
        this.instructions.add(instruction);
    }


}
