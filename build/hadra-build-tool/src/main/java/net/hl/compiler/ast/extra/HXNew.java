/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast.extra;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.util.ListBuilder;
import net.hl.compiler.ast.HNNodeId;
import net.hl.compiler.ast.HNode;

import java.util.List;

/**
 * @author vpc
 */
public class HXNew extends HNode {

    private HNode base;
    private HNode[] args;
    public HXNew() {
        super(HNNodeId.X_NEW);
    }

    public HXNew(HNode base, HNode[] args) {
        this();
        setBase(base);
        setArgs(args);
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return new ListBuilder<>().add(base).addAll(args).toList();
    }


    public HNode[] getArgs() {
        return args;
    }

    public HXNew setArgs(HNode[] args) {
        this.args = JNodeUtils.bind(this,args,"args");
        return this;
    }

    public HNode getBase() {
        return base;
    }

    public HXNew setBase(HNode base) {
        this.base = JNodeUtils.bind(this,base,"base");
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("new ").append(base).append("(");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            String sargi = args[i].toString();
            sb.append(sargi);
        }
        sb.append(")");
        return sb.toString();
    }

    public JNode get(int index) {
        return args[index];
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HXNew) {
            HXNew o = (HXNew) node;
            this.base = JNodeUtils.bindCopy(this, copyFactory, o.base);
            this.args = JNodeUtils.bindCopy(this, copyFactory, o.args,HNode.class);
        }
    }

}
