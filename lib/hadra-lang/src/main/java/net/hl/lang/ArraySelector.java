package net.hl.lang;

public interface ArraySelector<T> {
    T[] get();

    void set(T[] newValues);
}
