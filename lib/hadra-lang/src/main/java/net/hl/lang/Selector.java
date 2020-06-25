package net.hl.lang;

public interface Selector<T> {
    T get();
    T set(T value);
}
