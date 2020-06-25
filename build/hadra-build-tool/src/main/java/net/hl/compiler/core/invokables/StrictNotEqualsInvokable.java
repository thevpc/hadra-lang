package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.util.JTypeUtils;

public class StrictNotEqualsInvokable implements JFunction {
    JSignature sig;
    JType boolType;

    public StrictNotEqualsInvokable(JTypes types) {
        sig = JSignature.of("strictNotEquals",
                JTypeUtils.forObject(types),
                JTypeUtils.forObject(types)
        );
        boolType = JTypeUtils.forBoolean(types);
    }

    @Override
    public Object invoke(JInvokeContext context) {
        Object a = context.evaluateArg(0);
        Object b = context.evaluateArg(1);
        return a != b;
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
        return "strictNotEquals";
    }

    @Override
    public String getSourceName() {
        return "<runtime>";
    }
}
