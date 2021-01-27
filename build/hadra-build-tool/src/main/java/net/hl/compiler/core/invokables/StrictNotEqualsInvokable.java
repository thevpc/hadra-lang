package net.hl.compiler.core.invokables;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.AbstractJFunction;
import net.thevpc.jeep.impl.functions.AbstractJInvokable;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.util.JTypeUtils;

public class StrictNotEqualsInvokable extends AbstractJFunction {
    JSignature sig;
    JType boolType;

    public StrictNotEqualsInvokable(JTypes types) {
        super(types);
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
