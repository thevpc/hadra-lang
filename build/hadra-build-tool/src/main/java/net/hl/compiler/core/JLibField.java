package net.hl.compiler.core;

import net.thevpc.jeep.JField;
import net.thevpc.jeep.JToken;
import net.thevpc.jeep.JType;
import net.hl.compiler.ast.HNDeclareTokenBase;
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
