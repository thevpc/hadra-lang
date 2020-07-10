package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.hl.compiler.core.elements.HNElementExpr;

import java.util.Collections;
import java.util.List;

public class HNThis extends HNode {
    private HNThis() {
        super(HNNodeId.H_THIS);
    }
    public HNThis(JType type, JToken token) {
        this();
        setElement(new HNElementExpr(type));
        setStartToken(token);
        setEndToken(token);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Collections.emptyList();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNThis) {
            HNThis o =(HNThis) node;
//            this.XXX=bindCopy(o.XXX);
        }
    }

    @Override
    public String toString() {
        return "this";
    }
}
