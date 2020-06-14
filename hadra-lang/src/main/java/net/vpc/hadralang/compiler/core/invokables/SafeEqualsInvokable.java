package net.vpc.hadralang.compiler.core.invokables;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.util.JTypeUtils;

import java.util.Objects;

public class SafeEqualsInvokable implements JFunction {
    public SafeEqualsInvokable(JTypes types) {
        sig=JSignature.of("safeEquals",
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
        return Objects.deepEquals(a,b);
    }

    @Override
    public JSignature signature() {
        return sig;
    }

    @Override
    public JType returnType() {
        return boolType;
    }

    @Override
    public String name() {
        return "safeEquals";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SafeEqualsInvokable that = (SafeEqualsInvokable) o;
        return Objects.equals(sig, that.sig) &&
                Objects.equals(boolType, that.boolType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sig, boolType);
    }
}
