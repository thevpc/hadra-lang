package net.hl.lang.defaults;

import net.hl.lang.Range;

import java.util.Objects;

public abstract class AbstractRange<T> implements Range<T> {
    protected boolean startExclusive;
    protected boolean endExclusive;

    protected AbstractRange() {

    }
    protected AbstractRange(boolean startExclusive, boolean endExclusive) {
        this.startExclusive = startExclusive;
        this.endExclusive = endExclusive;
    }

    @Override
    public boolean isStartExclusive() {
        return startExclusive;
    }

    @Override
    public boolean isStartInclusive() {
        return !startExclusive;
    }


    @Override
    public boolean isEndExclusive() {
        return endExclusive;
    }

    @Override
    public boolean isEndInclusive() {
        return !endExclusive;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        if(isStartInclusive()){
            sb.append("<");
        }
        sb.append(start());
        sb.append("..");
        sb.append(end());
        if(isEndExclusive()){
            sb.append("<");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractRange<?> that = (AbstractRange<?>) o;
        return Objects.equals(start(),that.start()) &&
                Objects.equals(end(),that.end()) &&
                isStartInclusive() == that.isStartInclusive() &&
                isEndExclusive() == that.isEndExclusive();
    }

    @Override
    public int hashCode() {
        return Objects.hash(start(), end(), isStartInclusive(), isEndExclusive());
    }
}
