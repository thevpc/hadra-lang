package net.hl.compiler.parser.ast;

import net.vpc.common.jeep.JType;

public class ElementTypeAndConstraint {
    public JType valType;
    public InitValueConstraint valCstr;

    public ElementTypeAndConstraint(JType valType, InitValueConstraint valCstr) {
        this.valType = valType;
        this.valCstr = valCstr;
    }
}
