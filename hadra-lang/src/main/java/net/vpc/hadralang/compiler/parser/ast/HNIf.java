/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.util.JeepUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.hadralang.stdlib.Branch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author vpc
 */
public class HNIf extends HNode {

    private List<WhenDoBranchNode> branches = new ArrayList<WhenDoBranchNode>();
    private HNode elseNode;

    public HNIf() {
        super(HNNodeId.H_IF);
    }

    public HNIf(JToken token) {
        super(HNNodeId.H_IF);
        setStartToken(token);
    }

    public HNIf add(HNode condition, HNode thenResult) {
        WhenDoBranchNode n = JNodeUtils.bind(this,new WhenDoBranchNode(condition, thenResult, null),
                "branches",this.branches.size());
        this.branches.add(n);
        return this;
    }

    public void setElse(HNode result) {
        elseNode=JNodeUtils.bind(this,result,"else");
    }

    public HNode getElseNode() {
        return elseNode;
    }

    public List<WhenDoBranchNode> getBranches() {
        return branches;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (WhenDoBranchNode ifBranch : branches) {
            if (index == 0) {
                sb.append("if(");
                sb.append(ifBranch.whenNode.toString());
                sb.append("){\n");
                sb.append(JeepUtils.indent(ifBranch.doNode.toString()));
                sb.append("\n}");
            } else {
                sb.append(" else if(");
                sb.append(ifBranch.whenNode.toString());
                sb.append("){\n");
                sb.append(JeepUtils.indent(ifBranch.doNode.toString()));
                sb.append("\n}");
            }
            index++;
        }
        if (elseNode != null) {
            sb.append(" else {\n");
            sb.append(JeepUtils.indent(elseNode.toString()));
            sb.append("\n}");
        }
        return sb.toString();
    }

//    private HNode inlineIsVar(HNode cond, List<IsVarReplacer> allReplacements) {
//        for (IsVarReplacer r : allReplacements) {
//            cond=((HNode)cond).findAndReplace(r);
//        }
//        if (cond instanceof HNIs && ((HNIs) cond).getIdentifierName() != null) {
//            allReplacements.add(new IsVarReplacer(((HNIs) cond).getIdentifierName(),
//                    (HNode) ((HNIs) cond).getBase(),
//                    ((HNIs) cond).getIdentifierType(),
//                    ((HNIs) cond).startToken()
//            ));
//        }
//        if(cond instanceof HNOpBinaryCall ) {
//            if(((HNOpBinaryCall) cond).getName().equals("&&")){
//                HNOpBinaryCall binop = (HNOpBinaryCall) cond;
//                HNode leftNode = inlineIsVar(binop.getLeftNode(), allReplacements);
//                binop.setLeftNode(leftNode);
//                HNode rightNode = inlineIsVar(binop.getRightNode(), allReplacements);
//                binop.setRightNode(rightNode);
//            }else if(((HNOpBinaryCall) cond).getName().equals("||")){
//                allReplacements=new ArrayList<>();
//                HNOpBinaryCall binop = (HNOpBinaryCall) cond;
//                HNode leftNode = inlineIsVar(binop.getLeftNode(), allReplacements);
//                binop.setLeftNode(leftNode);
//                HNode rightNode = inlineIsVar(binop.getRightNode(), allReplacements);
//                binop.setRightNode(rightNode);
//            }
//        }
//        return cond;
//    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNIf) {
            HNIf o = (HNIf) node;
            this.elseNode = JNodeUtils.bindCopy(this, copyFactory, o.elseNode);
            this.branches = JNodeUtils.bindCopy(this, copyFactory, o.branches);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        List<JNode> li=new ArrayList<>();
        li.addAll(branches);
        li.add(elseNode);
        return li;
    }

    public HNode[] getExitPoints() {
        List<HNode> all = new ArrayList<>();
        for (WhenDoBranchNode branch : branches) {
            if (branch.doNode != null) {
                all.addAll(Arrays.asList(((HNode) branch.doNode).getExitPoints()));
            }
        }
        if (this.elseNode != null) {
            all.addAll(Arrays.asList(((HNode) this.elseNode).getExitPoints()));
        }
        return all.toArray(new HNode[0]);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this.getBranches());
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getElseNode, this::setElse);
    }

    public static class IsVarReplacer implements JNodeFindAndReplace {
        private JToken name;
        private HNode expr;
        private HNode castExpr;
        private JType type;
        private JToken placement;

        public IsVarReplacer(JToken name, HNode expr, JType type, JToken placement) {
            this.name = name;
            this.expr = expr;
            this.type = type;
            this.placement = placement;
            this.castExpr = new HNCast(
                    new HNTypeToken(type, expr.startToken()),
                    expr, null,expr.startToken(), expr.endToken()
            );
        }

        public JToken getName() {
            return name;
        }

        public HNode getExpr() {
            return expr;
        }

        public HNode getCastExpr() {
            return castExpr;
        }

        public JType getType() {
            return type;
        }

        public JToken getPlacement() {
            return placement;
        }

        @Override
        public boolean accept(JNode node) {
            return node instanceof HNIdentifier && ((HNIdentifier) node).getName().equals(name);
        }

        @Override
        public JNode replace(JNode node) {
            return castExpr.copy();
        }

        @Override
        public boolean isReplaceFirst() {
            return true;
        }
    }

    public static class WhenDoBranchNode extends HNode {
        private HNode whenNode;
        private HNode doNode;
        private JInvokablePrefilled impl;
        private List<HNDeclareIdentifier> idDeclarations=new ArrayList<>();

        private WhenDoBranchNode() {
            super(HNNodeId.H_IF_WHEN_DO);
        }

        public WhenDoBranchNode(HNode whenNode, HNode doNode, JInvokablePrefilled impl) {
            this();
            this.whenNode=JNodeUtils.bind(this,whenNode,"when");
            this.doNode=JNodeUtils.bind(this,doNode,"do");
            this.impl = impl;
            setStartToken(whenNode.startToken());
            if(doNode==null /*.startToken().startCharacterNumber<0*/){
                setEndToken(whenNode.endToken());
            }else {
                setEndToken(doNode.endToken());
            }
        }

        public List<HNDeclareIdentifier> getIdDeclarations() {
            return idDeclarations;
        }

        public WhenDoBranchNode addIdDeclaration(HNDeclareIdentifier id) {
            this.idDeclarations.add(JNodeUtils.bind(this,id,"idDeclarations",idDeclarations.size()));
            return this;
        }

        public WhenDoBranchNode setIdDeclarations(List<HNDeclareIdentifier> idDeclarations) {
            this.idDeclarations = JNodeUtils.bind(this,idDeclarations,"idDeclarations");
            return this;
        }

        public JInvokablePrefilled getImpl() {
            return impl;
        }

        public WhenDoBranchNode setImpl(JInvokablePrefilled impl) {
            this.impl = impl;
            return this;
        }

        public HNode getWhenNode() {
            return whenNode;
        }

        public WhenDoBranchNode setWhenNode(HNode whenNode) {
            this.whenNode=JNodeUtils.bind(this,whenNode,"when");
            return this;
        }

        public HNode getDoNode() {
            return doNode;
        }

        public WhenDoBranchNode setDoNode(HNode doNode) {
            this.doNode=JNodeUtils.bind(this,doNode,"do");
            return this;
        }

        public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
            super.copyFrom(node,copyFactory);
            if (node instanceof WhenDoBranchNode) {
                WhenDoBranchNode o = (WhenDoBranchNode) node;
                this.whenNode = JNodeUtils.bindCopy(this, copyFactory, o.whenNode);
                this.doNode = JNodeUtils.bindCopy(this, copyFactory, o.doNode);
                this.idDeclarations = (List<HNDeclareIdentifier>) JNodeUtils.bindCopy(this, copyFactory, o.idDeclarations);
                this.impl = o.impl;
            }
        }


        @Override
        public List<JNode> childrenNodes() {
            List<JNode> li=new ArrayList<>();
            li.add(whenNode);
            li.addAll(idDeclarations);
            li.add(doNode);
            return li;
        }
        @Override
        protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
            JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getWhenNode, this::setWhenNode);
            JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getDoNode, this::setDoNode);
            JNodeUtils.findAndReplaceNext(this, findAndReplace, this.getIdDeclarations());
        }
    }

    private static class BranchFromWhenDoBranchNode implements Branch {
        private WhenDoBranchNode node;
        private JInvokeContext context;

        public BranchFromWhenDoBranchNode(WhenDoBranchNode node, JInvokeContext context) {
            this.node = node;
            this.context = context;
        }

        @Override
        public Supplier condition() {
            return new Supplier() {
                @Override
                public Object get() {
                    return context.evaluate(node.whenNode);
                }
            };
        }

        @Override
        public Supplier result() {
            return new Supplier() {
                @Override
                public Object get() {
                    return context.evaluate(node.doNode);
                }
            };
        }
    }

    public static class JEvaluableFromBranchArray implements JEvaluable {
        private final JType branchesArrayType;
        private final List<WhenDoBranchNode> cond;

        public JEvaluableFromBranchArray(JType branchesArrayType, List<WhenDoBranchNode> cond) {
            this.branchesArrayType = branchesArrayType;
            this.cond = cond;
        }

        @Override
        public JType type() {
            return branchesArrayType;
        }

        @Override
        public Object evaluate(JInvokeContext context) {
            List<Branch> condRest = new ArrayList<>();
            for (int i = 0; i < cond.size(); i++) {
                WhenDoBranchNode c = cond.get(i);
                condRest.add(new BranchFromWhenDoBranchNode(c, context));
            }
            return condRest.toArray(new Branch[0]);
        }
    }

    public static class JEvaluableFromSupplier implements JEvaluable {
        private final JType elseType;
        private final HNode elseNode;

        public JEvaluableFromSupplier(HNode elseNode,JType elseType) {
            this.elseNode = elseNode;
            this.elseType = elseType;
        }

        @Override
        public JType type() {
            return elseType;
        }

        @Override
        public Object evaluate(JInvokeContext context) {
            return new Supplier() {
                @Override
                public Object get() {
                    return context.evaluate(elseNode);
                }
            };
        }
    }
}
