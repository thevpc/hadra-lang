package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypeOrLambda;
import net.vpc.hadralang.compiler.parser.ast.HNBracketsPostfix;
import net.vpc.hadralang.compiler.utils.HUtils;

public class HNElementBracketsVar extends HNElement {
    String name;
    HNBracketsPostfix declaration;
    JType effectiveType;
    JInvokable invokable;

    public HNElementBracketsVar(String name, HNBracketsPostfix declaration, JToken location) {
        super(HNElementKind.BRACKETS_VAR);
        this.name = name;
        setDeclaration(declaration);
        setLocation(location);
    }

    public HNElementBracketsVar setDeclaration(HNBracketsPostfix declaration) {
        this.declaration = declaration;
        if(declaration!=null){
            setLocation(declaration.startToken());
            setSource(HUtils.getSource(declaration));
        }
        return this;
    }

    public JInvokable getInvokable() {
        return invokable;
    }

    public HNElementBracketsVar setInvokable(JInvokable invokable) {
        this.invokable = invokable;
        return this;
    }

    public String getName() {
        return name;
    }

    public HNBracketsPostfix getDeclaration() {
        return declaration;
    }

    public JType getType(){
        return effectiveType;
    }

    public JType getEffectiveType() {
        return effectiveType;
    }

    public HNElementBracketsVar setEffectiveType(JType effectiveType) {
        this.effectiveType = effectiveType;
        return this;
    }

    @Override
    public JTypeOrLambda getTypeOrLambda() {
        JType t = getType();
        if(t!=null){
            return JTypeOrLambda.of(t);
        }
        return null;
    }

    @Override
    public String toString() {
        return "BracketsVar{" +
                name+
                '}';
    }
}
