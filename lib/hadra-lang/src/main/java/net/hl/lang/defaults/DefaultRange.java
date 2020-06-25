package net.hl.lang.defaults;

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
