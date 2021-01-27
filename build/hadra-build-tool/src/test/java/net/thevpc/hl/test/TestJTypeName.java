package net.thevpc.hl.test;

import net.thevpc.jeep.JTypeName;
import net.thevpc.jeep.core.types.DefaultTypeName;
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
