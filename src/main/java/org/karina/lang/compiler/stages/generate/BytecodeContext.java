package org.karina.lang.compiler.stages.generate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.utils.Variable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Getter
public class BytecodeContext {
    @Setter
    private int lastLineNumber = 0;
    private final InsnList instructions;
    private final MethodNode node;
    private final Map<Variable, Integer> variables = new HashMap<>();
    private int variablesCount = 0;
    @Setter
    private @Nullable LabelNode breakTarget;
    @Setter
    private @Nullable LabelNode continueTarget;

    public void add(AbstractInsnNode instruction) {
        this.instructions.add(instruction);
    }

    public void putVariable(Variable variable) {
        if (!this.variables.containsKey(variable)) {
            this.variables.put(variable, this.variablesCount);
            var type = variable.type();
            this.variablesCount += TypeConversion.jvmSize(TypeConversion.getType(type));
        }
    }

    public int getVariableIndex(Variable variable) {
        if (!this.variables.containsKey(variable)) {
            throw new NullPointerException("Variable not found " + variable);
        }
        return this.variables.get(variable);
    }

    public Set<Map.Entry<Variable, Integer>> getVariables() {
        return Set.copyOf(this.variables.entrySet());
    }


}