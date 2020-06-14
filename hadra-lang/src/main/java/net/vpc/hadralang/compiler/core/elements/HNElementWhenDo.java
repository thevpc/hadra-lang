package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;
import net.vpc.hadralang.compiler.parser.ast.HNIf;
import net.vpc.hadralang.compiler.utils.HUtils;

public class HNElementWhenDo extends HNElement {
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
        setLocation(branches[0].startToken());
        setSource(HUtils.getSource(branches[0]));
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
            return invokable.returnType();
        }
        return null;
    }

    @Override
    public JTypeOrLambda getTypeOrLambda() {
        if(invokable!=null){
            JType type = invokable.returnType();
            if(type==null){
                return null;
            }
            return JTypeOrLambda.of(type);
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
            JType type = invokable.returnType();
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
