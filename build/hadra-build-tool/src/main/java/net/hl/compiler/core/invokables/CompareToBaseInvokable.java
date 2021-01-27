package net.hl.compiler.core.invokables;

import net.thevpc.jeep.JContext;
import net.thevpc.jeep.JInvokable;
import net.thevpc.jeep.JType;
import net.thevpc.jeep.JTypes;
import net.thevpc.jeep.impl.functions.AbstractJInvokable;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.util.JTypeUtils;

public abstract class CompareToBaseInvokable extends AbstractJInvokableAdapterSpecial {

    public CompareToBaseInvokable(JInvokable base, JSignature signature, JContext context) {
        super(base,signature,context);
    }

    public JType getReturnType() {
        return JTypeUtils.forBoolean(context().types());
    }

}
