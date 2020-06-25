package net.hl.compiler.core;

import net.vpc.common.jeep.JField;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JType;
import net.hl.compiler.parser.ast.HNDeclareTokenBase;
import net.hl.compiler.utils.HTokenUtils;

public class JLibField implements HNDeclareTokenBase {
    private final JField field;

    @Override
    public String getName() {
        return field.name();
    }

    @Override
    public JToken getToken() {
        return HTokenUtils.createToken(field.name());
    }

    public JLibField(JField field) {
        this.field = field;
    }

    @Override
    public JType getIdentifierType() {
        return field.type();
    }

    public String getIdentifierName() {
        return field.name();
    }

    public JField getField() {
        return field;
    }

}
