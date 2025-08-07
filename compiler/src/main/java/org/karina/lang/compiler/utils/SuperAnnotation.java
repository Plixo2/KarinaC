package org.karina.lang.compiler.utils;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.utils.annotations.AnnotationAST;
import org.karina.lang.compiler.utils.annotations.AnnotationObject;
import org.karina.lang.compiler.utils.annotations.AnnotationValue;

import java.util.HashMap;

public record SuperAnnotation(Region region, KType superType) {

    public static SuperAnnotation fromAnnotation(Context c, AnnotationValue value) {

        if (!(value instanceof AnnotationObject object)) {
            Log.temp(c, value.region(), "Expected an annotation object");
            throw new Log.KarinaException();
        }
        var entries = new HashMap<>(object.values());

        KType type;
        if (entries.containsKey("type")) {
            type= intoKType(c, entries.get("type"));
            entries.remove("type");
        } else {
            Log.temp(c, value.region(), "missing 'fieldType' field");
            throw new Log.KarinaException();
        }

        if (!entries.isEmpty()) {
            Log.temp(c, value.region(), "Unexpected fields: " + entries.keySet());
            throw new Log.KarinaException();
        }


        return new SuperAnnotation(value.region(), type);
    }

    private static KType intoKType(Context c, AnnotationValue value) {
        if (value instanceof AnnotationAST.Type type) {
            return type.type();
        }
        Log.temp(c, value.region(), "Expected a fieldType");
        throw new Log.KarinaException();
    }
}
