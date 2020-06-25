package net.hl.lang;

import java.util.Arrays;
import java.util.function.IntPredicate;

public class DoublePredicateArraySelector implements Selector<double[]> {
    private IntPredicate predicate;
    private double[] base;

    public DoublePredicateArraySelector(double[] base,IntPredicate predicate) {
        this.predicate = predicate;
        this.base = base;
    }

    @Override
    public double[] get() {
        double[] ok = new double[base.length];
        int counter=0;
        for (int i = 0; i < base.length; i++) {
            if (predicate.test(i)) {
                ok[counter++]=base[i];
            }
        }
        return Arrays.copyOfRange(ok,0,counter);
    }

    @Override
    public double[] set(double[] selection) {
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
