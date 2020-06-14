package net.vpc.hadralang.stdlib;

@FunctionalInterface
public interface Int4ToIntFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    public int applyAsInt(int i1,int i2,int i3,int i4);
}
