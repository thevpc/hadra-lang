/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.core;

import net.thevpc.jeep.JContext;
import net.thevpc.nuts.NSession;

/**
 *
 * @author vpc
 */
public interface HadraContext extends JContext {


    @Override
    public HadraContext newContext();
    
}
