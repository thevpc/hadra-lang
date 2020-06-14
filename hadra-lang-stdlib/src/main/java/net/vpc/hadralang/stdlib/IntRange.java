package net.vpc.hadralang.stdlib;

import java.util.stream.IntStream;

public interface IntRange extends ComparableRange<Integer>{
    int startValue();
    int endValue();

    boolean isLowerInclusive();
    boolean isLowerExclusive();
    boolean isUpperInclusive();
    boolean isUpperExclusive();

    int lowerValueInclusive();
    int lowerValueExclusive();
    int lowerValue();
    int upperValue();
    int upperValueInclusive();
    int upperValueExclusive();
    boolean reversedOrder();

    IntStream stream();

    int size();

    int[] toIntArray();

    boolean contains(int value);
}
