package net.hl.compiler.core.elements;

import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypePattern;
import net.hl.compiler.ast.HNDeclareInvokable;
import net.hl.compiler.ast.HNode;
import net.hl.compiler.utils.HUtils;

public class HNElementConstructor extends HNElementInvokable implements Cloneable{

    JType declaringType;
    JInvokable invokable;
    HNDeclareInvokable declaration;
    public HNode[] argNodes;

    public HNElementConstructor(JType declaringType, JInvokable invokable,HNode[] argNodes) {
        super(HNElementKind.CONSTRUCTOR);
        this.declaringType = declaringType;
        this.invokable = invokable;
        this.argNodes = argNodes;
    }

    public HNode[] getArgNodes() {
        return argNodes;
    }

    public HNElement setArgNodes(HNode[] argNodes) {
        this.argNodes = argNodes;
        return this;
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

    public JTypePattern getTypePattern() {
        JType t = getDeclaringType();
        if (t == null) {
            throw new NullPointerException();
        }
        return JTypePattern.of(t);
    }

    @Override
    public String toString() {
        return "Constructor{"
                + (declaringType == null ? "null" : declaringType.getName())
                + '}';
    }
}
