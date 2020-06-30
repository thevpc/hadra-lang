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
public class HNBracketsPostfixLastIndex extends HNSemantic {

    private HNode base;
    private int pos;

    private HNBracketsPostfixLastIndex() {
        super(HNNodeId.H_BRACKETS_POSTFIX_LAST);
    }

    public HNBracketsPostfixLastIndex(HNode base, int pos, JToken token) {
        this();
        setBase(base);
        setPos(pos);
        setStartToken(token);
    }

    public HNode getBase() {
        return base;
    }

    public void setBase(HNode base) {
        this.base=JNodeUtils.bind(this,base,"base");
    }

    public int getPos() {
        return pos;
    }

    public HNBracketsPostfixLastIndex setPos(int pos) {
        this.pos = pos;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LengthOf(");
        if (base == null) {
            sb.append("?");
        } else {
            sb.append(base.toString());
        }
        sb.append(",");
        sb.append(pos);
        sb.append(")");
        return sb.toString();
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getBase,this::setBase);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNBracketsPostfixLastIndex) {
            HNBracketsPostfixLastIndex o = (HNBracketsPostfixLastIndex) node;
            this.base = JNodeUtils.bindCopy(this, copyFactory, o.base);
            this.pos = o.pos;
        }
    }


    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(base);
    }
}
