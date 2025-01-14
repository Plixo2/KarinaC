package org.karina.lang.eval;

import org.karina.lang.compiler.utils.ObjectPath;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.utils.Variable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FunctionCollection {
    private final Map<ObjectPath, RuntimeFunction> functions = new HashMap<>();
    private final Map<ObjectPath, Map<ObjectPath, RuntimeFunction>> vTables = new HashMap<>();

    public RuntimeFunction function(String name) {
        return this.function(ObjectPath.fromString(name, "."));
    }

    public RuntimeFunction function(ObjectPath path) {
        return Objects.requireNonNull(this.functions.get(path), "Function not found: " + path);
    }

    public RuntimeFunction vTable(ObjectPath path, ObjectPath key) {
        var table =
                Objects.requireNonNull(this.vTables.get(path), "V ATable not found: " + path);
        return Objects.requireNonNull(table.get(key), "VTable entry not found: " + key);
    }

    public void putFunction(String name, RuntimeFunction func ) {
        this.putFunction(ObjectPath.fromString(name, "."), func);
    }

    public void putFunction(ObjectPath path, RuntimeFunction func ) {
        this.functions.put(path, func);
    }

    public void putVTable(ObjectPath path, Map<ObjectPath, RuntimeFunction> vTable) {
        this.vTables.put(path, vTable);
    }



    public sealed interface RuntimeFunction {

        record KarinaFunction(KExpr expr, List<Variable> parameters) implements RuntimeFunction { }

        record JavaFunction(Method method) implements RuntimeFunction { }

        record ClosureFunction(KExpr expr, List<Variable> parameters, Map<Variable, Object> captures, Object self) implements RuntimeFunction { }
    }


    public void print() {
        System.out.println("Function Collection: ");
        System.out.println(">Functions");
        for (var entry : this.functions.entrySet()) {
            System.out.println("    fn " + entry.getKey());
        }

        System.out.println(">VTables");
        for (var entry : this.vTables.entrySet()) {
            System.out.println("    " +entry.getKey());
            for (var vTableEntry : entry.getValue().entrySet()) {
                System.out.println("        " + vTableEntry.getKey());
            }
        }
        System.out.println();
    }
}
