package net.hl.lang.ext;

import net.hl.lang.*;

import java.util.Arrays;
import java.util.function.IntPredicate;

public class ByteArrayExtensions {
    //region byte[] array extension points
    public static int upperBound(byte[] s) {
        return s.length - 1;
    }

    public static byte[] getAt(byte[] array, IntRange range) {

        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (from >= to) {
            return new byte[0];
        }
        if (range.reversedOrder()) {
            int count = to - from;
            byte[] a = new byte[count];
            for (int i = 0; i < count; i++) {
                a[i] = array[to - i - 1];
            }
            return a;
        } else {
            return Arrays.copyOfRange(array, from, to);
        }
    }

    public static byte[] setAt(byte[] array, IntRange range, byte[] other) {
        int from = range.lowerValueInclusive();
        int to = range.upperValueExclusive();
        if (to < 0) {
            to = array.length - to;
        }
        System.arraycopy(other, 0, array, 0,
                Math.min(to - from, other.length));
        return array;
    }

    public static byte[] setAt(byte[] array, IntPredicate predicate, byte[] other) {
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

    public static byte[] setAt(byte[] array, int[] indices, byte[] other) {
        for (int i = 0; i < indices.length; i++) {
            int j = indices[i];
            if (j < 0) {
                j = array.length - 1;
            }
            array[j] = other[i];
        }
        return array;
    }
    public static byte[] newPrimitiveByteArray(int size, IntToIntFunction filler){
        byte[] v = new byte[size];
        for (int i = 0; i < size; i++) {
            v[i]=(byte) filler.applyAsInt(i);
        }
        return v;
    }

    public static byte[][] newPrimitiveByteArray2(int size1, int size2, Int2ToIntFunction filler){
        byte[][] v = new byte[size1][size2];
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                v[i][j]=(byte) filler.applyAsInt(i,j);
            }
        }
        return v;
    }
    public static byte[][][] newPrimitiveByteArray3(int size1, int size2, int size3, Int3ToIntFunction filler){
        byte[][][] v = new byte[size1][size2][size3];
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                for (int k = 0; k < size3; k++) {
                    v[i][j][k]=(byte) filler.applyAsInt(i,j,k);
                }
            }
        }
        return v;
    }

    public static byte[][][][] newPrimitiveByteArray4(int size1, int size2, int size3, int size4, Int4ToIntFunction filler){
        byte[][][][] v = new byte[size1][size2][size3][size4];
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                for (int k = 0; k < size3; k++) {
                    for (int l = 0; l < size4; l++) {
                        v[i][j][k][l]=(byte) filler.applyAsInt(i,j,k,l);
                    }
                }
            }
        }
        return v;
    }

    public static byte[] newPrimitiveByteArray(int size,byte defaultValue){
        byte[] v = new byte[size];
        Arrays.fill(v,defaultValue);
        return v;
    }
    public static byte[][] newPrimitiveByteArray2(int size1,int size2,byte defaultValue){
        byte[][] v = new byte[size1][size2];
        for (int i = 0; i < v.length; i++) {
            v[i]=newPrimitiveByteArray(size2,defaultValue);
        }
        return v;
    }
    public static byte[][] newPrimitiveByteArray2(int size1,byte[] defaultValue){
        byte[][] v = new byte[size1][];
        Arrays.fill(v, defaultValue);
        return v;
    }
    public static byte[][][] newPrimitiveByteArray3(int size1,int size2,int size3,byte defaultValue){
        byte[][][] v = new byte[size1][size2][size3];
        for (int i = 0; i < v.length; i++) {
            v[i]=newPrimitiveByteArray2(size2,size3,defaultValue);
        }
        return v;
    }
    public static byte[][][] newPrimitiveByteArray3(int size1,int size2,byte[] defaultValue){
        byte[][][] v = new byte[size1][size2][];
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                v[i][j]=defaultValue;
            }
        }
        return v;
    }
    public static byte[][][] newPrimitiveByteArray3(int size1,byte[][] defaultValue){
        byte[][][] v = new byte[size1][][];
        Arrays.fill(v, defaultValue);
        return v;
    }
    public static byte[][][][] newPrimitiveByteArray4(int size1,int size2,int size3,int size4,byte defaultValue){
        byte[][][][] v = new byte[size1][size2][size3][size4];
        for (int i = 0; i < v.length; i++) {
            v[i]=newPrimitiveByteArray3(size2,size3,size4,defaultValue);
        }
        return v;
    }
    public static byte[][][][] newPrimitiveByteArray4(int size1,int size2,int size3,byte[] defaultValue){
        byte[][][][] v = new byte[size1][size2][size3][];
        for (int i = 0; i < v.length; i++) {
            v[i]=newPrimitiveByteArray3(size2,size3,defaultValue);
        }
        return v;
    }
    public static byte[][][][] newPrimitiveByteArray4(int size1,int size2,byte[][] defaultValue){
        byte[][][][] v = new byte[size1][size2][][];
        for (int i = 0; i < size1; i++) {
            v[i]=newPrimitiveByteArray3(size2,defaultValue);
        }
        return v;
    }
    public static byte[][][][] newPrimitiveByteArray4(int size1,byte[][][] defaultValue){
        byte[][][][] v = new byte[size1][][][];
        Arrays.fill(v, defaultValue);
        return v;
    }
    //endregion
}
