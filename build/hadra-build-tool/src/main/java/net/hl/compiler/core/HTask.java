/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.core;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author vpc
 */
public enum HTask {
    CLEAN,
    AST,
    RESOLVED_AST,
    COMPILE,
    RUN,
    
    JAVA,
    C,
    CS,
    CPP;

    public static Set<HTask> getLanguagePorts() {
        return new HashSet<HTask>(
                Arrays.asList(
                        JAVA,C,CPP,CS
                )
        );
    }
    
    public Set<HTask> getReverseDependencies() {
        EnumSet<HTask> a = EnumSet.noneOf(HTask.class);
        for (HTask value : values()) {
            if (value.getDependencies().contains(this)) {
                a.add(value);
            }
        }
        return a;
    }

    public Set<HTask> getDependencies() {
        switch (this) {
            case AST: {
                return EnumSet.noneOf(HTask.class);
            }
            case RESOLVED_AST: {
                return EnumSet.of(AST);
            }
            case JAVA:
            case C:
            case CPP:
            case CS: {
                return EnumSet.of(AST, RESOLVED_AST);
            }
            case RUN: {
                return EnumSet.of(AST, RESOLVED_AST, COMPILE);
            }
        }
        return EnumSet.noneOf(HTask.class);
    }

    public static Set<HTask> expandDependencies(HTask[] base) {
        return expandDependencies(EnumSet.copyOf(Arrays.asList(base)));
    }

    public static Set<HTask> expandReverseDependencies(HTask[] base) {
        return expandReverseDependencies(EnumSet.copyOf(Arrays.asList(base)));
    }

    public static Set<HTask> expandDependencies(Set<HTask> base) {
        EnumSet<HTask> a = EnumSet.copyOf(base);
        for (HTask x : base) {
            a.addAll(x.getDependencies());
        }
        return a;
    }

    public static Set<HTask> expandReverseDependencies(Set<HTask> base) {
        EnumSet<HTask> a = EnumSet.copyOf(base);
        for (HTask x : base) {
            a.addAll(x.getReverseDependencies());
        }
        return a;
    }

}
