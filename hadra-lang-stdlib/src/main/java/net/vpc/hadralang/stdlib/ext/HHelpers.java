package net.vpc.hadralang.stdlib.ext;

import net.vpc.hadralang.stdlib.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HHelpers {

    public static String[] resolveInstanceClassWithParentNames(Object cls) {
        if(cls==null){
            return new String[]{"null"};
        }
        return resolveClassWithParentNames(cls.getClass());
    }

    public static String[] resolveClassWithParentNames(Class cls) {
        return Arrays.stream(resolveClassWithParents(cls)).map(x->x.getName()).toArray(String[]::new);
    }

    public static Class[] resolveClassWithParents(Class cls) {
        //may be should add cache. But what about class loading???
        //i can still hope that hierarchy do not change across class loaders
        //of if classes are reloaded...
        Set<Class> visited=new LinkedHashSet<>();
        Stack<Class> stack=new Stack<>();
        stack.push(cls);
        visited.add(cls);
        while(!stack.isEmpty()){
            Class n = stack.pop();
            Class s = n.getSuperclass();
            if(s!=null){
                if(visited.add(s)){
                    stack.push(s);
                }
            }
            for (Class ii : n.getInterfaces()) {
                if(visited.add(ii)){
                    stack.push(ii);
                }
            }
        }
        return visited.toArray(new Class[0]);
    }

    public static int switchRegrex(String other, Pattern p, int count) {
        Matcher f = p.matcher(other);
        if (f.find()) {
            for (int i = 0; i < count; i++) {
                if (f.group("PATTERN" + (i + 1)) != null) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static <T> void swap(Ref<T> a, Ref<T> b) {
        T x = a.get();
        a.set(b.get());
        b.set(x);
    }

    public static <T> T nonNull(T a, Supplier<T> s) {
        return a != null ? a : s.get();
    }

    public static <T, V> V applyOrDefault(T a, Function<T, V> s) {
        return a != null ? s.apply(a) : null;
    }

    public static <T> T arrayGet(T[] a, IntToIntFunction arrayLenFunction) {
        return a[arrayLenFunction.applyAsInt(a.length)];
    }

    //region arrays of int
    public static int[] newArray(int size, int defaultValue) {
        int[] a = new int[size];
        Arrays.fill(a, defaultValue);
        return a;
    }

    public static int[][] newArray(int size1, int size2, int defaultValue) {
        int[][] a = new int[size1][size2];
        for (int i1 = 0; i1 < size1; i1++) {
            Arrays.fill(a[i1], defaultValue);
        }
        return a;
    }

    public static int[][][] newArray(int size1, int size2, int size3, int defaultValue) {
        int[][][] a = new int[size1][size2][size3];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                Arrays.fill(a[i1][i2], defaultValue);
            }
        }
        return a;
    }

    public static int[][][][] newArray(int size1, int size2, int size3, int size4, int defaultValue) {
        int[][][][] a = new int[size1][size2][size3][size4];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                for (int i3 = 0; i3 < size3; i3++) {
                    Arrays.fill(a[i1][i2][i3], defaultValue);
                }
            }
        }
        return a;
    }

    public static int[][][][][] newArray(int size1, int size2, int size3, int size4, int size5, int defaultValue) {
        int[][][][][] a = new int[size1][size2][size3][size4][size5];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                for (int i3 = 0; i3 < size3; i3++) {
                    for (int i4 = 0; i4 < size4; i4++) {
                        Arrays.fill(a[i1][i2][i3][i4], defaultValue);
                    }
                }
            }
        }
        return a;
    }

    public static int[] newArray(int size, IntToIntFunction supplier) {
        int[] a = new int[size];
        for (int i = 0; i < size; i++) {
            a[i] = supplier.applyAsInt(i);
        }
        return a;
    }

    public static int[][] newArray(int size1, int size2, Int2ToIntFunction supplier) {
        int[][] a = new int[size1][size2];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                a[i1][i2] = supplier.applyAsInt(i1, i2);
            }
        }
        return a;
    }

    public static int[][][] newArray(int size1, int size2, int size3, Int3ToIntFunction supplier) {
        int[][][] a = new int[size1][size2][size3];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                for (int i3 = 0; i3 < size3; i3++) {
                    a[i1][i2][i3] = supplier.applyAsInt(i1, i2, i3);
                }
            }
        }
        return a;
    }

    public static int[][][][] newArray(int size1, int size2, int size3, int size4, Int4ToIntFunction supplier) {
        int[][][][] a = new int[size1][size2][size3][size4];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                for (int i3 = 0; i3 < size3; i3++) {
                    for (int i4 = 0; i4 < size4; i4++) {
                        a[i1][i2][i3][i4] = supplier.applyAsInt(i1, i2, i3, i4);
                    }
                }
            }
        }
        return a;
    }

    public static int[][][][][] newArray(int size1, int size2, int size3, int size4, int size5, Int5ToIntFunction supplier) {
        int[][][][][] a = new int[size1][size2][size3][size4][size5];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                for (int i3 = 0; i3 < size3; i3++) {
                    for (int i4 = 0; i4 < size4; i4++) {
                        for (int i5 = 0; i5 < size5; i5++) {
                            a[i1][i2][i3][i4][i5] = supplier.applyAsInt(i1, i2, i3, i4, i5);
                        }
                    }
                }
            }
        }
        return a;
    }

    //endregion arrays of int
    //region arrays of double
    public static double[] newArray(int size, double defaultValue) {
        double[] a = new double[size];
        Arrays.fill(a, defaultValue);
        return a;
    }

    public static double[][] newArray(int size1, int size2, double defaultValue) {
        double[][] a = new double[size1][size2];
        for (int i1 = 0; i1 < size1; i1++) {
            Arrays.fill(a[i1], defaultValue);
        }
        return a;
    }

    public static double[][][] newArray(int size1, int size2, int size3, double defaultValue) {
        double[][][] a = new double[size1][size2][size3];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                Arrays.fill(a[i1][i2], defaultValue);
            }
        }
        return a;
    }

    public static double[][][][] newArray(int size1, int size2, int size3, int size4, double defaultValue) {
        double[][][][] a = new double[size1][size2][size3][size4];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                for (int i3 = 0; i3 < size3; i3++) {
                    Arrays.fill(a[i1][i2][i3], defaultValue);
                }
            }
        }
        return a;
    }

    public static double[][][][][] newArray(int size1, int size2, int size3, int size4, int size5, double defaultValue) {
        double[][][][][] a = new double[size1][size2][size3][size4][size5];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                for (int i3 = 0; i3 < size3; i3++) {
                    for (int i4 = 0; i4 < size4; i4++) {
                        Arrays.fill(a[i1][i2][i3][i4], defaultValue);
                    }
                }
            }
        }
        return a;
    }

    public static double[] newArray(int size, IntToDoubleFunction supplier) {
        double[] a = new double[size];
        for (int i = 0; i < size; i++) {
            a[i] = supplier.applyAsDouble(i);
        }
        return a;
    }

    public static double[][] newArray(int size1, int size2, Int2ToDoubleFunction supplier) {
        double[][] a = new double[size1][size2];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                a[i1][i2] = supplier.applyAsDouble(i1, i2);
            }
        }
        return a;
    }

    public static double[][][] newArray(int size1, int size2, int size3, Int3ToDoubleFunction supplier) {
        double[][][] a = new double[size1][size2][size3];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                for (int i3 = 0; i3 < size3; i3++) {
                    a[i1][i2][i3] = supplier.applyAsDouble(i1, i2, i3);
                }
            }
        }
        return a;
    }

    public static double[][][][] newArray(int size1, int size2, int size3, int size4, Int4ToDoubleFunction supplier) {
        double[][][][] a = new double[size1][size2][size3][size4];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                for (int i3 = 0; i3 < size3; i3++) {
                    for (int i4 = 0; i4 < size4; i4++) {
                        a[i1][i2][i3][i4] = supplier.applyAsDouble(i1, i2, i3, i4);
                    }
                }
            }
        }
        return a;
    }

    public static double[][][][][] newArray(int size1, int size2, int size3, int size4, int size5, Int5ToDoubleFunction supplier) {
        double[][][][][] a = new double[size1][size2][size3][size4][size5];
        for (int i1 = 0; i1 < size1; i1++) {
            for (int i2 = 0; i2 < size2; i2++) {
                for (int i3 = 0; i3 < size3; i3++) {
                    for (int i4 = 0; i4 < size4; i4++) {
                        for (int i5 = 0; i5 < size5; i5++) {
                            a[i1][i2][i3][i4][i5] = supplier.applyAsDouble(i1, i2, i3, i4, i5);
                        }
                    }
                }
            }
        }
        return a;
    }
    //endregion arrays of double

//    public static <C,R> R If(Branch<C,R>[] branches,Supplier<R> defaultBranch)

}
