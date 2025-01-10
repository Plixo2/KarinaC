package org.karina.lang.eval;

import java.math.BigDecimal;

public class SimpleLibrary extends Library {

    @Override
    public void addToInterpreter(Interpreter interpreter) {
        try {
            {
                var method = SimpleLibrary.class.getMethod("arrayOfSize", BigDecimal.class);
                var func = new FunctionCollection.RuntimeFunction.JavaFunction(method);
                interpreter.collection().putFunction("src.Main.array", func);
            }

            {
                var method = SimpleLibrary.class.getMethod("println", Object.class);
                var func = new FunctionCollection.RuntimeFunction.JavaFunction(method);
                interpreter.collection().putFunction("src.Main.println", func);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object[] arrayOfSize(BigDecimal size) {
        var intValue = size.intValue();
        if (intValue < 0) {
            throw new IllegalArgumentException("Size of Array must be non-negative");
        }

        return new Object[intValue];
    }

    public static void println(Object obj) {
        System.out.println(Interpreter.toString(obj));
    }

}
