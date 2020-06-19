package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JType;
import net.vpc.hadralang.compiler.parser.ast.HNDeclareTokenBase;
import net.vpc.common.jeep.JTypeOrLambda;
import net.vpc.hadralang.compiler.utils.HUtils;

public class HNElementLocalVar extends HNElement implements Cloneable{
    String name;
    HNDeclareTokenBase declaration;
    JType effectiveType;

    public HNElementLocalVar(String name) {
        super(HNElementKind.LOCAL_VAR);
        this.name = name;
    }

    public HNElementLocalVar(String name, HNDeclareTokenBase declaration, JToken location) {
        super(HNElementKind.LOCAL_VAR);
        this.name = name;
        setDeclaration(declaration);
        setLocation(location);
    }

    public HNElementLocalVar setDeclaration(HNDeclareTokenBase declaration) {
        this.declaration = declaration;
        if(declaration!=null){
            if(declaration instanceof JNode) {
                JNode node = (JNode) declaration;
                setLocation(node.startToken());
                setSource(HUtils.getSource(node));
            }
        }
        return this;
    }

    public String getName() {
        return name;
    }

    public HNDeclareTokenBase getDeclaration() {
        return declaration;
    }

    public JType getEffectiveType() {
        return effectiveType;
    }

    public HNElementLocalVar setEffectiveType(JType effectiveType) {
        this.effectiveType = effectiveType;
        return this;
    }

    @Override
    public JTypeOrLambda getTypeOrLambda() {
        if(effectiveType!=null){
            return JTypeOrLambda.of(effectiveType);
        }
        return null;
    }

    @Override
    public String toString() {
        return "LocalVar{" +
                name+
                '}';
    }
}
