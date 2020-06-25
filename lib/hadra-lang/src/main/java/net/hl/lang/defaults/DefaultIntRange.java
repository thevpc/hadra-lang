package net.hl.lang.defaults;

import net.hl.lang.ComparableRange;
import net.hl.lang.IntRange;

import java.util.function.IntSupplier;
import java.util.stream.IntStream;

public class DefaultIntRange extends AbstractComparableRange<Integer> implements IntRange {
    private int start;
    private int end;

    public DefaultIntRange(int first, int second, boolean firstExclusive, boolean secondExclusive) {
        super(firstExclusive, secondExclusive, (first > second));
        this.start = first;
        this.end = second;
    }

    @Override
    public Integer start() {
        return startValue();
    }

    @Override
    public Integer end() {
        return endValue();
    }

    public int startValue() {
        return start;
    }

    public int endValue() {
        return end;
    }


    @Override
    public int lowerValueInclusive() {
        return isLowerInclusive() ? lowerValue() : lowerValue() + 1;
    }

    @Override
    public int lowerValueExclusive() {
        return isLowerInclusive() ? lowerValue() - 1 : lowerValue();
    }

    @Override
    public int lowerValue() {
        return reversedOrder() ? end() : start();
    }

    @Override
    public int upperValue() {
        return reversedOrder() ? start() : end();
    }

    @Override
    public int upperValueInclusive() {
        return isUpperInclusive() ? upperValue() : upperValue() - 1;
    }

    @Override
    public int upperValueExclusive() {
        return isUpperExclusive() ? upperValue() : upperValue() + 1;
    }


    @Override
    public IntStream stream() {
        if (reversedOrder()) {
            return IntStream.generate(new IntSupplier() {
                int i = 0;

                @Override
                public int getAsInt() {
                    i++;
                    return upperValueExclusive() - i;
                }
            }).limit(size());
        } else {
            return IntStream.range(lowerValueInclusive(), upperValueExclusive());
        }
    }

    @Override
    public int size() {
        return upperValueExclusive() - lowerValueInclusive();
    }

    @Override
    public int[] toIntArray() {
        int[] a = new int[size()];
        if (reversedOrder()) {
            int i0 = upperValueInclusive();
            for (int i = 0; i < a.length; i++) {
                a[i] = i0 - i;
            }
        } else {
            int i0 = lowerValueInclusive();
            for (int i = 0; i < a.length; i++) {
                a[i] = i0 + i;
            }
        }
        return a;
    }

    @Override
    public boolean contains(int value) {
        return value >= lowerValueInclusive() && value < upperValueExclusive();
    }



    @Override
    public Integer lowerInclusive() {
        return lowerValueInclusive();
    }

    @Override
    public Integer lowerExclusive() {
        return lowerValueExclusive();
    }

    @Override
    public Integer lower() {
        return lowerValue();
    }

    @Override
    public Integer upper() {
        return upperValue();
    }

    @Override
    public Integer upperInclusive() {
        return upperValueInclusive();
    }

    @Override
    public Integer upperExclusive() {
        return upperValueExclusive();
    }

    @Override
    public boolean contains(Integer integer) {
        return contains(integer.intValue());
    }

    @Override
    public ComparableRange<Integer> intersect(ComparableRange<Integer> other) {
        int a1 = Math.max(lowerValueInclusive(), other.lowerInclusive());
        int a2 = Math.min(upperValueInclusive(), other.upperInclusive());
        if (a2 < a1) {
            return null;
        } else {
            return new DefaultIntRange(a1, a2, false, false);
        }
    }


}
