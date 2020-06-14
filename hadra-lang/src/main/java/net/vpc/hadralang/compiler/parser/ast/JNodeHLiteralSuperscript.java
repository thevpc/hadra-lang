///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package net.vpc.hadralang.compiler.parser.ast;
//
//import net.vpc.common.jeep.JCompilerContext;
//import net.vpc.common.jeep.JNode;
//import net.vpc.common.jeep.JToken;
//import net.vpc.common.jeep.JNodeVisitor;
//import net.vpc.common.jeep.JNodeFindAndReplace;
//import net.vpc.hadralang.compiler.core.invokables.HLJCompilerContext;
//
///**
// * @author vpc
// */
//public class JNodeHLiteralSuperscript extends HNode {
//
//    private int intValue;
//    private String superScriptValue;
//
//    private JNodeHLiteralSuperscript() {
//        super(HNode.H_LITERAL_SUPERSCRIPT);
//    }
//
//    public JNodeHLiteralSuperscript(String superScriptValue, int value, JToken token) {
//        super(HNode.H_LITERAL_SUPERSCRIPT);
//        this.superScriptValue = superScriptValue;
//        setStartToken(token);
//        intValue = intValue;
//    }
//
//    @Override
//    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
//    }
//
//    public String getSuperScriptValue() {
//        return superScriptValue;
//    }
//
//    public int getIntValue() {
//        return intValue;
//    }
//
//    @Override
//    public String toString() {
//        return superScriptValue;
//    }
//
//    @Override
//    public JNode processCompilerStage(JCompilerContext compilerContextBase) {
//        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
//        JNodeHLiteralSuperscript n = (JNodeHLiteralSuperscript) compilerContext.node();
//        return n;
//    }
//
//    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
//        super.copyFrom(node,copyFactory);
//        if (node instanceof JNodeHLiteralSuperscript) {
//            JNodeHLiteralSuperscript o = (JNodeHLiteralSuperscript) node;
//            this.superScriptValue = (o.superScriptValue);
//            this.intValue = (o.intValue);
//        }
//    }
//
//    @Override
//    public JNode copy() {
//        JNodeHLiteralSuperscript n = new JNodeHLiteralSuperscript();
//        n.copyFrom(this);
//        return n;
//    }
//
//    @Override
//    public void visit(JNodeVisitor visitor) {
//        visitor.startVisit(this);
//        visitor.endVisit(this);
//    }
//}
