//package net.hl.compiler.parser.ast;
//
//import net.vpc.common.jeep.*;
//import net.vpc.common.jeep.JNodeFindAndReplace;
//import net.vpc.common.jeep.util.JNodeUtils;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//public class HNMethodUncheckedCall extends HNUnused {
//    private JNode instanceNode;
//    private JNode methodName;
//    private JNode[] args;
//
//    protected HNMethodUncheckedCall() {
//        super(HNNodeId.H_INVOKE_METHOD_UNCHECKED);
//    }
//
//    public HNMethodUncheckedCall(JNode methodName, JNode[] args, JNode instanceNode, JToken token) {
//        this();
//        this.methodName = methodName;
//        setArgs(this.args);
//        setInstanceNode(instanceNode);
//        setStartToken(token);
//    }
//
//    public JNode getMethodName() {
//        return methodName;
//    }
//
//    public HNMethodUncheckedCall setMethodName(JNode methodName) {
//        this.methodName = methodName;
//        return this;
//    }
//
//    public JNode getInstanceNode() {
//        return instanceNode;
//    }
//
//    public HNMethodUncheckedCall setInstanceNode(JNode instanceNode) {
//        this.instanceNode=JNodeUtils.bind(this,instanceNode,"instance");
//        return this;
//    }
//
//    public JNode[] getArgs() {
//        return args;
//    }
//
//    public HNMethodUncheckedCall setArgs(JNode[] args) {
//        this.args=JNodeUtils.bind(this,args,"args");
//        return this;
//    }
//
//    @Override
//    public String toString() {
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(instanceNode);
//        sb.append(".");
//        sb.append("?");
//        sb.append(methodName).append("(");
//        for (int i = 0; i < args.length; i++) {
//            if (i > 0) {
//                sb.append(",");
//            }
//            String sargi = args[i].toString();
//            sb.append(sargi);
//        }
//        sb.append(")");
//        return sb.toString();
//    }
//
//    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
//        super.copyFrom(node,copyFactory);
//        if (node instanceof HNMethodUncheckedCall) {
//            HNMethodUncheckedCall o = (HNMethodUncheckedCall) node;
//            this.methodName = JNodeUtils.bindCopy(this, copyFactory, o.methodName);
//            this.instanceNode = JNodeUtils.bindCopy(this, copyFactory, o.instanceNode);
//            this.args = JNodeUtils.bindCopy(this, copyFactory, o.args);
//        }
//    }
//
//    @Override
//    public List<JNode> childrenNodes() {
//        List<JNode> li = new ArrayList<>();
//        li.add(this.instanceNode);
//        li.add(this.methodName);
//        li.addAll(Arrays.asList(this.args));
//        return li;
//    }
//
//
//    @Override
//    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
//        JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getInstanceNode, this::setInstanceNode);
//        JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getMethodName, this::setMethodName);
//        JNodeUtils.findAndReplaceNext(this, findAndReplace, this.getArgs());
//    }
//}
