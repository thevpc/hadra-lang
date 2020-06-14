package net.vpc.hadralang.compiler.core.invokables;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JInvokeContext;

public class EqualsToNotEqualsInvokable extends CompareToBaseInvokable {

    public EqualsToNotEqualsInvokable(JInvokable m2, JContext context) {
        super(m2, m2.signature(), context);
    }

    @Override
    public Object invoke(JInvokeContext context) {
        boolean v = (boolean) base.invoke(context);
        return !v;
    }
}
