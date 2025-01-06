package org.karina.lang.eval;

import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.stages.Variable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FunctionCollection {
    private final Map<ObjectPath, RuntimeFunction> functions = new HashMap<>();

    public RuntimeFunction function(String name) {
        return this.function(ObjectPath.fromString(name, "."));
    }


    public RuntimeFunction function(ObjectPath path) {
        return Objects.requireNonNull(this.functions.get(path), "Function not found: " + path);
    }

    public void putFunction(String name, RuntimeFunction func ) {
        this.putFunction(ObjectPath.fromString(name, "."), func);
    }

    public void putFunction(ObjectPath path, RuntimeFunction func ) {
        this.functions.put(path, func);
    }


    public sealed interface RuntimeFunction {

        record KarinaFunction(KExpr expr, List<Variable> parameters) implements RuntimeFunction { }

        record JavaFunction(Method method) implements RuntimeFunction { }

        record ClosureFunction(KExpr expr, List<Variable> parameters, Map<Variable, Object> captures, Object self) implements RuntimeFunction { }
    }

}
