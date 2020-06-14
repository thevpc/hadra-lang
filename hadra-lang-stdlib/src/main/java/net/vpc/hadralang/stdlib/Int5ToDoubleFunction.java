package net.vpc.hadralang.stdlib;

@FunctionalInterface
public interface Int5ToDoubleFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    public double applyAsDouble(int i1,int i2,int i3,int i4,int i5);
}
