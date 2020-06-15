package net.vpc.hadralang.stdlib.ext;

import net.vpc.hadralang.stdlib.IntRange;
import net.vpc.hadralang.stdlib.Tuple2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

public class CharSequenceExtensions {
    //region CharSequence extension Points
    public static String plus(CharSequence a, Object b) {
        return String.valueOf(a) + b;
    }

    public static String plus(Object a, CharSequence b) {
        return a + String.valueOf(b);
    }
    //endregion


    public static boolean newPrimitiveBoolean(CharSequence string) {
        return Boolean.parseBoolean(string.toString());
    }

    public static char newPrimitiveChar(CharSequence string) {
        if (string.length() == 1) {
            return string.charAt(0);
        }
        throw new ClassCastException("cannot cast string to char");
    }

    public static int newPrimitiveInt(CharSequence string) {
        return Integer.parseInt(string.toString());
    }

    public static long newPrimitiveLong(CharSequence string) {
        return Long.parseLong(string.toString());
    }

    public static byte newPrimitiveByte(CharSequence string) {
        return Byte.parseByte(string.toString());
    }

    public static short newPrimitiveShort(CharSequence string) {
        return Short.parseShort(string.toString());
    }

    public static float newPrimitiveFloat(CharSequence string) {
        return Float.parseFloat(string.toString());
    }

    public static double newPrimitiveDouble(CharSequence string) {
        return Double.parseDouble(string.toString());
    }

    public static Tuple2<String,String> splitFirst(CharSequence string,char c) {
        String s = string.toString();
        int t = s.indexOf(c);
        if(t<0){
            return null;
        }
        return new Tuple2<>(s.substring(0,t),s.substring(t+1));
    }
    public static Tuple2<String,String> splitFirst(CharSequence string,String pattern) {
        String s = string.toString();
        IntRange t = PatternExtensions.rangeOf(Pattern.compile(pattern),string.toString());
        if(t==null){
            return null;
        }
        return new Tuple2<>(s.substring(0,t.start()),s.substring(t.end()));
    }

    public static Tuple2<String,String> splitLast(CharSequence string,char c) {
        String s = string.toString();
        int t = s.lastIndexOf(c);
        if(t<0){
            return null;
        }
        return new Tuple2<>(s.substring(0,t),s.substring(t+1));
    }

    public static String trim(CharSequence string) {
        return string==null?"":string.toString().trim();
    }

    public static String[] trim(String[] strings) {
        return trim((CharSequence[])strings);
    }
    public static String[] trim(CharSequence[] strings) {
        String[] newStrings=new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            newStrings[i]=trim(strings[i]);
        }
        return newStrings;
    }

    public static Boolean newBoolean(CharSequence string) {
        if(StringExtensions.isBlank(string)){
            return null;
        }
        return Boolean.parseBoolean(string.toString());
    }

    public static Character newCharacter(CharSequence string) {
        if(StringExtensions.isBlank(string)){
            return null;
        }
        if (string.length() == 1) {
            return string.charAt(0);
        }
        throw new ClassCastException("cannot cast string to char");
    }

    public static Integer newInteger(CharSequence string) {
        if(StringExtensions.isBlank(string)){
            return null;
        }
        return Integer.parseInt(string.toString());
    }

    public static Long newLong(CharSequence string) {
        if(StringExtensions.isBlank(string)){
            return null;
        }
        return Long.parseLong(string.toString());
    }

    public static Byte newByte(CharSequence string) {
        if(StringExtensions.isBlank(string)){
            return null;
        }
        return Byte.parseByte(string.toString());
    }

    public static Short newShort(CharSequence string) {
        if(StringExtensions.isBlank(string)){
            return null;
        }
        return Short.parseShort(string.toString());
    }

    public static Float newFloat(CharSequence string) {
        if(StringExtensions.isBlank(string)){
            return null;
        }
        return Float.parseFloat(string.toString());
    }

    public static Double newDouble(CharSequence string) {
        if(StringExtensions.isBlank(string)){
            return null;
        }
        return Double.parseDouble(string.toString());
    }

    public static BigInteger newBigInteger(CharSequence string) {
        if(StringExtensions.isBlank(string)){
            return null;
        }
        return new BigInteger(string.toString());
    }

    public static BigDecimal newBigDecimal(CharSequence string) {
        if(StringExtensions.isBlank(string)){
            return null;
        }
        return new BigDecimal(string.toString());
    }
}
