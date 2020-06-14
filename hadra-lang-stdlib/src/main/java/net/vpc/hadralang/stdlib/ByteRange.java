package net.vpc.hadralang.stdlib;

import java.util.stream.IntStream;

public interface ByteRange extends ComparableRange<Byte>{
    byte startValue();
    byte endValue();

    boolean isLowerInclusive();
    boolean isLowerExclusive();
    boolean isUpperInclusive();
    boolean isUpperExclusive();

    byte lowerValueInclusive();
    byte lowerValueExclusive();
    byte lowerValue();
    byte upperValue();
    byte upperValueInclusive();
    byte upperValueExclusive();
    boolean reversedOrder();

    IntStream stream();

    int size();

    byte[] toByteArray();
}
