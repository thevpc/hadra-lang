/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.test;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.compiler.JSourceFactory;
import net.vpc.common.jeep.core.tokens.JTokenDef;
import net.vpc.hadralang.compiler.HL;
import net.vpc.hadralang.compiler.utils.HUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author vpc
 */
public class TestMisc {
    @Test
    public void testTemporal() {
        System.out.println(HUtils.parseTemporal("12:13"));
        System.out.println(HUtils.parseTemporal("12:13:33.666"));
        System.out.println(HUtils.parseTemporal("1960-12-10"));
        System.out.println(HUtils.parseTemporal("1960-12-10 12:10:01.200"));
        System.out.println(HUtils.parseTemporal("1960-12-10T12:10:01.200"));
        System.out.println(HUtils.parseTemporal("1960-12-10T12:10"));
        System.out.println(HUtils.parseTemporal("1960-12-10 12:10"));
    }


}
