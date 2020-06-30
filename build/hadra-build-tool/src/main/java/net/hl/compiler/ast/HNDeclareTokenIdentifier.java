package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.nodes.AbstractJNode;
import net.hl.compiler.core.elements.HNElement;

import java.util.Collections;
import java.util.List;

public class HNDeclareTokenIdentifier extends HNDeclareTokenTupleItem implements HNDeclareTokenBase {
    private JToken identifierToken;

    private HNDeclareTokenIdentifier() {
        super(HNNodeId.H_DECLARE_TOKEN_IDENTIFIER);
    }

    public HNDeclareTokenIdentifier(JToken identifierToken) {
        this();
        setIdentifierToken(identifierToken);
        setStartToken(identifierToken);
        setEndToken(identifierToken);
    }


    public JToken getToken() {
        return identifierToken;
    }

    public HNDeclareTokenIdentifier setIdentifierToken(JToken identifierToken) {
        this.identifierToken = identifierToken;
        return this;
    }

    public String getName() {
        return identifierToken.sval;
    }

    public String toString() {
        return identifierToken.sval;
    }

    @Override
    public JType getIdentifierType() {
        HNElement e = getElement();
        if(e!=null) {
            return e.getType();
        }
        return null;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNDeclareTokenIdentifier) {
            HNDeclareTokenIdentifier o = (HNDeclareTokenIdentifier) node;
            this.identifierToken = o.identifierToken;
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Collections.emptyList();
    }

    @Override
    public AbstractJNode parentNode(JNode parentNode) {
        return super.parentNode(parentNode);
    }

//    @Override
//    public HNode setElement(HNElement element) {
//        //TODO remove extra check!
//        if(element instanceof HNElementField || element instanceof HNElementLocalVar) {
//            return super.setElement(element);
//        }else{
//            throw new JShouldNeverHappenException();
//        }
//    }

}
