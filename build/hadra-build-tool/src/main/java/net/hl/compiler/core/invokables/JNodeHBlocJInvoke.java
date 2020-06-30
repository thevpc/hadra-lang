package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.*;
import net.hl.compiler.ast.*;
import net.hl.compiler.ast.HNBlock;
import net.hl.compiler.ast.HNDeclareIdentifier;
import net.hl.compiler.utils.HNodeUtils;

import java.util.ArrayList;
import java.util.List;

public class JNodeHBlocJInvoke implements JInvoke {
    private final List<HNDeclareIdentifier> vars=new ArrayList<>();
    private final List<JInvokable> invokables=new ArrayList<>();
    private final List<HNDeclareType> types=new ArrayList<>();
    private final List<HNode> runnables=new ArrayList<>();

    public JNodeHBlocJInvoke(HNBlock v) {
        this(v.getStatements());
    }

    public JNodeHBlocJInvoke(List<HNode> statements) {
        for (HNode statement : statements) {
            if(statement instanceof HNDeclareIdentifier){
                vars.add((HNDeclareIdentifier) statement);
                runnables.add(statement);
            }else  if(statement instanceof HNDeclareInvokable){
                invokables.add(((HNDeclareInvokable) statement).getInvokable());
            }else  if(statement instanceof HNDeclareType){
                types.add((HNDeclareType) statement);
            }else  {
                runnables.add(statement);
            }
        }
    }

    @Override
    public Object invoke(JInvokeContext context) {
        JContext c = context.getContext().newContext();
        try {
            //should i declare some things here?????
            for (HNDeclareIdentifier dec : vars) {
                //default value is null. It will be initialized at its valid place.
                for (String identifierName : HNodeUtils.flattenNames(dec.getIdentifierToken())) {
                    c.vars().declareVar(identifierName, HNodeUtils.getType(dec), null);
                }
            }
            for (JInvokable dec : invokables) {
                if(dec instanceof JFunction) {
                    c.functions().declare((JFunction) dec);
                }
            }
            for (HNDeclareType dec : types) {
                c.types().addAlias(dec.getName(), dec.getjType());
            }
//            Object i = context.instance();
//            if(i!=null){
//                c.vars().declareConst("this",context.context().types().typeOf(i),i);
//            }
            Object result = null;
            for (HNode jNode : runnables) {
                result = context.getEvaluator().evaluate(jNode, context.builder().setContext(c).build());
            }
            return result;
        } finally {
            //c.dispose();
        }
    }
}
