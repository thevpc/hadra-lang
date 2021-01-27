package net.hl.compiler.ast;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.JToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.thevpc.jeep.JNodeCopyFactory;
import net.thevpc.jeep.util.JNodeUtils;

public class HNDeclareTokenTuple extends HNDeclareTokenTupleItem implements HNDeclare {
    private HNDeclareTokenTupleItem[] items;
    private HNDeclareTokenTuple() {
        super(HNNodeId.H_DECLARE_TOKEN_TUPLE);
    }

    public HNDeclareTokenTuple(HNDeclareTokenTupleItem[] identifiers, JToken[] separators, JToken startToken, JToken endToken) {
        this();
        setItems(identifiers);
        setStartToken(startToken);
        setEndToken(endToken);
        setSeparators(separators);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
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
        sb.append(")");
        return sb.toString();
    }

    public HNDeclareTokenTupleItem[] getItems() {
        return items;
    }

    public HNDeclareTokenTuple setItems(HNDeclareTokenTupleItem[] identifiers) {
        this.items = JNodeUtils.bind(this,identifiers,"items");
        return this;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNDeclareTokenTuple) {
            HNDeclareTokenTuple o = (HNDeclareTokenTuple) node;
            this.items = JNodeUtils.bindCopy(this, copyFactory, o.items,HNDeclareTokenTupleItem.class);
        }
    }


    @Override
    public List<JNode> getChildrenNodes() {
        List<JNode> li=new ArrayList<>();
        li.addAll(Arrays.asList(this.items));
        return li;
    }
}
