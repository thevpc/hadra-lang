/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.hl.test;

import net.thevpc.hl.test.util.TestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author vpc
 */
public class TestSimple {

    @Test
    public void testCompiler0019() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0019_nulls.hl")
                        .getErrorCount());
    }
}
