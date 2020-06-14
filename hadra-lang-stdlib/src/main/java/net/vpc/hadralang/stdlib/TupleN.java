package net.vpc.hadralang.stdlib;

public class TupleN extends AbstractTuple {
    private Object[] values;
    public TupleN(Object[] values) {
        this.values = values;
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public <T> T valueAt(int index) {
        return (T) values[index - 1];
    }
}
