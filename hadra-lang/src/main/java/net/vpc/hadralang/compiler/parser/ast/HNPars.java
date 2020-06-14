/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JNodeVisitor;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.vpc.common.jeep.JNodeCopyFactory;

/**
 * @author vpc
 */
public class HNPars extends HNode {

    private HNode[] items;
    private List<JToken> separators;

    public HNPars() {
        super(HNNodeId.H_PARS);
    }

    public HNPars(HNode[] items, JToken startToken, List<JToken> separators, JToken endToken) {
        this();
        setItems(items);
        if (startToken == null && items.length > 0) {
            startToken = items[0].startToken();
        }
        this.separators = separators;
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public List<JToken> getSeparators() {
        return separators;
    }

    public HNPars setItems(HNode[] items) {
        this.items = JNodeUtils.bind(this,items,"items");
        return this;
    }

    public HNode[] getItems() {
        return items;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("(");
        for (int i = 0; i < items.length; i++) {
            JNode item = items[i];
            if (i > 0) {
                sb.append(",");
            }
            sb.append(item.toString());
        }
        sb.append(")");
        return sb.toString();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNPars) {
            HNPars o = (HNPars) node;
            this.items = JNodeUtils.bindCopy(this, copyFactory, o.items,HNode.class);
            this.separators = new ArrayList<>(o.separators);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(items);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this.getItems());
    }

}