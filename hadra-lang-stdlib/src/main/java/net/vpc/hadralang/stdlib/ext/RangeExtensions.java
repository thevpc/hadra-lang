package net.vpc.hadralang.stdlib.ext;

import net.vpc.hadralang.stdlib.DoubleRange;
import net.vpc.hadralang.stdlib.IntRange;
import net.vpc.hadralang.stdlib.defaults.DefaultDoubleRange;
import net.vpc.hadralang.stdlib.defaults.DefaultIntRange;

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
