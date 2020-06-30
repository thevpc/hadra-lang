package net.hl.compiler.core.elements;

import net.vpc.common.jeep.*;
import net.hl.compiler.ast.HNDeclareTokenBase;
import net.hl.compiler.index.HLIndexedField;
import net.vpc.common.jeep.JTypePattern;
import net.hl.compiler.utils.HUtils;

public class HNElementField extends HNElement implements Cloneable{

    public JType declaringType;
    public String name;
    public HLIndexedField indexedField;
    public JField field;
    private HNDeclareTokenBase declaration;
    private JType effectiveType;

    public HNElementField(JField field) {
        super(HNElementKind.FIELD);
        this.name = field.name();
        this.field = field;
        this.declaringType = field.getDeclaringType();
    }

    public HNElementField(String name, JType declaringType, HNDeclareTokenBase declaration, JToken location) {
        super(HNElementKind.FIELD);
        this.name = name;
        this.declaringType = declaringType;
        setDeclaration(declaration);
        setLocation(location);
    }

    public HNElementField(String name) {
        super(HNElementKind.FIELD);
        this.name = name;
    }

    public JType getEffectiveType() {
        return effectiveType;
    }

    public HNElementField setEffectiveType(JType effectiveType) {
        this.effectiveType = effectiveType;
        return this;
    }

    public HNDeclareTokenBase getDeclaration() {
        return declaration;
    }

    public HNElementField setDeclaration(HNDeclareTokenBase declaration) {
        this.declaration = declaration;
        if (declaration != null) {
            JNode node = (JNode) declaration;
            setLocation(node.startToken());
            setSource(HUtils.getSource(node));
        }
        return this;
    }

    @Override
    public JTypePattern getTypePattern() {
        if(effectiveType!=null){
            return JTypePattern.of(effectiveType);
        }
        if (field != null) {
            JType type = field.type();
            if (type == null) {
                //type is not resolved yet...
                return null;
            }
            return JTypePattern.of(field.type());
        }
        if (declaration != null) {
            if (declaration.getIdentifierType() != null) {
                return JTypePattern.of(declaration.getIdentifierType());
            }
        }
        return null;
    }
    
    public String toDescString() {
        return "field "+field+" @ "+super.toDescString();
    }

    @Override
    public String toString() {
        return "Field{"
                + "kind=" + getKind()
                + ", declaringType=" + declaringType
                + ", name='" + name + '\''
                + '}';
    }

    public JType getDeclaringType() {
        return declaringType;
    }

    public HNElementField setDeclaringType(JType declaringType) {
        this.declaringType = declaringType;
        return this;
    }

    public String getName() {
        return name;
    }

    public HNElementField setName(String name) {
        this.name = name;
        return this;
    }

    public HLIndexedField getIndexedField() {
        return indexedField;
    }

    public HNElementField setIndexedField(HLIndexedField indexedField) {
        this.indexedField = indexedField;
        return this;
    }

    public JField getField() {
        return field;
    }

    public HNElementField setField(JField field) {
        this.field = field;
        return this;
    }
}
