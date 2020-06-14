package net.vpc.hadralang.stdlib;

public class DefaultRef<T> implements Ref<T> {
    private T value;

    public DefaultRef(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T t) {
        this.value = t;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
