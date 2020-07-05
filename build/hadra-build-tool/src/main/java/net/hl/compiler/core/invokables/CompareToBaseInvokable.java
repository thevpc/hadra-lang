package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypes;
import net.vpc.common.jeep.impl.functions.AbstractJInvokable;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.util.JTypeUtils;

public abstract class CompareToBaseInvokable extends AbstractJInvokableAdapterSpecial {

    public CompareToBaseInvokable(JInvokable base, JSignature signature, JContext context) {
        super(base,signature,context);
    }

    public JType getReturnType() {
        return JTypeUtils.forBoolean(context().types());
    }

}
