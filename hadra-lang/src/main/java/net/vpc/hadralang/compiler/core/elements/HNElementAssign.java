package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;

public class HNElementAssign extends HNElement implements Cloneable{

    public JTypeOrLambda type;

    public HNElementAssign() {
        super(HNElementKind.ASSIGN);
    }

    public HNElementAssign(JType type) {
        super(HNElementKind.EXPR);
        setType(type);
    }

    public JType getType() {
        return type == null ? null : type.getType();
    }

    public HNElementAssign setType(JType type) {
        this.type = type == null ? null : JTypeOrLambda.of(type);
        return this;
    }

    public HNElementAssign setType(JTypeOrLambda type) {
        this.type = type;
        return this;
    }

    @Override
    public JTypeOrLambda getTypeOrLambda() {
        return type;
    }

    @Override
    public String toString() {
        return "Expr{"
                + (type == null ? "null" : type.toString())
                + '}';
    }
}
