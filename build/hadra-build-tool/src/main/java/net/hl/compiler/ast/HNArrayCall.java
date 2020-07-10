package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class HNArrayCall extends HNode {
    private HNode[] indexNodes;
    private HNode arrayInstanceNode;
    private JType arrayType;
    private JType componentType;

    public HNArrayCall() {
        super(HNNodeId.H_ARRAY_CALL);
    }

    public HNArrayCall(HNode arrayInstanceNode, HNode[] indexNodes, JType arrayType, JType componentType, JToken startToken, JToken endToken) {
        this();
        this.setArrayInstanceNode(arrayInstanceNode);
        this.setIndexNodes(indexNodes);
        this.arrayType = arrayType;
        this.componentType = componentType;
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public HNode getArrayInstanceNode() {
        return arrayInstanceNode;
    }

    public HNArrayCall setArrayInstanceNode(HNode arrayInstanceNode) {
        this.arrayInstanceNode = JNodeUtils.bind(this,arrayInstanceNode,"arrayInstance");
        return this;
    }

    public HNode[] getIndexNodes() {
        return indexNodes;
    }

    public HNArrayCall setIndexNodes(HNode[] indexNodes) {
        this.indexNodes = JNodeUtils.bind(this,indexNodes,"indexes");
        return this;
    }

    public JType getArrayType() {
        return arrayType;
    }

    public HNArrayCall setArrayType(JType arrayType) {
        this.arrayType = arrayType;
        return this;
    }

    public JType getComponentType() {
        return componentType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(arrayInstanceNode.toString()).append("[");
        for (int i = 0; i < indexNodes.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(indexNodes[i].toString());
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getIndexNodes());
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getArrayInstanceNode,this::setArrayInstanceNode);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNArrayCall) {
            HNArrayCall o = (HNArrayCall) node;
            this.indexNodes = JNodeUtils.bindCopy(this, copyFactory, o.indexNodes,HNode.class);
            this.arrayInstanceNode = JNodeUtils.bindCopy(this, copyFactory, o.arrayInstanceNode);
            this.arrayType = o.arrayType;
            this.componentType = o.componentType;
        }
    }

    @Override
    public void visit(JNodeVisitor visitor) {
        visitor.startVisit(this);
        visitNext(visitor, this.indexNodes);
        visitNext(visitor, this.arrayInstanceNode);
        visitor.endVisit(this);
    }

    @Override
    public List<JNode> getChildrenNodes() {
        List<JNode> list=new ArrayList<>();
        list.addAll(Arrays.asList(indexNodes));
        list.add(arrayInstanceNode);
        return list;
    }
}
