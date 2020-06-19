package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;

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
    public JTypeOrLambda getTypeOrLambda() {
        return null;
    }

    @Override
    public String toString() {
        return "IdentifierDef{" +
                (type == null ? "null" : type.name()) +
                '}';
    }
}
