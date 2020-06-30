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
 * @author vpc
 */
public class HNBrackets extends HNode {

    private HNode[] items;

    private HNBrackets() {
        super(HNNodeId.H_BRACKETS);
    }
    public HNBrackets(HNode[] items, JToken startToken, JToken[] separators, JToken endToken) {
        this();
        setItems(items);
        if(startToken==null && items.length>0){
            startToken=items[0].startToken();
        }
        setStartToken(startToken);
        setEndToken(endToken);
        setSeparators(separators);
    }

    public HNBrackets setItems(HNode[] items) {
        this.items = JNodeUtils.bind(this,items,"items");
        return this;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getItems());
    }

    public HNode[] getItems() {
        return items;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("[");
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            HNode item = items[i];
            sb.append(item.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNBrackets) {
            HNBrackets o = (HNBrackets) node;
            this.items = JNodeUtils.bindCopy(this, copyFactory, o.items,HNode.class);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(items);
    }
}