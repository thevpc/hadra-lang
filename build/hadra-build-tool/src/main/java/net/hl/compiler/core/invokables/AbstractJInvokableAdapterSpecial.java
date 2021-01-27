package net.hl.compiler.core.invokables;

import net.thevpc.jeep.JContext;
import net.thevpc.jeep.JInvokable;
import net.thevpc.jeep.JType;
import net.thevpc.jeep.impl.functions.JSignature;

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
