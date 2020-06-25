package net.hl.lang;

@FunctionalInterface
public interface Int5ToIntFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    public int applyAsInt(int i1,int i2,int i3,int i4,int i5);
}
