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
 * @author vpc
 */
public class HNOpDot extends HNode {

    private JToken op;
    private HNode left;
    private HNode right;
    private boolean nullableInstance;
    private boolean uncheckedMember;

    private HNOpDot() {
        super(HNNodeId.H_OP_DOT);
    }

    public HNOpDot(HNode left, JToken op, HNode right, JToken startToken, JToken endToken) {
        this();
        this.op = op;
        setLeft(left);
        setRight(right);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public JToken getOp() {
        return op;
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

    public HNOpDot setLeft(HNode left) {
        this.left=JNodeUtils.bind(this,left,"left");
        return this;
    }

    public HNOpDot setRight(HNode right) {
        this.right=JNodeUtils.bind(this,right,"right");
        return this;
    }

    @Override
    public String toString() {
        return left.toString() +
                "." + right;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNOpDot) {
            HNOpDot o = (HNOpDot) node;
            this.right = JNodeUtils.bindCopy(this, copyFactory, o.right);
            this.left = JNodeUtils.bindCopy(this, copyFactory, o.left);
            this.op = o.op;
            this.nullableInstance = o.nullableInstance;
            this.uncheckedMember = o.uncheckedMember;
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(left, right);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getLeft, this::setLeft);
    }

    public boolean isNullableInstance() {
        return nullableInstance;
    }

    public HNOpDot setNullableInstance(boolean nullableInstance) {
        this.nullableInstance = nullableInstance;
        return this;
    }

    public boolean isUncheckedMember() {
        return uncheckedMember;
    }

    public HNOpDot setUncheckedMember(boolean uncheckedMember) {
        this.uncheckedMember = uncheckedMember;
        return this;
    }
}
