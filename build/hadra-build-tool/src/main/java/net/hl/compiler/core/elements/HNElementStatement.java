package net.hl.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypePattern;
import net.vpc.common.jeep.JTypes;
import net.vpc.common.jeep.util.JTypeUtils;

public class HNElementStatement extends HNElement implements Cloneable{
    JTypePattern voidType;
    public HNElementStatement(JTypes types) {
        super(HNElementKind.STATEMENT);
        voidType= JTypePattern.of(JTypeUtils.forVoid(types));
    }

    public JType getType() {
        return voidType.getType();
    }

    public JTypePattern getTypePattern() {
        return voidType;
    }
    @Override
    public String toString() {
        return "Statement";
    }
}
