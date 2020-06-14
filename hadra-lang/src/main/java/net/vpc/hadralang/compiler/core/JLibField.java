package net.vpc.hadralang.compiler.core;

import net.vpc.common.jeep.JField;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.util.JTokenUtils;
import net.vpc.hadralang.compiler.parser.ast.HNDeclareTokenBase;
import net.vpc.hadralang.compiler.utils.HNodeUtils;

public class JLibField implements HNDeclareTokenBase {
    private final JField field;

    @Override
    public String getName() {
        return field.name();
    }

    @Override
    public JToken getToken() {
        return HNodeUtils.createToken(field.name());
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
