package net.hl.lang;

import java.util.Arrays;

public class DoubleRangeArraySelector implements Selector<double[]> {
    private double[] base;
    private IntRange range;

    public DoubleRangeArraySelector(double[] base,IntRange range) {
        this.base = base;
        this.range = range;
    }

    @Override
    public double[] get() {
        return Arrays.copyOfRange(base, range.lowerValueInclusive(), range.upperValueExclusive());
    }

    @Override
    public double[] set(double[] selection) {
        System.arraycopy(selection, 0, base,
                range.lowerValueInclusive(), range.upperValueExclusive() - range.lowerValueInclusive());
        return base;
    }
}
