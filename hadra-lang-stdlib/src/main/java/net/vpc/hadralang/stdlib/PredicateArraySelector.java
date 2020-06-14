package net.vpc.hadralang.stdlib;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

public class PredicateArraySelector<T> implements Selector<T[]> {
    private T[] base;
    private IntPredicate predicate;

    public PredicateArraySelector(T[] base,IntPredicate predicate) {
        this.base = base;
        this.predicate = predicate;
    }

    @Override
    public T[] get() {
        List<T> ok = new ArrayList<>();
        for (int i = 0; i < base.length; i++) {
            if (predicate.test(i)) {
                ok.add(base[i]);
            }
        }
        return ok.toArray((T[]) Array.newInstance(base.getClass().getComponentType(), 0));
    }

    @Override
    public T[] set(T[] value) {
        int curr = 0;
        for (int i = 0; i < base.length; i++) {
            if (predicate.test(i)) {
                if (curr < value.length) {
                    base[i] = value[curr++];
                }
            }
        }
        return base;
    }
}
