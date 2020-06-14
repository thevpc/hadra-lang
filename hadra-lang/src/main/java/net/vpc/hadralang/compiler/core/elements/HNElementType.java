package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;

public class HNElementType extends HNElement {
    JType type;

    public HNElementType(JType type) {
        super(HNElementKind.TYPE);
        this.type = type;
    }

    public JType getValue() {
        return type;
    }

    public JType getType(){
        return null;
    }

    @Override
    public JTypeOrLambda getTypeOrLambda() {
        return null;
    }

    @Override
    public String toString() {
        return "Type{" +
                (type == null ? "null" : type.name()) +
                '}';
    }
}
