/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;

import java.util.Arrays;
import java.util.List;

/**
 * x??y is equivalent to if(x!=default(typeof(x))) then x else y
 * @author vpc
 */
public class HNOpCoalesce extends HNode {

    private JToken op;
    private HNode left;
    private HNode right;

    private HNOpCoalesce() {
        super(HNNodeId.H_OP_COALESCE);
    }

    public HNOpCoalesce(HNode left, JToken op, HNode right, JToken startToken, JToken endToken) {
        this();
        this.op = op;
        this.setLeft(left);
        this.setRight(right);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public JToken getOp() {
        return op;
    }

    public HNOpCoalesce setLeft(HNode left) {
        this.left=JNodeUtils.bind(this,left,"left");
        return this;
    }

    public HNOpCoalesce setRight(HNode right) {
        this.right=JNodeUtils.bind(this,right,"right");
        return this;
    }

    public HNode getRight() {
        return right;
    }

    public HNode getLeft() {
        return left;
    }
//    @Override
//    public JType getType(JContext context) {
//        return context.types().forName(Object.class);
//    }

    @Override
    public String toString() {
        return "("+ left.toString() +
                "??" + right.toString()+")";
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getRight,this::setRight);
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getLeft,this::setLeft);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNOpCoalesce) {
            HNOpCoalesce o = (HNOpCoalesce) node;
            this.right = JNodeUtils.bindCopy(this, copyFactory, o.right);
            this.left = JNodeUtils.bindCopy(this, copyFactory, o.left);
        }
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Arrays.asList(left, right);
    }
}
