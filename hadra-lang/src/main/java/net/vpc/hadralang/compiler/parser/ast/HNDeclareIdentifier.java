package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.nodes.AbstractJNode;
import net.vpc.common.jeep.core.types.DefaultJField;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class HNDeclareIdentifier extends HNode implements HNDeclare,HNDeclareTokenHolder {
    private int modifiers;
    private HNDeclareToken identifierToken;
    private HNTypeToken identifierTypeName;
    private JType effectiveIdentifierType;
//    private JType identifierType;
    private HNode initValue;
    /**
     * this is init value when we want to initialize it elsewhere.
     * In that case initValue will hold 'null' value
     */
    private HNode hiddenInitValue;
    /**
     * type with ...
     */
    private boolean varArg;
    private HNode initializer;
    private boolean mainConstructor;
    private JToken assignOperator;
    private HNDeclareType declaringType;
    private InitValueConstraint initValueConstraint;
    private SyntacticType syntacticType;

    private HNDeclareIdentifier() {
        super(HNNodeId.H_DECLARE_IDENTIFIER);
    }

//    public HNDeclareIdentifier(JToken[] identifierTokens, HNode initValue, JType jtype, JToken assignOperator, JToken startToken, JToken endToken) {
//        this();
//        if(true){
//            throw new JFixMeLaterException();
//        }
//        setIdentifierToken(identifierTokens);
//        setInitValue(initValue);
//        setIdentifierTypeName(jtype == null ? null : HUtils.createTypeToken(jtype));
//        setAssignOperator(assignOperator);
//        setStartToken(startToken);
//        setEndToken(endToken);
//    }

    public HNDeclareIdentifier(HNDeclareToken identifierToken, HNode initValue, HNTypeToken jtype, JToken assignOperator, JToken startToken, JToken endToken) {
        this();
        setIdentifierToken(identifierToken);
        setInitValue(initValue);
        setIdentifierTypeName(jtype);
        setAssignOperator(assignOperator);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public boolean isStatic(){
        return Modifier.isStatic(getModifiers());
    }

    @Override
    public HNDeclareToken getDeclareIdentifierTokenBase() {
        return getIdentifierToken();
    }

    public HNDeclareToken getIdentifierToken() {
        return identifierToken;
    }

    public HNDeclareIdentifier setIdentifierToken(HNDeclareToken identifierToken) {
        this.identifierToken= JNodeUtils.bind(this,identifierToken,"identifierToken");
        return this;
    }



    public HNode getHiddenInitValue() {
        return hiddenInitValue;
    }

    public HNDeclareIdentifier setHiddenInitValue(HNode hiddenInitValue) {
        this.hiddenInitValue = hiddenInitValue;
        return this;
    }

    public InitValueConstraint getInitValueConstraint() {
        return initValueConstraint;
    }

    public HNDeclareIdentifier setInitValueConstraint(InitValueConstraint initValueConstraint) {
        this.initValueConstraint = initValueConstraint;
        return this;
    }

    public JToken getAssignOperator() {
        return assignOperator;
    }

    public HNDeclareIdentifier setAssignOperator(JToken assignOperator) {
        this.assignOperator = assignOperator;
        return this;
    }


    public HNDeclareType getDeclaringType() {
        return declaringType;
    }

    public HNDeclareIdentifier setDeclaringType(HNDeclareType declaringType) {
        this.declaringType = declaringType;
        return this;
    }

    public boolean isMainConstructor() {
        return mainConstructor;
    }

    public HNDeclareIdentifier setMainConstructor(boolean mainConstructor) {
        this.mainConstructor = mainConstructor;
        return this;
    }

    public int getModifiers() {
        return modifiers;
    }

    public HNDeclareIdentifier setModifiers(int modifiers) {
        this.modifiers = modifiers;
        return this;
    }

    public String getIdentifierName() {
        HNDeclareTokenIdentifier identifierTokens = (HNDeclareTokenIdentifier) getIdentifierToken();
        return identifierTokens.getName();
    }

    public HNTypeToken getIdentifierTypeName() {
        return identifierTypeName;
    }

    public HNDeclareIdentifier setIdentifierTypeName(HNTypeToken type) {
        this.identifierTypeName=JNodeUtils.bind(this,type,"identifierTypeName");
        return this;
    }

    public HNode getInitValue() {
        return initValue;
    }

    public HNDeclareIdentifier setInitValue(HNode initValue) {
        this.initValue=JNodeUtils.bind(this,initValue,"initValue");
        return this;
    }

    public boolean isVarArg() {
        return varArg;
    }

    public HNDeclareIdentifier setVarArg(boolean varArg) {
        this.varArg = varArg;
        return this;
    }

    public HNode getInitializerStatement() {
        return initializer;
    }

    public void setInitializerStatement(HNode node) {
        this.initializer=JNodeUtils.bind(this,node,"initializer");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(HUtils.modifiersToString(modifiers));
        if (sb.length() > 0) {
            sb.append(" ");
        }
        if (getIdentifierTypeName() == null) {
            sb.append("var ");
        } else {
            if (isVarArg()) {
                sb.append(getIdentifierTypeName().componentType());
                sb.append("...");
                sb.append(" ");
            } else {
                sb.append(getIdentifierTypeName());
                sb.append(" ");
            }
        }
        sb.append(identifierToken);

        if (initValue != null) {
            sb.append(assignOperator.image);
            sb.append(initValue);
        }
        return sb.toString();
    }

    public JType getIdentifierType() {
        if(effectiveIdentifierType!=null){
            return effectiveIdentifierType;
        }
        return identifierTypeName==null?null:identifierTypeName.getTypeVal();
    }

//    @Override
//    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
//        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getInitValue,this::setInitValue);
//        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getInitializerStatement,this::setInitializerStatement);
//        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getHiddenInitValue,this::setHiddenInitValue);
//    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNDeclareIdentifier) {
            HNDeclareIdentifier o = (HNDeclareIdentifier) node;
            this.initializer = JNodeUtils.bindCopy(this, copyFactory, o.initializer);
            this.initValue = JNodeUtils.bindCopy(this, copyFactory, o.initValue);
            this.hiddenInitValue = JNodeUtils.bindCopy(this, copyFactory, o.hiddenInitValue);
            this.modifiers = (o.modifiers);
            this.identifierToken = JNodeUtils.bindCopy(this, copyFactory, o.identifierToken);
            this.identifierTypeName = JNodeUtils.bindCopy(this, copyFactory, o.identifierTypeName);
            this.initValueConstraint = o.initValueConstraint;
            this.varArg = (o.varArg);
            this.assignOperator = (o.assignOperator);
            this.mainConstructor = (o.mainConstructor);
            this.declaringType = (o.declaringType);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(identifierTypeName, identifierToken, initializer,initValue);
    }

    @Override
    public AbstractJNode parentNode(JNode parentNode) {
        return super.parentNode(parentNode);
    }

    public JType getEffectiveIdentifierType() {
        return effectiveIdentifierType;
    }

    public HNDeclareIdentifier setEffectiveIdentifierType(JType effectiveIdentifierType) {
        this.effectiveIdentifierType = effectiveIdentifierType;
        return this;
    }

    public SyntacticType getSyntacticType() {
        return syntacticType;
    }

    public HNDeclareIdentifier setSyntacticType(SyntacticType syntacticType) {
        this.syntacticType = syntacticType;
        return this;
    }

    public enum SyntacticType{
        FIELD,
        LOCAL,
        ARG,
    }
}
