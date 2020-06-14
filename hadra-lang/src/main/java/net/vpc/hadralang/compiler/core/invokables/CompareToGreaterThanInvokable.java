package net.vpc.hadralang.compiler.core.invokables;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JInvokeContext;

public class CompareToGreaterThanInvokable extends CompareToBaseInvokable {

    public CompareToGreaterThanInvokable(JInvokable m2, JContext context) {
        super(m2, m2.signature(), context);
    }

    @Override
    public Object invoke(JInvokeContext context) {
        int v = (int) base.invoke(context);
        return v > 0;
    }
}
