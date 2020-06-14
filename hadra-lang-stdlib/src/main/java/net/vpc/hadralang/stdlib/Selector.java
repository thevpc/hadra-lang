package net.vpc.hadralang.stdlib;

public interface Selector<T> {
    T get();
    T set(T value);
}
