package net.hl.lang;

import java.math.BigInteger;
import java.util.stream.Stream;

public interface BigIntRange extends ComparableRange<Integer> {
    BigInteger startValue();

    BigInteger endValue();

    boolean isLowerInclusive();

    boolean isLowerExclusive();

    boolean isUpperInclusive();

    boolean isUpperExclusive();

    BigInteger lowerValueInclusive();

    BigInteger lowerValueExclusive();

    BigInteger lowerValue();

    BigInteger upperValue();

    BigInteger upperValueInclusive();

    BigInteger upperValueExclusive();

    boolean reversedOrder();

    Stream<BigInteger> stream();

    BigInteger size();

    BigInteger[] toBigIntArray();
}
