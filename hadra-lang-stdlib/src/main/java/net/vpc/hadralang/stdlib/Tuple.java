package net.vpc.hadralang.stdlib;

import java.util.List;

public interface Tuple {
    int MAX_ELEMENTS = 128;

    int size();

    /**
     * zero based index.
     * <pre>
     *     Uplet2&lt;String,Integer> u=new Uplet2&lt;String,Integer>(String.class,Integer.class,"Hello",null);
     *     Integer i2=u._2;
     *     Integer i2=u.valueAt(1);
     * </pre>
     * @param index zero based Index.
     * @return tuple value at index
     */
    <T> T valueAt(int index);
}
