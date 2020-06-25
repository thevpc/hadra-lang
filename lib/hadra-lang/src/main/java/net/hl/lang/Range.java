package net.hl.lang;

public interface Range<T> {
    boolean isStartInclusive();
    boolean isStartExclusive();
    boolean isEndInclusive();
    boolean isEndExclusive();
    T start();
    T end();
}
