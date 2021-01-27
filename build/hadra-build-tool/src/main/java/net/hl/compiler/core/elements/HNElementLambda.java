package net.hl.compiler.core.elements;

import net.thevpc.jeep.JType;
import net.thevpc.jeep.JTypePattern;

public class HNElementLambda extends HNElement implements Cloneable{
    public JTypePattern typePattern;
    public JType inferredType;

    public HNElementLambda() {
        super(HNElementKind.LAMBDA);
    }

    public HNElementLambda setArgTypes(JType[] lambdaArgType,JType returnType) {
        this.typePattern = JTypePattern.of(lambdaArgType,returnType);
        return this;
    }

    @Override
    public JTypePattern getTypePattern() {
        return typePattern;
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
                typePattern +
                ')';
    }
}
