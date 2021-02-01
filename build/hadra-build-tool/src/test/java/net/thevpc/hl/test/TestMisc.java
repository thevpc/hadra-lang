/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.hl.test;

import net.hl.compiler.utils.HSharedUtils;
import org.junit.jupiter.api.Test;

/**
 * @author vpc
 */
public class TestMisc {
    @Test
    public void testTemporal() {
        System.out.println(HSharedUtils.parseTemporal("12:13"));
        System.out.println(HSharedUtils.parseTemporal("12:13:33.666"));
        System.out.println(HSharedUtils.parseTemporal("1960-12-10"));
        System.out.println(HSharedUtils.parseTemporal("1960-12-10 12:10:01.200"));
        System.out.println(HSharedUtils.parseTemporal("1960-12-10T12:10:01.200"));
        System.out.println(HSharedUtils.parseTemporal("1960-12-10T12:10"));
        System.out.println(HSharedUtils.parseTemporal("1960-12-10 12:10"));
    }


}
