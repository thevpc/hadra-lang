package net.hl.lang;

import java.util.List;

public class IntRangeListSelector<T> implements Selector<List<T>> {
    private List<T> base;
    private IntRange range;

    public IntRangeListSelector(List<T> base, IntRange range) {
        this.base = base;
        this.range = range;
    }

    @Override
    public List<T> get() {
        return base.subList(range.lowerValueInclusive(), range.upperValueExclusive());
    }

    @Override
    public List<T> set(List<T> value) {
        int i0 = range.lowerValueInclusive();
        int max=range.upperValueExclusive() - i0;
        for (int i = 0; i <max ; i++) {
            base.set(i0+i,value.get(i));
        }
        return base;
    }
}
