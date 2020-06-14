package net.vpc.hadralang.stdlib;

public class Tuple0 extends AbstractTuple{
    public static final Tuple0 INSTANCE=new Tuple0();
    public Tuple0() {
    }
    @Override
    public int size() {
        return 0;
    }
    @Override
    public <T> T valueAt(int index) {
        checkRange(index);
        return null;
    }
}
