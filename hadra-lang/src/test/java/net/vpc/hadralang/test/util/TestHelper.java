/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.test.util;

import net.vpc.hadralang.compiler.HL;
import net.vpc.hadralang.compiler.core.HLProject;
import net.vpc.hadralang.compiler.utils.SetLog;

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
        HL hl=HL.create();
        return hl
                .addSourceResourcesFile("net/vpc/hadralang/test/" + resourceFileName)
                .generateJavaFolder("build/generated/tmp/" + resourceFileName)
                .compile();

    }

    public static HLProject compileOnlyResource(String resourceFileName) {
        SetLog.prepare();
        printHeader(resourceFileName);
        HL hl=HL.create();
        return hl
                .addSourceResourcesFile("net/vpc/hadralang/test/" + resourceFileName)
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
        HL hl=HL.create();
        return hl
                .addSourceText(text, "<user-text>")
                .compile();
    }
    public static HLProject parseOnlyText(String id, String text) {
        SetLog.prepare();
        printHeader(id);
        HL hl=HL.create();
        return hl
                .addSourceText(text, "<user-text>")
                .parse();
    }

}
