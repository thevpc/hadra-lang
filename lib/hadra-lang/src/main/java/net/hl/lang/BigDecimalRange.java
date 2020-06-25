package net.hl.lang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Stream;

public interface BigDecimalRange extends ComparableRange<BigDecimal> {
    BigDecimal lowerValueInclusive();

    BigDecimal lowerValueExclusive();

    BigDecimal lowerValue();

    BigDecimal upperValue();

    BigDecimal upperValueInclusive();

    BigDecimal upperValueExclusive();

    Stream<BigDecimal> stream();

    BigDecimal size();

    BigInteger[] toBigIntArray();
}
