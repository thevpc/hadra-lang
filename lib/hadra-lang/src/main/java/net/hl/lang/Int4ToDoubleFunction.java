package net.hl.lang;

@FunctionalInterface
public interface Int4ToDoubleFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    public double applyAsDouble(int i1,int i2,int i3,int i4);
}
