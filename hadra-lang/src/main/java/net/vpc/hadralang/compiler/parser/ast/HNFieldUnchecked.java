//package net.vpc.hadralang.compiler.parser.ast;
//
//import net.vpc.common.jeep.*;
//import net.vpc.common.jeep.JNodeFindAndReplace;
//import net.vpc.common.jeep.util.JNodeUtils;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class HNFieldUnchecked extends HNode {
//    private JToken op;
//    private JNode fieldName;
//    private JNode instanceNode;
//    private HNFieldUnchecked() {
//        super(HNNodeId.H_FIELD_UNCHECKED);
//    }
//
//    public HNFieldUnchecked(JNode instanceNode, JToken op, JNode field, JToken startToken, JToken endToken) {
//        super(HNNodeId.H_FIELD);
//        this.op = op;
//        this.fieldName = field;
//        this.instanceNode = instanceNode;
//        this.setStartToken(token);
//        this.setEndToken(endToken);
//    }
//
//    public JToken getOp() {
//        return op;
//    }
//
//    @Override
//    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
//        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getFieldName,this::setFieldName);
//        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getInstanceNode,this::setInstanceNode);
//    }
//
//    @Override
//    public void visit(JNodeVisitor visitor) {
//        visitor.startVisit(this);
//        visitNext(visitor,this.instanceNode);
//        visitNext(visitor,this.fieldName);
//        visitor.endVisit(this);
//    }
//
//    @Override
//    public List<JNode> childrenNodes() {
//        return Arrays.asList(instanceNode,fieldName);
//    }
//
//    public HNFieldUnchecked setInstanceNode(JNode instanceNode) {
//        this.instanceNode = instanceNode;
//        return this;
//    }
//
//    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
//        super.copyFrom(node,copyFactory);
//        if (node instanceof HNFieldUnchecked) {
//            HNFieldUnchecked o =(HNFieldUnchecked) node;
//            this.instanceNode=bindCopy(o.instanceNode);
//            this.fieldName =bindCopy(o.fieldName);
//        }
//    }
//
//    @Override
//    public JNode copy() {
//        HNFieldUnchecked n=new HNFieldUnchecked();
//        n.copyFrom(this);
//        return n;
//    }
//    public JNode getInstanceNode() {
//        return instanceNode;
//    }
//
//    @Override
//    public String toString() {
//        return instanceNode+".?"+fieldName;
//    }
//
//    public JNode getFieldName() {
//        return fieldName;
//    }
//
//    public HNFieldUnchecked setFieldName(JNode fieldName) {
//        this.fieldName = fieldName;
//        return this;
//    }
//}
