package net.vpc.hadralang.stdlib.ext;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalExtensions {

    //region BigDecimal Extension points

    // BigDecimal  div
    public static BigDecimal div(BigDecimal a, byte b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal div(BigDecimal a, short b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal div(BigDecimal a, int b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal div(BigDecimal a, long b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal div(BigDecimal a, float b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal div(BigDecimal a, double b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal div(BigDecimal a, BigInteger b) {
        return a.multiply(new BigDecimal(b));
    }

    public static BigDecimal div(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    public static BigDecimal neg(BigDecimal a) {
        return a.negate();
    }

    public static BigDecimal plus(byte a, BigDecimal b) {
        return BigDecimal.valueOf(a).add(b);
    }

    public static BigDecimal plus(short a, BigDecimal b) {
        return BigDecimal.valueOf(a).add(b);
    }

    public static BigDecimal plus(int a, BigDecimal b) {
        return BigDecimal.valueOf(a).add(b);
    }

    public static BigDecimal plus(long a, BigDecimal b) {
        return BigDecimal.valueOf(a).add(b);
    }

    public static BigDecimal plus(float a, BigInteger b) {
        return BigDecimal.valueOf(a).add(new BigDecimal(b));
    }

    public static BigDecimal plus(float a, BigDecimal b) {
        return BigDecimal.valueOf(a).add(b);
    }

    public static BigDecimal plus(double a, BigInteger b) {
        return BigDecimal.valueOf(a).add(new BigDecimal(b));
    }

    public static BigDecimal plus(double a, BigDecimal b) {
        return BigDecimal.valueOf(a).add(b);
    }

    public static BigDecimal plus(BigInteger a, BigDecimal b) {
        return new BigDecimal(a).add(b);
    }

    //    public static Complex plus(BigInteger a, Complex b) {
//        throw new IllegalArgumentException("Unsupported");
//    }
//
//    public static Expr plus(BigInteger a, Expr b) {
//        throw new IllegalArgumentException("Unsupported");
//    }
    // BigDecimal ADD
    public static BigDecimal plus(BigDecimal a, byte b) {
        return a.add(BigDecimal.valueOf(b));
    }

    public static BigDecimal plus(BigDecimal a, short b) {
        return a.add(BigDecimal.valueOf(b));
    }

    public static BigDecimal plus(BigDecimal a, int b) {
        return a.add(BigDecimal.valueOf(b));
    }

    public static BigDecimal plus(BigDecimal a, long b) {
        return a.add(BigDecimal.valueOf(b));
    }

    public static BigDecimal plus(BigDecimal a, float b) {
        return a.add(BigDecimal.valueOf(b));
    }

    public static BigDecimal plus(BigDecimal a, double b) {
        return a.add(BigDecimal.valueOf(b));
    }

    public static BigDecimal plus(BigDecimal a, BigInteger b) {
        return a.add(new BigDecimal(b));
    }

    public static BigDecimal div(BigInteger a, BigDecimal b) {
        return new BigDecimal(a).multiply(b);
    }

    // BigDecimal mul
    public static BigDecimal mul(BigDecimal a, byte b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal mul(BigDecimal a, short b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal mul(BigDecimal a, int b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal mul(BigDecimal a, long b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal mul(BigDecimal a, float b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal mul(BigDecimal a, double b) {
        return a.multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal mul(BigDecimal a, BigInteger b) {
        return a.multiply(new BigDecimal(b));
    }

    public static BigDecimal mul(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    public static BigDecimal div(byte a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }

    public static BigDecimal div(short a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }


    public static BigDecimal div(int a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }


    public static BigDecimal div(long a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }

    public static BigDecimal div(float a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }


    public static BigDecimal div(double a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }

    public static BigDecimal mul(byte a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }

    public static BigDecimal mul(short a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }


    public static BigDecimal mul(int a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }


    public static BigDecimal mul(long a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }

    public static BigDecimal mul(float a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }


    public static BigDecimal mul(double a, BigDecimal b) {
        return BigDecimal.valueOf(a).multiply(b);
    }

    public static BigDecimal mul(BigInteger a, BigDecimal b) {
        return new BigDecimal(a).multiply(b);
    }

    public static BigDecimal plus(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    public static BigDecimal minus(byte a, BigDecimal b) {
        return BigDecimal.valueOf(a).subtract(b);
    }


    public static BigDecimal minus(short a, BigDecimal b) {
        return BigDecimal.valueOf(a).subtract(b);
    }


    public static BigDecimal minus(int a, BigDecimal b) {
        return BigDecimal.valueOf(a).subtract(b);
    }


    public static BigDecimal minus(long a, BigDecimal b) {
        return BigDecimal.valueOf(a).subtract(b);
    }


    public static BigDecimal minus(float a, BigDecimal b) {
        return BigDecimal.valueOf(a).subtract(b);
    }


    public static BigDecimal minus(double a, BigDecimal b) {
        return BigDecimal.valueOf(a).subtract(b);
    }

    public static BigDecimal minus(BigInteger a, BigDecimal b) {
        return new BigDecimal(a).subtract(b);
    }

    // BigDecimal sub
    public static BigDecimal minus(BigDecimal a, byte b) {
        return a.subtract(BigDecimal.valueOf(b));
    }

    public static BigDecimal minus(BigDecimal a, short b) {
        return a.subtract(BigDecimal.valueOf(b));
    }

    public static BigDecimal minus(BigDecimal a, int b) {
        return a.subtract(BigDecimal.valueOf(b));
    }

    public static BigDecimal minus(BigDecimal a, long b) {
        return a.subtract(BigDecimal.valueOf(b));
    }

    public static BigDecimal minus(BigDecimal a, float b) {
        return a.subtract(BigDecimal.valueOf(b));
    }

    public static BigDecimal minus(BigDecimal a, double b) {
        return a.subtract(BigDecimal.valueOf(b));
    }

    public static BigDecimal minus(BigDecimal a, BigInteger b) {
        return a.subtract(new BigDecimal(b));
    }

    public static BigDecimal minus(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }

    //endregion
}
