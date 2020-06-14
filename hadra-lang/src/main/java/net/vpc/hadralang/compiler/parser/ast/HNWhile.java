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

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author vpc
 */
public class HNWhile extends HNode {

    private HNode expr;
    private HNode block;
    private String label;

    public HNWhile() {
        super(HNNodeId.H_WHILE);
    }

    public HNWhile(JToken token) {
        this();
        setStartToken(token);
    }
    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getExpr,this::setExpr);
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getBlock,this::setBlock);
    }

    public HNode getExpr() {
        return expr;
    }

    public HNWhile setExpr(HNode expr) {
        this.expr=JNodeUtils.bind(this,expr,"expr");
        return this;
    }

    public HNode getBlock() {
        return block;
    }

    public HNWhile setBlock(HNode block) {
        this.block=JNodeUtils.bind(this,block,"block");
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("while(").append(expr).append("){\n");
        sb.append(JeepUtils.indent(block.toString()));
        sb.append("\n}");
        return sb.toString();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNWhile) {
            HNWhile o = (HNWhile) node;
            this.expr = JNodeUtils.bindCopy(this, copyFactory, o.expr);
            this.block = JNodeUtils.bindCopy(this, copyFactory, o.block);
            this.label = o.label;
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(expr,block);
    }

    public String getLabel() {
        return label;
    }

    public HNWhile setLabel(String label) {
        this.label = label;
        return this;
    }

    public HNode[] getExitPoints() {
        if (this.block != null) {
            return (((HNode) this.block).getExitPoints());
        }
        return super.getExitPoints();
    }

    public static class JEvaluableNodeSupplier implements JEvaluable {
        private final JType supplierType;
        private final HNode node;

        public JEvaluableNodeSupplier(JType supplierType, HNode node) {
            this.supplierType = supplierType;
            this.node = node;
        }

        @Override
        public JType type() {
            return supplierType;
        }

        @Override
        public Object evaluate(JInvokeContext context) {
            return (Supplier) () -> context.evaluate(node);
        }
    }

}
