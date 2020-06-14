package net.vpc.hadralang.stdlib;

public class Tuple2<V1,V2> extends AbstractTuple{
    public final V1 _1;
    public final V2 _2;

    public Tuple2(V1 _1,V2 _2) {
        this._1 = _1;
        this._2 = _2;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public <T> T valueAt(int index) {
        checkRange(index);
        switch (index){
            case 0: return (T) _1;
            case 1: return (T) _2;
        }
        throw new IllegalArgumentException("Should never Happen");
    }
}
