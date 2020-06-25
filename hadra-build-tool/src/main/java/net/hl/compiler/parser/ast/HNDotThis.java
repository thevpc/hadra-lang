/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNDotThis extends HNode {

    private HNTypeToken typeRefName;
    private JType typeRef;
    protected HNDotThis() {
        super(HNNodeId.H_DOT_THIS);
    }
    private JToken dotToken;
    public HNDotThis(HNTypeToken typeRefName, JToken dotToken,JToken startToken, JToken endToken) {
        this();
        this.dotToken=dotToken;
        setTypeRefName(typeRefName);
        setStartToken(startToken);
        setEndToken(endToken);
    }
    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }

    public JToken getDotToken() {
        return dotToken;
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(typeRefName);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNDotThis) {
            HNDotThis o =(HNDotThis) node;
            this.typeRefName= JNodeUtils.bindCopy(this, copyFactory, o.typeRefName);
            this.dotToken=o.dotToken;
        }
    }

    public HNTypeToken getTypeRefName() {
        return typeRefName;
    }

    public HNDotThis setTypeRefName(HNTypeToken typeRefName) {
        this.typeRefName=JNodeUtils.bind(this,typeRefName,"typeRefName");
        return this;
    }


    @Override
    public String toString() {
        return String.valueOf(typeRefName)+".this";
    }

}
