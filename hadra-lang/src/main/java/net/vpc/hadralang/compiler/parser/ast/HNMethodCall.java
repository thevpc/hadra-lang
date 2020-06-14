package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.util.JTokenUtils;
import net.vpc.common.jeep.util.JeepUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.hadralang.compiler.utils.HNodeUtils;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HNMethodCall extends HNode {
    private HNode instanceNode;
    private JInvokable method;
    private JTypeName declaringTypeName;
    private JToken methodName;
    private HNode[] args;
    private JInvokablePrefilled impl;
    private boolean nullableInstance;
    private JToken op;

    protected HNMethodCall() {
        super(HNNodeId.H_INVOKE_METHOD);
    }

    public HNMethodCall(JMethod method, HNode[] args, HNode instanceNode, JToken startToken, JToken endToken) {
        this();
        this.method = method;
        this.methodName = HNodeUtils.createToken(method.name());
        setArgs(args);
        setInstanceNode(instanceNode);
        if(HNodeUtils.isTypeSet(args)){
            setImpl(HUtils.createJInvokablePrefilled(method, args));
        }
        setStartToken(startToken);
        setStartToken(endToken);
    }
    public HNMethodCall(JToken op, JToken methodName, HNode[] args, HNode instanceNode, JToken startToken, JToken endToken) {
        this();
        this.op = op;
        this.method = null;
        this.methodName = methodName;
        setArgs(args);
        setInstanceNode(instanceNode);
//        argTypes = JeepUtils.getTypes(args);
//        evaluableArguments = JeepUtils.getEvaluatables(this.args);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public HNMethodCall setArgs(HNode[] args) {
        this.args = JNodeUtils.bind(this,args,"args");
        return this;
    }

    public JTypeName getDeclaringTypeName() {
        return declaringTypeName;
    }

    public HNMethodCall setDeclaringTypeName(JTypeName declaringTypeName) {
        this.declaringTypeName = declaringTypeName;
        return this;
    }

    public boolean isNullableInstance() {
        return nullableInstance;
    }

    public HNMethodCall setNullableInstance(boolean nullableInstance) {
        this.nullableInstance = nullableInstance;
        return this;
    }

    @Override
    public List<JNode> childrenNodes() {
        List<JNode> li=new ArrayList<>();
        li.add(this.instanceNode);
        li.addAll(Arrays.asList(args));
        return li;
    }
    public JInvokablePrefilled impl() {
        return impl;
    }

    public void setImpl(JInvokablePrefilled impl) {
        this.impl = impl;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getInstanceNode,this::setInstanceNode);
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getArgs());
    }
    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNMethodCall) {
            HNMethodCall o =(HNMethodCall) node;
            this.instanceNode=JNodeUtils.bindCopy(this, copyFactory, o.instanceNode);
            this.args=JNodeUtils.bindCopy(this, copyFactory, o.args,HNode.class);
            this.nullableInstance=(o.nullableInstance);
            this.method=(o.method);
            this.methodName=(o.methodName);
//            this.argTypes=(o.argTypes);
//            this.evaluableArguments=(o.evaluableArguments);
        }
    }

//    public JType[] getArgTypes() {
//        return argTypes;
//    }
//
//    public JEvaluable[] getEvaluableArguments() {
//        return evaluableArguments;
//    }

    public HNode getInstanceNode() {
        return instanceNode;
    }

    public HNMethodCall setInstanceNode(HNode instanceNode) {
        this.instanceNode=JNodeUtils.bind(this,instanceNode,"instance");
        return this;
    }

    public JInvokable getMethod() {
        return method;
    }

    public HNMethodCall setMethod(JInvokable method) {
        this.method = method;
        return this;
    }

    public HNode[] getArgs() {
        return args;
    }

    public String getMethodName() {
        return methodName.image;
    }

    public JToken getMethodNameToken() {
        return methodName;
    }

    @Override
    public String toString() {

        String n = methodName.image;
        if (JeepUtils.isDefaultOp(n)) {
            switch (args.length) {
                case 1: {
                    return /*"(" + */ n + JNodeUtils.toPar(args[0])/*+ ")"*/;
                }
                case 2: {
                    return /*"(" +*/ JNodeUtils.toPar(args[0]) + n + JNodeUtils.toPar(args[1]) /*+ ")"*/;
                }
            }
        }
        StringBuilder sb=new StringBuilder();
        if((method!=null && method instanceof JMethod && ((JMethod)method).isStatic())) {
            sb.append(((JMethod) method).declaringType().name());
            sb.append(".");
        }else if((method!=null && ! (method instanceof JMethod))) {
//            sb.append(((JMethod)method).declaringType().name());
//            sb.append(".");
        }else if(method==null && instanceNode==null){
            sb.append("<declaringType>");
            sb.append(".");
        }else{
            if(instanceNode==null){
                sb.append("(this)");
                sb.append(".");
            }else{
                sb.append(instanceNode);
                sb.append(nullableInstance?"?":".");
            }
        }
        sb.append(n).append("(");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            String sargi = args[i].toString();
            sb.append(sargi);
        }
        sb.append(")");
        return sb.toString();
    }

    public JInvokablePrefilled getImpl() {
        return impl;
    }

}
