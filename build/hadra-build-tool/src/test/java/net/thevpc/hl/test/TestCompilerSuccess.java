package net.thevpc.hl.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.thevpc.hl.test.util.TestHelper;

public class TestCompilerSuccess {

    @Test
    public void testCompiler0001() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0001.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0002() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0002.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0003() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0003.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0004() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0004.hl")
                        .getErrorCount());
    }

    /**
     * def int m(int x)-> x+1;
     */
    @Test
    public void testCompiler0005() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0005.hl")
                        .getErrorCount());

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
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0006.hl")
                        .getErrorCount());

    }

    /**
     * Type inference in Forward declarations
     */
    @Test
    public void testCompiler0007() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0007.hl")
                        .getErrorCount());

    }

    /**
     * Type inference in Forward declarations
     */
    @Test
    public void testCompiler0008() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0008.hl")
                        .getErrorCount());

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
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0010.hl")
                        .getErrorCount());
    }

    /**
     * Test type matching
     */
    @Test
    public void testCompiler0011() {
        Assertions.assertEquals(1,
                TestHelper.compileOnlySuccessResource("fail/testCompiler0011.hl")
                        .getErrorCount());
    }

    /**
     * Test array assignment
     */
    @Test
    public void testCompiler0012() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0012.hl")
                        .getErrorCount());
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
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0013_tuple.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0014() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0014.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0015() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0015.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0016() {
//        Assertions.assertEquals(1,
//                _compileResource("fail/testCompiler0016.hl")
//                        .getErrorCount());
    }

    @Test
    public void testCompiler0017() {
//        Assertions.assertEquals(3,
//                _compileResource("fail/testCompiler0017.hl")
//                        .getErrorCount());
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
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0019_nulls.hl")
                        .getErrorCount());
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
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0020_array_selectors.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0021() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0021_tuples.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0022() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0022_array_len_selector.hl")
                        .getErrorCount());
    }

    /**
     * if statement
     */
    @Test
    public void testCompiler0023() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0023_if_stmt.hl")
                        .getErrorCount());
    }

    /**
     * while statement
     */
    @Test
    public void testCompiler0024() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0024_while_stmt.hl")
                        .getErrorCount());
    }

    /**
     * for statement
     */
    @Test
    public void testCompiler0025() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0025_for_stmt.hl")
                        .getErrorCount());
    }

    /**
     * switch statement
     */
    @Test
    public void testCompiler0026() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0026_switch_stmt.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0027() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0027_switch_expr.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0028() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0028.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0029() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0029_str_interp.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0030() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0030.hl")
                        .getErrorCount());
    }

    /**
     * Support for json format
     */
    @Test
    public void testCompiler0031() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0031.hl")
                        .getErrorCount());
    }

    /**
     * Support for tuple/class conversion
     */
    @Test
    public void testCompiler0032() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0032.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0033() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0033.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0034() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0034.hl")
                        .getErrorCount());
    }

    /**
     * using Global Class Name
     */
    @Test
    public void testCompiler0035() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0035.hl")
                        .getErrorCount());
    }

    /**
     * test precompiler expressions (this:varName,this:lineNumber,...)
     */
    @Test
    public void testCompiler0036() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0036.hl")
                        .getErrorCount());
    }

    /**
     * Logging framework
     */
    @Test
    public void testCompiler0037() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0037.hl")
                        .getErrorCount());
    }

    /**
     * try/catch
     */
    @Test
    public void testCompiler0038() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0038_try.hl")
                        .getErrorCount());
    }

    @Test
    public void testCompiler0039() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess0039_lambda.hl")
                        .getErrorCount());
    }

//    @Test // not correct for now...
//    public void testCompiler0050() {
//        Assertions.assertEquals(0,
//                TestHelper.compileOnlySuccessResource("testCompilerSuccess0050.hl")
//                        .getErrorCount());
//    }
//
    @Test
    public void testCompiler_snippet() {
        Assertions.assertEquals(0,
                TestHelper.compileOnlySuccessResource("testCompilerSuccess_snippet.hl")
                        .getErrorCount());
    }

}
