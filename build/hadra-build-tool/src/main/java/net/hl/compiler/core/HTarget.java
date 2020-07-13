/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.core;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @author vpc
 */
public enum HTarget {
    AST,
    RESOLVED_AST,
    JAVA,
    CLASS,
    JAR,
    RUN,
    C,
    BIN_C,
    CS,
    BIN_CS,
    CPP,
    BIN_CPP;

    public Set<HTarget> getReverseDependencies() {
        EnumSet<HTarget> a = EnumSet.noneOf(HTarget.class);
        for (HTarget value : values()) {
            if (value.getDependencies().contains(this)) {
                a.add(value);
            }
        }
        return a;
    }

    public Set<HTarget> getDependencies() {
        switch (this) {
            case AST: {
                return EnumSet.noneOf(HTarget.class);
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
            case CLASS: {
                return EnumSet.of(AST, RESOLVED_AST, JAVA);
            }
            case JAR: {
                return EnumSet.of(AST, RESOLVED_AST, JAVA, CLASS);
            }
            case BIN_C: {
                return EnumSet.of(AST, RESOLVED_AST, C);
            }
            case BIN_CPP: {
                return EnumSet.of(AST, RESOLVED_AST, CPP);
            }
            case BIN_CS: {
                return EnumSet.of(AST, RESOLVED_AST, CS);
            }
            case RUN: {
                return EnumSet.of(AST, RESOLVED_AST, JAVA);
            }
        }
        return EnumSet.noneOf(HTarget.class);
    }

    public static Set<HTarget> expandDependencies(HTarget[] base) {
        return expandDependencies(EnumSet.copyOf(Arrays.asList(base)));
    }

    public static Set<HTarget> expandReverseDependencies(HTarget[] base) {
        return expandReverseDependencies(EnumSet.copyOf(Arrays.asList(base)));
    }

    public static Set<HTarget> expandDependencies(Set<HTarget> base) {
        EnumSet<HTarget> a = EnumSet.copyOf(base);
        for (HTarget x : base) {
            a.addAll(x.getDependencies());
        }
        return a;
    }

    public static Set<HTarget> expandReverseDependencies(Set<HTarget> base) {
        EnumSet<HTarget> a = EnumSet.copyOf(base);
        for (HTarget x : base) {
            a.addAll(x.getReverseDependencies());
        }
        return a;
    }

}
