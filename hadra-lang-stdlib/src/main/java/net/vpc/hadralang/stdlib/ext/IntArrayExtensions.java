package net.vpc.hadralang.stdlib.ext;

import net.vpc.hadralang.stdlib.*;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public class IntArrayExtensions {
    //region int[] array extension points
    public static int upperBound(int[] s) {
        return s.length - 1;
    }

    public static int[] getAt(int[] array, IntRange range) {

        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (from >= to) {
            return new int[0];
        }
        if (range.reversedOrder()) {
            int count = to - from;
            int[] a = new int[count];
            for (int i = 0; i < count; i++) {
                a[i] = array[to - i - 1];
            }
            return a;
        } else {
            return Arrays.copyOfRange(array, from, to);
        }
    }

    public static int[] setAt(int[] array, IntRange range, int[] other) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (to < 0) {
            to = array.length - to;
        }
        System.arraycopy(other, 0, array, 0,
                Math.min(to - from, other.length));
        return array;
    }

    public static int[] setAt(int[] array, IntPredicate predicate, int[] other) {
        int j = 0;
        for (int i = 0; i < array.length; i++) {
            if (predicate.test(i)) {
                array[i] = other[j];
                j++;
                if (j >= other.length) {
                    break;
                }
            }
        }
        return array;
    }

    public static int[] setAt(int[] array, int[] indices, int[] other) {
        for (int i = 0; i < indices.length; i++) {
            int j = indices[i];
            if (j < 0) {
                j = array.length - 1;
            }
            array[j] = other[i];
        }
        return array;
    }
    public static int[] newPrimitiveIntArray(int size, IntToIntFunction filler){
        int[] v = new int[size];
        for (int i = 0; i < size; i++) {
            v[i]=filler.applyAsInt(i);
        }
        return v;
    }

    public static int[][] newPrimitiveIntArray2(int size1, int size2, Int2ToIntFunction filler){
        int[][] v = new int[size1][size2];
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                v[i][j]=filler.applyAsInt(i,j);
            }
        }
        return v;
    }
    public static int[][][] newPrimitiveIntArray3(int size1, int size2, int size3, Int3ToIntFunction filler){
        int[][][] v = new int[size1][size2][size3];
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                for (int k = 0; k < size3; k++) {
                    v[i][j][k]=filler.applyAsInt(i,j,k);
                }
            }
        }
        return v;
    }

    public static int[][][][] newPrimitiveIntArray4(int size1, int size2, int size3, int size4, Int4ToIntFunction filler){
        int[][][][] v = new int[size1][size2][size3][size4];
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                for (int k = 0; k < size3; k++) {
                    for (int l = 0; l < size4; l++) {
                        v[i][j][k][l]=filler.applyAsInt(i,j,k,l);
                    }
                }
            }
        }
        return v;
    }

    public static int[] newPrimitiveIntArray(int size,int defaultValue){
        int[] v = new int[size];
        Arrays.fill(v,defaultValue);
        return v;
    }
    public static int[][] newPrimitiveIntArray2(int size1,int size2,int defaultValue){
        int[][] v = new int[size1][size2];
        for (int i = 0; i < v.length; i++) {
            v[i]=newPrimitiveIntArray(size2,defaultValue);
        }
        return v;
    }
    public static int[][] newPrimitiveIntArray2(int size1,int[] defaultValue){
        int[][] v = new int[size1][];
        Arrays.fill(v, defaultValue);
        return v;
    }
    public static int[][][] newPrimitiveIntArray3(int size1,int size2,int size3,int defaultValue){
        int[][][] v = new int[size1][size2][size3];
        for (int i = 0; i < v.length; i++) {
            v[i]=newPrimitiveIntArray2(size2,size3,defaultValue);
        }
        return v;
    }
    public static int[][][] newPrimitiveIntArray3(int size1,int size2,int[] defaultValue){
        int[][][] v = new int[size1][size2][];
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                v[i][j]=defaultValue;
            }
        }
        return v;
    }
    public static int[][][] newPrimitiveIntArray3(int size1,int[][] defaultValue){
        int[][][] v = new int[size1][][];
        Arrays.fill(v, defaultValue);
        return v;
    }
    public static int[][][][] newPrimitiveIntArray4(int size1,int size2,int size3,int size4,int defaultValue){
        int[][][][] v = new int[size1][size2][size3][size4];
        for (int i = 0; i < v.length; i++) {
            v[i]=newPrimitiveIntArray3(size2,size3,size4,defaultValue);
        }
        return v;
    }
    public static int[][][][] newPrimitiveIntArray4(int size1,int size2,int size3,int[] defaultValue){
        int[][][][] v = new int[size1][size2][size3][];
        for (int i = 0; i < v.length; i++) {
            v[i]=newPrimitiveIntArray3(size2,size3,defaultValue);
        }
        return v;
    }
    public static int[][][][] newPrimitiveIntArray4(int size1,int size2,int[][] defaultValue){
        int[][][][] v = new int[size1][size2][][];
        for (int i = 0; i < size1; i++) {
            v[i]=newPrimitiveIntArray3(size2,defaultValue);
        }
        return v;
    }
    public static int[][][][] newPrimitiveIntArray4(int size1,int[][][] defaultValue){
        int[][][][] v = new int[size1][][][];
        Arrays.fill(v, defaultValue);
        return v;
    }
    //endregion
}
