package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;

public class HNElementNonExpr extends HNElement implements Cloneable{
    public HNElementNonExpr() {
        super(HNElementKind.NON_EXPR);
    }

    public JType getType() {
        return null;
    }

    public JTypeOrLambda getTypeOrLambda() {
        return null;
    }
    @Override
    public String toString() {
        return "NonExpr";
    }
}
