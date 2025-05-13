package net.hl.lang;

@FunctionalInterface
public interface Int3ToIntFunction {

    /**
     * Applies this function to the given argument.
     * @param i1 i1
     * @param i2 i2
     * @param i3 i3
     * @return value
     */
    public int applyAsInt(int i1,int i2,int i3);
}
