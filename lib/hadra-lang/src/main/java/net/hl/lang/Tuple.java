package net.hl.lang;

import java.util.List;

public interface Tuple {
    int MAX_ELEMENTS = 128;

    int size();

    /**
     *
     * zero based index.
     * <pre>
     *     Uplet2&lt;String,Integer&gt; u=new Uplet2&lt;String,Integer&gt;(String.class,Integer.class,"Hello",null);
     *     Integer i2=u._2;
     *     Integer i2=u.valueAt(1);
     * </pre>
     * @param index index
     * @return tuple value at index
     * @param <T> T
     * @param index zero based Index.
     */
    <T> T valueAt(int index);
}
