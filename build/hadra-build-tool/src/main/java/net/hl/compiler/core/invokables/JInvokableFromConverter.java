/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.impl.functions.AbstractJInvokable;
import net.vpc.common.jeep.impl.functions.JSignature;

/**
 *
 * @author vpc
 */
public class JInvokableFromConverter extends AbstractJInvokable {
    
    private final JConverter converter;
    private final JTypes types;

    public JInvokableFromConverter(JConverter converter,JTypes types) {
        this.converter = converter;
        this.types = types;
    }

    @Override
    public JTypes getTypes() {
        return types;
    }

    @Override
    public Object invoke(JInvokeContext context) {
        return converter.convert(context.getArguments()[0].evaluate(context), context);
    }

    @Override
    public JSignature getSignature() {
        return JSignature.of(getName(), converter.originalType().getType());
    }

    @Override
    public JType getReturnType() {
        return converter.targetType().getType();
    }

    @Override
    public String getName() {
        return "implicitConvert";
    }

    public JConverter getConverter() {
        return converter;
    }

    @Override
    public String getSourceName() {
        return "<unknown-source>";
    }


}
