package net.vpc.hadralang.compiler.utils;

import net.vpc.hadralang.stdlib.ext.HHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpSwitchBuilder {
    private List<String> regexps = new ArrayList<>();
    private StringBuilder globals = new StringBuilder();

    public static void main(String[] args) {
        RegexpSwitchBuilder b = new RegexpSwitchBuilder();
        b.addPattern("(a){3}b");
        b.addPattern("(a){2}b");

        {
            Pattern gp = Pattern.compile(b.finalPatternString());
            long s1 = System.currentTimeMillis();
            for (int i = 0; i < 10000000; i++) {
                HHelpers.switchRegrex("aab", gp, 2);
            }
            long s2 = System.currentTimeMillis();
            System.out.println(s2 - s1);
        }
        {
            Pattern p1 = Pattern.compile("(a){3}b");
            Pattern p2 = Pattern.compile("(a){2}b");
            long s1 = System.currentTimeMillis();
            for (int i = 0; i < 10000000; i++) {
                if(p1.matcher("aab").matches()) {

                }else if(p2.matcher("aab").matches()){

                }
            }
            long s2 = System.currentTimeMillis();
            System.out.println(s2 - s1);
        }
    }

    public RegexpSwitchBuilder addString(String p) {
        return addPattern(Pattern.quote(p));
    }

    public RegexpSwitchBuilder addPattern(Pattern p) {
        return addPattern(p.pattern());
    }

    public RegexpSwitchBuilder addPattern(String p) {
        regexps.add(p);
        if (globals.length() > 0) {
            globals.append("|");
        }
        globals.append("(?<PATTERN").append(regexps.size()).append(">").append(p).append(")");
        return this;
    }

    public String finalPatternString() {
        return "^" + globals.toString() + "$";
    }

    public int match(String other) {
        Matcher f = Pattern.compile(finalPatternString()).matcher(other);
        if (f.find()) {
            for (int i = 0; i < regexps.size(); i++) {
                if (f.group("PATTERN" + (i + 1)) != null) {
                    return i;
                }
            }
        }
        return -1;
    }
}
