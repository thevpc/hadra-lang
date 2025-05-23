package net.thevpc.hl.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;
import net.thevpc.hl.test.util.TestHelper;

public class TestCompilerOther {

    private static final Logger log = Logger.getLogger(TestCompilerOther.class.getName());
    @BeforeAll
    static void beforeAll(){
        TestHelper.openWorkspace();
    }

    /**
     * compile text
     */
    @Test
    public void testCompiler0001() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlyText("0001", "java.lang.System.out.println('Hello');")
                        .getErrorCount());
    }

}
