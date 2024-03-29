package net.hl.compiler.ast;

import net.thevpc.jeep.*;
import net.thevpc.jeep.JNodeFindAndReplace;
import net.hl.compiler.core.elements.HNElementExpr;

import java.util.Collections;
import java.util.List;

public class HNSuper extends HNode {
    private JType thisType;
    private HNSuper() {
        super(HNNodeId.H_SUPER);
    }
    public HNSuper(JType thisType, JToken token) {
        this();
        this.thisType=thisType;
        if(thisType!=null){
            setElement(new HNElementExpr(thisType.getSuperType()));
        }
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
        if (node instanceof HNSuper) {
            HNSuper o =(HNSuper) node;
//            this.XXX=bindCopy(o.XXX);
            this.thisType=(o.thisType);
        }
    }

    @Override
    public String toString() {
        return "super";
    }
}
