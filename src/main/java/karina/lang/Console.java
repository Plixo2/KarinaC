package karina.lang;

import java.util.Arrays;

public final class Console {

    public static void println(String text) {
        System.out.println(text);
    }

    public static void println(int number) {
        System.out.println(number);
    }

    public static void println(long number) {
        System.out.println(number);
    }

    public static void println(boolean bool) {
        System.out.println(bool);
    }

    public static void println(char character) {
        System.out.println(character);
    }

    public static void println(double number) {
        System.out.println(number);
    }

    public static void println(float number) {
        System.out.println(number);
    }

    public static void println(Object object) {
        switch (object) {
            case Object[] objects -> System.out.println(Arrays.deepToString(objects));
            case int[] a -> System.out.println(Arrays.toString(a));
            case long[] a -> System.out.println(Arrays.toString(a));
            case boolean[] a -> System.out.println(Arrays.toString(a));
            case char[] a -> System.out.println(Arrays.toString(a));
            case double[] a -> System.out.println(Arrays.toString(a));
            case float[] a -> System.out.println(Arrays.toString(a));
            case byte[] a -> System.out.println(Arrays.toString(a));
            case short[] a -> System.out.println(Arrays.toString(a));
            case null -> System.out.println("null");
            default -> {
                System.out.println(object);
            }
        }
    }


}
