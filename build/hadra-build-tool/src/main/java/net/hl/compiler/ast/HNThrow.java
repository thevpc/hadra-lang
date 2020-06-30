/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNThrow extends HNode {
    private HNode exceptionInstance;
    private HNThrow() {
        super(HNNodeId.H_THROW);
    }
    public HNThrow(HNode exceptionInstance, JToken startToken, JToken endToken) {
        super(HNNodeId.H_THROW);
        setExceptionInstance(exceptionInstance);
        setStartToken(startToken);
        setEndToken(endToken);
    }


    public HNode getExceptionInstance() {
        return exceptionInstance;
    }

    public HNThrow setExceptionInstance(HNode exceptionInstance) {
        this.exceptionInstance = JNodeUtils.bind(this,exceptionInstance,"exceptionInstance");
        return this;
    }

    @Override
    public String toString() {
        return "throw "+ getExceptionInstance();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNThrow) {
            HNThrow o = (HNThrow) node;
            this.exceptionInstance = JNodeUtils.bindCopy(this, copyFactory, o.exceptionInstance);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(exceptionInstance);
    }

}
