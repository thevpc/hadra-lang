/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.thevpc.jeep.*;

import java.util.Collections;
import java.util.List;

/**
 * @author vpc
 */
public class HNIdentifier extends HNode {
    private JToken name;

    private HNIdentifier() {
        super(HNNodeId.H_IDENTIFIER);
    }

    public HNIdentifier(JToken name) {
        super(HNNodeId.H_IDENTIFIER);
        this.name = name;
        setStartToken(name);
        setEndToken(name);
    }

    public JToken getNameToken() {
        return name;
    }

    public String getName() {
        return name.sval;
    }

    public String toString() {
        return getName();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNIdentifier) {
            HNIdentifier o = (HNIdentifier) node;
//            this.XXX=bindCopy(o.XXX);
            this.name = (o.name);
        }
    }


    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Collections.emptyList();
    }

}
