/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.hadralang.compiler.core.HTokenId;
import net.vpc.hadralang.stdlib.Branch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vpc
 */
public class HNTryCatch extends HNode {

    private HNode resource;
    private HNode body;
    private List<CatchBranch> catches = new ArrayList<CatchBranch>();
    private HNode finallyBranch;
    private boolean expressionMode;
    public HNTryCatch() {
        super(HNNodeId.H_SWITCH);
    }

    public HNTryCatch(JToken token) {
        this();
        setStartToken(token);
    }

    public void addCatch(CatchBranch branch) {
        this.catches.add((CatchBranch) JNodeUtils.bind(this,branch, "catches", catches.size()));
    }

    public List<CatchBranch> getCatches() {
        return catches;
    }

    public HNTryCatch setCases(List<CatchBranch> cases) {
        this.catches = JNodeUtils.bind(this,cases, "catches");
        return this;
    }

    public HNode getFinallyBranch() {
        return finallyBranch;
    }

    public void setFinallyBranch(HNode result) {
        finallyBranch=JNodeUtils.bind(this,result, "finallyBranch");
    }

    public HNode getBody() {
        return body;
    }

    public void setBody(HNode result) {
        body=JNodeUtils.bind(this,result, "body");
    }

    public HNode getResource() {
        return resource;
    }

    public HNTryCatch setResource(HNode resource) {
        this.resource=JNodeUtils.bind(this,resource, "resource");
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("try");
        if(resource!=null){
            sb.append("(");
            sb.append(resource);
            sb.append(")");
        }
        if(expressionMode){
            sb.append("->");
            sb.append(body);
        }else{
            sb.append("\n");
            sb.append(body);
        }
        for (CatchBranch switchBranch : catches) {
            sb.append("\n");
            sb.append(switchBranch);
        }
        if (finallyBranch != null) {
            sb.append("\nfinally ");
            sb.append(finallyBranch.toString());
        }
        sb.append("\n}");
        return sb.toString();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNTryCatch) {
            HNTryCatch o = (HNTryCatch) node;
            this.resource = JNodeUtils.bindCopy(this, copyFactory, o.resource);
            this.body = JNodeUtils.bindCopy(this, copyFactory, o.body);
            this.catches = JNodeUtils.bindCopy(this, copyFactory, o.catches);
            this.finallyBranch = JNodeUtils.bindCopy(this, copyFactory, o.finallyBranch);
        }
    }


    @Override
    public List<JNode> childrenNodes() {
        List<JNode> li = new ArrayList<>();
        li.add(resource);
        li.add(resource);
        li.addAll(catches);
        li.add(finallyBranch);
        return li;
    }

    public HNode[] getExitPoints() {
        List<HNode> all = new ArrayList<>();
        if (this.body != null) {
            all.addAll(Arrays.asList(((HNode) this.body).getExitPoints()));
        }
        for (CatchBranch aCase : catches) {
            if (aCase.doNode != null) {
                all.addAll(Arrays.asList(((HNode) aCase.doNode).getExitPoints()));
            }
        }
        return all.toArray(new HNode[0]);
    }

    public boolean isExpressionMode() {
        return expressionMode;
    }

    public HNTryCatch setExpressionMode(boolean expressionMode) {
        this.expressionMode = expressionMode;
        return this;
    }

    public static class CatchBranch extends HNode {

        protected HNTypeToken[] exceptionTypes;
        protected HNDeclareTokenIdentifier identifier;
        protected HNode doNode;
        protected boolean exprMode;

        public CatchBranch(HNTypeToken[] exceptionTypes,HNDeclareTokenIdentifier identifier,
                           HNode doNode,boolean exprMode,JToken startToken,JToken endToken,JToken[] separators) {
            super(HNNodeId.H_CATCH);
            setExceptionTypes(exceptionTypes);
            setIdentifier(identifier);
            setDoNode(doNode);
            setStartToken(startToken);
            setEndToken(endToken);
            setSeparators(separators);
            setExprMode(exprMode);
        }

        public boolean isExprMode() {
            return exprMode;
        }

        public CatchBranch setExprMode(boolean exprMode) {
            this.exprMode = exprMode;
            return this;
        }

        public HNTypeToken[] getExceptionTypes() {
            return exceptionTypes;
        }

        public CatchBranch setExceptionTypes(HNTypeToken[] exceptionTypes) {
            this.exceptionTypes=JNodeUtils.bind(this,exceptionTypes, "exceptionTypes");
            return this;
        }

        public HNDeclareTokenIdentifier getIdentifier() {
            return identifier;
        }

        public CatchBranch setIdentifier(HNDeclareTokenIdentifier identifier) {
            this.identifier=JNodeUtils.bind(this,identifier, "identifier");
            return this;
        }

        public JToken getOp() {
            for (JToken separator : getSeparators()) {
                if(separator.id()== HTokenId.MINUS_GT){
                    return separator;
                }
            }
            return null;
        }

        public JToken getCatchToken() {
            return startToken();
        }

        public HNode getDoNode() {
            return doNode;
        }

        public CatchBranch setDoNode(HNode doNode) {
            this.doNode=JNodeUtils.bind(this,doNode, "do");
            return this;
        }

        @Override
        public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
            super.copyFrom(node,copyFactory);
            if (node instanceof CatchBranch) {
                CatchBranch o = (CatchBranch) node;
                this.exceptionTypes = JNodeUtils.bindCopy(this, copyFactory, o.exceptionTypes,HNTypeToken.class);
                this.identifier = JNodeUtils.bindCopy(this, copyFactory, o.identifier);
                this.doNode = JNodeUtils.bindCopy(this, copyFactory, o.doNode);
            }
        }

        @Override
        public List<JNode> childrenNodes() {
            List<JNode> list=new ArrayList<>();
            list.addAll(exceptionTypes==null? Collections.emptyList() : Arrays.asList(exceptionTypes));
            list.add(identifier);
            list.add(doNode);
            return list;
        }
    }

}