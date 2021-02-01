/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.hl.test.util;

import net.hl.compiler.HL;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.HTarget;
import net.hl.compiler.utils.SetLog;

/**
 *
 * @author vpc
 */
public class TestHelper {

    public static void printHeader(String header) {
//        log.log(Level.FINE, "Test :: " + header);
        System.out.println("*******************************************************");
        System.out.println("  " + header);
        System.out.println("*******************************************************");
    }

    public static HProject compile2JavaResource(String resourceFileName) {
        SetLog.prepare();
        printHeader(resourceFileName);
        return HL.create()
                .addSourceResourcesFile("net/hl/test/" + resourceFileName)
                .setJavaFolder("target/custom-generated-test-sources/tmp/" + resourceFileName)
                .addTarget(HTarget.JAVA)
                .compile();

    }

    public static HProject compileOnlySuccessResource(String resourceFileName) {
        return TestHelper.compileOnlyResource("compiler/success/" + resourceFileName);
    }
    
    public static HProject compileOnlyResource(String resourceFileName) {
        SetLog.prepare();
        printHeader(resourceFileName);
        return HL.create()
                .addSourceResourcesFile("net/hl/test/" + resourceFileName)
                .addTarget(HTarget.RESOLVED_AST)
                .compile();

    }

    /**
     * compile text
     *
     * @param text text to compile
     * @return project compilation result
     */
    public static HProject compileOnlyText(String id, String text) {
        SetLog.prepare();
        printHeader(id);
        HL hl=HL.create();
        return hl
                .addSourceText(text, "<user-text>")
                .addTarget(HTarget.RESOLVED_AST)
                .compile();
    }
    
    public static HProject parseOnlyText(String id, String text) {
        SetLog.prepare();
        printHeader(id);
        return HL.create()
                .addSourceText(text, "<user-text>")
                .addTarget(HTarget.AST)
                .compile();
    }

}
