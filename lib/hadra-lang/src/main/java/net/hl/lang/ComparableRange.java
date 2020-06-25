package net.hl.lang;

public interface ComparableRange<T extends Comparable> extends Range<T> {
    boolean isLowerInclusive();

    boolean isLowerExclusive();

    boolean isUpperInclusive();

    boolean isUpperExclusive();

    boolean reversedOrder();

    T lowerInclusive();

    T lowerExclusive();

    T lower();

    T upper();

    T upperInclusive();

    T upperExclusive();

    boolean contains(T t);

    ComparableRange<T> intersect(ComparableRange<T> other);

    boolean isEmpty();
}
