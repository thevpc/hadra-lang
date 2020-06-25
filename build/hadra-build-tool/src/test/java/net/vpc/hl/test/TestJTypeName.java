package net.vpc.hl.test;

import net.vpc.common.jeep.JTypeName;
import net.vpc.common.jeep.core.types.DefaultTypeName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestJTypeName {
    @Test
    public void test1(){
//        System.out.println(JTypeName.of("int"));
        for (String typeName : new String[]{
                "net.hl.lang.Tuple1",
                "int<int extends long[],? extends hello & String<T super ?>>[][]"
        }) {
            JTypeName s = DefaultTypeName.of(typeName);
            System.out.println(s);
            Assertions.assertEquals(typeName,s.toString());
        }
    }

}
