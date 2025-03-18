package org.karina.lang.compiler.objects.annotations;

import org.karina.lang.compiler.logging.Log;
import org.karina.lang.compiler.utils.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * very primitive
 */
public class AnnotationConversion {

    public static <T> Object convert(AnnotationValue value, Class<T> cls) {
        if (cls == boolean.class || cls == Boolean.class) {
            return asBoolean(value);
        } else if (cls == int.class || cls == Integer.class) {
            return asInt(value);
        } else if (cls == long.class || cls == Long.class) {
            return (long) asInt(value);
        } else if (cls == String.class) {
            return asString(value);
        } else if (cls == Region.class) {
            return value.region();
        } else if (cls.isAssignableFrom(List.class)) {
            return asList(value);
        } else if (cls.isAssignableFrom(Map.class)) {
            //TODO
        }

        if (cls == Object.class) {
            //TODO
        }


        Log.temp(value.region(), "Unsupported conversion to " + cls.getSimpleName());
        throw new Log.KarinaException();


    }



    private static List<?> asList(AnnotationValue value) {
        switch (value) {
            case AnnotationArray list -> {
                return list.values().stream().map(ref -> convert(ref, Object.class)).toList();
            }
            case AnnotationObject obj -> {
               List<Map.Entry<String, ?>> entries = new ArrayList<>();
                for (var entry : obj.values().entrySet()) {
                     entries.add(Map.entry(entry.getKey(), convert(entry.getValue(), Object.class)));
                }
                return entries;
            }
            default -> {
                Log.temp(value.region(), "Expected list value");
                throw new Log.KarinaException();
            }
        }
    }

    private static int asInt(AnnotationValue value) {
        switch (value) {
            case AnnotationNumber number -> {
                return number.value().intValue();
            }
            case AnnotationString string -> {
                try {
                    return Integer.parseInt(string.value());
                } catch (NumberFormatException e) {
                    Log.temp(string.region(), "Expected integer value");
                    throw new Log.KarinaException();
                }
            }
            case AnnotationBool bool -> {
                return bool.value() ? 1 : 0;
            }
            default -> {
                Log.temp(value.region(), "Expected integer value");
                throw new Log.KarinaException();
            }
        }
    }

    private static String asString(AnnotationValue value) {
        switch (value) {
            case AnnotationString string -> {
                return string.value();
            }
            case AnnotationBool bool -> {
                return Boolean.toString(bool.value());
            }
            case AnnotationNumber number -> {
                return number.value().toString();
            }
            default -> {
                Log.temp(value.region(), "Expected string value");
                throw new Log.KarinaException();
            }
        }
    }

    private static boolean asBoolean(AnnotationValue value) {
        switch (value) {
            case AnnotationBool bool -> {
                return bool.value();
            }
            case AnnotationNumber number -> {
                return number.value().doubleValue() != 0;
            }
            case AnnotationString string -> {
                return Boolean.parseBoolean(string.value());
            }
            default -> {
                Log.temp(value.region(), "Expected boolean value");
                throw new Log.KarinaException();
            }
        }
    }
}
