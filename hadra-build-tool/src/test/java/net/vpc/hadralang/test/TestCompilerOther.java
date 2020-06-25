package net.vpc.hadralang.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;
import net.vpc.hadralang.test.util.TestHelper;

public class TestCompilerOther {

    private static final Logger log = Logger.getLogger(TestCompilerOther.class.getName());

    /**
     * compile text
     */
    @Test
    public void testCompiler0001() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlyText("0001", "java.lang.System.out.println('Hello');")
                        .errorCount());
    }

}
