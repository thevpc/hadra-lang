/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.parser.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.ArrayList;
import java.util.List;
import net.vpc.common.jeep.JNodeCopyFactory;

/**
 * @author vpc
 */
public class HNBracketsPostfix extends HNode {

    private HNode left;
    private List<HNode> right;
    private JToken rightStartToken;
    private List<JToken> rightSeparators;

    private HNBracketsPostfix() {
        super(HNNodeId.H_BRACKETS_POSTFIX);
    }
    public HNBracketsPostfix(HNode left, List<HNode> right, JToken startToken,JToken rightStartToken,List<JToken> rightSeparators, JToken endToken) {
        this();
        this.rightStartToken=rightStartToken;
        this.rightSeparators=rightSeparators;
        setLeft(left);
        setRight(right);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public JToken getRightStartToken() {
        return rightStartToken;
    }

    public List<JToken> getRightSeparators() {
        return rightSeparators;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getLeft,this::setLeft);
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getRight());
    }


    @Override
    public List<JNode> childrenNodes() {
        List<JNode> list=new ArrayList<>();
        list.add(left);
        list.addAll(right);
        return list;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNBracketsPostfix) {
            HNBracketsPostfix o =(HNBracketsPostfix) node;
            this.left =JNodeUtils.bindCopy(this, copyFactory, o.left);
            this.right =JNodeUtils.bindCopy(this, copyFactory, o.right);
            this.rightStartToken =o.rightStartToken;
            this.rightSeparators =new ArrayList<>(o.rightSeparators);
        }
    }


    public HNode getLeft() {
        return left;
    }

    public void setLeft(HNode left) {
        this.left=JNodeUtils.bind(this,left,"left");
    }

    public void setRight(List<HNode> right) {
        this.right = JNodeUtils.bind(this,right,"right");
    }

    public List<HNode> getRight() {
        return right;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(left ==null){
            sb.append("?");
        }else{
            sb.append(left.toString());
        }
        sb.append("[");
        List<HNode> items = this.right;
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(items.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

}
