/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.JNodeCopyFactory;
import net.thevpc.jeep.JNodeFindAndReplace;
import net.thevpc.jeep.JToken;

import java.util.Collections;
import java.util.List;

/**
 * @author vpc
 */
public class HNStaticEval extends HNode {

    private JToken staticToken;
    private JToken dotToken;
    private JToken identifierToken;

    private HNStaticEval() {
        super(HNNodeId.H_STATIC_EVAL);
    }

    public HNStaticEval(JToken staticToken, JToken dotToken, JToken identifierToken, JToken startToken, JToken endToken) {
        this();
        this.staticToken = staticToken;
        this.dotToken = dotToken;
        this.identifierToken = identifierToken;
        setStartToken(startToken);
        setEndToken(endToken);
    }


    @Override
    public String toString() {
        return "static." +identifierToken.image;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNStaticEval) {
            HNStaticEval o = (HNStaticEval) node;
            this.staticToken = o.staticToken;
            this.dotToken = o.dotToken;
            this.identifierToken = o.identifierToken;
        }
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Collections.emptyList();
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }

    public JToken getStaticToken() {
        return staticToken;
    }

    public HNStaticEval setStaticToken(JToken staticToken) {
        this.staticToken = staticToken;
        return this;
    }

    public JToken getDotToken() {
        return dotToken;
    }

    public HNStaticEval setDotToken(JToken dotToken) {
        this.dotToken = dotToken;
        return this;
    }

    public JToken getIdentifierToken() {
        return identifierToken;
    }

    public HNStaticEval setIdentifierToken(JToken identifierToken) {
        this.identifierToken = identifierToken;
        return this;
    }
}
