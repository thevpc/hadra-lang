package net.vpc.hadralang.stdlib;

import java.util.stream.IntStream;

public interface CharRange extends ComparableRange<Character>{
    char startValue();
    char endValue();

    boolean isLowerInclusive();
    boolean isLowerExclusive();
    boolean isUpperInclusive();
    boolean isUpperExclusive();

    char lowerValueInclusive();
    char lowerValueExclusive();
    char lowerValue();
    char upperValue();
    char upperValueInclusive();
    char upperValueExclusive();
    boolean reversedOrder();

    IntStream stream();

    int size();

    char[] toCharArray();
}
