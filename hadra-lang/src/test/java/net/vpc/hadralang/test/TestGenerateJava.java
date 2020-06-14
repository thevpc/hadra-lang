package net.vpc.hadralang.test;

import net.vpc.hadralang.compiler.core.HLProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;
import net.vpc.hadralang.test.util.TestHelper;

public class TestGenerateJava {

    private static final Logger log = Logger.getLogger(TestGenerateJava.class.getName());

//    @Test
//    public void testGenerateJava0001() {
//        Assertions.assertEquals(0,
//                _compileResource("java/testGenerateJava0001.hl")
//                        .errorCount());
//    }
//    @Test
//    public void testCompilerSuccess0001() {
//        Assertions.assertEquals(0,
//                _compileResource("compiler/success/testCompilerSuccess0001.hl")
//                        .errorCount());
//    }

//    @Test
//    public void testCompilerSuccess0002() {
//        Assertions.assertEquals(0,
//                _compileResource("compiler/success/testCompilerSuccess0002.hl")
//                        .errorCount());
//    }

//    @Test
//    public void testCompilerSuccess0003() {
//        Assertions.assertEquals(0,
//                _compileResource("compiler/success/testCompilerSuccess0003.hl")
//                        .errorCount());
//    }
//
//    @Test
//    public void testCompilerSuccess0004() {
//        Assertions.assertEquals(0,
//                _compileResource("compiler/success/testCompilerSuccess0004.hl")
//                        .errorCount());
//    }

//    @Test
//    public void testCompilerSuccess0005() {
//        Assertions.assertEquals(0,
//                _compileResource("compiler/success/testCompilerSuccess0005.hl")
//                        .errorCount());
//    }

//    @Test
//    public void testCompilerSuccess0006() {
//        Assertions.assertEquals(0,
//                _compileResource("compiler/success/testCompilerSuccess0006.hl")
//                        .errorCount());
//    }

//    @Test
//    public void testCompilerSuccess0007() {
//        Assertions.assertEquals(0,
//                _compileResource("compiler/success/testCompilerSuccess0007.hl")
//                        .errorCount());
//    }

//    @Test
//    public void testCompilerSuccess0008() {
//        Assertions.assertEquals(0,
//                _compileResource("compiler/success/testCompilerSuccess0008.hl")
//                        .errorCount());
//    }
//
//    @Test
//    public void testCompilerSuccess0009() {
//        Assertions.assertEquals(0,
//                _compileResource("compiler/success/testCompilerSuccess0009.hl")
//                        .errorCount());
//    }
//
//    @Test
//    public void testCompilerSuccess0010() {
//        Assertions.assertEquals(0,
//                _compileResource("compiler/success/testCompilerSuccess0010.hl")
//                        .errorCount());
//    }
//
//    @Test
//    public void testCompilerSuccess0012() {
//        Assertions.assertEquals(0,
//                _compileResource("compiler/success/testCompilerSuccess0012.hl")
//                        .errorCount());
//    }
//
    @Test
    public void testCompilerSuccess0013() {
        Assertions.assertEquals(0,
                _compileResource("compiler/success/testCompilerSuccess0013.hl")
                        .errorCount());
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
        return TestHelper.compile2JavaResource(resourceFileName);
    }

}
