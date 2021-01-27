/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.JToken;
import net.thevpc.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;
import net.thevpc.jeep.JNodeCopyFactory;

/**
 * @author vpc
 */
public class HNCast extends HNode {
    private HNode typeNode;
    private HNode base;
    private HNCast() {
        super(HNNodeId.H_CAST);
    }

    public HNCast(HNode typeNode, HNode base, JToken[] separators,JToken startToken, JToken endToken) {
        this();
        setTypeNode(typeNode);
        setBase(base);
        setStartToken(startToken);
        setEndToken(endToken);
        setSeparators(separators);
    }

    public HNode getTypeNode() {
        return typeNode;
    }

    public HNCast setTypeNode(HNode typeNode) {
        this.typeNode = JNodeUtils.bind(this,typeNode,"items");
        return this;
    }

    public HNode getBase() {
        return base;
    }

    public HNCast setBase(HNode base) {
        this.base = JNodeUtils.bind(this,base,"base");
        return this;
    }

    @Override
    public String toString() {
        return "(" + getTypeNode() + ")" + base;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNCast) {
            HNCast o = (HNCast) node;
            this.typeNode = JNodeUtils.bindCopy(this, copyFactory, o.typeNode);
            this.base = JNodeUtils.bindCopy(this, copyFactory, o.base);
        }
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Arrays.asList(typeNode, base);
    }
}
