package net.vpc.hadralang.stdlib;

import java.util.stream.IntStream;

public interface ShortRange extends ComparableRange<Byte>{
    short startValue();
    short endValue();

    boolean isLowerInclusive();
    boolean isLowerExclusive();
    boolean isUpperInclusive();
    boolean isUpperExclusive();

    short lowerValueInclusive();
    short lowerValueExclusive();
    short lowerValue();
    short upperValue();
    short upperValueInclusive();
    short upperValueExclusive();
    boolean reversedOrder();

    IntStream stream();

    int size();

    short[] toShortArray();
}
