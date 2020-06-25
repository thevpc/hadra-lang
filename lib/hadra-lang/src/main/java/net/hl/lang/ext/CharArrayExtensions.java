package net.hl.lang.ext;

import net.hl.lang.IntRange;

import java.util.Arrays;
import java.util.function.IntPredicate;

public class CharArrayExtensions {
    //region char[] array extension points

    public static int upperBound(char[] s) {
        return s.length - 1;
    }

    public static char[] plus(char[] one, char[] other) {
        char[] container = new char[one.length + other.length];
        System.arraycopy(one, 0, container, 0, one.length);
        System.arraycopy(other, 0, container, one.length, other.length);
        return container;
    }

    public static char[] plus(char[] one, char c) {
        char[] container = new char[one.length + 1];
        System.arraycopy(one, 0, container, 0, one.length);
        container[one.length] = c;
        return container;
    }

    public static char[] plus(char c, char[] one) {
        char[] container = new char[one.length + 1];
        container[0] = c;
        System.arraycopy(one, 0, container, 1, one.length);
        return container;
    }

    public static String plus(char[] one, CharSequence other) {
        StringBuilder s = new StringBuilder(one.length + other.length());
        s.append(one);
        s.append(other);
        return s.toString();
    }

    public static String plus(CharSequence other, char[] one) {
        StringBuilder s = new StringBuilder(one.length + other.length());
        s.append(other);
        s.append(one);
        return s.toString();
    }

    public static char[] getAt(char[] array, IntRange range) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (from >= to) {
            return new char[0];
        }
        if (range.reversedOrder()) {
            int count = to - from;
            char[] a = new char[count];
            for (int i = 0; i < count; i++) {
                a[i] = array[to - i - 1];
            }
            return a;
        } else {
            return Arrays.copyOfRange(array, from, to);
        }
    }

    public static char[] setAt(char[] array, IntRange range, int[] other) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (to < 0) {
            to = array.length - to;
        }
        System.arraycopy(other, 0, array, 0,
                Math.min(to - from, other.length));
        return array;
    }

    public static char[] setAt(char[] array, IntPredicate predicate, char[] other) {
        int j = 0;
        for (int i = 0; i < array.length; i++) {
            if (predicate.test(i)) {
                array[i] = other[j];
                j++;
                if (j >= other.length) {
                    break;
                }
            }
        }
        return array;
    }

    public static char[] setAt(char[] array, int[] indices, char[] other) {
        for (int i = 0; i < indices.length; i++) {
            int j = indices[i];
            if (j < 0) {
                j = array.length - 1;
            }
            array[j] = other[i];
        }
        return array;
    }

    public static char[] newPrimitiveCharArray(String string) {
        return string.toCharArray();
    }

    //endregion
}
