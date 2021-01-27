package net.hl.compiler.core.invokables;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.AbstractJFunction;
import net.thevpc.jeep.impl.functions.AbstractJInvokable;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.util.JTypeUtils;

import java.util.Objects;

public class SafeNotEqualsInvokable extends AbstractJFunction {
    JSignature sig;
    JType boolType;

    public SafeNotEqualsInvokable(JTypes types) {
        super(types);
        sig=JSignature.of("safeNotEquals",
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
        return "safeNotEquals";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SafeNotEqualsInvokable that = (SafeNotEqualsInvokable) o;
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
