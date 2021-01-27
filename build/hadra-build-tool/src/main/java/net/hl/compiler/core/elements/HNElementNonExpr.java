package net.hl.compiler.core.elements;

import net.thevpc.jeep.JType;
import net.thevpc.jeep.JTypePattern;

public class HNElementNonExpr extends HNElement implements Cloneable{
    public HNElementNonExpr() {
        super(HNElementKind.NON_EXPR);
    }

    public JType getType() {
        return null;
    }

    public JTypePattern getTypePattern() {
        return null;
    }
    @Override
    public String toString() {
        return "NonExpr";
    }
}
