package org.karina.lang.compiler.utils;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.objects.KExpr;
import org.karina.lang.compiler.objects.KType;
import org.karina.lang.compiler.objects.annotations.AnnotationAST;
import org.karina.lang.compiler.objects.annotations.AnnotationObject;
import org.karina.lang.compiler.objects.annotations.AnnotationValue;

import java.util.HashMap;

public record SuperAnnotation(Region region, KType superType) {

    public static SuperAnnotation fromAnnotation(AnnotationValue value) {

        if (!(value instanceof AnnotationObject object)) {
            Log.temp(value.region(), "Expected an annotation object");
            throw new Log.KarinaException();
        }
        var entries = new HashMap<>(object.values());

        KType type;
        if (entries.containsKey("type")) {
            type= intoKType(entries.get("type"));
            entries.remove("type");
        } else {
            Log.temp(value.region(), "missing 'fieldType' field");
            throw new Log.KarinaException();
        }

        if (!entries.isEmpty()) {
            Log.temp(value.region(), "Unexpected fields: " + entries.keySet());
            throw new Log.KarinaException();
        }


        return new SuperAnnotation(value.region(), type);
    }

    private static KExpr intoKExpr(AnnotationValue value) {
        if (value instanceof AnnotationAST.Expression expr) {
            return expr.expr();
        }
        Log.temp(value.region(), "Expected an expression");
        throw new Log.KarinaException();
    }

    private static KType intoKType(AnnotationValue value) {
        if (value instanceof AnnotationAST.Type type) {
            return type.type();
        }
        Log.temp(value.region(), "Expected a fieldType");
        throw new Log.KarinaException();
    }
}
