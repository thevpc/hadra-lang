package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;

public class HNElementAssign extends HNElement {

    public JTypeOrLambda type;
    public String declareTempVarName;

    public HNElementAssign() {
        super(HNElementKind.ASSIGN);
    }

    public HNElementAssign(JType type) {
        super(HNElementKind.EXPR);
        setType(type);
    }

    public String getDeclareTempVarName() {
        return declareTempVarName;
    }

    public void setDeclareTempVarName(String declareTempVarName) {
        this.declareTempVarName = declareTempVarName;
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
