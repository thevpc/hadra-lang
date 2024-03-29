package net.hl.compiler.core.invokables;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.AbstractJFunction;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.util.JTypeUtils;

public class StrictEqualsInvokable extends AbstractJFunction {
    JSignature sig;
    JType boolType;

    public StrictEqualsInvokable(JTypes types) {
        super(types);
        sig=JSignature.of("strictEquals",
                JTypeUtils.forObject(types),
                JTypeUtils.forObject(types)
        );
        boolType= JTypeUtils.forBoolean(types);
    }

    @Override
    public Object invoke(JInvokeContext context) {
        Object a = context.evaluateArg(0);
        Object b = context.evaluateArg(1);
        return a==b;
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
        return "strictEquals";
    }

    @Override
    public String getSourceName() {
        return "<runtime>";
    }

}
