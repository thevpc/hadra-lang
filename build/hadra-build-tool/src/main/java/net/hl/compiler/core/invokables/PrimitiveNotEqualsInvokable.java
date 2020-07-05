package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.AbstractJFunction;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.util.JTypeUtils;

import java.util.Objects;

public class PrimitiveNotEqualsInvokable extends AbstractJFunction {
    JSignature sig;
    JType boolType;

    public PrimitiveNotEqualsInvokable(JTypes types) {
        super(types);
        sig=JSignature.of("primitiveNotEquals",
                JTypeUtils.forObject(types),
                JTypeUtils.forObject(types)
        );
        boolType= JTypeUtils.forBoolean(types);
    }

    @Override
    public Object invoke(JInvokeContext context) {
        Object a = context.evaluateArg(0);
        Object b = context.evaluateArg(1);
        return !Objects.deepEquals(a,b);
    }

    @Override
    public JSignature getSignature() {
        return sig;
    }

    @Override
    public JType getReturnType() {
        return boolType;
    }

    @Override
    public String getName() {
        return "primitiveNotEquals";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimitiveNotEqualsInvokable that = (PrimitiveNotEqualsInvokable) o;
        return Objects.equals(sig, that.sig) &&
                Objects.equals(boolType, that.boolType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sig, boolType);
    }

    @Override
    public String getSourceName() {
        return "<runtime>";
    }
}
