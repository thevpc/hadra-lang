package net.hl.lang;

import java.util.Arrays;

public class IntRangeArraySelector implements Selector<int[]> {
    private IntRange range;
    private int[] base;

    public IntRangeArraySelector(int[] base,IntRange range) {
        this.range = range;
        this.base = base;
    }

    @Override
    public int[] get() {
        return Arrays.copyOfRange(base, range.lowerValueInclusive(), range.upperValueExclusive());
    }

    @Override
    public int[] set(int[] selection) {
        System.arraycopy(selection, 0, base,
                range.lowerValueInclusive(), range.upperValueExclusive() - range.lowerValueInclusive());
        return base;
    }
}
