/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author vpc
 */
public class HNTuple extends HNode {

    private HNode[] items;

    public HNTuple() {
        super(HNNodeId.H_TUPLE);
    }
    public HNTuple(HNode[] items, JToken startToken, JToken[] separators,JToken endToken) {
        this();
        setItems(items);
        if(startToken==null && items.length>0){
            startToken=items[0].startToken();
        }
        setSeparators(separators);
        setStartToken(startToken);
        setEndToken(endToken);
    }


    public HNTuple setItems(HNode[] items) {
        this.items = JNodeUtils.bind(this,items,"items");
        return this;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getItems());
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(items);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNTuple) {
            HNTuple o =(HNTuple) node;
            this.items=JNodeUtils.bindCopy(this, copyFactory, o.items,HNode.class);
        }
    }

    public HNode[] getItems() {
        return items;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("(");
        for (int i = 0; i < items.length; i++) {
            HNode item = items[i];
            if(i>0) {
                sb.append(",");
            }
            sb.append(item.toString());
        }
        sb.append(")");
        return sb.toString();
    }

}
