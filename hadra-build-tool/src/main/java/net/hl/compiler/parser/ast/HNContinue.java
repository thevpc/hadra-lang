/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.parser.ast;

import net.vpc.common.jeep.JToken;

/**
 *
 * @author vpc
 */
public class HNContinue extends HNBreakOrContinue {
    protected HNContinue() {
        super(HNNodeId.H_CONTINUE);
    }

    public HNContinue(JToken leaps, JToken startToken, JToken endToken) {
        super(HNNodeId.H_CONTINUE,leaps,startToken,endToken);
    }

    @Override
    public String toString() {
        if(leapVal()<=0){
            return "continue";
        }else{
            return "continue "+leapVal();
        }
    }
}
