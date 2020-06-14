/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JNodeFindAndReplace;

import java.util.Collections;
import java.util.List;
import net.vpc.common.jeep.JNodeCopyFactory;

/**
 *
 * @author vpc
 */
public abstract class HNBreakOrContinue extends HNode {
    private JToken leaps;
    private String label;

    protected HNBreakOrContinue(HNNodeId id) {
        super(id);
    }

    protected HNBreakOrContinue(HNNodeId id, JToken leaps, JToken token,JToken endToken) {
        super(id);
        this.leaps = leaps;
        setStartToken(token);
        setEndToken(endToken);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }

    @Override
    public List<JNode> childrenNodes() {
        return  Collections.emptyList();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNBreakOrContinue) {
            HNBreakOrContinue o =(HNBreakOrContinue) node;
            this.leaps=(o.leaps);
            this.label =(o.label);
        }
    }

    public JToken getLeaps() {
        return leaps;
    }

    public HNBreakOrContinue setLeaps(JToken leaps) {
        this.leaps = leaps;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public HNBreakOrContinue setLabel(String label) {
        this.label = label;
        return this;
    }

    public  int leapVal(){
        return leaps==null?0:Integer.parseInt(leaps.image);
    }

}
