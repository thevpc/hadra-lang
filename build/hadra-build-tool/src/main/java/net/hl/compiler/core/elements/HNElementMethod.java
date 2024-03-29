package net.hl.compiler.core.elements;

import net.thevpc.jeep.*;
import net.thevpc.jeep.util.JeepUtils;
import net.hl.compiler.ast.HNDeclareInvokable;
import net.hl.compiler.ast.HNThis;
import net.hl.compiler.index.HIndexedMethod;
import net.thevpc.jeep.JTypePattern;
import net.hl.compiler.ast.HNode;
import net.hl.compiler.utils.HSharedUtils;

public class HNElementMethod extends HNElementInvokable implements Cloneable{
    public JType declaringType;
    public JInvokable invokable;
    public HIndexedMethod indexedMethod;
    public HNDeclareInvokable declaration;
    public HNode[] argNodes;
    public JTypePattern[] argTypes;
    public Arg0Kind arg0Kind = Arg0Kind.NONE;
    public JTypePattern arg0Type = null;
    public boolean arg0TypeProcessed = false;

    public HNElementMethod(JInvokable invokable) {
        super(HNElementKind.METHOD);
        this.invokable = invokable;
    }

//    public HNElementMethod(String methodName) {
//        super(HNElementKind.METHOD);
//        this.methodName = methodName;
//    }

    public HIndexedMethod getIndexedMethod() {
        return indexedMethod;
    }

    public HNode[] getArgNodes() {
        return argNodes;
    }

    public JTypePattern[] getArgTypes() {
        return argTypes;
    }

    public HNElementMethod setArgTypes(JTypePattern[] argTypes) {
        this.argTypes = argTypes;
        return this;
    }

    public HNElementMethod setArgNodes(HNode[] argNodes) {
        this.argNodes = argNodes;
        return this;
    }

    public HNDeclareInvokable getDeclaration() {
        return declaration;
    }

    public HNElementMethod setDeclaration(HNDeclareInvokable declaration) {
        this.declaration = declaration;
        if(declaration!=null){
            JNode node = (JNode) declaration;
            setLocation(node.getStartToken());
            setSource(HSharedUtils.getSource(node));
        }
        return this;
    }

    public HNElementMethod setDeclaringType(JType declaringType) {
        this.declaringType = declaringType;
        return this;
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
            JType type = invokable.getGenericReturnType();
            if(type==null){
                return null;
            }
            return JTypePattern.of(type);
        }
        return null;
    }

    public HNElementMethod setInvokable(JInvokable invokable) {
        this.invokable = invokable;
        return this;
    }

    public JType getDeclaringType() {
        return declaringType;
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
                getInvokable().getName()+
                '}';
    }

    public Arg0Kind getArg0Kind() {
        return arg0Kind;
    }

    public HNElementMethod setArg0Kind(Arg0Kind arg0Kind) {
        this.arg0Kind = arg0Kind;
        return this;
    }

    public JTypePattern getArg0Type() {
        return arg0Type;
    }

    public HNElementMethod setArg0Type(JTypePattern arg0Type) {
        this.arg0Type = arg0Type;
        return this;
    }

    public boolean isArg0TypeProcessed() {
        return arg0TypeProcessed;
    }

    public HNElementMethod setArg0TypeProcessed(boolean arg0TypeProcessed) {
        this.arg0TypeProcessed = arg0TypeProcessed;
        return this;
    }

    public void processArg0(HNode dotBase){
        if(!this.isArg0TypeProcessed()) {
            if (this.getArg0Kind() == HNElementMethod.Arg0Kind.THIS) {
                this.setArgNodes(
                        JeepUtils.arrayAppend(JNode.class, new HNThis(this.getArg0Type().getType(), null), this.getArgNodes())
                );
                this.setArgTypes(
                        JeepUtils.arrayAppend(JTypePattern.class, this.getArg0Type(), this.getArgTypes())
                );
                this.setArg0TypeProcessed(true);
            } else if (this.getArg0Kind() == HNElementMethod.Arg0Kind.BASE) {
                this.setArgNodes(
                        JeepUtils.arrayAppend(HNode.class, dotBase, this.getArgNodes()==null?new HNode[0]:this.getArgNodes())
                );
                this.setArgTypes(
                        JeepUtils.arrayAppend(JTypePattern.class, this.getArg0Type(), this.getArgTypes()==null?new JTypePattern[0]:this.getArgTypes())
                );
                this.setArg0TypeProcessed(true);
            }
        }
    }

    public enum Arg0Kind {
        NONE,
        BASE,
        THIS,
    }
}
