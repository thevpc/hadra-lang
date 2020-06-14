package net.vpc.hadralang.stdlib.ext;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigIntegerExtensions {
    //region BigInteger extension points


    // BigInteger ADD
    public static BigInteger plus(BigInteger a, byte b) {
        return a.add(BigInteger.valueOf(b));
    }

    public static BigInteger plus(BigInteger a, short b) {
        return a.add(BigInteger.valueOf(b));
    }

    public static BigInteger plus(BigInteger a, int b) {
        return a.add(BigInteger.valueOf(b));
    }

    public static BigInteger plus(BigInteger a, long b) {
        return a.add(BigInteger.valueOf(b));
    }

    public static BigDecimal plus(BigInteger a, float b) {
        return new BigDecimal(a).add(BigDecimal.valueOf(b));
    }

    public static BigDecimal plus(BigInteger a, double b) {
        return new BigDecimal(a).add(BigDecimal.valueOf(b));
    }

    public static BigInteger tilde(BigInteger a) {
        // for negative BigInteger, top byte is negative
        byte[] contents = a.toByteArray();

        // prepend byte of opposite sign
        byte[] result = new byte[contents.length + 1];
        System.arraycopy(contents, 0, result, 1, contents.length);
        result[0] = (contents[0] < 0) ? 0 : (byte) -1;

        // this will be two's complement
        return new BigInteger(result);
    }

    public static BigInteger div(short a, BigInteger b) {
        return BigInteger.valueOf(a).multiply(b);
    }

    public static BigInteger div(int a, BigInteger b) {
        return BigInteger.valueOf(a).multiply(b);
    }

    public static BigInteger div(long a, BigInteger b) {
        return BigInteger.valueOf(a).multiply(b);
    }

    public static BigDecimal div(float a, BigInteger b) {
        return BigDecimal.valueOf(a).multiply(new BigDecimal(b));
    }

    public static BigDecimal div(double a, BigInteger b) {
        return BigDecimal.valueOf(a).multiply(new BigDecimal(b));
    }

    // BigInteger  div
    public static BigInteger div(BigInteger a, byte b) {
        return a.multiply(BigInteger.valueOf(b));
    }

    public static BigInteger div(BigInteger a, short b) {
        return a.multiply(BigInteger.valueOf(b));
    }

    public static BigInteger div(BigInteger a, int b) {
        return a.multiply(BigInteger.valueOf(b));
    }

    public static BigInteger div(BigInteger a, long b) {
        return a.multiply(BigInteger.valueOf(b));
    }

    public static BigDecimal div(BigInteger a, float b) {
        return new BigDecimal(a).multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal div(BigInteger a, double b) {
        return new BigDecimal(a).multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal div(BigInteger a, BigInteger b) {
        return new BigDecimal(a).multiply(new BigDecimal(b));
    }

    public static BigDecimal minus(double a, BigInteger b) {
        return BigDecimal.valueOf(a).subtract(new BigDecimal(b));
    }

    public static BigDecimal minus(float a, BigInteger b) {
        return BigDecimal.valueOf(a).subtract(new BigDecimal(b));
    }

    public static BigInteger minus(long a, BigInteger b) {
        return BigInteger.valueOf(a).subtract(b);
    }

    public static BigInteger minus(byte a, BigInteger b) {
        return BigInteger.valueOf(a).subtract(b);
    }

    public static BigInteger minus(short a, BigInteger b) {
        return BigInteger.valueOf(a).subtract(b);
    }

    public static BigInteger minus(int a, BigInteger b) {
        return BigInteger.valueOf(a).subtract(b);
    }

    // BigInteger sub
    public static BigInteger minus(BigInteger a, byte b) {
        return a.subtract(BigInteger.valueOf(b));
    }

    public static BigInteger minus(BigInteger a, short b) {
        return a.subtract(BigInteger.valueOf(b));
    }

    public static BigInteger minus(BigInteger a, int b) {
        return a.subtract(BigInteger.valueOf(b));
    }

    public static BigInteger minus(BigInteger a, long b) {
        return a.subtract(BigInteger.valueOf(b));
    }

    public static BigDecimal minus(BigInteger a, float b) {
        return new BigDecimal(a).subtract(BigDecimal.valueOf(b));
    }

    public static BigDecimal minus(BigInteger a, double b) {
        return new BigDecimal(a).subtract(BigDecimal.valueOf(b));
    }

    public static BigDecimal minus(BigInteger a, BigInteger b) {
        return new BigDecimal(a).subtract(new BigDecimal(b));
    }

    public static BigInteger div(byte a, BigInteger b) {
        return BigInteger.valueOf(a).multiply(b);
    }

    public static BigDecimal mul(BigInteger a, BigInteger b) {
        return new BigDecimal(a).multiply(new BigDecimal(b));
    }

    // BigInteger mul
    public static BigInteger mul(BigInteger a, byte b) {
        return a.multiply(BigInteger.valueOf(b));
    }

    public static BigInteger mul(BigInteger a, short b) {
        return a.multiply(BigInteger.valueOf(b));
    }

    public static BigInteger mul(BigInteger a, int b) {
        return a.multiply(BigInteger.valueOf(b));
    }

    public static BigInteger mul(BigInteger a, long b) {
        return a.multiply(BigInteger.valueOf(b));
    }

    public static BigDecimal mul(BigInteger a, float b) {
        return new BigDecimal(a).multiply(BigDecimal.valueOf(b));
    }

    public static BigDecimal mul(BigInteger a, double b) {
        return new BigDecimal(a).multiply(BigDecimal.valueOf(b));
    }

    public static BigInteger mul(short a, BigInteger b) {
        return BigInteger.valueOf(a).multiply(b);
    }

    public static BigInteger mul(byte a, BigInteger b) {
        return BigInteger.valueOf(a).multiply(b);
    }

    public static BigDecimal mul(double a, BigInteger b) {
        return BigDecimal.valueOf(a).multiply(new BigDecimal(b));
    }

    public static BigInteger mul(int a, BigInteger b) {
        return BigInteger.valueOf(a).multiply(b);
    }

    public static BigInteger mul(long a, BigInteger b) {
        return BigInteger.valueOf(a).multiply(b);
    }

    public static BigDecimal mul(float a, BigInteger b) {
        return BigDecimal.valueOf(a).multiply(new BigDecimal(b));
    }

    public static BigInteger plus(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    public static BigInteger neg(BigInteger a) {
        return a.negate();
    }


    public static BigInteger plus(byte a, BigInteger b) {
        return BigInteger.valueOf(a).add(b);
    }


    public static BigInteger plus(short a, BigInteger b) {
        return BigInteger.valueOf(a).add(b);
    }


    public static BigInteger plus(int a, BigInteger b) {
        return BigInteger.valueOf(a).add(b);
    }


    public static BigInteger plus(long a, BigInteger b) {
        return BigInteger.valueOf(a).add(b);
    }
    //endregion
}
