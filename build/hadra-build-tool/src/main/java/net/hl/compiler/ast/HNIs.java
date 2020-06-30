/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNIs extends HNode implements HNDeclare,HNDeclareTokenHolder {
    private HNTypeToken identifierTypeName;
    private HNode base;
    private HNDeclareTokenIdentifier identifierToken;
    private HNIs() {
        super(HNNodeId.H_IS);
    }
    public HNIs(HNTypeToken identifierTypeName, HNode base, HNDeclareTokenIdentifier identifierToken, JToken startToken, JToken endToken) {
        super(HNNodeId.H_IS);
        setIdentifierTypeName(identifierTypeName);
        setBase(base);
        this.identifierToken = identifierToken;
        setIdentifierToken(identifierToken);
        setStartToken(startToken);
        setEndToken(endToken);
    }
    @Override
    public HNDeclareToken getDeclareIdentifierTokenBase() {
        return getIdentifierToken();
    }

    public HNIs setIdentifierToken(HNDeclareTokenIdentifier identifierToken) {
        this.identifierToken=JNodeUtils.bind(this,identifierToken,"identifierToken");
        return this;
    }

    public HNDeclareTokenIdentifier getIdentifierToken() {
        return identifierToken;
    }


    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getBase,this::setBase);
    }

//    @Override
    public JType getIdentifierType() {
        return identifierTypeName.getTypeVal();
    }

    public HNTypeToken getIdentifierTypeName() {
        return identifierTypeName;
    }

    public HNIs setIdentifierTypeName(HNTypeToken identifierTypeName) {
        this.identifierTypeName = identifierTypeName;
        return this;
    }

    public HNode getBase() {
        return base;
    }

    public HNIs setBase(HNode base) {
        this.base=JNodeUtils.bind(this,base,"base");
        return this;
    }

    @Override
    public String toString() {
        return
                base+" is "+ getIdentifierTypeName()
                +(identifierToken ==null?"":(" "+ identifierToken))
                ;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNIs) {
            HNIs o = (HNIs) node;
            this.base = JNodeUtils.bindCopy(this, copyFactory, o.base);
            this.identifierTypeName = JNodeUtils.bindCopy(this, copyFactory, o.identifierTypeName);
            this.identifierToken = JNodeUtils.bindCopy(this, copyFactory, o.identifierToken);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(base,identifierTypeName,identifierToken);
    }

//    public static class FindAndReplaceIsNode implements JNodeFindAndReplace {
//        private final HNIs node;
//
//        public FindAndReplaceIsNode(HNIs node) {
//            this.node = node;
//        }
//
//        @Override
//        public boolean accept(JNode other) {
//            //okkay
//            return HUtils.isIdNode(node.getIdentifierName(),other);
//        }
//
//        @Override
//        public JNode replace(JNode other) {
//            return new HNCast(
//                    node.identifierTypeName,
//                    ((HNode) node.getBase()).copy(),
//                    null,node.startToken(),
//                    node.endToken()
//            );
//        }
//    }
}
