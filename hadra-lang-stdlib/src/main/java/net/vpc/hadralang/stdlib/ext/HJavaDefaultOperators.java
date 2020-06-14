package net.vpc.hadralang.stdlib.ext;

public class HJavaDefaultOperators {

    //region neg
    public static int minus(byte a) {
        return -a;
    }

    public static int minus(short a) {
        return -a;
    }

    public static int minus(int a) {
        return -a;
    }

    public static long minus(long a) {
        return -a;
    }

    public static float minus(float a) {
        return -a;
    }

    public static double minus(double a) {
        return -a;
    }
    //endregion

    //region tilde
    public static int tilde(byte a) {
        return ~a;
    }

    public static int tilde(short a) {
        return ~a;
    }

    public static int tilde(int a) {
        return ~a;
    }

    public static long tilde(long a) {
        return ~a;
    }
    //endregion

    //region neg
    public static boolean or(boolean a, boolean b) {
        return a || b;
    }
    //endregion

    //region binary or
    public static int binaryOr(int a, int b) {
        return a | b;
    }

    public static long binaryOr(long a, long b) {
        return a | b;
    }

    public static int binaryAnd(int a, int b) {
        return a & b;
    }

    public static long binaryAnd(long a, long b) {
        return a & b;
    }
    //endregion

    //region and
    public static boolean and(boolean a, boolean b) {
        return a && b;
    }
    //endregion

    //region plus
    public static String plus(String a, Object b) {
        return a + b;
    }

    public static String plus(Object a, String b) {
        return a + b;
    }

    // byte ADD
    public static int plus(byte a, byte b) {
        return a + b;
    }

    public static int plus(byte a, short b) {
        return a + b;
    }

    public static int plus(byte a, int b) {
        return a + b;
    }

    public static long plus(byte a, long b) {
        return a + b;
    }

    public static float plus(byte a, float b) {
        return a + b;
    }

    public static double plus(byte a, double b) {
        return a + b;
    }

    // short ADD
    public static int plus(short a, byte b) {
        return a + b;
    }

    public static int plus(short a, short b) {
        return a + b;
    }

    public static int plus(short a, int b) {
        return a + b;
    }

    public static long plus(short a, long b) {
        return a + b;
    }

    public static float plus(short a, float b) {
        return a + b;
    }

    public static double plus(short a, double b) {
        return a + b;
    }

    // int ADD
    public static int plus(int a, byte b) {
        return a + b;
    }

    public static int plus(int a, short b) {
        return a + b;
    }

    public static int plus(int a, int b) {
        return a + b;
    }

    public static long plus(int a, long b) {
        return a + b;
    }

    public static float plus(int a, float b) {
        return a + b;
    }

    public static double plus(int a, double b) {
        return a + b;
    }

    // long ADD
    public static long plus(long a, byte b) {
        return a + b;
    }

    public static long plus(long a, short b) {
        return a + b;
    }

    public static long plus(long a, int b) {
        return a + b;
    }

    public static long plus(long a, long b) {
        return a + b;
    }

    public static float plus(long a, float b) {
        return a + b;
    }

    public static double plus(long a, double b) {
        return a + b;
    }

    // float ADD
    public static float plus(float a, byte b) {
        return a + b;
    }

    public static float plus(float a, short b) {
        return a + b;
    }

    public static float plus(float a, int b) {
        return a + b;
    }

    public static float plus(float a, long b) {
        return a + b;
    }

    public static float plus(float a, float b) {
        return a + b;
    }

    public static double plus(float a, double b) {
        return a + b;
    }

    // double ADD
    public static double plus(double a, byte b) {
        return a + b;
    }

    public static double plus(double a, short b) {
        return a + b;
    }

    public static double plus(double a, int b) {
        return a + b;
    }

    public static double plus(double a, long b) {
        return a + b;
    }

    public static double plus(double a, float b) {
        return a + b;
    }

    public static double plus(double a, double b) {
        return a + b;
    }
    //endregion

    //region rem
    public static int rem(byte a, byte b) {
        return a % b;
    }

    public static int rem(byte a, short b) {
        return a % b;
    }

    public static int rem(byte a, int b) {
        return a % b;
    }

    public static long rem(byte a, long b) {
        return a % b;
    }

    public static float rem(byte a, float b) {
        return a % b;
    }

    public static double rem(byte a, double b) {
        return a % b;
    }

    // short ADD
    public static int rem(short a, byte b) {
        return a % b;
    }

    public static int rem(short a, short b) {
        return a % b;
    }

    public static int rem(short a, int b) {
        return a % b;
    }

    public static long rem(short a, long b) {
        return a % b;
    }

    public static float rem(short a, float b) {
        return a % b;
    }

    public static double rem(short a, double b) {
        return a % b;
    }

    // int ADD
    public static int rem(int a, byte b) {
        return a % b;
    }

    public static int rem(int a, short b) {
        return a % b;
    }

    public static int rem(int a, int b) {
        return a % b;
    }

    public static long rem(int a, long b) {
        return a % b;
    }

    public static float rem(int a, float b) {
        return a % b;
    }

    public static double rem(int a, double b) {
        return a % b;
    }

    // long ADD
    public static long rem(long a, byte b) {
        return a % b;
    }

    public static long rem(long a, short b) {
        return a % b;
    }

    public static long rem(long a, int b) {
        return a % b;
    }

    public static long rem(long a, long b) {
        return a % b;
    }

    public static float rem(long a, float b) {
        return a % b;
    }

    public static double rem(long a, double b) {
        return a % b;
    }

    // float ADD
    public static float rem(float a, byte b) {
        return a % b;
    }

    public static float rem(float a, short b) {
        return a % b;
    }

    public static float rem(float a, int b) {
        return a % b;
    }

    public static float rem(float a, long b) {
        return a % b;
    }

    public static float rem(float a, float b) {
        return a % b;
    }

    public static double rem(float a, double b) {
        return a % b;
    }

    // double ADD
    public static double rem(double a, byte b) {
        return a % b;
    }

    public static double rem(double a, short b) {
        return a % b;
    }

    public static double rem(double a, int b) {
        return a % b;
    }

    public static double rem(double a, long b) {
        return a % b;
    }

    public static double rem(double a, float b) {
        return a % b;
    }

    public static double rem(double a, double b) {
        return a % b;
    }
    //endregion

    //region minus
    public static int minus(byte a, byte b) {
        return a - b;
    }

    public static int minus(byte a, short b) {
        return a - b;
    }

    public static int minus(byte a, int b) {
        return a - b;
    }

    public static long minus(byte a, long b) {
        return a - b;
    }

    public static float minus(byte a, float b) {
        return a - b;
    }

    public static double minus(byte a, double b) {
        return a - b;
    }

    // short sub
    public static int minus(short a, byte b) {
        return a - b;
    }

    public static int minus(short a, short b) {
        return a - b;
    }

    public static int minus(short a, int b) {
        return a - b;
    }

    public static long minus(short a, long b) {
        return a - b;
    }

    public static float minus(short a, float b) {
        return a - b;
    }

    public static double minus(short a, double b) {
        return a - b;
    }

    // int sub
    public static int minus(int a, byte b) {
        return a - b;
    }

    public static int minus(int a, short b) {
        return a - b;
    }

    public static int minus(int a, int b) {
        return a - b;
    }

    public static long minus(int a, long b) {
        return a - b;
    }

    public static float minus(int a, float b) {
        return a - b;
    }

    public static double minus(int a, double b) {
        return a - b;
    }

    // long sub
    public static long minus(long a, byte b) {
        return a - b;
    }

    public static long minus(long a, short b) {
        return a - b;
    }

    public static long minus(long a, int b) {
        return a - b;
    }

    public static long minus(long a, long b) {
        return a - b;
    }

    public static float minus(long a, float b) {
        return a - b;
    }

    public static double minus(long a, double b) {
        return a - b;
    }

    // float sub
    public static float minus(float a, byte b) {
        return a - b;
    }

    public static float minus(float a, short b) {
        return a - b;
    }

    public static float minus(float a, int b) {
        return a - b;
    }

    public static float minus(float a, long b) {
        return a - b;
    }

    public static float minus(float a, float b) {
        return a - b;
    }

    public static double minus(float a, double b) {
        return a - b;
    }

    // double sub
    public static double minus(double a, byte b) {
        return a - b;
    }

    public static double minus(double a, short b) {
        return a - b;
    }

    public static double minus(double a, int b) {
        return a - b;
    }

    public static double minus(double a, long b) {
        return a - b;
    }

    public static double minus(double a, float b) {
        return a - b;
    }

    public static double minus(double a, double b) {
        return a - b;
    }
    //endregion

    //region mul
    public static int mul(byte a, byte b) {
        return a * b;
    }

    public static int mul(byte a, short b) {
        return a * b;
    }

    public static int mul(byte a, int b) {
        return a * b;
    }

    public static long mul(byte a, long b) {
        return a * b;
    }

    public static float mul(byte a, float b) {
        return a * b;
    }

    public static double mul(byte a, double b) {
        return a * b;
    }

    // short mul
    public static int mul(short a, byte b) {
        return a * b;
    }

    public static int mul(short a, short b) {
        return a * b;
    }

    public static int mul(short a, int b) {
        return a * b;
    }

    public static long mul(short a, long b) {
        return a * b;
    }

    public static float mul(short a, float b) {
        return a * b;
    }

    public static double mul(short a, double b) {
        return a * b;
    }

    // int mul
    public static int mul(int a, byte b) {
        return a * b;
    }

    public static int mul(int a, short b) {
        return a * b;
    }

    public static int mul(int a, int b) {
        return a * b;
    }

    public static long mul(int a, long b) {
        return a * b;
    }

    public static float mul(int a, float b) {
        return a * b;
    }

    public static double mul(int a, double b) {
        return a * b;
    }

    // long mul
    public static long mul(long a, byte b) {
        return a * b;
    }

    public static long mul(long a, short b) {
        return a * b;
    }

    public static long mul(long a, int b) {
        return a * b;
    }

    public static long mul(long a, long b) {
        return a * b;
    }

    public static float mul(long a, float b) {
        return a * b;
    }

    public static double mul(long a, double b) {
        return a * b;
    }

    // float mul
    public static float mul(float a, byte b) {
        return a * b;
    }

    public static float mul(float a, short b) {
        return a * b;
    }

    public static float mul(float a, int b) {
        return a * b;
    }

    public static float mul(float a, long b) {
        return a * b;
    }

    public static float mul(float a, float b) {
        return a * b;
    }

    public static double mul(float a, double b) {
        return a * b;
    }

    // double mul
    public static double mul(double a, byte b) {
        return a * b;
    }

    public static double mul(double a, short b) {
        return a * b;
    }

    public static double mul(double a, int b) {
        return a * b;
    }

    public static double mul(double a, long b) {
        return a * b;
    }

    public static double mul(double a, float b) {
        return a * b;
    }

    public static double mul(double a, double b) {
        return a * b;
    }
    //endregion

    //region div
    public static int div(byte a, byte b) {
        return a / b;
    }

    public static int div(byte a, short b) {
        return a / b;
    }

    public static int div(byte a, int b) {
        return a / b;
    }

    public static long div(byte a, long b) {
        return a / b;
    }

    public static float div(byte a, float b) {
        return a / b;
    }

    public static double div(byte a, double b) {
        return a / b;
    }

    // short  div
    public static int div(short a, byte b) {
        return a / b;
    }

    public static int div(short a, short b) {
        return a / b;
    }

    public static int div(short a, int b) {
        return a / b;
    }

    public static long div(short a, long b) {
        return a / b;
    }

    public static float div(short a, float b) {
        return a / b;
    }

    public static double div(short a, double b) {
        return a / b;
    }

    // int  div
    public static int div(int a, byte b) {
        return a / b;
    }

    public static int div(int a, short b) {
        return a / b;
    }

    public static int div(int a, int b) {
        return a / b;
    }

    public static long div(int a, long b) {
        return a / b;
    }

    public static float div(int a, float b) {
        return a / b;
    }

    public static double div(int a, double b) {
        return a / b;
    }

    // long  div
    public static long div(long a, byte b) {
        return a / b;
    }

    public static long div(long a, short b) {
        return a / b;
    }

    public static long div(long a, int b) {
        return a / b;
    }

    public static long div(long a, long b) {
        return a / b;
    }

    public static float div(long a, float b) {
        return a / b;
    }

    public static double div(long a, double b) {
        return a / b;
    }

    // float  div
    public static float div(float a, byte b) {
        return a / b;
    }

    public static float div(float a, short b) {
        return a / b;
    }

    public static float div(float a, int b) {
        return a / b;
    }

    public static float div(float a, long b) {
        return a / b;
    }

    public static float div(float a, float b) {
        return a / b;
    }

    public static double div(float a, double b) {
        return a / b;
    }

    // double  div
    public static double div(double a, byte b) {
        return a / b;
    }

    public static double div(double a, short b) {
        return a / b;
    }

    public static double div(double a, int b) {
        return a / b;
    }

    public static double div(double a, long b) {
        return a / b;
    }

    public static double div(double a, float b) {
        return a / b;
    }

    public static double div(double a, double b) {
        return a / b;
    }
    //endregion

    //region compare
    public static int compare(int a, int b) {
        return Integer.compare(a, b);
    }

    public static int compare(long a, long b) {
        return Long.compare(a, b);
    }

    public static int compare(float a, float b) {
        return Float.compare(a, b);
    }

    public static int compare(double a, double b) {
        return Double.compare(a, b);
    }
    //endregion

    //region implicit convert
    public static double newPrimitiveDouble(byte a) {
        return a;
    }
    public static double newPrimitiveDouble(short a) {
        return a;
    }
    public static double newPrimitiveDouble(int a) {
        return a;
    }
    public static double newPrimitiveDouble(long a) {
        return a;
    }
    public static double newPrimitiveDouble(float a) {
        return a;
    }

    public static short newPrimitiveShort(byte a) {
        return a;
    }
    public static int newPrimitiveInt(byte a) {
        return a;
    }
    public static int newPrimitiveInt(short a) {
        return a;
    }
    public static long newPrimitiveLong(byte a) {
        return a;
    }
    public static long newPrimitiveLong(short a) {
        return a;
    }
    public static long newPrimitiveLong(int a) {
        return a;
    }

    //endregion

}
