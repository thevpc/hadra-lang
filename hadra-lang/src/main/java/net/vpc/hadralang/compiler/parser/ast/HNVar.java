/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author vpc
 */
public class HNVar extends HNode {
    private JToken name;
    private HNTypeToken varTypeName;
    private HNVar() {
        super(HNNodeId.H_VAR);
    }

    public HNVar(JToken name, HNTypeToken type, JToken token) {
        this();
        this.name=name;
        this.varTypeName=bind(type,"varTypeName");
        setStartToken(token);
        setEndToken(token);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }

//    public HNVar(JToken name, JTypeName typeName, JToken token) {
//        super(HNode.H_VAR_GET);
//        this.name=name;
//        this.varTypeName=typeName;
//        setStartToken(token);
//    }

    @Override
    public List<JNode> childrenNodes() {
        return Collections.emptyList();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNVar) {
            HNVar o =(HNVar) node;
//            this.XXX=bindCopy(o.XXX);
            this.name=(o.name);
            this.varTypeName= JNodeUtils.bindCopy(this, copyFactory, o.varTypeName);
        }
    }


    public HNTypeToken getVarTypeName() {
        return varTypeName;
    }

    public HNVar setVarTypeName(HNTypeToken varTypeName) {
        this.varTypeName = varTypeName;
        return this;
    }


    public String getName() {
        return name.image;
    }

    public JToken getNameToken() {
        return name;
    }

//    @Override
//    public Object evaluate(JContext context) {
//        return context.vars().getValue(getName());
//    }

    public String toString() {
        return getName();
    }
}
