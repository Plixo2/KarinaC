package org.karina.lang.compiler.stages.generate;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.utils.Context;
import org.karina.lang.compiler.utils.IntoContext;
import org.karina.lang.compiler.utils.Region;
import org.karina.lang.compiler.utils.Variable;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class GenerationContext implements IntoContext {
    @Setter
    private int lastLineNumber = 0;
    private final InsnList instructions;
    private final Map<Variable, Integer> variables = new HashMap<>();
    private final List<LocalVariableNode> localVariables;
    private final Context c;

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
            this.variablesCount += TypeEncoding.jvmSize(TypeEncoding.getType(type));
        }
    }

    public int getVariableIndex(Region region, Variable variable) {
        if (!this.variables.containsKey(variable)) {
            var available = this.variables.keySet().stream().map(Variable::name).toList();
            Log.temp(this, region, "Variable '" + variable.name() + "' not found. This is a bug in the compiler, available " + available);
            throw new Log.KarinaException();
        }
        return this.variables.get(variable);
    }

    public ImmutableMap<Variable, Integer> getVariables() {
        return ImmutableMap.copyOf(this.variables);
    }


    @Override
    public Context intoContext() {
        return this.c;
    }
}