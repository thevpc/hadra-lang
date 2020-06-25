package net.hl.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HNArrayNew extends HNode {
    private HNTypeToken arrayTypeName;
    private JType arrayType;
    private HNode[] inits;
    //constructor call!!
    private HNode constructor;

    public HNArrayNew() {
        super(HNNodeId.H_ARRAY_NEW);
    }

    public HNArrayNew(HNTypeToken arrayTypeName, HNode[] inits, HNode constructor, JToken startToken, JToken endToken) {
        this();
        setArrayTypeName(arrayTypeName);
        setInits(inits);
        setConstructor(constructor);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    @Override
    public void visit(JNodeVisitor visitor) {
        visitor.startVisit(this);
        visitNext(visitor, this.inits);
        visitNext(visitor, this.constructor);
        visitor.endVisit(this);
    }

    @Override
    public List<JNode> childrenNodes() {
        List<JNode> li = new ArrayList<>();
        li.add(arrayTypeName);
        li.addAll(Arrays.asList(inits));
        li.add(constructor);
        return li;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this.getInits());
        JNodeUtils.findAndReplaceNext(this, findAndReplace, this::getConstructor, this::setConstructor);
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNArrayNew) {
            HNArrayNew o = (HNArrayNew) node;
            this.arrayTypeName = o.arrayTypeName;
            this.arrayType = o.arrayType;
            this.inits = JNodeUtils.bindCopy(this, copyFactory, o.inits,HNode.class);
            this.constructor = JNodeUtils.bindCopy(this, copyFactory, o.constructor);
        }
    }


    public HNode getConstructor() {
        return constructor;
    }

    public void setConstructor(HNode constructor) {
        this.constructor = JNodeUtils.bind(this,constructor, "constructor");
    }

    public HNTypeToken getArrayTypeName() {
        return arrayTypeName;
    }

    public HNArrayNew setArrayTypeName(HNTypeToken arrayTypeName) {
        this.arrayTypeName=JNodeUtils.bind(this,arrayTypeName, "arrayTypeName");
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(arrayTypeName.getTypename().rootComponentType().name());
        for (int i = 0; i < arrayTypeName.getTypename().arrayDimension(); i++) {
            sb.append("[");
            if (i < inits.length) {
                sb.append(inits[i].toString());
            }
            sb.append("]");
        }
        if (constructor != null) {
            sb.append("(");
            sb.append(constructor.toString());
            sb.append(")");
        }
        return sb.toString();
    }

    public JTypeArray getArrayType() {
        if (arrayType != null && !(arrayType instanceof JTypeArray)) {
            return (JTypeArray) arrayType;
        }
        return (JTypeArray) arrayType;
    }

    public HNArrayNew setArrayType(JType arrayType) {
        this.arrayType = arrayType;
        return this;
    }

    public HNode[] getInits() {
        return inits;
    }

    public HNArrayNew setInits(HNode[] inits) {
        this.inits = JNodeUtils.bind(this,inits, "init");
        return this;
    }

}
