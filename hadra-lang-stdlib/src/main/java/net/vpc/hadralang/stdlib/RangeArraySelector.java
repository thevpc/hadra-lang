package net.vpc.hadralang.stdlib;

import java.util.Arrays;

public class RangeArraySelector<T> implements Selector<T[]> {
    private T[] base;
    private IntRange range;

    public RangeArraySelector(T[] base,IntRange range) {
        this.base = base;
        this.range = range;
    }

    @Override
    public T[] get() {
        return Arrays.copyOfRange(base, range.lowerValueInclusive(), range.upperValueExclusive());
    }

    @Override
    public T[] set(T[] value) {
        System.arraycopy(value, 0, base,
                range.lowerValueInclusive(), range.upperValueExclusive() - range.lowerValueInclusive());
        return base;
    }
}
