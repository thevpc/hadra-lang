package net.vpc.hadralang.stdlib.ext;

import java.util.regex.Pattern;

public class PatternExtensions {
    public static boolean matches(Pattern pattern, CharSequence string){
        return pattern.matcher(string).matches();
    }
}
