/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JNodeVisitor;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;
import net.hl.compiler.core.elements.HNElementExpr;

import java.time.temporal.Temporal;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import net.vpc.common.jeep.JNodeCopyFactory;

/**
 * @author vpc
 */
public class HNLiteral extends HNode {
    private Object value;
    private HNode associatedExpression;

    private HNLiteral() {
        super(HNNodeId.H_LITERAL);
    }

    public HNLiteral(Object value, JToken token) {
        this(value,null,token);
    }

    public HNLiteral(Object value, JType type, JToken token) {
        super(HNNodeId.H_LITERAL);
        this.value = value;
        setElement(type==null?null:new HNElementExpr(type));
        setStartToken(token);
        setEndToken(token);
    }

    public HNLiteral setValue(Object value) {
        this.value = value;
        return this;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getAssociatedExpression,this::setAssociatedExpression);
    }

    public HNode getAssociatedExpression() {
        return associatedExpression;
    }

    public HNLiteral setAssociatedExpression(HNode associatedExpression) {
        this.associatedExpression = associatedExpression;
        return this;
    }
    //    public HNLiteral(Object value) {
//        super(HNode.H_LITERAL);
//        this.value = value;
//    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value instanceof String) {
            StringBuilder sb = new StringBuilder("\"");
            sb.append(JToken.escapeString(value.toString()));
            sb.append("\"");
            return String.valueOf(sb);
        }
        if (value instanceof Temporal) {
            Temporal t = (Temporal) value;
            StringBuilder sb = new StringBuilder("d\"");
            sb.append(t.toString());
            sb.append("\"");
            return String.valueOf(sb);
        }
        if (value instanceof Pattern) {
            Pattern t = (Pattern) value;
            StringBuilder sb = new StringBuilder("p\"");
            sb.append(JToken.escapeString(t.toString()));
            sb.append("\"");
            return String.valueOf(sb);
        }
        return String.valueOf(value);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNLiteral) {
            HNLiteral o = (HNLiteral) node;
            this.value = (o.value);
        }
    }

    @Override
    public void visit(JNodeVisitor visitor) {
        visitor.startVisit(this);
        visitor.endVisit(this);
    }

    @Override
    public List<JNode> childrenNodes() {
        return Collections.emptyList();
    }
}
