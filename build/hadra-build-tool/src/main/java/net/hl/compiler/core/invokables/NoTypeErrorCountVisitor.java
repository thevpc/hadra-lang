package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.*;
import net.hl.compiler.ast.HNIdentifier;
import net.hl.compiler.ast.HNLiteral;
import net.hl.compiler.ast.HNode;

public class NoTypeErrorCountVisitor implements JNodeVisitor {
    public int errors = 0;
    private boolean log;
    private JCompilerLog logger;

    public NoTypeErrorCountVisitor(boolean log,JCompilerLog logger) {
        this.log = log;
        this.logger = logger;
    }

    @Override
    public void endVisit(JNode node) {
//        int len = new Throwable().getStackTrace().length;
//        for (int i = 0; i < len; i++) {
//            System.out.print(' ');
//        }
//        System.out.println("visit :: " + node.getClass().getSimpleName() + " :: " + JToken.escapeString(node.toString()));
        HNode hnode=(HNode) node;
        JType type = hnode.getElement()==null?null:hnode.getElement().getType();
        if (type == null) {
            if(log) {
                System.err.println("Node " + node.getClass().getSimpleName()
                        + " has no type :: " + JToken.escapeString(node.toString())
                        + "  __PARENT__ " + node.getParentNode()
                );
            }
            if(logger!=null){
                if(node instanceof HNLiteral) {
                    if(((HNLiteral) node).getValue()==null){
                        return;
                    }
                    logger.error("X33", null, "unable to resolve type for [" + node.getClass().getSimpleName() + "]", node.getStartToken());
                }else if(node instanceof HNIdentifier) {
                    logger.error("X33", null, "unable to resolve symbol : " + ((HNIdentifier) node).getName(), node.getStartToken());
//                }else if(node instanceof HNField){
//                    HNField g=(HNField) node;
//                    JType instanceNodeType = g.getInstanceNode()==null?null:((HNode)g.getInstanceNode()).getElement().getType();
//                    logger.error("X33", "unable to resolve field : " + instanceNodeType +"."+g.getFieldName(), node.startToken());
                }else {
                    logger.error("X33", null, "unable to resolve type for [" + node.getClass().getSimpleName() + "]", node.getStartToken());
                }
            }
            errors++;
        }
    }
}
