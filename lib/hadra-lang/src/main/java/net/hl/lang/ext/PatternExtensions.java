package net.hl.lang.ext;

import net.hl.lang.IntRange;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternExtensions {
    public static boolean matches(Pattern pattern, CharSequence string){
        return pattern.matcher(string).matches();
    }

    public static IntRange rangeOf(Pattern pattern, String string) {
        Matcher matcher = pattern.matcher(string);
        if(matcher.find()){
            return RangeExtensions.newRangeIE(matcher.start(),matcher.end());
        }
        return null;
    }
    public static int indexOf(Pattern pattern, String string) {
        Matcher matcher = pattern.matcher(string);
        return matcher.find() ? matcher.start() : -1;
    }
}
