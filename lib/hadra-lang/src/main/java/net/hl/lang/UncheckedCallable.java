package net.hl.lang;

import java.util.concurrent.Callable;

public interface UncheckedCallable<V> extends Callable<V> {
    @Override
    V call();
}
