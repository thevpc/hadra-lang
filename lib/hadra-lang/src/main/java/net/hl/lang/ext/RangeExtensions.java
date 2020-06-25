package net.hl.lang.ext;

import net.hl.lang.DoubleRange;
import net.hl.lang.IntRange;
import net.hl.lang.defaults.DefaultDoubleRange;
import net.hl.lang.defaults.DefaultIntRange;

public class RangeExtensions {
    //region Int Range extension points
    public static IntRange newRangeEE(int from, int to) {
        return new DefaultIntRange(from, to, true, true);
    }

    public static IntRange newRangeEI(int from, int to) {
        return new DefaultIntRange(from, to, true, false);
    }

    public static IntRange newRangeIE(int from, int to) {
        return new DefaultIntRange(from, to, false, true);
    }

    public static IntRange newRangeII(int from, int to) {
        return new DefaultIntRange(from, to, false, false);
    }
    //endregion

    //region Double Range extension points
    public static DoubleRange newRangeEE(double from, double to) {
        return new DefaultDoubleRange(from, to, true, true);
    }

    public static DoubleRange newRangeEI(double from, double to) {
        return new DefaultDoubleRange(from, to, true, false);
    }

    public static DoubleRange newRangeIE(double from, double to) {
        return new DefaultDoubleRange(from, to, false, true);
    }

    public static DoubleRange newRangeII(double from, double to) {
        return new DefaultDoubleRange(from, to, false, false);
    }

    public static DoubleRange newDoubleRange(IntRange range) {
        return new DefaultDoubleRange(range.startValue(), range.endValue(), range.isStartExclusive(), range.isEndExclusive());
    }
    //endregion
}
