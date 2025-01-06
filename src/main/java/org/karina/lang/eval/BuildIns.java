package org.karina.lang.eval;

import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BuildIns {

    @SneakyThrows
    public static void addToSolver(Solver solver) {
        {
            var method = BuildIns.class.getMethod("arrayOfSize", BigDecimal.class);
            var func = new FunctionCollection.RuntimeFunction.JavaFunction(method);
            solver.collection().putFunction("src.Main.array", func);
        }

        {
            var method = BuildIns.class.getMethod("println", Object.class);
            var func = new FunctionCollection.RuntimeFunction.JavaFunction(method);
            solver.collection().putFunction("src.Main.println", func);
        }
    }

    public static List<Object> arrayOfSize(BigDecimal size) {
        var intValue = size.intValue();
        if (intValue < 0) {
            throw new IllegalArgumentException("Size of Array must be non-negative");
        }
        var list = new ArrayList<Object>();

        for (var i = 0; i < intValue; i++) {
            list.add(null);
        }

        return list;
    }

    public static void println(Object obj) {
        System.out.println(obj);
    }

}
