package net.hl.lang.ext;

import net.hl.lang.IntRange;

import java.util.Arrays;
import java.util.function.IntPredicate;

public class StringExtensions {
    public static String getAt(String array, IntRange range) {

        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (from >= to) {
            return "";
        }
        if (range.reversedOrder()) {
            int count = to - from;
            char[] a0 = new char[count];
            char[] a = new char[count];
            array.getChars(from, to, a0, 0);
            for (int i = 0; i < count; i++) {
                a[i] = a0[to - i - 1];
            }
            return new String(a);
        } else {
            return array.substring(from, to);
        }
    }

    public static String setAt(String array, IntRange range, CharSequence other) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        StringBuilder sb = new StringBuilder();
        sb.append(array.substring(0, from));
        sb.append(other);
        sb.append(array.substring(to));
        return sb.toString();
    }

    public static int upperBound(CharSequence s) {
        return s.length() - 1;
    }

    public static String setAt(String str, IntPredicate predicate, CharSequence other) {
        int j = 0;
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (predicate.test(i)) {
                array[i] = other.charAt(j);
                j++;
                if (j >= other.length()) {
                    break;
                }
            }
        }
        return new String(array);
    }

    public static String setAt(String str, int[] indices, CharSequence other) {
        char[] array = str.toCharArray();
        for (int i = 0; i < indices.length; i++) {
            int j = indices[i];
            if (j < 0) {
                j = array.length - 1;
            }
            array[j] = other.charAt(i);
        }
        return new String(array);
    }

    public static String setAt(String str, int index, char c) {
        char[] array = str.toCharArray();
        array[index] = c;
        return new String(array);
    }

    /**
     * this should be inlined by the HL compiler...
     *
     * @param str   str
     * @param index index
     * @return char at position
     */
    public static char getAt(String str, int index) {
        return str.charAt(index);
    }

    public static String setAt(String array, IntRange range, char[] other) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        StringBuilder sb = new StringBuilder();
        sb.append(array.substring(0, from));
        sb.append(other);
        sb.append(array.substring(to));
        return sb.toString();
    }

    public static String setAt(String str, IntPredicate predicate, char[] other) {
        int j = 0;
        char[] array = str.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (predicate.test(i)) {
                array[i] = other[j];
                j++;
                if (j >= other.length) {
                    break;
                }
            }
        }
        return new String(array);
    }

    public static String setAt(String str, int[] indices, char[] other) {
        char[] array = str.toCharArray();
        for (int i = 0; i < indices.length; i++) {
            int j = indices[i];
            if (j < 0) {
                j = array.length - 1;
            }
            array[j] = other[i];
        }
        return new String(array);
    }

    public static String mul(CharSequence str, int count) {
        switch (str.length()) {
            case 0:
                return "";
            case 1: {
                char[] c = new char[count];
                Arrays.fill(c, str.charAt(0));
                return new String(c);
            }
            default: {
                char[] chars = str.toString().toCharArray();
                StringBuilder sb = new StringBuilder(str.length() * count);
                for (int i = 0; i < count; i++) {
                    sb.append(chars);
                }
                return sb.toString();
            }
        }
    }

    public static boolean isBlank(CharSequence string) {
        return string == null || string.toString().trim().isEmpty();
    }

    public static boolean isEmpty(CharSequence string) {
        return string == null || string.toString().isEmpty();
    }
}
