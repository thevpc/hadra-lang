/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.thevpc.jeep.JToken;

/**
 *
 * @author vpc
 */
public class HNBreak extends HNBreakOrContinue {
    protected HNBreak() {
        super(HNNodeId.H_BREAK);
    }

    public HNBreak(JToken leaps, JToken startToken, JToken endToken) {
        super(HNNodeId.H_BREAK,leaps,startToken,endToken);
    }


    @Override
    public String toString() {
        if(leapVal()<=0){
            return "break";
        }else{
            return "break "+leapVal();
        }
    }
}
