package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.impl.functions.JSignature;

public abstract class AbstractJInvokableAdapterSpecial extends AbstractJInvokableAdapter {
    protected final JSignature signature;
    protected final JContext context;

    public AbstractJInvokableAdapterSpecial(JInvokable base, JSignature signature, JContext context) {
        super(base);
        this.signature = signature;
        this.context = context;
    }


    public JContext context() {
        return context;
    }

    @Override
    public JSignature getSignature() {
        return signature;
    }

    @Override
    public JType getReturnType() {
        return getBase().getReturnType();
    }

    @Override
    public String getSourceName() {
        return getBase().getSourceName();
    }
}
