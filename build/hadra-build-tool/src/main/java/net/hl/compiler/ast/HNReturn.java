/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;
import net.vpc.common.jeep.JNodeCopyFactory;

/**
 *
 * @author vpc
 */
public class HNReturn extends HNode {
    private HNode expr;

    protected HNReturn() {
        super(HNNodeId.H_RETURN);
    }

    public HNReturn(HNode value, JToken startToken, JToken endToken) {
        this();
        setExpr(value);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public HNReturn setExpr(HNode expr) {
        this.expr=JNodeUtils.bind(this,expr,"expr");
        return this;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getExpr,this::setExpr);
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Arrays.asList(expr);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNReturn) {
            HNReturn o =(HNReturn) node;
            this.expr=JNodeUtils.bindCopy(this, copyFactory, o.expr);
        }
    }


    public HNode getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        if(expr==null){
            return "return";
        }else{
            return "return "+expr.toString();
        }
    }

}
