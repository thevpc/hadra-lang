package net.vpc.hadralang.stdlib;

public interface ArraySelector<T> {
    T[] get();

    void set(T[] newValues);
}
