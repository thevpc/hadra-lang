package net.vpc.hadralang.test;

import net.vpc.hadralang.stdlib.HDefaults;
import net.vpc.hadralang.stdlib.defaults.DefaultIntRange;
import net.vpc.hadralang.stdlib.ext.RangeExtensions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static net.vpc.hadralang.stdlib.HDefaults.println;

public class IntRangeTest {

    @Test
    public void testDirectOrder(){
        DefaultIntRange x = new DefaultIntRange(1, 3, false, false);
        System.out.println(x);
        Assertions.assertEquals(x.lowerValueInclusive(),1);
        Assertions.assertEquals(x.lowerValueExclusive(),0);
        Assertions.assertEquals(x.upperValueInclusive(),3);
        Assertions.assertEquals(x.upperValueExclusive(),4);

        x = new DefaultIntRange(1, 3, true, true);
        System.out.println(x);
        Assertions.assertEquals(x.lowerValueInclusive(),2);
        Assertions.assertEquals(x.lowerValueExclusive(),1);
        Assertions.assertEquals(x.upperValueInclusive(),2);
        Assertions.assertEquals(x.upperValueExclusive(),3);

        x = new DefaultIntRange(1, 3, true, false);
        System.out.println(x);
        Assertions.assertEquals(x.lowerValueInclusive(),2);
        Assertions.assertEquals(x.lowerValueExclusive(),1);
        Assertions.assertEquals(x.upperValueInclusive(),3);
        Assertions.assertEquals(x.upperValueExclusive(),4);

        x = new DefaultIntRange(1, 3, false, true);
        System.out.println(x);
        Assertions.assertEquals(x.lowerValueInclusive(),1);
        Assertions.assertEquals(x.lowerValueExclusive(),0);
        Assertions.assertEquals(x.upperValueInclusive(),2);
        Assertions.assertEquals(x.upperValueExclusive(),3);
    }
    @Test
    public void testReverseOrder(){
        DefaultIntRange x = new DefaultIntRange(3, 1, false, false);
        System.out.println(x);
        Assertions.assertEquals(x.lowerValueInclusive(),1);
        Assertions.assertEquals(x.lowerValueExclusive(),0);
        Assertions.assertEquals(x.upperValueInclusive(),3);
        Assertions.assertEquals(x.upperValueExclusive(),4);

        x = new DefaultIntRange(3, 1, true, true);
        System.out.println(x);
        Assertions.assertEquals(x.lowerValueInclusive(),2);
        Assertions.assertEquals(x.lowerValueExclusive(),1);
        Assertions.assertEquals(x.upperValueInclusive(),2);
        Assertions.assertEquals(x.upperValueExclusive(),3);

        x = new DefaultIntRange(3, 1, false, true);
        System.out.println(x);
        Assertions.assertEquals(x.lowerValueInclusive(),2);
        Assertions.assertEquals(x.lowerValueExclusive(),1);
        Assertions.assertEquals(x.upperValueInclusive(),3);
        Assertions.assertEquals(x.upperValueExclusive(),4);

        x = new DefaultIntRange(3, 1, true, false);
        System.out.println(x);
        Assertions.assertEquals(x.lowerValueInclusive(),1);
        Assertions.assertEquals(x.lowerValueExclusive(),0);
        Assertions.assertEquals(x.upperValueInclusive(),2);
        Assertions.assertEquals(x.upperValueExclusive(),3);
    }

    @Test
    public void testStream(){
        int[] ints = RangeExtensions.newRangeEE(10, 6).stream().toArray();
        Assertions.assertArrayEquals(new int[]{9,8,7},ints);
        ints = RangeExtensions.newRangeEI(6,10).stream().toArray();
        Assertions.assertArrayEquals(new int[]{7,8,9,10},ints);

        ints = RangeExtensions.newRangeEE(10, 6).toIntArray();
        Assertions.assertArrayEquals(new int[]{9,8,7},ints);
        ints = RangeExtensions.newRangeEI(6,10).toIntArray();
        Assertions.assertArrayEquals(new int[]{7,8,9,10},ints);
    }
}
