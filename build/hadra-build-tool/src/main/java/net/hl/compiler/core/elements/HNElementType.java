package net.hl.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypePattern;
import net.vpc.common.jeep.JTypes;
import net.vpc.common.jeep.util.JTypeUtils;
import net.hl.compiler.parser.ast.HNode;

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

    public static HNElementType get(HNode n) {
        return get(n.getElement());
    }

    public static HNElementType get(HNElement element) {
        return (HNElementType) element;
    }
}
