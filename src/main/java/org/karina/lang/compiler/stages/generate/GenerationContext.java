package org.karina.lang.compiler.stages.generate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.Variable;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Getter
public class GenerationContext {
    @Setter
    private int lastLineNumber = 0;
    private final InsnList instructions;
//    private final MethodNode node;
    private final Map<Variable, Integer> variables = new HashMap<>();
    private final List<LocalVariableNode> localVariables;

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

    public int getVariableIndex(Region region, Variable variable) {
        if (!this.variables.containsKey(variable)) {
            Log.temp(region, "Variable not found " + variable);
            throw new Log.KarinaException();
        }
        return this.variables.get(variable);
    }

    public ImmutableMap<Variable, Integer> getVariables() {
        return ImmutableMap.copyOf(this.variables);
    }


}