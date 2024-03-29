package net.hl.lang;

@FunctionalInterface
public interface IntToIntFunction {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    public int applyAsInt(int value);
}
