/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author vpc
 */
public abstract class HNTokenSuite extends HNode {
    private List<JToken> tokens=new ArrayList<>();

    public HNTokenSuite(HNNodeId id) {
        super(id);
    }

    public void addToken(JToken token){
        if(startToken()==null){
            setStartToken(token);
        }
        setEndToken(token);
        tokens.add(token);
    }

    public List<JToken> getTokens() {
        return tokens;
    }

    public String getValue() {
        StringBuilder sb=new StringBuilder();
        for (JToken token : tokens) {
            sb.append(token.sval);
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb=new StringBuilder();
        for (JToken token : tokens) {
            sb.append(token.image);
        }
        return sb.toString();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNTokenSuite) {
            HNTokenSuite o = (HNTokenSuite) node;
//            this.XXX=bindCopy(o.XXX);
            this.tokens = new ArrayList<>(o.tokens);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Collections.emptyList();
    }
}
