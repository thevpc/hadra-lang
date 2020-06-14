package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JType;
import net.vpc.hadralang.compiler.parser.ast.HNDeclareInvokable;
import net.vpc.common.jeep.JTypeOrLambda;
import net.vpc.hadralang.compiler.utils.HUtils;

public class HNElementConstructor extends HNElement {

    JType declaringType;
    JInvokable invokable;
    HNDeclareInvokable declaration;
    public JNode[] argNodes;

    public HNElementConstructor(JType declaringType, JInvokable invokable,JNode[] argNodes) {
        super(HNElementKind.CONSTRUCTOR);
        this.declaringType = declaringType;
        this.invokable = invokable;
        this.argNodes = argNodes;
    }

    public JNode[] getArgNodes() {
        return argNodes;
    }

    public void setArgNodes(JNode[] argNodes) {
        this.argNodes = argNodes;
    }

    public HNDeclareInvokable getDeclaration() {
        return declaration;
    }

    public HNElementConstructor setDeclaration(HNDeclareInvokable declaration) {
        this.declaration = declaration;
        if (declaration != null) {
            JNode node = (JNode) declaration;
            setLocation(node.startToken());
            setSource(HUtils.getSource(node));
        }
        return this;
    }

    public JInvokable getInvokable() {
        return invokable;
    }

    public HNElementConstructor setInvokable(JInvokable invokable) {
        this.invokable = invokable;
        return this;
    }

    public JType getDeclaringType() {
        return declaringType;
    }

    public JTypeOrLambda getTypeOrLambda() {
        JType t = getDeclaringType();
        if (t == null) {
            throw new NullPointerException();
        }
        return JTypeOrLambda.of(t);
    }

    @Override
    public String toString() {
        return "Constructor{"
                + (declaringType == null ? "null" : declaringType.name())
                + '}';
    }
}
