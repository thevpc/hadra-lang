package net.thevpc.hl.test;

import net.thevpc.jeep.JNode;
import net.hl.compiler.core.HProject;
import net.thevpc.hl.test.util.TestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestParser {
    @BeforeAll
    static void beforeAll(){
        TestHelper.openWorkspace();
    }
//    static class T{
//        int x;
//    }

    @Test
    public void testParser0001() {
//        T t=new T();
//        ++t.x;
        HProject hlProject = TestHelper.parseOnlyText(null, "!a.b;");
        JNode n = hlProject.getCompilationUnit(0).getAst();

        Assertions.assertEquals(0,
                hlProject
                        .getErrorCount());
    }


}
