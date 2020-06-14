package net.vpc.hadralang.stdlib;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

/**
 * @author vpc
 */
//@JeepImported({ElementType.TYPE})
public class HDefaults {
    private static Scanner scanner = null;

    public static String format(Object any) {
        if (any == null) {
            return "null";
        } else {
            Class<?> aClass = any.getClass();
            if (aClass.isArray()) {
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                int len = Array.getLength(any);
                for (int i = 0; i < len; i++) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(format(Array.get(any, i)));
                }
                sb.append("]");
                return sb.toString();
            } else if (Collection.class.isAssignableFrom(aClass)) {
                int index = 0;
                StringBuilder sb = new StringBuilder();
                for (Object o : ((Collection) any)) {
                    if (index > 0) {
                        sb.append(",");
                    }
                    sb.append(format(o));
                    index++;
                }
                sb.append("]");
                return sb.toString();
            } else if (Map.class.isAssignableFrom(aClass)) {
                int index = 0;
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                for (Map.Entry o : ((Map<?, ?>) any).entrySet()) {
                    if (index > 0) {
                        sb.append(",");
                    }
                    sb.append(format(o.getKey()));
                    sb.append(":");
                    sb.append(format(o.getValue()));
                    index++;
                }
                sb.append("}");
                return sb.toString();
            } else {
                return String.valueOf(any);
            }
        }
    }

    public static void println(Object any) {
        System.out.println(format(any));
    }

    public static void println(Object... any) {
        if(any!=null && any.length>0) {
            for (int i = 0; i < any.length - 1; i++) {
                System.out.print(format(any[i]));
            }
            System.out.println(format(any[any.length - 1]));
        }
    }

    public static void printf(String pattern, Object... any) {
        System.out.printf(pattern, any);
    }

    private static Scanner _s() {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        return scanner;
    }

    public static void disposeScanner(){
        if(scanner!=null) {
            scanner.close();
            scanner = null;
        }
    }

    public static String readln() {
        return _s().nextLine();
    }

    public static int readInt() {
        return _s().nextInt();
    }

    public static long readLong() {
        return _s().nextLong();
    }

    public static float readFloat() {
        return _s().nextFloat();
    }

    public static double readDouble() {
        return _s().nextDouble();
    }

    public static boolean readBoolean() {
        return _s().nextBoolean();
    }

}
