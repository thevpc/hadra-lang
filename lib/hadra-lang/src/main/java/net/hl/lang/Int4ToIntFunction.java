package net.hl.lang;

@FunctionalInterface
public interface Int4ToIntFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param i1 arg 1
     * @return the function result
     */
    public int applyAsInt(int i1,int i2,int i3,int i4);
}
