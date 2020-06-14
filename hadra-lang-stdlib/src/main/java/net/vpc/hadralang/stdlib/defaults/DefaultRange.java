package net.vpc.hadralang.stdlib.defaults;

import net.vpc.hadralang.stdlib.Range;

public class DefaultRange<T> extends AbstractRange<T> {
    private T lower;
    private T upper;

    public DefaultRange(T lower, T upper,boolean lowerExclusive,boolean upperExclusive) {
        super(lowerExclusive, upperExclusive);
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public T start() {
        return lower;
    }

    @Override
    public T end() {
        return upper;
    }


}
