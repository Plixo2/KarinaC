package karina.lang;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//Build-in Utility class for auto importing of print, println, and toString methods
public final class Console {
//    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#,###.############");

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

    public static String toString(int number) {
        return toString(Integer.valueOf(number));
    }

    public static String toString(byte b) {
        return toString(Integer.valueOf(b));
    }

    public static String toString(short s) {
        return toString(Integer.valueOf(s));
    }

    public static String toString(long number) {
        return toString(Long.valueOf(number));
    }

    public static String toString(boolean bool) {
        return toString(Boolean.valueOf(bool));
    }

    public static String toString(char character) {
        return toString(Character.valueOf(character));
    }

    public static String toString(double number) {
        return toString(Double.valueOf(number));
    }

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



}
