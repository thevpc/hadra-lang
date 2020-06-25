package net.hl.lang.ext;

import net.hl.lang.IntRange;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayExtensions {
    //region Reference Arrays extension points
    public static int upperBound(Object[] s) {
        return s.length - 1;
    }

    public static <T> T[] getAt(T[] array, IntRange range) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (from >= to) {
            return Arrays.copyOfRange(array, 0, 0);
        }
        if (range.reversedOrder()) {
            int count = to - from;
            T[] a = (T[]) Array.newInstance((Class<? extends T[]>) array.getClass().getComponentType(), count);
            for (int i = 0; i < count; i++) {
                a[i] = array[to - i - 1];
            }
            return a;
        } else {
            return Arrays.copyOfRange(array, from, to);
        }
    }

    public static <T> T[] setAt(T[] array, IntRange range, T[] other) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (to < 0) {
            to = array.length - to;
        }
        System.arraycopy(other, from, array, 0,
                Math.min(to - from, other.length));
        return array;
    }
    //endregion
}
