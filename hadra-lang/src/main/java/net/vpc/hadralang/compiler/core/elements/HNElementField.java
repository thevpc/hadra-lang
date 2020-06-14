package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JField;
import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JType;
import net.vpc.hadralang.compiler.parser.ast.HNDeclareTokenBase;
import net.vpc.hadralang.compiler.index.HLIndexedField;
import net.vpc.common.jeep.JTypeOrLambda;
import net.vpc.hadralang.compiler.utils.HUtils;

public class HNElementField extends HNElement {

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
        this.declaringType = field.declaringType();
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
    public JTypeOrLambda getTypeOrLambda() {
        if(effectiveType!=null){
            return JTypeOrLambda.of(effectiveType);
        }
        if (field != null) {
            JType type = field.type();
            if (type == null) {
                //type is not resolved yet...
                return null;
            }
            return JTypeOrLambda.of(field.type());
        }
        if (declaration != null) {
            if (declaration.getIdentifierType() != null) {
                return JTypeOrLambda.of(declaration.getIdentifierType());
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