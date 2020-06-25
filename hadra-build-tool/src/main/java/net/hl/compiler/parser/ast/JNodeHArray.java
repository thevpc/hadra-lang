///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package net.hl.compiler.parser.ast;
//
//import net.vpc.common.jeep.JCompilerContext;
//import net.vpc.common.jeep.JNode;
//import net.vpc.common.jeep.JNodeVisitor;
//import net.vpc.common.jeep.JToken;
//import net.vpc.common.jeep.JNodeFindAndReplace;
//import net.hl.compiler.core.invokables.HLJCompilerContext;
//import net.hl.compiler.utils.HUtils;
//
//import java.util.Arrays;
//
///**
// *
// * @author vpc
// */
//public class JNodeHArray extends HNode {
//
//    private String arrayType;
//    private JNode[] values;
//
//    public JNodeHArray() {
//        super(HNode.H_NODE_ARRAY);
//    }
//    public JNodeHArray(String arrayType, JNode[] values, JToken token) {
//        super(HNode.H_NODE_ARRAY);
//        setValues(values);
//        this.arrayType= arrayType;
//        setStartToken(token);
//    }
//
//    @Override
//    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
//        HUtils.findAndReplaceNext(this,findAndReplace,this.getValues());
//    }
//
//    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
//        super.copyFrom(node,copyFactory);
//        if (node instanceof JNodeHArray) {
//            JNodeHArray o =(JNodeHArray) node;
//            o.values=bindCopy(o.values);
//            o.arrayType=o.arrayType;
//        }
//    }
//
//    @Override
//    public JNode copy() {
//        JNodeHArray n=new JNodeHArray();
//        n.copyFrom(this);
//        return n;
//    }
//
//    public JNodeHArray setValues(JNode[] values) {
//        this.values=JNodeUtils.bind(this,values);
//        return this;
//    }
//
//    public String getArrayType() {
//        return arrayType;
//    }
//
//    public JNode get(int i) {
//        return values[i];
//    }
//
//    public JNode[] getValues() {
//        return values;
//    }
//
//
//    @Override
//    public String toString() {
//        return Arrays.toString(values);
//    }
//
//    @Override
//    public JNode processCompilerStage(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext=(HLJCompilerContext) compilerContextBase;
//        JNodeHArray node = (JNodeHArray) compilerContext.node();
//        JNode[] values = node.getValues();
//        compilerContext.processNextCompilerStage(node, values);
//        return compilerContext.node();
//    }
//
//    @Override
//    public void visit(JNodeVisitor visitor) {
//        visitor.startVisit(this);
//        visitNext(visitor,getValues());
//        visitor.endVisit(this);
//    }
//
//}
