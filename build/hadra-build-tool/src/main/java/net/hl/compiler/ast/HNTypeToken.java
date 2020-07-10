package net.hl.compiler.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.*;

public class HNTypeToken extends HNode {
    private JType typeVal;
    private JToken nameToken;
    private JTypeNameOrVariable typename;
    private List<HNTypeToken> vars = new ArrayList<>();
    private List<HNTypeToken> lowerBounds = new ArrayList<>();
    private List<HNTypeToken> upperBounds = new ArrayList<>();

    protected HNTypeToken() {
        super(HNNodeId.H_TYPE_TOKEN);
    }

    public HNTypeToken(JType type, JToken startToken) {
        this();
        this.typeVal = type;
        this.typename = type.typeName();
        setStartToken(startToken);
    }

    public HNTypeToken(JToken nameToken, JTypeNameOrVariable typename, HNTypeToken[] vars,
                       HNTypeToken[] lowerBounds, HNTypeToken[] upperBounds,
                       JToken startToken, JToken endToken) {
        this();
        this.typeVal = null;
        this.typename = typename;
        this.nameToken = nameToken;
        this.setVars(vars==null?Collections.emptyList():Arrays.asList(vars));
        this.setLowerBounds(lowerBounds==null?Collections.emptyList():Arrays.asList(lowerBounds));
        this.setUpperBounds(upperBounds==null?Collections.emptyList():Arrays.asList(upperBounds));
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public void setVars(List<HNTypeToken> vars) {
        this.vars = JNodeUtils.bind(this,vars,"vars");
    }

    public void setLowerBounds(List<HNTypeToken> lowerBounds) {
        this.lowerBounds = JNodeUtils.bind(this,vars,"lowerBounds");
    }

    public void setUpperBounds(List<HNTypeToken> upperBounds) {
        this.upperBounds = JNodeUtils.bind(this,vars,"upperBounds");
    }
    
    

    public JToken getNameToken() {
        return nameToken;
    }

    public JTypeNameOrVariable getTypenameOrVar() {
        return typename;
    }

    public JTypeName getTypename() {
        return (JTypeName) typename;
    }

    public List<HNTypeToken> vars() {
        return vars;
    }

    public List<HNTypeToken> getLowerBounds() {
        return lowerBounds;
    }

    public List<HNTypeToken> getUpperBounds() {
        return upperBounds;
    }

    public JType getTypeVal() {
        return typeVal;
    }

    public HNTypeToken setTypeVal(JType typeVal) {
        this.typeVal = typeVal;
        return this;
    }

    @Override
    public String toString() {
        return typename.name();
    }

    @Override
    public List<JNode> getChildrenNodes() {
        List<JNode> list=new ArrayList<>();
        list.addAll(vars);
        list.addAll(lowerBounds);
        list.addAll(upperBounds);
        return list;
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNTypeToken) {
            HNTypeToken o = (HNTypeToken) node;
            this.typeVal = o.typeVal;
            this.typename = o.typename;
            this.vars = JNodeUtils.bindCopy(this, copyFactory, o.vars);
            this.lowerBounds = JNodeUtils.bindCopy(this, copyFactory, o.lowerBounds);
            this.upperBounds = JNodeUtils.bindCopy(this, copyFactory, o.upperBounds);
//            this.XXX=bindCopy(o.XXX);
        }
    }

    @Override
    public void visit(JNodeVisitor visitor) {
        visitor.startVisit(this);
        visitNext(visitor, getChildrenNodes());
        visitor.endVisit(this);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }

    public HNTypeToken componentType(){
        HNTypeToken c = (HNTypeToken) copy();
        c.typename=c.getTypename().componentType();
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HNTypeToken that = (HNTypeToken) o;
        return Objects.equals(typename, that.typename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typename);
    }
}
