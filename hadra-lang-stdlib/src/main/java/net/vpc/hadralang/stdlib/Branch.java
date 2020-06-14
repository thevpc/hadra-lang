package net.vpc.hadralang.stdlib;

import java.util.function.Supplier;

public interface Branch<C,R> {
    Supplier<C> condition();
    Supplier<R> result();
}
