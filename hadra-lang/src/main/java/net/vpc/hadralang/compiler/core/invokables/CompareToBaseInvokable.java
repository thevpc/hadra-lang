package net.vpc.hadralang.compiler.core.invokables;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.util.JTypeUtils;

public abstract class CompareToBaseInvokable implements JInvokable {
    protected final JInvokable base;
    protected final JSignature signature;
    protected final JContext context;

    public CompareToBaseInvokable(JInvokable base, JSignature signature, JContext context) {
        this.base = base;
        this.signature = signature;
        this.context = context;
    }

    public JInvokable getBase() {
        return base;
    }

    public JContext context() {
        return context;
    }

    @Override
    public JSignature signature() {
        return signature;
    }

    @Override
    public JType returnType() {
        return JTypeUtils.forBoolean(context().types());
    }

    @Override
    public String name() {
        return signature.name();
    }

    @Override
    public String getSourceName() {
        return base.getSourceName();
    }
}
