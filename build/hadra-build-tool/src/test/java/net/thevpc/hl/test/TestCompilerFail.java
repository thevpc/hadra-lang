package net.thevpc.hl.test;

import net.hl.compiler.core.HProject;
import net.thevpc.nuts.Nuts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;
import net.thevpc.hl.test.util.TestHelper;

public class TestCompilerFail {

    private static final Logger log = Logger.getLogger(TestCompilerFail.class.getName());

    @BeforeAll
    static void beforeAll(){
        TestHelper.openWorkspace();
    }

    @Test
    public void testCompiler0001() {
        //error : expected ';' at the end of a statement
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0001.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0003() {
        //error: 'expected value assignment'
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0003.hl")
                        .getErrorCount());
    }

 
    /**
     * Type infinite inference recursion
     */
    @Test
    public void testCompiler0009() {
        Assertions.assertTrue(
                _compileResource("testCompilerFail0009.hl")
                        .getErrorCount() >= 2);
    }

    

    /**
     * Test type matching
     */
    @Test
    public void testCompiler0011() {
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0011.hl")
                        .getErrorCount());
    }

   

    
    @Test
    public void testCompiler0016() {
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0016.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0017() {
        Assertions.assertEquals(3,
                _compileResource("testCompilerFail0017.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0018() {
        Assertions.assertEquals(2,
                _compileResource("testCompilerFail0018.hl")
                        .getErrorCount());
    }

  

    @Test
    public void testCompiler0040() {
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0040_hadruwaves.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0041() {
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0041_hadruwaves_mon.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0042() {
        Assertions.assertTrue(
                _compileResource("testCompilerFail0042.hl")
                        .getErrorCount() > 0);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    // HELPER METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * compile resource File Name
     *
     * @param resourceFileName resourceFileName to compile
     * @return project compilation result
     */
    private HProject _compileResource(String resourceFileName) {
        return TestHelper.compileOnlyResource("compiler/fail/" + resourceFileName);
    }

}
