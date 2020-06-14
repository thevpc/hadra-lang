package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypes;
import net.vpc.common.jeep.JTypeOrLambda;
import net.vpc.common.jeep.util.JTypeUtils;

public class HNElementStatement extends HNElement {
    JTypeOrLambda voidType;
    public HNElementStatement(JTypes types) {
        super(HNElementKind.STATEMENT);
        voidType=JTypeOrLambda.of(JTypeUtils.forVoid(types));
    }

    public JType getType() {
        return voidType.getType();
    }

    public JTypeOrLambda getTypeOrLambda() {
        return voidType;
    }
    @Override
    public String toString() {
        return "Statement";
    }
}
