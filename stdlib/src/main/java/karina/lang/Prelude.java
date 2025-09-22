package karina.lang;



import java.util.*;

//Auto imported functions and fields
public final class Prelude {

    public static <T> Option.Some<T> some(T value) {
        return new Option.Some<>(value);
    }
    public static <T> Option.None<T> none() {
        return new Option.None<>();
    }
    public static <T, E> Result.Ok<T, E> ok(T value) {
        return new Result.Ok<>(value);
    }
    public static <T, E> Result.Err<T, E> err(E error) {
        return new Result.Err<>(error);
    }



    public static <T> Range range(Collection<T> collection) {
        return range(collection.size());
    }

    public static <T> Range range(T[] array) {
        return range(array.length);
    }

    public static Range range(int end) {
        return new Range(0, end, 1);
    }

    public static Range range(int start, int end) {
        return new Range(start, end, 1);
    }

    public static Range range(int start, int end, int step) {
        return new Range(start, end, step);
    }

    public static void println() {
        System.out.println();
    }

    public static void println(String text) {
        println((Object) text);
    }

    public static void println(int number) {
        println(toString(number));
    }

    public static void println(long number) {
        println(toString(number));
    }

    public static void println(boolean bool) {
        println(toString(bool));
    }

    public static void println(char character) {
        println(toString(character));
    }

    public static void println(double number) {
        println(toString(number));
    }

    public static void println(float number) {
        println(toString(number));
    }

    public static void println(Object object) {
        System.out.println(toString(object));
    }


    public static void print(String text) {
        print((Object) text);
    }

    public static void print(int number) {
        print(toString(number));
    }

    public static void print(long number) {
        print(toString(number));
    }

    public static void print(boolean bool) {
        print(toString(bool));
    }

    public static void print(char character) {
        print(toString(character));
    }

    public static void print(double number) {
        print(toString(number));
    }

    public static void print(float number) {
        print(toString(number));
    }

    public static void print(Object object) {
        System.out.print(toString(object));
    }


    public static String toString(String toStr) {
        return Objects.requireNonNullElse(toStr, "null");
    }

    @Extension
    public static String toString(int number) {
        return toString(Integer.valueOf(number));
    }

    @Extension
    public static String toString(byte number) {
        return toString(Integer.valueOf(number));
    }

    @Extension
    public static String toString(short number) {
        return toString(Integer.valueOf(number));
    }

    @Extension
    public static String toString(long number) {
        return toString(Long.valueOf(number));
    }

    @Extension
    public static String toString(boolean bool) {
        return toString(Boolean.valueOf(bool));
    }

    @Extension
    public static String toString(char character) {
        return toString(Character.valueOf(character));
    }

    @Extension
    public static String toString(double number) {
        return toString(Double.valueOf(number));
    }

    @Extension
    public static String toString(float number) {
        return toString(Float.valueOf(number));
    }

    public static String toString(Object object) {
        return switch (object) {
            case Object[] objects -> toDeepString(objects);
            case int[] a -> Arrays.toString(a);
            case long[] a -> Arrays.toString(a);
            case boolean[] a -> Arrays.toString(a);
            case char[] a -> Arrays.toString(a);
            case double[] a -> Arrays.toString(a);
            case float[] a -> Arrays.toString(a);
            case byte[] a -> Arrays.toString(a);
            case short[] a -> Arrays.toString(a);
            case null -> "null";
            default -> object.toString();
        };
    }

    //from Arrays.deepToString
    private static String toDeepString(Object[] objects) {
        int bufLen = 20 * objects.length;
        if (objects.length != 0 && bufLen <= 0)
            bufLen = Integer.MAX_VALUE;
        StringBuilder buf = new StringBuilder(bufLen);
        deepToString(objects, buf, new HashSet<>());
        return buf.toString();
    }

    //from Arrays.deepToString
    private static void deepToString(Object[] a, StringBuilder buf, Set<Object[]> dejaVu) {
        if (a == null) {
            buf.append("null");
            return;
        }
        int iMax = a.length - 1;
        if (iMax == -1) {
            buf.append("[]");
            return;
        }

        dejaVu.add(a);
        buf.append('[');
        for (int i = 0; ; i++) {

            Object element = a[i];
            if (element == null) {
                buf.append("null");
            } else {
                Class<?> eClass = element.getClass();

                if (eClass.isArray()) {

                    if (eClass == byte[].class)
                        buf.append(Arrays.toString((byte[]) element));
                    else if (eClass == short[].class)
                        buf.append(Arrays.toString((short[]) element));
                    else if (eClass == int[].class)
                        buf.append(Arrays.toString((int[]) element));
                    else if (eClass == long[].class)
                        buf.append(Arrays.toString((long[]) element));
                    else if (eClass == char[].class)
                        buf.append(Arrays.toString((char[]) element));
                    else if (eClass == float[].class)
                        buf.append(Arrays.toString((float[]) element));
                    else if (eClass == double[].class)
                        buf.append(Arrays.toString((double[]) element));
                    else if (eClass == boolean[].class)
                        buf.append(Arrays.toString((boolean[]) element));
                    else { // element is an array of object references
                        if (dejaVu.contains(element))
                            buf.append("[...]");
                        else
                            deepToString((Object[])element, buf, dejaVu);
                    }
                } else {  // element is non-null and not an array
                    buf.append(toString(element));
                }
            }
            if (i == iMax)
                break;
            buf.append(", ");
        }
        buf.append(']');
        dejaVu.remove(a);
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    public static void assertTrue(boolean condition, Option<String> message) {
        assertTrue(condition, message.orElse("Assertion failed"));
    }

    public static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(" Expected: " + toString(expected) + ", but was: " + toString(actual));
        }
    }

    @Extension
    public static int debug(int number) {
        debugPrint(number);
        return number;
    }

    @Extension
    public static byte debug(byte number) {
        debugPrint(number);
        return number;
    }

    @Extension
    public static short debug(short number) {
        debugPrint(number);
        return number;
    }

    @Extension
    public static long debug(long number) {
        debugPrint(number);
        return number;
    }

    @Extension
    public static boolean debug(boolean bool) {
        debugPrint(bool);
        return bool;
    }

    @Extension
    public static char debug(char character) {
        debugPrint(character);
        return character;
    }

    @Extension
    public static double debug(double number) {
        debugPrint(number);
        return number;
    }

    @Extension
    public static float debug(float number) {
        debugPrint(number);
        return number;
    }

    @Extension
    public static <T> T debug(T obj) {
        debugPrint(obj);
        return obj;
    }

    private static void debugPrint(Object obj) {
        int OFFSET = 3;
        var traces = Thread.currentThread().getStackTrace();
        if (traces.length < OFFSET) {
            println(obj);
        }
        var trace = traces[OFFSET];
        System.out.println("'" + toString(obj) + "' at " + getTrace(trace));
    }



    private static String getTrace(StackTraceElement stackTrace) {
        var sb = new StringBuilder();
        var path = stackTrace.getClassName().split("\\.");
        String errorClass;
        if (path.length == 0) {
            // should not happen
            errorClass = "";
        } else {
            errorClass = path[path.length - 1];
        }

        sb.append(errorClass).append('.').append(stackTrace.getMethodName()).append('(');

        if (stackTrace.isNativeMethod()) {
            sb.append("Native Method");
        } else if (stackTrace.getFileName() == null) {
            sb.append("Unknown Source");
        } else {
            sb.append(stackTrace.getFileName());
            if (stackTrace.getLineNumber() >= 0) {
                sb.append(':').append(stackTrace.getLineNumber());
            }
        }
        sb.append(')');

        return sb.toString();
    }



    @Extension
    public static float sqrt(float number) {
        return (float) Math.sqrt(number);
    }

    @Extension
    public static double sqrt(double number) {
        return Math.sqrt(number);
    }

    @Extension
    public static double sqrt(int number) {
        return Math.sqrt(number);
    }

    @Extension
    public static double sqrt(long number) {
        return Math.sqrt(number);
    }

    @Extension
    public static long abs(long number) {
        return Math.abs(number);
    }

    @Extension
    public static int abs(int number) {
        return Math.abs(number);
    }

    @Extension
    public static float abs(float number) {
        return Math.abs(number);
    }

    @Extension
    public static double abs(double number) {
        return Math.abs(number);
    }


    @Extension
    public static long min(long number, long other) {
        return Math.min(number, other);
    }

    @Extension
    public static int min(int number, int other) {
        return Math.min(number, other);
    }

    @Extension
    public static float min(float number, float other) {
        return Math.min(number, other);
    }

    @Extension
    public static double min(double number, double other) {
        return Math.min(number, other);
    }

    @Extension
    public static long max(long number, long other) {
        return Math.max(number, other);
    }

    @Extension
    public static int max(int number, int other) {
        return Math.max(number, other);
    }
    @Extension
    public static float max(float number, float other) {
        return Math.max(number, other);
    }
    @Extension
    public static double max(double number, double other) {
        return Math.max(number, other);
    }



}
