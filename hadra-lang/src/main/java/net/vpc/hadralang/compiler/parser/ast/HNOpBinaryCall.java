/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNOpBinaryCall extends HNode {

    private JToken name;
    private HNode left;
    private HNode right;
    private JInvokablePrefilled impl;

    protected HNOpBinaryCall() {
        super(HNNodeId.H_OP_BINARY);
    }

    public HNOpBinaryCall(JToken name, HNode left, HNode right, JToken startToken, JToken endToken) {
        this();
        this.name = name;
        setLeft(left);
        setRight(right);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public JInvokablePrefilled getImpl() {
        return impl;
    }

    public HNOpBinaryCall setImpl(JInvokablePrefilled impl) {
        this.impl = impl;
        return this;
    }

    public HNode getLeft() {
        return left;
    }

    public HNOpBinaryCall setLeft(HNode left) {
        this.left=JNodeUtils.bind(this,left,"left");
        return this;
    }

    public HNode getRight() {
        return right;
    }

    public HNOpBinaryCall setRight(HNode right) {
        this.right=JNodeUtils.bind(this,right,"right");
        return this;
    }

    @Override
    public String toString() {
        return (left ==null?"<?>":JNodeUtils.toPar(left)) + getName()
                + (right ==null?"<?>":JNodeUtils.toPar(right));
    }

    public String getName() {
        return name.image;
    }

    public JToken getNameToken() {
        return name;
    }




    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getLeft,this::setLeft);
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getRight,this::setRight);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNOpBinaryCall) {
            HNOpBinaryCall o = (HNOpBinaryCall) node;
            this.name = (o.name);
            this.left = JNodeUtils.bindCopy(this, copyFactory, o.left);
            this.right = JNodeUtils.bindCopy(this, copyFactory, o.right);
            this.impl = (o.impl);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(left, right);
    }

}
