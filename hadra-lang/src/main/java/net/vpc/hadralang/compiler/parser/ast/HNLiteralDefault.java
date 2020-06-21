/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.hadralang.compiler.core.elements.HNElementExpr;
import net.vpc.hadralang.compiler.utils.HNodeUtils;

import java.util.Collections;
import java.util.List;

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
    public List<JNode> childrenNodes() {
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

}
