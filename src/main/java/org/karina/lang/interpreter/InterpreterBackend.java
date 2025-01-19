package org.karina.lang.interpreter;

import lombok.RequiredArgsConstructor;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.utils.ObjectPath;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@RequiredArgsConstructor
public class InterpreterBackend {
    private final boolean verbose;
    private final PrintStream stdOut;

    public Interpreter accept(KTree.KPackage kPackage) {
        var functions = new ArrayList<KTree.KFunction>();
        var collection = new FunctionCollection();
        for (var kUnit : kPackage.getAllUnitsRecursively()) {
            for (var item : kUnit.items()) {
                if (item instanceof KTree.KFunction function) {
                    functions.add(function);
                } else if (item instanceof KTree.KStruct struct) {
                    functions.addAll(struct.functions());

                    var vTable = new HashMap<ObjectPath, FunctionCollection.RuntimeFunction>();

                    for (var implBlock : struct.implBlocks()) {
                        functions.addAll(implBlock.functions());
                        for (var function : implBlock.functions()) {
                            if (!(implBlock.type() instanceof KType.ClassType clsType)) {
                                throw new NullPointerException("invalid interface type");
                            }
                            var functionName = function.name().value();
                            var interfacePath = clsType.path();
                            var runtime = toRuntime(function);
                            var interfaceFunctionPath = interfacePath.append(functionName);
                            vTable.put(interfaceFunctionPath, runtime);
                        }
                    }
                    collection.putVTable(struct.path(), vTable);
                } else if (item instanceof KTree.KInterface kInterface) {
                    for (var function : kInterface.functions()) {
                        if (function.expr() != null) {
                            functions.add(function);
                        }
                    }
                }
            }
        }

        for (var function : functions) {
            collection.putFunction(function.path(), toRuntime(function));
        }
        if (this.verbose) {
            collection.print(this.stdOut);
        }

        return new Interpreter(collection);

    }


    private static FunctionCollection.RuntimeFunction toRuntime(KTree.KFunction function) {
        var expr = Objects.requireNonNull(function.expr());
        var params = function.parameters().stream().map(KTree.KParameter::symbol).toList();
        if (params.contains(null)) {
            throw new NullPointerException("Parameter is null");
        }
        return new FunctionCollection.RuntimeFunction.KarinaFunction(expr, params);
    }

}
