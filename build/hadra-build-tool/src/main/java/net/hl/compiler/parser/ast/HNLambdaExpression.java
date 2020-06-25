package net.hl.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.impl.functions.JFunctionLocal;
import net.vpc.common.jeep.impl.functions.JNameSignature;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.hl.compiler.core.invokables.BodyJInvoke;
import net.hl.compiler.utils.HNodeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Methods, Constructors and Functions are depicted by this node type
 */
public class HNLambdaExpression extends HNode {
    private List<HNDeclareIdentifier> arguments = new ArrayList<>();
    private HNode body;
    private JTypeName returnTypeName;
    private JType returnType;
    private boolean immediateBody;
    private JNameSignature signature;
    private JInvokable invokable;
    private JMethod targetMethod;
    private Object proxy;
    private JToken op;

    private HNLambdaExpression() {
        super(HNNodeId.H_LAMBDA_EXPR);
    }

    public HNLambdaExpression(JToken op, JToken startToken, JToken endToken) {
        super(HNNodeId.H_LAMBDA_EXPR);
        this.op=op;
        this.setStartToken(startToken);
        this.setEndToken(endToken);
    }

    public JNameSignature getSignature() {
        return signature;
    }

    public HNLambdaExpression setSignature(JNameSignature signature) {
        this.signature = signature;
        return this;
    }

    public List<HNDeclareIdentifier> getArguments() {
        return arguments;
    }

    public HNLambdaExpression setArguments(List<HNDeclareIdentifier> arguments) {
        this.arguments = JNodeUtils.bind(this,arguments,"arguments");
        return this;
    }

    public HNLambdaExpression addArgument(HNDeclareIdentifier argument) {
        arguments.add(JNodeUtils.bind(this,argument,"arguments",arguments.size()));
        return this;
    }

    public HNode getBody() {
        return body;
    }

    public HNLambdaExpression setBody(HNode body) {
        this.body=JNodeUtils.bind(this,body,"body");
        return this;
    }

    public boolean isImmediateBody() {
        return immediateBody;
    }

    public HNLambdaExpression setImmediateBody(boolean immediateBody) {
        this.immediateBody = immediateBody;
        return this;
    }

    public JMethod getTargetMethod() {
        return targetMethod;
    }

    public HNLambdaExpression setTargetMethod(JMethod targetMethod) {
        this.targetMethod = targetMethod;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (arguments.size() > 0) {
            boolean first = true;
            for (HNDeclareIdentifier argument : arguments) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                if (argument.getIdentifierTypeNode() != null) {
                    if (argument.isVarArg()) {
                        sb.append(argument.getIdentifierTypeNode().componentType());
                        sb.append("...");
                        sb.append(" ");
                    } else {
                        sb.append(argument.getIdentifierTypeNode());
                        sb.append(" ");
                    }
                }
                sb.append(argument.getIdentifierName());
            }
        }
        sb.append(")");
        if (immediateBody) {
            sb.append(" -> ");
            sb.append(body == null ? "" : body);
        } else {
            sb.append(" ").append(body.toString());
        }
        return sb.toString();
    }

    public JTypeName getReturnTypeName() {
        return returnTypeName;
    }

    public HNLambdaExpression setReturnTypeName(JTypeName returnTypeName) {
        this.returnTypeName = returnTypeName;
        return this;
    }

    public JInvokable getInvokable() {
        return invokable;
    }

    public HNLambdaExpression setInvokable(JInvokable invokable) {
        this.invokable = invokable;
        return this;
    }

    public HNLambdaExpression buildInvokable() {
        JType[] jTypes = getArguments().stream().map(x -> HNodeUtils.getType(x)).toArray(JType[]::new);
        boolean varArg = getArguments().size() > 0 && getArguments().get(getArguments().size() - 1).getIdentifierTypeNode().getTypename().isVarArg();
        setInvokable(new JFunctionLocal(
                "", HNodeUtils.getType(this),
                jTypes, varArg,
                new BodyJInvoke(this)
        ));
        return this;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNLambdaExpression) {
            HNLambdaExpression o = (HNLambdaExpression) node;
            this.arguments = JNodeUtils.bindCopy(this, copyFactory, o.arguments);
            this.body = JNodeUtils.bindCopy(this, copyFactory, o.body);
            this.immediateBody = o.immediateBody;
            this.invokable = o.invokable;
            this.signature = o.signature;
            this.returnTypeName = o.returnTypeName;
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        List<JNode> li = new ArrayList<>();
        li.addAll(arguments);
        li.add(body);
        return li;
    }

    @Override
    public void visit(JNodeVisitor visitor) {
        visitor.startVisit(this);
        visitNext(visitor, this.arguments);
        visitNext(visitor, this.body);
        visitor.endVisit(this);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this.getArguments());
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getBody, this::setBody);
    }

    public JType getReturnType() {
        return returnType;
    }

    public HNLambdaExpression setReturnType(JType returnType) {
        this.returnType = returnType;
        return this;
    }

    public Object getProxy() {
        return proxy;
    }

    public HNLambdaExpression setProxy(Object proxy) {
        this.proxy = proxy;
        return this;
    }
}
