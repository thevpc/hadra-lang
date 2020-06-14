package net.vpc.hadralang.stdlib;

public class Tuple3<V1,V2,V3> extends AbstractTuple{
    public final V1 _1;
    public final V2 _2;
    public final V3 _3;

    public Tuple3(V1 _1, V2 _2, V3 _3) {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public <T> T valueAt(int index) {
        checkRange(index);
        switch (index){
            case 0: return (T) _1;
            case 1: return (T) _2;
            case 2: return (T) _3;
        }
        throw new IllegalArgumentException("Should never Happen");
    }
}
