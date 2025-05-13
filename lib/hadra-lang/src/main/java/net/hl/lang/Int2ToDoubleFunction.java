package net.hl.lang;

@FunctionalInterface
public interface Int2ToDoubleFunction {

    /**
     * Applies this function to the given argument.
     * @param i1 i1
     * @param i2 i2
     * @return value
     */
    public double applyAsDouble(int i1,int i2);
}
