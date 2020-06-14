package net.vpc.hadralang.stdlib;

public interface Ref<T> {
    static IntRef of(int value) {
        return new IntRef(value);
    }

    static <T> Ref<T> of(T value) {
        return new DefaultRef<>(value);
    }

    T get();

    void set(T t);

}
