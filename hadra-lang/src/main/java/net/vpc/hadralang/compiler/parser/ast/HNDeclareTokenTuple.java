package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.vpc.common.jeep.JNodeCopyFactory;
import net.vpc.common.jeep.util.JNodeUtils;

public class HNDeclareTokenTuple extends HNDeclareTokenTupleItem implements HNDeclare {
    private HNDeclareTokenTupleItem[] items;
    private JToken[] separators;
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

    public JToken[] getSeparators() {
        return separators;
    }

    public HNDeclareTokenTuple setSeparators(JToken[] separators) {
        this.separators = separators;
        return this;
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
            this.separators = o.separators;
        }
    }


    @Override
    public List<JNode> childrenNodes() {
        List<JNode> li=new ArrayList<>();
        li.addAll(Arrays.asList(this.items));
        return li;
    }
}
