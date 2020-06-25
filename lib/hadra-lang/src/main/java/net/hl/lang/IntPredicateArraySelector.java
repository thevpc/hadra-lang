package net.hl.lang;

import java.util.Arrays;
import java.util.function.IntPredicate;

public class IntPredicateArraySelector implements Selector<int[]> {
    private int[] base;
    private IntPredicate predicate;

    public IntPredicateArraySelector(int[] base,IntPredicate predicate) {
        this.base = base;
        this.predicate = predicate;
    }

    @Override
    public int[] get() {
        int[] ok = new int[base.length];
        int counter=0;
        for (int i = 0; i < base.length; i++) {
            if (predicate.test(i)) {
                ok[counter++]=base[i];
            }
        }
        return Arrays.copyOfRange(ok,0,counter);
    }

    @Override
    public int[] set(int[] selection) {
        int curr = 0;
        for (int i = 0; i < base.length; i++) {
            if (predicate.test(i)) {
                if (curr < selection.length) {
                    base[i] = selection[curr++];
                }
            }
        }
        return base;
    }
}
