package net.hl.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypePattern;

public class HNElementIdentifierDef extends HNElement implements Cloneable {
    JType type;

    public HNElementIdentifierDef(JType type) {
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
    public JTypePattern getTypePattern() {
        return null;
    }

    @Override
    public String toString() {
        return "IdentifierDef{" +
                (type == null ? "null" : type.getName()) +
                '}';
    }
}
