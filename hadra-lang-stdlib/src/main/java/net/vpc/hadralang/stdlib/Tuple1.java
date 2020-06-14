package net.vpc.hadralang.stdlib;

public class Tuple1<V1> extends AbstractTuple{
    public final V1 _1;

    public Tuple1(V1 _1) {
        this._1 = _1;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public <T> T valueAt(int index) {
        checkRange(index);
        return (T) _1;
    }
}
