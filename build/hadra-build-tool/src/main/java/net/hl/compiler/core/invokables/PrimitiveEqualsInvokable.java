package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.AbstractJFunction;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.util.JTypeUtils;

import java.util.Objects;

public class PrimitiveEqualsInvokable extends AbstractJFunction {
    public PrimitiveEqualsInvokable(JTypes types) {
        super(types);
        sig=JSignature.of("safePrimitiveEquals",
                JTypeUtils.forObject(types),
                JTypeUtils.forObject(types)
        );
        boolType= JTypeUtils.forBoolean(types);
    }

    JSignature sig;
    JType boolType;
    @Override
    public Object invoke(JInvokeContext context) {
        Object a = context.evaluateArg(0);
        Object b = context.evaluateArg(1);
        return Objects.equals(a,b);
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
        return "safePrimitiveEquals";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimitiveEqualsInvokable that = (PrimitiveEqualsInvokable) o;
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
