/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.JNode;
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
public class HNBrackets extends HNode {

    private HNode[] items;
    private List<JToken> separators;

    private HNBrackets() {
        super(HNNodeId.H_BRACKETS);
    }
    public HNBrackets(HNode[] items, JToken startToken, List<JToken> separators, JToken endToken) {
        this();
        setItems(items);
        if(startToken==null && items.length>0){
            startToken=items[0].startToken();
        }
        this.separators = separators;
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public List<JToken> getSeparators() {
        return separators;
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
            this.separators = new ArrayList<>(o.separators);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(items);
    }
}
