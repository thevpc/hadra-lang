package net.hl.compiler.core.invokables;

import net.thevpc.jeep.JContext;
import net.thevpc.jeep.JInvokable;
import net.thevpc.jeep.JInvokeContext;

public class CompareToGreaterEqualsThanInvokable extends CompareToBaseInvokable {

    public CompareToGreaterEqualsThanInvokable(JInvokable m2, JContext context) {
        super(m2, m2.getSignature(), context);
    }

    @Override
    public Object invoke(JInvokeContext context) {
        int v = (int) getBase().invoke(context);
        return v >= 0;
    }
}
