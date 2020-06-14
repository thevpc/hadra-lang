/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.test.util;

import net.vpc.hadralang.compiler.HL;
import net.vpc.hadralang.compiler.core.HLCWithOptions;
import net.vpc.hadralang.compiler.core.HLProject;

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

    public static HLProject compile2JavaResource(String resourceFileName) {
        SetLog.prepare();
        printHeader(resourceFileName);
        HLCWithOptions hlc = new HL().withOptions();
        return hlc
                .includeResourcesFile("net/vpc/hadralang/test/" + resourceFileName)
                .generateJavaFolder("build/generated/tmp/" + resourceFileName)
                .compile();

    }

    public static HLProject compileOnlyResource(String resourceFileName) {
        SetLog.prepare();
        printHeader(resourceFileName);
        HLCWithOptions hlc = new HL().withOptions();
        return hlc
                .includeResourcesFile("net/vpc/hadralang/test/" + resourceFileName)
                .compile();

    }

    /**
     * compile text
     *
     * @param text text to compile
     * @return project compilation result
     */
    public static HLProject compileOnlyText(String id, String text) {
        SetLog.prepare();
        printHeader(id);
        HLCWithOptions hlc = new HL().withOptions();
        return hlc
                .includeText(text, "<user-text>")
                .compile();
    }

}
