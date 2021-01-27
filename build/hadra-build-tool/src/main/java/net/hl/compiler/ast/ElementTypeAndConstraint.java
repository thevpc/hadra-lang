package net.hl.compiler.ast;

import net.thevpc.jeep.JType;

public class ElementTypeAndConstraint {
    public JType valType;
    public InitValueConstraint valCstr;

    public ElementTypeAndConstraint(JType valType, InitValueConstraint valCstr) {
        this.valType = valType;
        this.valCstr = valCstr;
    }
}
