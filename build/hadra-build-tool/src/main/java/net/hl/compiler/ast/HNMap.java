/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JNodeCopyFactory;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNMap extends HNode {

    private HNMapEntry[] entries;

    private HNMap() {
        super(HNNodeId.H_MAP);
    }

    public HNMap(HNMapEntry[] entries, JToken startToken, JToken endToken) {
        this();
        setEntries(entries);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public HNMapEntry[] getEntries() {
        return entries;
    }

    public HNMap setEntries(HNMapEntry[] entries) {
        this.entries=JNodeUtils.bind(this,entries,"entries");
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("{");
        for (int i = 0; i < entries.length; i++) {
            if(i>0){
                sb.append(", ");
            }
            sb.append(entries[i]);
        }
        sb.append("}");
        return sb.toString();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNMap) {
            HNMap o = (HNMap) node;
            this.entries = JNodeUtils.bindCopy(this, copyFactory, o.entries,HNMapEntry.class);
        }
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Arrays.asList(entries);
    }


    public static class HNMapEntry extends HNode{
        private JToken op;
        private HNode left;
        private HNode right;

        private HNMapEntry() {
            super(HNNodeId.H_MAP_ENTRY);
        }

        public HNMapEntry(HNode left, JToken op, HNode right, JToken startToken, JToken endToken) {
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

        public HNMapEntry setLeft(HNode left) {
            this.left=JNodeUtils.bind(this,left,"left");
            return this;
        }

        public HNMapEntry setRight(HNode right) {
            this.right=JNodeUtils.bind(this,right,"right");
            return this;
        }


        public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
            super.copyFrom(node,copyFactory);
            if (node instanceof HNMapEntry) {
                HNMapEntry o = (HNMapEntry) node;
                this.right = JNodeUtils.bindCopy(this, copyFactory, o.right);
                this.left = JNodeUtils.bindCopy(this, copyFactory, o.left);
                this.op = o.op;
            }
        }

        @Override
        public List<JNode> getChildrenNodes() {
            return Arrays.asList(left, right);
        }


    }
}
