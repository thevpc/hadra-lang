/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNDotClass extends HNode {

    private HNTypeToken typeRefName;
    private JToken dotToken;
    protected HNDotClass() {
        super(HNNodeId.H_DOT_CLASS);
    }

    public HNDotClass(HNTypeToken typeRefName, JToken dotToken,JToken startToken, JToken endToken) {
        this();
        this.dotToken=dotToken;
        setTypeRefName(typeRefName);
        setStartToken(startToken);
        setEndToken(startToken);
    }
    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }

    public JToken getDotToken() {
        return dotToken;
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Arrays.asList(typeRefName);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNDotClass) {
            HNDotClass o =(HNDotClass) node;
            this.typeRefName= (HNTypeToken) JNodeUtils.bindCopy(this, copyFactory, o.typeRefName);
            this.dotToken=o.dotToken;
        }
    }

    public HNTypeToken getTypeRefName() {
        return typeRefName;
    }

    public HNDotClass setTypeRefName(HNTypeToken typeRefName) {
        this.typeRefName=JNodeUtils.bind(this,typeRefName,"typeRefName");
        return this;
    }


    @Override
    public String toString() {
        return String.valueOf(typeRefName)+".class";
    }

}
