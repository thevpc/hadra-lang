//package net.vpc.hadralang.compiler.parser.ast;
//
//import net.vpc.common.jeep.*;
//import net.vpc.common.jeep.impl.JArgumentTypes;
//import net.vpc.common.jeep.impl.functions.JSignature;
//import net.vpc.common.jeep.util.JeepUtils;
//import net.vpc.hadralang.compiler.core.invokables.HLJCompilerContext;
//import net.vpc.hadralang.compiler.utils.HUtils;
//
//public class JNodeHArraySet extends HNode {
//    JType arrayType;
//    JType componentType;
//    JNode valueNode;
//    JNode arrayInstanceNode;
//    private JNode[] indexNodes;
//
//    public JNodeHArraySet() {
//        super(HNode.H_ARRAY_SET);
//    }
//    public JNodeHArraySet(JNode arrayInstanceNode, JNode[] indexNodes, JNode valueNode, JType arrayType, JType componentType, JToken token) {
//        super(HNode.H_ARRAY_SET);
//        this.arrayInstanceNode=JNodeUtils.bind(this,arrayInstanceNode);
//        this.indexNodes=JNodeUtils.bind(this,indexNodes);
//        this.arrayType = arrayType;
//        this.valueNode = valueNode;
//        this.componentType = componentType;
//        setType(this.componentType);
//        setToken(token);
//    }
//
//    public JNode getArrayInstanceNode() {
//        return arrayInstanceNode;
//    }
//
//    public JNodeHArraySet setArrayInstanceNode(JNode arrayInstanceNode) {
//        this.arrayInstanceNode=JNodeUtils.bind(this,arrayInstanceNode);
//        return this;
//    }
//
//    public JNode[] getIndexNodes() {
//        return indexNodes;
//    }
//
//    public JNodeHArraySet setIndexNodes(JNode[] indexNodes) {
//        this.indexNodes=JNodeUtils.bind(this,indexNodes);
//        return this;
//    }
//
//    public JType getArrayType() {
//        return arrayType;
//    }
//
//    public JNodeHArraySet setArrayType(JType arrayType) {
//        this.arrayType = arrayType;
//        return this;
//    }
//
//    public JType getComponentType() {
//        return componentType;
//    }
//
//    public JNodeHArraySet setComponentType(JType componentType) {
//        this.componentType = componentType;
//        return this;
//    }
//
//    public JNode getValueNode() {
//        return valueNode;
//    }
//
//    public JNodeHArraySet setValueNode(JNode valueNode) {
//        this.valueNode=JNodeUtils.bind(this,valueNode);
//        return this;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb.append(getArrayInstanceNode().toString()).append("[");
//        sb.append(indexNodes.toString());
//        sb.append("]");
//        sb.append("=");
//        JNode value = getValueNode();
//        sb.append(value == null ? "null" : value.toString());
//        return sb.toString();
//    }
//
//    @Override
//    public JNode processCompilerStage(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        JNodeHArraySet node = (JNodeHArraySet) compilerContext.node();
//        compilerContext.processNextCompilerStage(node.getIndexNodes());
//        JNode base = compilerContext.processNextCompilerStage(node::getArrayInstanceNode, node::setArrayInstanceNode);
//        JType baseType = base.getType();
//        if (node.getArrayType() == null) {
//            if (base.getType() != null) {
//                node.setArrayType(base.getType());
//            }
//        }
//        if (baseType != null) {
//            if (node.getArrayType().isArray()) {
//                JNode b = base;
//                JType bt = baseType;
//                JNode[] array = node.getIndexNodes();
//                if (array.length == 0) {
//                    throw new JParseException("Missing index");
//                }
//                if (array.length > 1) {
//                    for (int i = 0; i < array.length - 1; i++) {
//                        JNode jNode = array[i];
//                        b = new HNArrayCall(b, new JNode[]{jNode}, bt, bt.componentType(),jNode.token());
//                        bt = bt.componentType();
//                    }
//                    b = new JNodeHArraySet(b, new JNode[]{array[array.length - 1]}, node.getValueNode(), bt, bt.componentType(),
//                            array[array.length - 1].token() );
//                }
//                return b;
//            } else {
//                if (compilerContext.isStage(STAGE_3_WIRE_CALLS)) {
//                    JNode[] oldNodes = node.getIndexNodes();
//                    JType[] oldTypes = JeepUtils.getTypes(oldNodes);
//
//                    JNode[] nargs = new JNode[oldNodes.length + 2];
//                    JType[] ntypes = new JType[oldNodes.length + 2];
//                    System.arraycopy(oldNodes, 0, nargs, 1, oldNodes.length);
//                    System.arraycopy(oldTypes, 0, ntypes, 1, oldTypes.length);
//                    nargs[0] = base;
//                    ntypes[0] = baseType;
//                    nargs[nargs.length - 1] = node.getValueNode();
//                    ntypes[nargs.length - 1] = JeepUtils.getType(node.getValueNode());
//                    JInvokable m = compilerContext.findMatchOrNull(JSignature.of("set", ntypes),node.token());
//                    if (m != null) {
//                        return HUtils.createFunctionCall(node.token(),m, nargs);
//                    }
//                    String argsString = new JArgumentTypes(oldTypes, false).toString(false);
//                    compilerContext.log().error("S044", "To use "
//                                    + baseType.name() + "[" + argsString + "] operator you should implement either \n"
//                                    + "\tinstance method: " + baseType.name() + ".set(" + argsString + ") \n"
//                                    + "\tor\n"
//                                    + "\tstatic method  : set(" + new JArgumentTypes(ntypes, false).toString(false) + ") \n"
//                            , base.token());
//                }
//
//            }
//        }
//        return node;
//    }
//
//    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
//        super.copyFrom(node,copyFactory);
//        if (node instanceof JNodeHArraySet) {
//            JNodeHArraySet o = (JNodeHArraySet) node;
//            this.arrayInstanceNode = JNodeUtils.bindCopy(this, copyFactory, o.arrayInstanceNode);
//            this.indexNodes = JNodeUtils.bindCopy(this, copyFactory, o.indexNodes);
//            this.arrayType = o.arrayType;
//            this.valueNode = JNodeUtils.bindCopy(this, copyFactory, o.valueNode);
//            this.componentType = o.componentType;
//        }
//    }
//
//    @Override
//    public JNode copy() {
//        JNodeHArraySet n = new JNodeHArraySet();
//        n.copyFrom(this);
//        return n;
//    }
//
//    @Override
//    public void visit(JNodeVisitor visitor) {
//        visitor.startVisit(this);
//        visitNext(visitor, this.arrayInstanceNode);
//        visitNext(visitor, this.indexNodes);
//        visitor.endVisit(this);
//    }
//
//}
