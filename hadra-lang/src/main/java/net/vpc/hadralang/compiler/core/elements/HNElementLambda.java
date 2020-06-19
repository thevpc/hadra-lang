package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;

public class HNElementLambda extends HNElement implements Cloneable{
    public JTypeOrLambda typeOrLambda;
    public JType inferredType;

    public HNElementLambda() {
        super(HNElementKind.LAMBDA);
    }

    public HNElementLambda setArgTypes(JType[] lambdaArgType) {
        this.typeOrLambda = new JTypeOrLambda(lambdaArgType);
        return this;
    }

    @Override
    public JTypeOrLambda getTypeOrLambda() {
        return typeOrLambda;
    }

    public JType getInferredType() {
        return inferredType;
    }

    public HNElementLambda setInferredType(JType inferredType) {
        this.inferredType = inferredType;
        return this;
    }

    @Override
    public String toString() {
        return "Lambda(" +
                typeOrLambda +
                ')';
    }
}
