package org.karina.lang.compiler.stages.variables;

import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.ObjectPath;
import org.karina.lang.compiler.Span;
import org.karina.lang.compiler.SpanOf;
import org.karina.lang.compiler.VariableCollection;
import org.karina.lang.compiler.errors.Log;
import org.karina.lang.compiler.errors.types.LinkError;
import org.karina.lang.compiler.objects.KTree;
import org.karina.lang.compiler.objects.KType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Method scope is the enclosing method, or function expression
//The Key in functions is the unit name
record VariableContext(@Nullable KType selfType, Span methodRegion, VariableCollection collection, Map<String, List<KTree.KFunction>> functions) {
    VariableContext {
        functions = Map.copyOf(functions);
    }


    public static VariableContext empty(KTree.KPackage root, Span methodRegion, List<SpanOf<ObjectPath>> functions) {
        var newFunctions = new HashMap<String, List<KTree.KFunction>>();

        for (var function : functions) {
            var path = function.value();
            var functionItem = root.findItem(path);
            if (functionItem instanceof KTree.KFunction kFunction) {
                if (path.size() < 2) {
                    Log.temp(function.region(), "Function path is too short");
                    throw new Log.KarinaException();
                }
                //last should be the function name
                //second to last should be the unit name
                var unitName = path.elements().get(path.size() - 2);
                newFunctions.putIfAbsent(unitName, new ArrayList<>());
                newFunctions.get(unitName).add(kFunction);
            } else {
                Log.linkError(new LinkError.UnknownIdentifier(function.region(), path.toString()));
                throw new Log.KarinaException();
            }
        }

        return new VariableContext(null, methodRegion, VariableCollection.empty(), newFunctions);
    }
}
