package net.hl.compiler.core.invokables;

import net.thevpc.jeep.*;
import net.hl.compiler.ast.HNDeclareInvokable;
import net.hl.compiler.ast.HNDeclareIdentifier;
import net.hl.compiler.ast.HNLambdaExpression;
import net.hl.compiler.utils.HNodeUtils;

import java.util.ArrayList;
import java.util.List;

public class BodyJInvoke implements JInvoke {

    private VarDef[] vars;
    private JNode body;

    public BodyJInvoke(VarDef[] vars, JNode body) {
        this.vars = vars;
        this.body = body;

    }

    public BodyJInvoke(HNDeclareInvokable v) {
        rebuild(v);
    }

    public BodyJInvoke(HNLambdaExpression v) {
        rebuild(v);
    }

    public void rebuild(HNLambdaExpression v) {
        List<HNDeclareIdentifier> vArguments = v.getArguments();
        List<VarDef> vars2 = new ArrayList<>(vArguments.size());
        for (int i = 0; i < vArguments.size(); i++) {
            HNDeclareIdentifier argument = vArguments.get(i);
            for (String identifierName : HNodeUtils.flattenNames(argument.getIdentifierToken())) {
                vars2.add(new VarDef(identifierName, argument.getIdentifierType()));
            }
        }
        this.vars = vars2.toArray(new VarDef[0]);
        this.body = v.getBody();
    }

    public void rebuild(HNDeclareInvokable v) {
        List<HNDeclareIdentifier> vArguments = v.getArguments();
        List<VarDef> vars2 = new ArrayList<>(vArguments.size());
        for (int i = 0; i < vArguments.size(); i++) {
            HNDeclareIdentifier argument = vArguments.get(i);
            for (String identifierName : HNodeUtils.flattenNames(argument.getIdentifierToken())) {
                vars2.add(new VarDef(identifierName, argument.getIdentifierType()));
            }
        }
        this.vars = vars2.toArray(new VarDef[0]);
        this.body = v.getBody();
    }

    @Override
    public Object invoke(JInvokeContext context) {
        JContext c = context.getContext().newContext();
        try {
            for (int i = 0; i < vars.length; i++) {
                c.vars().declareVar(vars[i].name, vars[i].type, context.getArguments()[i].evaluate(context));
            }
            return context.getEvaluator().evaluate(body, context.builder().setContext(c).build());
        } finally {
            //c.dispose();
        }
    }

    public static class VarDef {

        private String name;
        private JType type;

        public VarDef(String name, JType type) {
            this.name = name;
            this.type = type;
            if (type == null) {
                throw new JParseException("Null Type");
            }
        }

        public String getName() {
            return name;
        }

        public JType getType() {
            return type;
        }
    }
}
