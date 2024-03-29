package net.hl.compiler.ast;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.JToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.thevpc.jeep.JNodeCopyFactory;
import net.thevpc.jeep.JType;
import net.thevpc.jeep.util.JNodeUtils;

public class HNDeclareTokenList extends HNDeclareToken implements HNDeclare {
    private HNDeclareTokenIdentifier[] items;
    private HNDeclareTokenList() {
        super(HNNodeId.H_DECLARE_TOKEN_LIST);
    }

    public HNDeclareTokenList(HNDeclareTokenIdentifier[] identifiers, JToken startToken, JToken endToken) {
        this();
        setItems(identifiers);
        setStartToken(startToken);
        setEndToken(endToken);
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
//        sb.append("(");
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
//            if (identifier.getIdentifierType() != null) {
//                sb.append(identifier.getIdentifierType());
//            }
//            sb.append(" ");
            sb.append(items[i]);
        }
//        sb.append(")");
        return sb.toString();
    }

    public JType getIdentifierType() {
        if(items!=null && items.length>0){
            return items[0].getIdentifierType();
        }
        return null;
    }
    
    public HNDeclareTokenIdentifier[] getItems() {
        return items;
    }

    public HNDeclareTokenList setItems(HNDeclareTokenIdentifier[] identifiers) {
        this.items = (HNDeclareTokenIdentifier[]) JNodeUtils.bind(this,identifiers,"items");
        return this;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNDeclareTokenList) {
            HNDeclareTokenList o = (HNDeclareTokenList) node;
            this.items = JNodeUtils.bindCopy(this, copyFactory, o.items,HNDeclareTokenIdentifier.class);
        }
    }

    @Override
    public List<JNode> getChildrenNodes() {
        List<JNode> li=new ArrayList<>();
        li.addAll(Arrays.asList(this.items));
        return li;
    }
}
