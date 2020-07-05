package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JInvokeContext;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.util.JTypeUtils;

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
