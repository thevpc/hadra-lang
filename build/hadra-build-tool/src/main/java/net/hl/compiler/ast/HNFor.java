/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.util.JeepUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;

import java.util.ArrayList;
import java.util.List;
import net.vpc.common.jeep.JNodeCopyFactory;

/**
 * @author vpc
 */
public class HNFor extends HNode {

    private List<HNode> initExprs = new ArrayList<>();
    private List<HNode> incs = new ArrayList<>();
    private HNode filter = null;

    private HNode body;
    private boolean iteratorType;
    private String label;
    private boolean expressionMode;


    public HNFor() {
        super(HNNodeId.H_FOR);
    }

    public HNFor(JToken token) {
        super(HNNodeId.H_FOR);
        setStartToken(token);
    }

    public boolean isIteratorType() {
        return iteratorType;
    }

    public HNFor setIteratorType(boolean iteratorType) {
        this.iteratorType = iteratorType;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public HNFor setLabel(String label) {
        this.label = label;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(label!=null){
            sb.append(label).append(": ");
        }
        sb.append("for(");
        for (int i1 = 0; i1 < initExprs.size(); i1++) {
            if (i1 > 0) {
                sb.append(",");
            }
            sb.append(initExprs.get(i1));
        }
        sb.append(";");
        if (filter != null) {
            sb.append(filter.toString());
        }
        if (!iteratorType || incs.size() > 0) {
            sb.append(";");
            for (int i = 0; i < incs.size(); i++) {
                HNode inc = incs.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(inc.toString());
            }
        }
        sb.append(")");
        sb.append(JeepUtils.indent(body == null ? "" : body.toString()));
        return sb.toString();
    }

    public void addInit(HNode init) {
        initExprs.add(JNodeUtils.bind(this,init,"initExprs",initExprs.size()));
    }

    public void addInc(HNode inc) {
        incs.add(JNodeUtils.bind(this,inc,"incs",incs.size()));
    }

    public List<HNode> getInitExprs() {
        return initExprs;
    }

    public HNFor setInitExprs(List<HNode> initExprs) {
        this.initExprs = JNodeUtils.bind(this,initExprs,"initExprs");
        return this;
    }

    public List<HNode> getIncs() {
        return incs;
    }

    public HNFor setIncs(List<HNode> incs) {
        this.incs = JNodeUtils.bind(this,incs,"incs");
        return this;
    }

    public HNode getFilter() {
        return filter;
    }

    public HNFor setFilter(HNode filter) {
        this.filter=JNodeUtils.bind(this,filter,"filter");
        return this;
    }

    public HNode getBody() {
        return body;
    }

    public HNFor setBody(HNode body) {
        this.body=JNodeUtils.bind(this,body,"body");
        return this;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getIncs());
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getInitExprs());
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getFilter,this::setFilter);
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getBody,this::setBody);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNFor) {
            HNFor o = (HNFor) node;
            this.incs = JNodeUtils.bindCopy(this, copyFactory, o.incs);
            this.filter = JNodeUtils.bindCopy(this, copyFactory, o.filter);
            this.body = JNodeUtils.bindCopy(this, copyFactory, o.body);
            this.initExprs = JNodeUtils.bindCopy(this, copyFactory, o.initExprs);
            this.iteratorType = o.iteratorType;
            this.label = o.label;
            this.expressionMode = o.expressionMode;
        }
    }



    @Override
    public List<JNode> getChildrenNodes() {
        List<JNode> li=new ArrayList<>();
        li.addAll(initExprs);
        li.add(this.filter);
        li.addAll(this.incs);
        li.add(this.body);
        return li;
    }

    public HNode[] getExitPoints() {
        HNode bloc = getBody();
        if (bloc != null) {
            return ((HNode) bloc).getExitPoints();
        }
        return super.getExitPoints();
    }

    public boolean isExpressionMode() {
        return expressionMode;
    }

    public HNFor setExpressionMode(boolean expressionMode) {
        this.expressionMode = expressionMode;
        return this;
    }
}
