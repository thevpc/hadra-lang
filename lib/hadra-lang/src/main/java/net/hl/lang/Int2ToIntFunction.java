package net.hl.lang;

@FunctionalInterface
public interface Int2ToIntFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    public int applyAsInt(int i1,int i2);
}
