/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.hl.test;

import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.NutsSession;
import net.thevpc.nuts.NutsWorkspace;
import org.junit.jupiter.api.Test;

/**
 *
 * @author vpc
 */
public class TestGenerateTokenClass {

    @Test
    public void testGen() {
        final NutsSession ws = Nuts.openWorkspace();
        new net.hl.compiler.core.HadraLanguage(ws).generateTokensClass(System.out, "net.hl.compiler.core.HTokenId");
    }
}
