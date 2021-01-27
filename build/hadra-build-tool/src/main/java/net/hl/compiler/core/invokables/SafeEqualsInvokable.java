package net.hl.compiler.core.invokables;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.AbstractJFunction;
import net.thevpc.jeep.impl.functions.AbstractJInvokable;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.util.JTypeUtils;

import java.util.Objects;

public class SafeEqualsInvokable extends AbstractJFunction {
    public SafeEqualsInvokable(JTypes types) {
        super(types);
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
    public JSignature getSignature() {
        return sig;
    }

    @Override
    public JType getReturnType() {
        return boolType;
    }

    @Override
    public String getName() {
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
    @Override
    public String getSourceName() {
        return "<runtime>";
    }

}
