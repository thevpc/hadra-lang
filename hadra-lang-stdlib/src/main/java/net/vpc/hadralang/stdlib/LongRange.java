package net.vpc.hadralang.stdlib;

import java.util.stream.IntStream;

public interface LongRange extends ComparableRange<Long>{
    long startValue();
    long endValue();

    boolean isLowerInclusive();
    boolean isLowerExclusive();
    boolean isUpperInclusive();
    boolean isUpperExclusive();

    long lowerValueInclusive();
    long lowerValueExclusive();
    long lowerValue();
    long upperValue();
    long upperValueInclusive();
    long upperValueExclusive();
    boolean reversedOrder();

    IntStream stream();

    long size();

    long[] toLongArray();
}
