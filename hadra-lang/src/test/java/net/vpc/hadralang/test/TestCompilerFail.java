package net.vpc.hadralang.test;

import net.vpc.hadralang.compiler.HL;
import net.vpc.hadralang.compiler.core.HLCWithOptions;
import net.vpc.hadralang.compiler.core.HLProject;
import net.vpc.hadralang.test.util.SetLog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.hadralang.test.util.TestHelper;

public class TestCompilerFail {

    private static final Logger log = Logger.getLogger(TestCompilerFail.class.getName());

    @Test
    public void testCompiler0001() {
        //error : expected ';' at the end of a statement
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0001.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0003() {
        //error: 'expected value assignment'
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0003.hl")
                        .errorCount());
    }

 
    /**
     * Type infinite inference recursion
     */
    @Test
    public void testCompiler0009() {
        Assertions.assertTrue(
                _compileResource("testCompilerFail0009.hl")
                        .errorCount() >= 2);
    }

    

    /**
     * Test type matching
     */
    @Test
    public void testCompiler0011() {
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0011.hl")
                        .errorCount());
    }

   

    
    @Test
    public void testCompiler0016() {
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0016.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0017() {
        Assertions.assertEquals(3,
                _compileResource("testCompilerFail0017.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0018() {
        Assertions.assertEquals(2,
                _compileResource("testCompilerFail0018.hl")
                        .errorCount());
    }

  

    @Test
    public void testCompiler0040() {
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0040.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0041() {
        Assertions.assertEquals(1,
                _compileResource("testCompilerFail0041.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0042() {
        Assertions.assertTrue(
                _compileResource("testCompilerFail0042.hl")
                        .errorCount() > 0);
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
    private HLProject _compileResource(String resourceFileName) {
        return TestHelper.compileOnlyResource("compiler/fail/" + resourceFileName);
    }

}
