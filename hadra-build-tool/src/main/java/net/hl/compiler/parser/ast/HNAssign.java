/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.hl.compiler.core.elements.HNElement;
import net.hl.compiler.core.elements.HNElementField;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNAssign extends HNode {
    private JToken op;
    public HNode left;
    private HNode right;
    public AssignType assignType;
    public HNode[] tupleSubAssignments;

    public HNAssign() {
        super(HNNodeId.H_ASSIGN);
    }

    public HNAssign(HNode left, JToken op, HNode right, JToken startToken, JToken endToken) {
        this();
        this.op = op;
        setLeft(left);
        left.setUserObject("AssignLeftNode");
        setRight(right);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public boolean isStatic(){
        HNode f = getLeft();
        if(f.isSetUserObject("StaticLHS")){
            return true;
        }
        switch (f.id()){
            case H_IDENTIFIER:{
                HNElement e = f.getElement();
                switch (e.getKind()){
                    case FIELD:{
                        return ((HNElementField)e).getField().isStatic();
                    }
                }
                return false;
            }
        }
        HNElement e = f.getElement();
        switch (e.getKind()){
            case FIELD:{
                return ((HNElementField)e).getField().isStatic();
            }
        }
        return false;
    }

    public JToken getOp() {
        return op;
    }

    public AssignType getAssignType() {
        return assignType;
    }

    public HNode getLeft() {
        return left;
    }

    public HNAssign setLeft(HNode left) {
        this.left=JNodeUtils.bind(this,left,"left");
        left.setUserObject("AssignLeftNode");
        return this;
    }

    public HNode getRight() {
        return right;
    }

    public HNAssign setRight(HNode right) {
        this.right=JNodeUtils.bind(this,right,"right");
        return this;
    }

    @Override
    public String toString() {
        return left + "=" + right;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getLeft,this::setLeft);
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getRight,this::setRight);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNAssign) {
            HNAssign o = (HNAssign) node;
            this.left = JNodeUtils.bindCopy(this, copyFactory, o.left);
            this.right = JNodeUtils.bindCopy(this, copyFactory, o.right);
            this.assignType = (o.assignType);
        }
    }

    @Override
    public void visit(JNodeVisitor visitor) {
        visitor.startVisit(this);
        visitNext(visitor, this.left);
        visitNext(visitor, this.right);
        visitor.endVisit(this);
    }
    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(left, right);
    }
    public HNode[] getTupleSubAssignments() {
        return tupleSubAssignments;
    }


    public enum AssignType {
        VAR,
        FIELD,
        ARRAY,
        TUPLE,
    }

}
