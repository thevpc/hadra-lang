/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.core;

import net.thevpc.jeep.JContext;
import net.thevpc.jeep.impl.JContextImpl;
import net.thevpc.nuts.NutsSession;

/**
 *
 * @author vpc
 */
public class HadraContextImpl extends JContextImpl implements HadraContext {

    private NutsSession session;

    public HadraContextImpl(NutsSession session, JContext context) {
        super(context);
        this.session = session;
    }

    @Override
    public HadraContext newContext() {
        return new HadraContextImpl(session, this);
    }

    @Override
    public NutsSession getSession() {
        return session;
    }

}
