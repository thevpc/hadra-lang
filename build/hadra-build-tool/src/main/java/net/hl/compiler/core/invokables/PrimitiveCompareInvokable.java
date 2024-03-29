package net.hl.compiler.core.invokables;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.AbstractJFunction;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.util.JTypeUtils;

import java.util.Objects;

public class PrimitiveCompareInvokable extends AbstractJFunction {

    JSignature sig;
    JType boolType;
    String op;
    public PrimitiveCompareInvokable(JTypes types,String op) {
        super(types);
        this.op=op;
        switch (op){
            case "<":
            case "<=":
            case ">":
            case ">=": {
                //okkay
                break;
            }
            default:{
                throw new IllegalArgumentException("Unsupported compare operator "+op);
            }
        }
        sig=JSignature.of("primitiveCompare",
                JTypeUtils.forObject(types),
                JTypeUtils.forObject(types)
        );
        boolType= JTypeUtils.forBoolean(types);
    }

    public String getOp() {
        return op;
    }

    @Override
    public Object invoke(JInvokeContext context) {
        Object a = context.evaluateArg(0);
        Object b = context.evaluateArg(1);
        int x=((Comparable)a).compareTo(b);
        switch (op){
            case "<": return x<0;
            case "<=": return x<=0;
            case ">": return x>0;
            case ">=": return x>=0;
        }
        return 0;
    }

    @Override
    public JSignature getSignature() {
        return sig;
    }

    @Override
    public JType getReturnType() {
        return boolType;
    }

    @Override
    public String getName() {
        return "primitiveCompare";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrimitiveCompareInvokable that = (PrimitiveCompareInvokable) o;
        return Objects.equals(sig, that.sig) &&
                Objects.equals(boolType, that.boolType) &&
                Objects.equals(op, that.op);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sig, boolType, op);
    }
    @Override
    public String getSourceName() {
        return "<runtime>";
    }

}
