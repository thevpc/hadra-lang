/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.util.JeepUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNInvokerCall extends HNode {

    private JToken name;
    private JNode[] args;
    private JInvokablePrefilled impl;
    public HNInvokerCall() {
        super(HNNodeId.H_INVOKER_CALL);
    }

    public HNInvokerCall(JToken name, JNode[] args, JToken startToken, JToken endToken) {
        this();
        this.name = name;
        setArgs(args);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        if(impl!=null){
            System.out.println("really?");
        }
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getArgs());
    }


    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(args);
    }

    public JInvokablePrefilled impl() {
        return impl;
    }

    public void setImpl(JInvokablePrefilled impl) {
        this.impl = impl;
    }

    public JToken getNameToken() {
        return name;
    }
    public String getName() {
        return name.image;
    }

//    public HNInvokerCall setName(String name) {
//        this.name = name;
//        return this;
//    }

//    @Override
//    public Object evaluate(JContext context) {
//        return context.functions().evaluate(getName(), getArgs());
//    }

    public JNode[] getArgs() {
        return args;
    }

    public HNInvokerCall setArgs(JNode[] args) {
        this.args = JNodeUtils.bind(this,args,"args");
        return this;
    }

//    @Override
//    public JType getType(JContext context) {
//        String name = getName();
//        JType[] argTypes = JeepUtils.getTypesOrNulls(args);
//        JFunction f = context.functions().findMatchOrNull(name, argTypes);
//        if(f==null){
//            if(name.isEmpty()){
//                throw new NoSuchElementException("Implicit JFunction not found "+ Arrays.asList(getArgs()));
//            }
//            throw new NoSuchElementException("JFunction not found "+ name +Arrays.asList(getArgs()));
////            return Object.class;
//        }
//        return f.returnType();
//    }

    @Override
    public String toString() {
        String n = getName();
        if (JeepUtils.isDefaultOp(n)) {
            switch (args.length) {
                case 1: {
                    return /*"(" + */ getName() + JNodeUtils.toPar(args[0])/*+ ")"*/;
                }
                case 2: {
                    return /*"(" +*/ JNodeUtils.toPar(args[0]) + getName() + JNodeUtils.toPar(args[1]) /*+ ")"*/;
                }
            }
        }
        StringBuilder sb = new StringBuilder().append(n).append("(");
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

    public JNode get(int index) {
        return args[index];
    }

    public boolean is(String name) {
        return getName().equals(name);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNInvokerCall) {
            HNInvokerCall o = (HNInvokerCall) node;
            this.name = (o.name);
            this.args = JNodeUtils.bindCopy(this, copyFactory, o.args);
            this.impl = (o.impl);
        }
    }

}
