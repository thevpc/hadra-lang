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
public class HNBraces extends HNode {

    private HNode[] items;

    private HNBraces() {
        super(HNNodeId.H_BRACES);
    }
    public HNBraces(HNode[] items, JToken startToken, JToken endToken) {
        this();
        setItems(items);
        setStartToken(startToken);
        setEndToken(endToken);
    }


    @Override
    public List<JNode> getChildrenNodes() {
        return Arrays.asList(this.items);
    }

    public HNode[] getItems() {
        return items;
    }

    public HNBraces setItems(HNode[] items) {
        this.items = JNodeUtils.bind(this,items,"items");
        return this;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getItems());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("{\n");
        for (int i = 0; i < items.length; i++) {
            if(i>0){
                sb.append(",");
            }
            HNode item = items[i];
            sb.append(item);
        }
        sb.append("\t}");
        return sb.toString();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNBraces) {
            HNBraces o = (HNBraces) node;
            this.items = JNodeUtils.bindCopy(this, copyFactory, o.items,HNode.class);
        }
    }

}
