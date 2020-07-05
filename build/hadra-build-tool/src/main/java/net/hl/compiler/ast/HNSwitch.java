/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.ListBuilder;

import java.util.*;

/**
 * @author vpc
 */
public class HNSwitch extends HNode {

    private HNode expr;
    private List<SwitchBranch> cases = new ArrayList<SwitchBranch>();
    private HNode elseNode;
    private boolean expressionMode;
    private SwitchType switchType = null;

    public HNSwitch() {
        super(HNNodeId.H_SWITCH);
    }

    public HNSwitch(JToken token) {
        this();
        setStartToken(token);
    }

    public void add(SwitchBranch branch) {
        switchType
                = branch instanceof SwitchCase ? SwitchType.CASE
                        : branch instanceof SwitchIf ? SwitchType.IF
                                : branch instanceof SwitchIs ? SwitchType.IS
                                        : SwitchType.CASE;
        this.cases.add((SwitchBranch) JNodeUtils.bind(this,branch, "cases", cases.size()));
    }

    public List<SwitchBranch> getCases() {
        return cases;
    }

    public HNSwitch setCases(List<SwitchBranch> cases) {
        this.cases = JNodeUtils.bind(this,cases, "cases");
        return this;
    }

    public HNode getElseNode() {
        return elseNode;
    }

    public void setElse(HNode result) {
        elseNode=JNodeUtils.bind(this,result, "else");
    }

    public HNode getExpr() {
        return expr;
    }

    public HNSwitch setExpr(HNode expr) {
        this.expr=JNodeUtils.bind(this,expr, "expr");
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("switch(").append(expr).append("){");
        for (SwitchBranch switchBranch : cases) {
            if (switchBranch instanceof SwitchCase) {
                sb.append("\n  case ");
                List<HNode> whenNodes = ((SwitchCase) switchBranch).whenNodes;
                for (int i = 0; i < whenNodes.size(); i++) {
                    HNode aCase = whenNodes.get(i);
                    if (i > 0) {
                        sb.append("|");
                    }
                    sb.append(aCase.toString());
                }
                sb.append(": ");
                if(switchBranch.doNode!=null) {
                    sb.append(switchBranch.doNode.toString());
                }
            } else if (switchBranch instanceof SwitchIf) {
                sb.append("\n  if ");
                HNode whenNode = ((SwitchIf) switchBranch).whenNode;
                sb.append(whenNode.toString());
                sb.append(": ");
                sb.append(switchBranch.doNode.toString());
            } else if (switchBranch instanceof SwitchIs) {
                sb.append("\n  is ");
                List<HNTypeToken> whenTypes = ((SwitchIs) switchBranch).whenTypes;
                for (int i = 0; i < whenTypes.size(); i++) {
                    HNTypeToken whenType = whenTypes.get(i);
                    if (i > 0) {
                        sb.append("|");
                    }
                    sb.append(whenType);
                }
                HNDeclareTokenIdentifier whenVar = ((SwitchIs) switchBranch).identifierToken;
                if (whenVar != null) {
                    sb.append(" ");
                    sb.append(whenVar);
                }
                sb.append(": ");
                sb.append(switchBranch.doNode.toString());
            }

        }
        if (elseNode != null) {
            sb.append("\ndefault: ");
            sb.append(elseNode.toString());
        }
        sb.append("\n}");
        return sb.toString();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNSwitch) {
            HNSwitch o = (HNSwitch) node;
            this.expr = JNodeUtils.bindCopy(this, copyFactory, o.expr);
            this.elseNode = JNodeUtils.bindCopy(this, copyFactory, o.elseNode);
            this.cases = JNodeUtils.bindCopy(this, copyFactory, o.cases);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        List<JNode> li = new ArrayList<>();
        li.add(expr);
        li.addAll(cases);
        li.add(elseNode);
        return li;
    }

    public HNode[] getExitPoints() {
        List<HNode> all = new ArrayList<>();
        for (SwitchBranch aCase : cases) {
            if (aCase.doNode != null) {
                all.addAll(Arrays.asList(((HNode) aCase.doNode).getExitPoints()));
            }
        }
        if (this.elseNode != null) {
            all.addAll(Arrays.asList(((HNode) this.elseNode).getExitPoints()));
        }
        return all.toArray(new HNode[0]);
    }

    public boolean isExpressionMode() {
        return expressionMode;
    }

    public HNSwitch setExpressionMode(boolean expressionMode) {
        this.expressionMode = expressionMode;
        return this;
    }

    public SwitchType getSwitchType() {
        return switchType;
    }

    public HNSwitch setSwitchType(SwitchType switchType) {
        this.switchType = switchType;
        return this;
    }

    public enum SwitchType {
        CASE,
        IF,
        IS
    }

    public abstract static class SwitchBranch extends HNode {

        protected HNode doNode;
        protected JInvokablePrefilled impl;
        protected JToken op;

        public SwitchBranch(HNNodeId id) {
            super(id);
        }

        public HNode getDoNode() {
            return doNode;
        }

        public SwitchBranch setDoNode(HNode doNode) {
            this.doNode=JNodeUtils.bind(this,doNode, "do");
            return this;
        }

        @Override
        public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
            super.copyFrom(node,copyFactory);
            if (node instanceof SwitchBranch) {
                SwitchBranch o = (SwitchBranch) node;
                this.doNode = JNodeUtils.bindCopy(this, copyFactory, o.doNode);
                this.impl = o.impl;
                this.op = o.op;
            }
        }

        public JInvokablePrefilled getImpl() {
            return impl;
        }

        public SwitchBranch setImpl(JInvokablePrefilled impl) {
            this.impl = impl;
            return this;
        }

        public JToken getOp() {
            return op;
        }

    }

    public static class SwitchCase extends SwitchBranch {

        private List<HNode> whenNodes;
        private List<Object> simplifiedWhenNodes = new ArrayList<>();

        public SwitchCase() {
            super(HNNodeId.H_SWITCH_CASE);
        }
        public SwitchCase(List<HNode> whenNodes, JToken op, HNode doNode, JToken startToken, JToken endToken) {
            super(HNNodeId.H_SWITCH_CASE);
            this.setWhenNodes(whenNodes);
            this.setDoNode(doNode);
            this.op = op;
            setStartToken(startToken);
            setEndToken(endToken);
        }

        @Override
        protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
            JNodeUtils.findAndReplaceNext(this, findAndReplace, this.getWhenNodes());
            JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getDoNode, this::setDoNode);
        }

        public List<HNode> getWhenNodes() {
            return whenNodes;
        }

        public SwitchBranch setWhenNodes(List<HNode> whenNodes) {
            this.whenNodes = JNodeUtils.bind(this,whenNodes, "when");
            return this;
        }

        @Override
        public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
            super.copyFrom(node,copyFactory);
            if (node instanceof SwitchCase) {
                SwitchCase o = (SwitchCase) node;
                this.whenNodes = JNodeUtils.bindCopy(this, copyFactory, o.whenNodes);
                this.simplifiedWhenNodes = new ArrayList<>(simplifiedWhenNodes);
            }
        }

        @Override
        public List<JNode> childrenNodes() {
            List<JNode> li = new ArrayList<>();
            li.addAll(whenNodes);
            li.add(doNode);
            return li;
        }
    }

    public static class SwitchIf extends SwitchBranch {

        private HNode whenNode;

        private SwitchIf() {
            super(HNNodeId.H_SWITCH_IF);
        }

        public SwitchIf(HNode whenNode, JToken op, HNode doNode, JToken startToken, JToken endToken) {
            super(HNNodeId.H_SWITCH_IF);
            this.whenNode=JNodeUtils.bind(this,whenNode, "when");
            this.doNode=JNodeUtils.bind(this,doNode, "do");
            this.op = op;
            setStartToken(startToken);
            setEndToken(endToken);
        }

        @Override
        protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
            JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getWhenNode, this::setWhenNode);
            JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getDoNode, this::setDoNode);
        }

        public HNode getWhenNode() {
            return whenNode;
        }

        public SwitchIf setWhenNode(HNode whenNode) {
            this.whenNode=JNodeUtils.bind(this,whenNode, "when");
            return this;
        }

        @Override
        public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
            super.copyFrom(node,copyFactory);
            if (node instanceof SwitchIf) {
                SwitchIf o = (SwitchIf) node;
                this.whenNode = JNodeUtils.bindCopy(this, copyFactory, o.whenNode);
            }
        }

        @Override
        public List<JNode> childrenNodes() {
            List<JNode> li = new ArrayList<>();
            li.add(whenNode);
            li.add(doNode);
            return li;
        }

    }

    public static class SwitchIs extends SwitchBranch implements HNDeclare, HNDeclareTokenHolder {

        private List<HNTypeToken> whenTypes;
//        private JType whenIdentifierType;
        /**
         * whenVar should be null when more than one type is provided
         */
        private HNDeclareTokenIdentifier identifierToken;

        private SwitchIs() {
            super(HNNodeId.H_SWITCH_IS);
        }

        public SwitchIs(List<HNTypeToken> whenTypes, HNDeclareTokenIdentifier identifierToken, JToken op, HNode doNode, JToken token, JToken endtoken) {
            super(HNNodeId.H_SWITCH_IS);
            this.setWhenTypes(whenTypes);
            this.setIdentifierToken(identifierToken);
            setDoNode(doNode);
            this.op = op;
            setStartToken(token);
            setEndToken(endtoken);
        }

        @Override
        public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
            super.copyFrom(node,copyFactory);
            if (node instanceof SwitchIs) {
                SwitchIs o = (SwitchIs) node;
                this.whenTypes = JNodeUtils.bindCopy(this, copyFactory, o.whenTypes);
                this.identifierToken = JNodeUtils.bindCopy(this, copyFactory, o.identifierToken);
            }
        }

        public void setIdentifierToken(HNDeclareTokenIdentifier identifierToken) {
            this.identifierToken=JNodeUtils.bind(this,identifierToken, "identifierToken");
        }

        public void setWhenTypes(List<HNTypeToken> whenTypes) {
            this.whenTypes = JNodeUtils.bind(this,whenTypes, "when");
        }

        @Override
        public HNDeclareToken getDeclareIdentifierTokenBase() {
            return getIdentifierToken();
        }

//        public SwitchIs setWhenIdentifierType(JType whenIdentifierType) {
//            this.whenIdentifierType = whenIdentifierType;
//            return this;
//        }
        public List<HNTypeToken> getWhenTypes() {
            return whenTypes;
        }

//        public JType getWhenIdentifierType() {
//            return whenIdentifierType;
//        }
        public HNDeclareTokenIdentifier getIdentifierToken() {
            return identifierToken;
        }

//        @Override
//        public JToken[] getIdentifierTokens() {
//            return new JToken[]{identifierToken};
//        }
        @Override
        protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
            JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getDoNode, this::setDoNode);
        }

//        @Override
//        public String[] getIdentifierNames() {
//            return new String[]{identifierToken==null?null:identifierToken.image};
//        }
        public SwitchIs setWhenType(List<HNTypeToken> whenTypes) {
            this.whenTypes = JNodeUtils.bind(this,whenTypes, "when");
            return this;
        }

//        public String getIdentifierNameCheck() {
//            if(getIdentifierNames().length!=1){
//                throw new JShouldNeverHappenException();
//            }
//            return getIdentifierNames()[0];
//        }
//        @Override
//        public JType getIdentifierType() {
//            return whenIdentifierType;
//        }
        @Override
        public List<JNode> childrenNodes() {
            return new ListBuilder<JNode>().setSkipNulls(true)
                    .addAll(whenTypes)
                    .add(identifierToken)
                    .add(doNode)
                    .toList();
        }
    }
}
