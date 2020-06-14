package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HNObjectNew extends HNode {
    private HNTypeToken objectTypeName;
    private JNode[] inits;
    private JInvokablePrefilled constructor;

    protected HNObjectNew() {
        super(HNNodeId.H_OBJECT_NEW);
    }

    public HNObjectNew(HNTypeToken objectTypeName, JNode[] inits, JToken startToken, JToken endToken) {
        this();
        setObjectTypeName(objectTypeName);
        setInits(inits);
        setStartToken(startToken);
        setEndToken(endToken);
    }



    @Override
    public List<JNode> childrenNodes() {
        List<JNode> li = new ArrayList<>();
        li.add(this.objectTypeName);
        li.addAll(Arrays.asList(this.inits));
        return li;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getInits());
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNObjectNew) {
            HNObjectNew o =(HNObjectNew) node;
            this.objectTypeName=JNodeUtils.bindCopy(this, copyFactory, o.objectTypeName);
            this.inits=JNodeUtils.bindCopy(this, copyFactory, o.inits);
            this.constructor=(o.constructor);
        }
    }


    public JInvokablePrefilled getConstructor() {
        return constructor;
    }

    public void setConstructor(JInvokablePrefilled constructor) {
        this.constructor = constructor;
    }

    public HNTypeToken getObjectTypeName() {
        return objectTypeName;
    }

    public void setObjectTypeName(HNTypeToken objectTypeName) {
        this.objectTypeName=JNodeUtils.bind(this,objectTypeName,"objectTypeName");
    }

    public void setInits(JNode[] inits) {
        this.inits = JNodeUtils.bind(this,inits,"inits");
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(objectTypeName.getTypenameOrVar().name());
        sb.append("(");
        for (int i = 0; i < inits.length; i++) {
            if(i>0){
                sb.append(",");
            }
            sb.append(inits[i].toString());
        }
        sb.append(")");
        return sb.toString();
    }

    public JNode[] getInits() {
        return inits;
    }
}
