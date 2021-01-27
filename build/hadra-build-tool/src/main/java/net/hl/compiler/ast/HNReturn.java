/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.JToken;
import net.thevpc.jeep.JNodeFindAndReplace;
import net.thevpc.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;
import net.thevpc.jeep.JNodeCopyFactory;

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
