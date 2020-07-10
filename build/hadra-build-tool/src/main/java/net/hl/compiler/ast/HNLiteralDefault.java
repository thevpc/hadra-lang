/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;
import net.hl.compiler.core.elements.HNElementExpr;
import net.hl.compiler.utils.HNodeUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author vpc
 */
public class HNLiteralDefault extends HNode {
    private HNTypeToken typeName;
    private HNLiteralDefault() {
        super(HNNodeId.H_LITERAL_DEFAULT);
    }
    public HNLiteralDefault(HNTypeToken typeName, JToken token) {
        super(HNNodeId.H_LITERAL_DEFAULT);
        setTypeName(typeName);
        setStartToken(token);
        setEndToken(token);
    }

    public HNLiteralDefault(JType type) {
        super(HNNodeId.H_LITERAL_DEFAULT);
        setElement(new HNElementExpr(type));
        this.typeName=type==null?null: HNodeUtils.createTypeToken(type);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNLiteralDefault) {
            HNLiteralDefault o =(HNLiteralDefault) node;
//            this.XXX=bindCopy(o.XXX);
            this.typeName= JNodeUtils.bindCopy(this, copyFactory, o.typeName);
        }
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }


    @Override
    public List<JNode> getChildrenNodes() {
        return Collections.emptyList();
    }

    public HNTypeToken getTypeNameToken() {
        return typeName;
    }

    public JTypeName getTypeName() {
        return typeName.getTypename();
    }

    public HNLiteralDefault setTypeName(HNTypeToken typeName) {
        this.typeName=JNodeUtils.bind(this,typeName,"typeName");
        return this;
    }
    //    public HNLiteralDefault setTypeName(JTypeName typeName) {
//        this.typeName = typeName;
//        return this;
//    }

    @Override
    public String toString() {
        return "default("+getElement().getTypePattern()+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HNLiteralDefault that = (HNLiteralDefault) o;
        return Objects.equals(typeName, that.typeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeName);
    }
}
