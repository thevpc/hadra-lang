package net.hl.compiler.core.elements;

import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypePattern;
import net.hl.compiler.parser.ast.HNBracketsPostfix;
import net.hl.compiler.utils.HUtils;

public class HNElementBracketsVar extends HNElement implements Cloneable{
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
    public JTypePattern getTypePattern() {
        JType t = getType();
        if(t!=null){
            return JTypePattern.of(t);
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
