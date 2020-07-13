package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.impl.functions.JFunctionLocal;
import net.vpc.common.jeep.impl.functions.JNameSignature;
import net.vpc.common.jeep.util.JNodeUtils;
import net.hl.compiler.core.invokables.BodyJInvoke;
import net.hl.compiler.utils.HNodeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Methods, Constructors and Functions are depicted by this node type
 */
public class HNDeclareInvokable extends HNode implements HNDeclare {

    private List<HNDeclareIdentifier> arguments = new ArrayList<>();
    private HNTypeToken[] genericVariables = new HNTypeToken[0];
    private HNode body;
    private boolean constr;
    private JToken nameToken;
    private HNTypeToken returnTypeName;
    private JType effectiveReturnType;
    private boolean immediateBody;
    private JNameSignature signature;
    private JInvokable invokable;
    private HNDeclareType declaringType;
    private HLInvokableType invokableType;
    private String genType;
    private boolean initBlock;

    private HNDeclareInvokable() {
        super(HNNodeId.H_DECLARE_INVOKABLE);
    }

    public HNDeclareInvokable(JToken nameToken, JToken startToken, JToken endToken) {
        this();
        this.nameToken = nameToken;
        this.setStartToken(startToken);
        this.setEndToken(endToken);
    }

    public boolean isInitBlock() {
        return initBlock;
    }

    public void setInitBlock(boolean initBlock) {
        this.initBlock = initBlock;
    }

    public String getGenType() {
        return genType;
    }

    public void setGenType(String genType) {
        this.genType = genType;
    }

    public boolean isConstr() {
        return constr || (this.getInvokable() instanceof JConstructor);
    }

    public HNDeclareInvokable setConstr(boolean constr) {
        this.constr = constr;
        return this;
    }

    public HNTypeToken[] getGenericVariables() {
        return genericVariables;
    }

    public HNDeclareInvokable setGenericVariables(HNTypeToken[] genericVariables) {
        this.genericVariables = genericVariables;
        return this;
    }

    public HLInvokableType getInvokableType() {
        return invokableType;
    }

    public HNDeclareInvokable setInvokableType(HLInvokableType invokableType) {
        this.invokableType = invokableType;
        return this;
    }

    public String[] getArgNames() {
        return getArguments().stream().map(HNDeclareIdentifier::getIdentifierName).toArray(String[]::new);
    }

    public JNameSignature getSignature() {
        return signature;
    }

    public HNDeclareInvokable setSignature(JNameSignature signature) {
        this.signature = signature;
        return this;
    }

    public List<HNDeclareIdentifier> getArguments() {
        return arguments;
    }

    public HNDeclareInvokable setArguments(List<HNDeclareIdentifier> arguments) {
        this.arguments = JNodeUtils.bind(this, arguments, "arguments");
        return this;
    }

    public JToken getNameToken() {
        return nameToken;
    }

    public HNDeclareInvokable setNameToken(JToken nameToken) {
        this.nameToken = nameToken;
        return this;
    }

    public String getName() {
        return nameToken == null ? null : nameToken.image;
    }

//    public int getModifiers() {
//        return modifiers;
//    }
//
//    public HNDeclareInvokable setModifiers(int modifiers) {
//        this.modifiers = modifiers;
//        if (invokable != null) {
//            if (invokable instanceof JMethod) {
//                ((DefaultJRawMethod) invokable).setModifiers(modifiers);
//            } else if (invokable instanceof JFunction) {
//                //
//            } else if (invokable instanceof JConstructor) {
//                ((DefaultJConstructor) invokable).setModifiers(modifiers);
//            }
//        }
//        return this;
//    }
    public HNode getBody() {
        return body;
    }

    public HNDeclareInvokable setBody(HNode body) {
        this.body = JNodeUtils.bind(this, body, "body");
        return this;
    }

    public boolean isImmediateBody() {
        return immediateBody;
    }

    public HNDeclareInvokable setImmediateBody(boolean immediateBody) {
        this.immediateBody = immediateBody;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean anonymous = getName() == null || getName().equals("");
        if (!anonymous) {
            sb.append(HNAnnotationList.nonNull(getAnnotations()));
            if (sb.length() > 0) {
                sb.append(" ");
            }
            if (returnTypeName != null) {
                sb.append(returnTypeName);
                sb.append(" ");
            }
            if (nameToken != null && nameToken.image.length() > 0) {
                sb.append(nameToken.image);
            }
            if (genericVariables.length > 0) {
                sb.append("<");
                for (int i = 0; i < genericVariables.length; i++) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    HNTypeToken genericVariable = genericVariables[i];
                    sb.append(genericVariable.getTypenameOrVar());
                }
                sb.append(">");
            }
        }
        //type if not anonymous!
        if (arguments.size() > 0) {
            sb.append("(");
            boolean first = true;
            for (HNDeclareIdentifier argument : arguments) {
                HNTypeToken identifierType = argument.getIdentifierTypeNode();
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                if (argument.isVarArg()) {
                    sb.append(identifierType.componentType());
                    sb.append("...");
                    sb.append(" ");
                } else {
                    sb.append(identifierType);
                    sb.append(" ");
                }
                sb.append(argument.getIdentifierToken());
            }
            sb.append(")");
        }
        if (immediateBody) {
            sb.append(" -> ");
            sb.append(body == null ? "" : body);
        } else if (HNAnnotationList.isAbstract(getAnnotations())) {
            //
        } else {
            if (body != null) {
                sb.append(" ").append(body);
            }
        }
        return sb.toString();
    }

    public JType getReturnType() {
        if (effectiveReturnType != null) {
            return effectiveReturnType;
        }
        if (returnTypeName != null) {
            return returnTypeName.getTypeVal();
        }
        return null;
    }

    public HNTypeToken getReturnTypeName() {
        return returnTypeName;
    }

    public HNDeclareInvokable setReturnTypeName(HNTypeToken returnTypeName) {
        this.returnTypeName = JNodeUtils.bind(this, returnTypeName, "returnTypeName");
        return this;
    }

    public JConstructor getConstructor() {
        return (JConstructor) getInvokable();
    }

    public boolean isConstructor() {
        return isConstr() || invokable instanceof JConstructor;
    }

    public boolean isFunction() {
        return invokable instanceof JFunction;
    }

//    public JType[] getArgTypes() {
//        return argTypes;
//    }
//
//    public HNDeclareInvokable setArgTypes(JType[] argTypes) {
//        this.argTypes = argTypes;
//        return this;
//    }
//
//    public JType getReturnType() {
//        return returnType;
//    }
//
//    public HNDeclareInvokable setReturnType(JType returnType) {
//        this.returnType = returnType;
//        return this;
//    }
//
//    public JSignature getSignature() {
//        return signature;
//    }
//
//    public void setSignature(JSignature sig) {
//        this.signature = sig;
//    }
    public boolean isMethod() {
        return invokable instanceof JMethod;
    }

    public JInvokable getInvokable() {
        return invokable;
    }

    public HNDeclareInvokable setInvokable(JInvokable invokable) {
        this.invokable = invokable;
        return this;
    }

    public HNDeclareInvokable buildInvokable() {
        JType[] jTypes = getArguments().stream().map(x -> HNodeUtils.getType(x)).toArray(JType[]::new);
        boolean varArg = getArguments().size() > 0 && getArguments().get(getArguments().size() - 1).getIdentifierTypeNode().getTypename().isVarArg();
        setInvokable(new JFunctionLocal(
                getName(), HNodeUtils.getType(this),
                jTypes, varArg,
                new BodyJInvoke(this)
        ));
        return this;
    }

    public void copyFrom(JNode node, JNodeCopyFactory copyFactory) {
        super.copyFrom(node, copyFactory);
        if (node instanceof HNDeclareInvokable) {
            HNDeclareInvokable o = (HNDeclareInvokable) node;
            this.arguments = JNodeUtils.bindCopy(this, copyFactory, o.arguments);
            this.body = JNodeUtils.bindCopy(this, copyFactory, o.body);
            this.genericVariables = JNodeUtils.bindCopy(this, copyFactory, o.genericVariables, HNTypeToken.class);
            this.nameToken = o.nameToken;
            this.immediateBody = o.immediateBody;
            this.declaringType = o.declaringType;
            this.invokable = o.invokable;
            this.signature = o.signature;
            this.invokableType = o.invokableType;
            this.returnTypeName = JNodeUtils.bindCopy(this, copyFactory, o.returnTypeName);
            this.effectiveReturnType = o.effectiveReturnType;
            this.initBlock = o.initBlock;
            this.genType = o.genType;
        }
    }

    @Override
    public List<JNode> getChildrenNodes() {
        List<JNode> li = new ArrayList<>();
        li.add(returnTypeName);
        li.addAll(Arrays.asList(genericVariables));
        li.addAll(arguments);
        li.add(body);
        return li;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this.getArguments());
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getBody, this::setBody);
    }

    public HNDeclareType getDeclaringType() {
        return declaringType;
    }

    public HNDeclareInvokable setDeclaringType(HNDeclareType declaringType) {
        this.declaringType = declaringType;
        return this;
    }

    public JType getEffectiveReturnType() {
        return effectiveReturnType;
    }

    public HNDeclareInvokable setEffectiveReturnType(JType effectiveReturnType) {
        this.effectiveReturnType = effectiveReturnType;
        return this;
    }
}
