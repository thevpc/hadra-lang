package net.hl.lang.defaults;

import net.hl.lang.ComparableRange;
import net.hl.lang.DoubleRange;

public class DefaultDoubleRange extends AbstractComparableRange<Double> implements DoubleRange {
    private double start;
    private double end;

    public DefaultDoubleRange(double first, double second, boolean firstExclusive, boolean secondExclusive) {
        super(firstExclusive, secondExclusive, (first > second));
        this.start = first;
        this.end = second;
    }

    @Override
    public Double start() {
        return startValue();
    }

    @Override
    public Double end() {
        return endValue();
    }

    public double startValue() {
        return start;
    }

    public double endValue() {
        return end;
    }


    @Override
    public double lowerValueInclusive() {
        return isLowerInclusive() ? lowerValue() : lowerValue() + 1;
    }

    @Override
    public double lowerValueExclusive() {
        return isLowerInclusive() ? lowerValue() - 1 : lowerValue();
    }

    @Override
    public double lowerValue() {
        return reversedOrder() ? end() : start();
    }

    @Override
    public double upperValue() {
        return reversedOrder() ? start() : end();
    }

    @Override
    public double upperValueInclusive() {
        return isUpperInclusive() ? upperValue() : upperValue() - 1;
    }

    @Override
    public double upperValueExclusive() {
        return isUpperExclusive() ? upperValue() : upperValue() + 1;
    }


    @Override
    public double size() {
        return upperValueExclusive() - lowerValueInclusive();
    }

    @Override
    public boolean contains(double value) {
        return value >= lowerValueInclusive() && value < upperValueExclusive();
    }

    public double[] times(int step) {
        throw new IllegalArgumentException("FIX ME LATER");
    }

    public double[] steps(double step) {
        if (step <= 0) {
            throw new IllegalArgumentException("Unsupported negative or null step");
        }
        if (true) {
            throw new IllegalArgumentException("FIX ME LATER");
        }
        double min = start();
        double max = end();
        if (reversedOrder()) {
            if (max < min) {
                return new double[0];
            }
            int times = (int) Math.abs((max - min) / step) + 1;
            double[] d = new double[times];
            for (int i = 0; i < d.length; i++) {
                d[i] = min + i * step;
            }
            return d;
        } else {
            if (max < min) {
                return new double[0];
            }
            int times = (int) Math.abs((max - min) / step) + 1;
            double[] d = new double[times];
            for (int i = 0; i < d.length; i++) {
                d[i] = min + i * step;
            }
            return d;
        }
    }

    @Override
    public Double lowerInclusive() {
        return lowerValueInclusive();
    }

    @Override
    public Double lowerExclusive() {
        return lowerValueExclusive();
    }

    @Override
    public Double lower() {
        return lowerValue();
    }

    @Override
    public Double upper() {
        return upperValue();
    }

    @Override
    public Double upperInclusive() {
        return upperValueInclusive();
    }

    @Override
    public Double upperExclusive() {
        return upperValueExclusive();
    }

    @Override
    public boolean contains(Double value) {
        return contains(value.doubleValue());
    }

    @Override
    public ComparableRange<Double> intersect(ComparableRange<Double> other) {
        double a1 = Math.max(lowerValueInclusive(), other.lowerInclusive());
        double a2 = Math.min(upperValueInclusive(), other.upperInclusive());
        if (a2 < a1) {
            return null;
        } else {
            return new DefaultDoubleRange(a1, a2, false, false);
        }
    }

}
