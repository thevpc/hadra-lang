package net.vpc.hadralang.stdlib.ext;

import net.vpc.hadralang.stdlib.IntRange;

import java.util.*;

public class ListExtensions {
    //region List extension points
    public static <T> T getAt(List<T> list, int index) {
        return list.get(index);
    }

    public static <T> List<T> getAt(List<T> list, IntRange range) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (from >= to) {
            return Collections.emptyList();
        }
        if (range.reversedOrder()) {
            int count = to - from;
            ArrayList<T> y = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                y.add(list.get(to - i - 1));
            }
            return y;
        } else {
            //get a copy of it!!
            return new ArrayList<>(list.subList(from, to));
        }
    }

    public static <T> List<T> setAt(List<T> list, IntRange range, T[] other) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (to < 0) {
            to = list.size() - to;
        }

        int max = Math.min(to - from, other.length);
        for (int i = 0; i < max; i++) {
            list.set(from + i, other[i]);
        }
        return list;
    }

    public static <T> List<T> setAt(List<T> list, IntRange range, List<T> other) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (to < 0) {
            to = list.size() - to;
        }

        int max = Math.min(to - from, other.size());
        for (int i = 0; i < max; i++) {
            list.set(from + i, other.get(i));
        }
        return list;
    }

    public static <T> List<T> newList() {
        return new ArrayList<>();
    }

    public static <T> List<T> newList(Collection<T> other) {
        return new ArrayList<>(other);
    }

    public static <T> List<T> newList(T[] other) {
        return new ArrayList<>(Arrays.asList(other));
    }

    public static List<Boolean> newList(boolean[] other) {
        ArrayList<Boolean> list = new ArrayList<>(other.length);
        for (boolean i : other) {
            list.add(i);
        }
        return list;
    }
    public static List<Byte> newList(byte[] other) {
        ArrayList<Byte> list = new ArrayList<>(other.length);
        for (byte i : other) {
            list.add(i);
        }
        return list;
    }
    public static List<Character> newList(char[] other) {
        ArrayList<Character> list = new ArrayList<>(other.length);
        for (char i : other) {
            list.add(i);
        }
        return list;
    }
    public static List<Short> newList(short[] other) {
        ArrayList<Short> list = new ArrayList<>(other.length);
        for (short i : other) {
            list.add(i);
        }
        return list;
    }
    public static List<Integer> newList(int[] other) {
        ArrayList<Integer> list = new ArrayList<>(other.length);
        for (int i : other) {
            list.add(i);
        }
        return list;
    }
    public static List<Long> newList(long[] other) {
        ArrayList<Long> list = new ArrayList<>(other.length);
        for (long i : other) {
            list.add(i);
        }
        return list;
    }
    public static List<Float> newList(float[] other) {
        ArrayList<Float> list = new ArrayList<>(other.length);
        for (float i : other) {
            list.add(i);
        }
        return list;
    }
    public static List<Double> newList(double[] other) {
        ArrayList<Double> list = new ArrayList<>(other.length);
        for (double i : other) {
            list.add(i);
        }
        return list;
    }
    //endregion
}
