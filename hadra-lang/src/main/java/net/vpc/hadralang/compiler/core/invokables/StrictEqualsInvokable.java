package net.vpc.hadralang.compiler.core.invokables;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.util.JTypeUtils;

public class StrictEqualsInvokable implements JFunction {
    JSignature sig;
    JType boolType;

    public StrictEqualsInvokable(JTypes types) {
        sig=JSignature.of("strictEquals",
                JTypeUtils.forObject(types),
                JTypeUtils.forObject(types)
        );
        boolType= JTypeUtils.forBoolean(types);
    }

    @Override
    public Object invoke(JInvokeContext context) {
        Object a = context.evaluateArg(0);
        Object b = context.evaluateArg(1);
        return a==b;
    }

    @Override
    public JSignature signature() {
        return sig;
    }

    @Override
    public JType returnType() {
        return boolType;
    }

    @Override
    public String name() {
        return "strictEquals";
    }
}
