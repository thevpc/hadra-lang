/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNOpUnaryCall extends HNode {

    private JToken name;
    private HNode expr;
    private JInvokablePrefilled impl;
    private boolean prefixOperator;

    protected HNOpUnaryCall() {
        super(HNNodeId.H_OP_UNARY);
    }

    public HNOpUnaryCall(JToken name, HNode expr, boolean prefixOperator, JToken startToken, JToken endToken) {
        this();
        this.name = name;
        this.prefixOperator = prefixOperator;
        setExpr(expr);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public boolean isPostfixOperator() {
        return !prefixOperator;
    }

    public boolean isPrefixOperator() {
        return prefixOperator;
    }

    public JInvokablePrefilled impl() {
        return impl;
    }

    public HNOpUnaryCall setImpl(JInvokablePrefilled implFunction) {
        this.impl = implFunction;
        return this;
    }

    public HNode getExpr() {
        return expr;
    }

    public HNOpUnaryCall setExpr(HNode expr) {
        this.expr=JNodeUtils.bind(this,expr,"expr");
        return this;
    }

    public String getName() {
        return name.image;
    }

    public JToken getNameToken() {
        return name;
    }

    @Override
    public String toString() {
        if (isPostfixOperator()) {
            return JNodeUtils.toPar(expr) + getName();
        } else {
            return getName() + JNodeUtils.toPar(expr);
        }
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNOpUnaryCall) {
            HNOpUnaryCall o = (HNOpUnaryCall) node;
            this.expr = JNodeUtils.bindCopy(this, copyFactory, o.expr);
            this.impl = (o.impl);
            this.name = (o.name);
            this.prefixOperator = (o.prefixOperator);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(expr);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getExpr, this::setExpr);
    }
}
