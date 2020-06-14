package net.vpc.hadralang.stdlib.ext;

import net.vpc.hadralang.stdlib.IntRange;

import java.util.function.IntPredicate;

public class StringBuilderExtensions {
    //region StringBuilder extension points
    public static String getAt(StringBuilder str, IntRange range) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (from >= to) {
            return "";
        }
        if (range.reversedOrder()) {
            int count = to - from;
            char[] a0 = new char[count];
            char[] a = new char[count];
            str.getChars(from,to,a0,0);
            for (int i = 0; i < count; i++) {
                a[i] = a0[to - i - 1];
            }
            return new String(a);
        }
        return str.substring(from, to);
    }


    public static StringBuilder setAt(StringBuilder str, IntPredicate predicate, CharSequence other) {
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            if (predicate.test(i)) {
                str.setCharAt(i, other.charAt(j));
                j++;
                if (j >= other.length()) {
                    break;
                }
            }
        }
        return str;
    }

    public static StringBuilder setAt(StringBuilder str, int[] indices, CharSequence other) {
        for (int i = 0; i < indices.length; i++) {
            int j = indices[i];
            if (j < 0) {
                j = str.length() - 1;
            }
            str.setCharAt(j, other.charAt(i));
        }
        return str;
    }

    public static StringBuilder setAt(StringBuilder str, int index, char c) {
        str.setCharAt(index, c);
        return str;
    }

    /**
     * this should be inlined by the HLC compiler...
     *
     * @param str   str
     * @param index index
     * @return char at position
     */
    public static char getAt(StringBuilder str, int index) {
        return str.charAt(index);
    }

    public static StringBuilder setAt(StringBuilder str, IntRange range, char[] other) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (to < 0) {
            to = str.length() - to;
        }
        str.delete(from, to);
        str.insert(from, other);
        return str;
    }

    public static StringBuilder setAt(StringBuilder str, IntPredicate predicate, char[] other) {
        int j = 0;
        for (int i = 0; i < str.length(); i++) {
            if (predicate.test(i)) {
                str.setCharAt(i, other[j]);
                j++;
                if (j >= other.length) {
                    break;
                }
            }
        }
        return str;
    }

    public static StringBuilder setAt(StringBuilder str, int[] indices, char[] other) {
        for (int i = 0; i < indices.length; i++) {
            int j = indices[i];
            if (j < 0) {
                j = str.length() - 1;
            }
            str.setCharAt(j, other[i]);
        }
        return str;
    }

    public static StringBuilder setAt(StringBuilder str, IntRange range, CharSequence other) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        str.delete(from, to);
        str.insert(from, other);
        return str;
    }

    public static StringBuilder plusAssign(StringBuilder str, CharSequence other) {
        str.append(other);
        return str;
    }

    public static StringBuilder plusAssign(StringBuilder str, int other) {
        str.append(other);
        return str;
    }

    public static StringBuilder plusAssign(StringBuilder str, char other) {
        str.append(other);
        return str;
    }

    public static StringBuilder plusAssign(StringBuilder str, char[] other) {
        str.append(other);
        return str;
    }

    public static StringBuilder plusAssign(StringBuilder str, Object other) {
        str.append(other);
        return str;
    }

    public static StringBuilder plusAssign(StringBuilder str, String other) {
        str.append(other);
        return str;
    }

    public static StringBuilder plusAssign(StringBuilder str, boolean other) {
        str.append(other);
        return str;
    }

    public static StringBuilder plusAssign(StringBuilder str, long other) {
        str.append(other);
        return str;
    }

    public static StringBuilder plusAssign(StringBuilder str, double other) {
        str.append(other);
        return str;
    }
    public static StringBuilder colonAssign(StringBuilder str, String other) {
        str.setLength(0);
        str.append(other);
        return str;
    }

    //endregion
}
