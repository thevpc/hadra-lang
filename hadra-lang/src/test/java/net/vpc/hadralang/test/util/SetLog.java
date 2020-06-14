package net.vpc.hadralang.test.util;

public class SetLog {
    public static void prepare(){
        System.setProperty("java.util.logging.SimpleFormatter.format","%1$tF %1$tT %4$s:%2$s(...) %5$s%6$s%n");
    }
}
