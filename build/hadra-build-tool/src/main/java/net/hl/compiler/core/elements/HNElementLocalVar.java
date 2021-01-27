package net.hl.compiler.core.elements;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.JToken;
import net.thevpc.jeep.JType;
import net.hl.compiler.ast.HNDeclareTokenBase;
import net.thevpc.jeep.JTypePattern;
import net.hl.compiler.ast.HNode;
import net.hl.compiler.utils.HUtils;

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
                setLocation(node.getStartToken());
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
    public JTypePattern getTypePattern() {
        if(effectiveType!=null){
            return JTypePattern.of(effectiveType);
        }
        return null;
    }

    @Override
    public String toString() {
        return "LocalVar{" +
                name+
                '}';
    }

    public static HNElementLocalVar get(HNode n) {
        return get(n.getElement());
    }

    public static HNElementLocalVar get(HNElement element) {
        return (HNElementLocalVar) element;
    }
}
