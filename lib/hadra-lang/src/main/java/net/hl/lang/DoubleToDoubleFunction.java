package net.hl.lang;

@FunctionalInterface
public interface DoubleToDoubleFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    public double applyAsDouble(double value);
}
