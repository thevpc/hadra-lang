package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypePattern;
import net.vpc.common.jeep.JTypes;
import net.vpc.common.jeep.util.JTypeUtils;

public class HNElementType extends HNElement implements Cloneable{
    JType type;
    JType voidType;

    public HNElementType(JType type, JTypes types) {
        super(HNElementKind.TYPE);
        this.type = type;
        voidType = JTypeUtils.forVoid(types);
    }

    public JType getValue() {
        return type;
    }

    public JType getType(){
        return voidType;
    }

    @Override
    public JTypePattern getTypePattern() {
        return JTypePattern.ofTypeOrNull(getType());
    }

    @Override
    public String toString() {
        return "Type{" +
                (type == null ? "null" : type.getName()) +
                '}';
    }
}
