package net.hl.compiler.core.elements;

import net.thevpc.jeep.JInvokable;
import net.thevpc.jeep.JNode;
import net.thevpc.jeep.JType;
import net.thevpc.jeep.JTypePattern;
import net.hl.compiler.ast.HNIf;
import net.hl.compiler.utils.HSharedUtils;

public class HNElementWhenDo extends HNElement implements Cloneable{
    public String methodName;
    public JInvokable invokable;
    private JType condType;
    private HNIf.WhenDoBranchNode[] branches;
    private JNode elseNode;
    private JType resultType;

    public HNElementWhenDo(String methodName, JInvokable invokable, JType condType, JType resultType, HNIf.WhenDoBranchNode[] branches, JNode elseNode) {
        super(HNElementKind.WHEN_DO);
        this.methodName = methodName;
        this.invokable = invokable;
        this.condType = condType;
        this.branches = branches;
        this.elseNode = elseNode;
        this.resultType = resultType;
        setLocation(branches[0].getStartToken());
        setSource(HSharedUtils.getSource(branches[0]));
    }

    public JType getResultType() {
        return resultType;
    }

    public HNElementWhenDo(String methodName) {
        super(HNElementKind.METHOD);
        this.methodName = methodName;
    }

    public JType getCondType() {
        return condType;
    }

    public HNIf.WhenDoBranchNode[] getBranches() {
        return branches;
    }

    public JNode getElseNode() {
        return elseNode;
    }

    public JInvokable getInvokable() {
        return invokable;
    }

    public JType getReturnType() {
        if(invokable!=null){
            return invokable.getReturnType();
        }
        return null;
    }

    @Override
    public JTypePattern getTypePattern() {
        if(invokable!=null){
            JType type = invokable.getReturnType();
            if(type==null){
                return null;
            }
            return JTypePattern.of(type);
        }
        return null;
    }

    public HNElementWhenDo setInvokable(JInvokable invokable) {
        this.invokable = invokable;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public JType getType(){
        if(invokable!=null){
            JType type = invokable.getReturnType();
            return type;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Method{" +
                methodName+
                '}';
    }
}
