package net.hl.compiler.core.invokables;

import net.thevpc.jeep.JContext;
import net.thevpc.jeep.JInvokable;
import net.thevpc.jeep.JInvokeContext;
import net.thevpc.jeep.JType;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.util.JTypeUtils;

public class NegateInvokable extends AbstractJInvokableAdapterSpecial {

    public NegateInvokable(JInvokable base, JSignature signature, JContext context) {
        super(base,signature,context);
    }

    public JType getReturnType() {
        return JTypeUtils.forBoolean(context().types());
    }

    @Override
    public Object invoke(JInvokeContext context) {
        boolean v = (boolean) getBase().invoke(context);
        return !v;
    }
}
