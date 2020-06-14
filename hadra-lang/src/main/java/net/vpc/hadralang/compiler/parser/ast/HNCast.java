/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;
import net.vpc.common.jeep.JNodeCopyFactory;

/**
 * @author vpc
 */
public class HNCast extends HNode {
    private HNode typeNode;
    private HNode base;
    private JToken parsEndToken;

    private HNCast() {
        super(HNNodeId.H_CAST);
    }

    public HNCast(HNode typeNode, HNode base, JToken parsEndToken,JToken startToken, JToken endToken) {
        this();
        this.typeNode = typeNode;
        this.base = base;
        setStartToken(startToken);
        setEndToken(endToken);
        this.parsEndToken=parsEndToken;
    }

    public JToken getParsEndToken() {
        return parsEndToken;
    }
    //    public HNLiteral(Object value) {
//        super(HNode.H_LITERAL);
//        this.value = value;
//    }


    public HNode getTypeNode() {
        return typeNode;
    }

    public HNCast setTypeNode(HNode typeNode) {
        this.typeNode = typeNode;
        return this;
    }

    public HNode getBase() {
        return base;
    }

    public HNCast setBase(HNode base) {
        this.base = base;
        return this;
    }

    @Override
    public String toString() {
        return "(" + getTypeNode() + ")" + base;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getBase, this::setBase);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNCast) {
            HNCast o = (HNCast) node;
            this.typeNode = JNodeUtils.bindCopy(this, copyFactory, o.typeNode);
            this.base = JNodeUtils.bindCopy(this, copyFactory, o.base);
            this.parsEndToken = o.parsEndToken;
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(typeNode, base);
    }
}
