/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.JConverter;
import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JInvokeContext;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.impl.functions.JSignature;

/**
 *
 * @author vpc
 */
public class JInvokableFromConverter implements JInvokable {
    
    private final JConverter converter;

    public JInvokableFromConverter(JConverter converter) {
        this.converter = converter;
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
