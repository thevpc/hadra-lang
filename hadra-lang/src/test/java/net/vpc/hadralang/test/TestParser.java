package net.vpc.hadralang.test;

import net.vpc.common.jeep.JNode;
import net.vpc.hadralang.compiler.core.HLProject;
import net.vpc.hadralang.compiler.parser.ast.HNBlock;
import net.vpc.hadralang.test.util.TestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestParser {
//    static class T{
//        int x;
//    }

    @Test
    public void testParser0001() {
//        T t=new T();
//        ++t.x;
        HLProject hlProject = TestHelper.compileOnlyText(null, "!a.b");
        JNode n = hlProject.getCompilationUnit(0).getAst();

        Assertions.assertEquals(0,
                hlProject
                        .errorCount());
    }


}
