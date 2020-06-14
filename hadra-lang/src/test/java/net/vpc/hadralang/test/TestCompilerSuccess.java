package net.vpc.hadralang.test;

import net.vpc.hadralang.compiler.core.HLProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.vpc.hadralang.test.util.TestHelper;

public class TestCompilerSuccess {

    @Test
    public void testCompiler0001() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0001.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0002() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0002.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0003() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0003.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0004() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0004.hl")
                        .errorCount());
    }

    /**
     * def int m(int x)-> x+1;
     */
    @Test
    public void testCompiler0005() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0005.hl")
                        .errorCount());

    }

    /**
     * Type inference in valid declaration order :
     * <pre>
     * def m(int x)-> x+1;
     * var h=m(3);
     * </pre>
     */
    @Test
    public void testCompiler0006() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0006.hl")
                        .errorCount());

    }

    /**
     * Type inference in Forward declarations
     */
    @Test
    public void testCompiler0007() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0007.hl")
                        .errorCount());

    }

    /**
     * Type inference in Forward declarations
     */
    @Test
    public void testCompiler0008() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0008.hl")
                        .errorCount());

    }

    /**
     * Type infinite inference recursion
     */
    @Test
    public void testCompiler0009() {
//        Assertions.assertTrue(
//                _compileResource("fail/testCompiler0009.hl")
//                        .errorCount()>=2);
    }

    /**
     * Type inference with arrays
     */
    @Test
    public void testCompiler0010() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0010.hl")
                        .errorCount());
    }

    /**
     * Test type matching
     */
    @Test
    public void testCompiler0011() {
//        Assertions.assertEquals(1,
//                _compileResource("fail/testCompiler0011.hl")
//                        .errorCount());
    }

    /**
     * Test array assignment
     */
    @Test
    public void testCompiler0012() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0012.hl")
                        .errorCount());
    }

    /**
     * Test Tuples
     * <pre>
     * var (c,d)=(1,2);
     * var (a1,(a2,a3))=n();
     * </pre>
     */
    @Test
    public void testCompiler0013() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0013.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0014() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0014.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0015() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0015.hl")
                        .errorCount());
        ;
    }

    @Test
    public void testCompiler0016() {
//        Assertions.assertEquals(1,
//                _compileResource("fail/testCompiler0016.hl")
//                        .errorCount());
    }

    @Test
    public void testCompiler0017() {
//        Assertions.assertEquals(3,
//                _compileResource("fail/testCompiler0017.hl")
//                        .errorCount());
    }

    @Test
    public void testCompiler0018() {
//        Assertions.assertEquals(2,
//                _compileResource("fail/testCompiler0018.hl")
//                        .errorCount());
    }

    @Test
    public void testCompiler0019() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0019.hl")
                        .errorCount());
    }

    /**
     * array selectors
     * <pre>
     *     int[10] arr((i)->2*i);
     *     println("arr=",arr);
     *
     *     int[] arr2=arr[3..9];
     *     println("1::arr2=",arr2);
     *
     *     arr2[3..5]=arr[1..3];
     *     println("2::arr2=",arr2);
     *
     *     arr2[(i)->i%2==0]=arr[1..3];
     *     println("3::arr2=",arr2);
     * </pre>
     */
    @Test
    public void testCompiler0020() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0020.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0021() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0021.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0022() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0022.hl")
                        .errorCount());
    }

    /**
     * if statement
     */
    @Test
    public void testCompiler0023() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0023.hl")
                        .errorCount());
    }

    /**
     * while statement
     */
    @Test
    public void testCompiler0024() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0024.hl")
                        .errorCount());
    }

    /**
     * for statement
     */
    @Test
    public void testCompiler0025() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0025.hl")
                        .errorCount());
    }

    /**
     * switch statement
     */
    @Test
    public void testCompiler0026() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0026.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0027() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0027.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0028() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0028.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0029() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0029.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0030() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0030.hl")
                        .errorCount());
    }

    /**
     * Support for json format
     */
    @Test
    public void testCompiler0031() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0031.hl")
                        .errorCount());
    }

    /**
     * Support for tuple/class conversion
     */
    @Test
    public void testCompiler0032() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0032.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0033() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0033.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0034() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0034.hl")
                        .errorCount());
    }

    /**
     * using Global Class Name
     */
    @Test
    public void testCompiler0035() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0035.hl")
                        .errorCount());
    }

    /**
     * test precompiler expressions (this:varName,this:lineNumber,...)
     */
    @Test
    public void testCompiler0036() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0036.hl")
                        .errorCount());
    }

    /**
     * Logging framework
     */
    @Test
    public void testCompiler0037() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0037.hl")
                        .errorCount());
    }

    /**
     * try/catch
     */
    @Test
    public void testCompiler0038() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0038.hl")
                        .errorCount());
    }

    @Test
    public void testCompiler0039() {
        Assertions.assertEquals(0,
                _compileResource("testCompilerSuccess0039.hl")
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
        return TestHelper.compileOnlyResource("compiler/success/" + resourceFileName);
    }

}