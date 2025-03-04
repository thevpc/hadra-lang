/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.hl.test;

import net.thevpc.hl.test.util.TestHelper;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.NSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author vpc
 */
public class TestGenerateTokenClass {
    @BeforeAll
    static void beforeAll(){
        TestHelper.openWorkspace();
    }

    @Test
    public void testGen() {
        new net.hl.compiler.core.HadraLanguage().generateTokensClass(System.out, "net.hl.compiler.core.HTokenId");
    }
}
