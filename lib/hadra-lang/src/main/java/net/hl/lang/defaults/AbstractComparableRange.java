package net.hl.lang.defaults;

import net.hl.lang.ComparableRange;

public abstract class AbstractComparableRange<T extends Comparable> extends AbstractRange<T> implements ComparableRange<T> {
    private boolean reversed;

    public AbstractComparableRange(boolean firstExclusive, boolean secondExclusive, boolean reversed) {
        this.startExclusive = firstExclusive;
        this.endExclusive = secondExclusive;
        this.reversed = reversed;
    }

    @Override
    public boolean isLowerInclusive() {
        return !isLowerExclusive();
    }

    @Override
    public boolean isLowerExclusive() {
        return reversedOrder() ? isEndExclusive() : isStartExclusive();
    }

    @Override
    public boolean isUpperInclusive() {
        return !isUpperExclusive();
    }

    @Override
    public boolean isUpperExclusive() {
        return reversedOrder() ? isStartExclusive() : isEndExclusive();
    }

    public boolean reversedOrder() {
        return reversed;
    }

    @Override
    public boolean isEmpty() {
        return isLowerExclusive() && isUpperExclusive()
                && start().equals(end());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(start());
        if (isStartExclusive()) {
            if (reversedOrder()) {
                sb.append(">");
            } else {
                sb.append("<");
            }
        }
        sb.append("..");
        if (isEndExclusive()) {
            if (reversedOrder()) {
                sb.append(">");
            } else {
                sb.append("<");
            }
        }
        sb.append(end());

        return sb.toString();
    }
}
