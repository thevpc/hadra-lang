package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JeepUtils;
import net.vpc.hadralang.compiler.parser.ast.HNDeclareInvokable;
import net.vpc.hadralang.compiler.parser.ast.HNThis;
import net.vpc.hadralang.compiler.index.HLIndexedMethod;
import net.vpc.common.jeep.JTypePattern;
import net.vpc.hadralang.compiler.parser.ast.HNode;
import net.vpc.hadralang.compiler.utils.HUtils;

public class HNElementMethod extends HNElementInvokable implements Cloneable{
    public String methodName;
    public JType declaringType;
    public JInvokable invokable;
    public HLIndexedMethod indexedMethod;
    public HNDeclareInvokable declaration;
    public HNode[] argNodes;
    public JTypePattern[] argTypes;
    public Arg0Kind arg0Kind = Arg0Kind.NONE;
    public JTypePattern arg0Type = null;
    public boolean arg0TypeProcessed = false;

    public HNElementMethod(JInvokable invokable) {
        super(HNElementKind.METHOD);
        this.methodName = invokable.name();
        this.invokable = invokable;
    }

//    public HNElementMethod(String methodName) {
//        super(HNElementKind.METHOD);
//        this.methodName = methodName;
//    }

    public HLIndexedMethod getIndexedMethod() {
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
            setLocation(node.startToken());
            setSource(HUtils.getSource(node));
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
            return invokable.returnType();
        }
        return null;
    }

    public HNElementMethod setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    @Override
    public JTypePattern getTypePattern() {
        if(invokable!=null){
            JType type = invokable.genericReturnType();
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

    public String getMethodName() {
        return methodName;
    }

    public JType getDeclaringType() {
        return declaringType;
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
