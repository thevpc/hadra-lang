/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.core.invokables;

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
        return converter.convert(context.arguments()[0].evaluate(context), context);
    }

    @Override
    public JSignature signature() {
        return JSignature.of(name(), converter.originalType().getType());
    }

    @Override
    public JType returnType() {
        return converter.targetType().getType();
    }

    @Override
    public String name() {
        return "implicitConvert";
    }

    public JConverter getConverter() {
        return converter;
    }
    
    
}