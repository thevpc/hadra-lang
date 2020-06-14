package net.vpc.hadralang.stdlib;

import java.util.stream.IntStream;

public interface DoubleRange extends ComparableRange<Double>{
    double startValue();
    double endValue();

    double lowerValueInclusive();
    double lowerValueExclusive();
    double lowerValue();
    double upperValue();
    double upperValueInclusive();
    double upperValueExclusive();
    boolean reversedOrder();

//    IntStream stream();

    double size();

//    int[] toIntArray();

    boolean contains(double value);
}
