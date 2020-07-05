package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JInvokeContext;

public class CompareToLessEqualsThanInvokable extends CompareToBaseInvokable {

    public CompareToLessEqualsThanInvokable(JInvokable m2, JContext context) {
        super(m2, m2.getSignature(), context);
    }

    @Override
    public Object invoke(JInvokeContext context) {
        int v = (int) getBase().invoke(context);
        return v <= 0;
    }
}
