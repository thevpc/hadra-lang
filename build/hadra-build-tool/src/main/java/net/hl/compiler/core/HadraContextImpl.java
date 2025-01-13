/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.core;

import net.thevpc.jeep.JContext;
import net.thevpc.jeep.impl.JContextImpl;

/**
 *
 * @author vpc
 */
public class HadraContextImpl extends JContextImpl implements HadraContext {

    public HadraContextImpl(JContext context) {
        super(context);
    }

    @Override
    public HadraContext newContext() {
        return new HadraContextImpl(this);
    }


}
