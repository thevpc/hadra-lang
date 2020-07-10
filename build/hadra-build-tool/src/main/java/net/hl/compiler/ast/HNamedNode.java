package net.hl.compiler.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JNodeCopyFactory;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;

public class HNamedNode extends HNode implements Cloneable{
    private JToken name;
    private JToken sep;
    private HNode node;

    public HNamedNode(JToken name, JToken sep,HNode node) {
        super(HNNodeId.H_NAMED);
        this.name = name;
        this.sep = sep;
        setNode(node);
    }

    public JToken getName() {
        return name;
    }

    public void setName(JToken name) {
        this.name = name;
    }

    public HNode getNode() {
        return node;
    }

    public void setNode(HNode node) {
        this.node = JNodeUtils.bind(this, node,"name");
    }

    @Override
    public HNamedNode clone(){
        try {
            return (HNamedNode) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException("Not Cloneable");
        }
    }

    public void copyFrom(JNode node, JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNamedNode) {
            HNamedNode o = (HNamedNode) node;
            this.name = o.name;
            this.sep = o.sep;
            this.node = JNodeUtils.bindCopy(this, copyFactory, o.node);
        }
    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Arrays.asList(node);
    }

}
