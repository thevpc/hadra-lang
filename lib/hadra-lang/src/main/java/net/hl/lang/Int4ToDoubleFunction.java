package net.hl.lang;

@FunctionalInterface
public interface Int4ToDoubleFunction {

    /**
     * Applies this function to the given argument.
     * @param i1 i1
     * @param i2 i2
     * @param i3 i3
     * @param i4 i4
     * @return value
     */
    public double applyAsDouble(int i1,int i2,int i3,int i4);
}
