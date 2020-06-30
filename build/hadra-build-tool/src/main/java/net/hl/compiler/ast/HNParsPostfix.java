/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.nodes.AbstractJNode;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
public class HNParsPostfix extends HNode {

    private HNode left;
    private List<HNode> right;
    private JToken rightStartToken;
    private List<JToken> rightSeparators;

    private HNParsPostfix() {
        super(HNNodeId.H_PARS_POSTFIX);
    }
    public HNParsPostfix(HNode left, List<HNode> right, JToken startToken,JToken rightStartToken,List<JToken> rightSeparators, JToken endToken) {
        this();
        this.rightSeparators=rightSeparators;
        this.rightStartToken=rightStartToken;
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
        List<JNode> li=new ArrayList<>();
        li.add(left);
        li.addAll(right);
        return li;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNParsPostfix) {
            HNParsPostfix o =(HNParsPostfix) node;
            this.left =JNodeUtils.bindCopy(this, copyFactory, o.left);
            this.right =JNodeUtils.bindCopy(this, copyFactory, o.right);
            this.rightSeparators =new ArrayList<>(o.rightSeparators);
            this.rightStartToken =o.rightStartToken;
        }
    }

    @Override
    public AbstractJNode parentNode(JNode parentNode) {
        return super.parentNode(parentNode);
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
        sb.append("(");
        List<HNode> items = this.right;
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(items.get(i));
        }
        sb.append(")");
        return sb.toString();
    }

}
