package net.hl.compiler.core.elements;

import net.thevpc.jeep.JInvokable;
import net.thevpc.jeep.JToken;
import net.thevpc.jeep.JType;
import net.thevpc.jeep.JTypePattern;
import net.hl.compiler.ast.HNBracketsPostfix;
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
            setLocation(declaration.getStartToken());
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
