/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.hl.compiler.utils.HTokenUtils;
import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNAnnotationCall extends HNode {

    private HNode name;
    private HNode[] args;
    public HNAnnotationCall() {
        super(HNNodeId.H_ANNOTATION);
    }

    public HNAnnotationCall(HNode name, HNode[] args, JTokenBounds bounds) {
        this();
        setName(name);
        setArgs(args);
        setBounds(bounds);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getArgs());
    }


    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(args);
    }


    public HNode[] getArgs() {
        return args;
    }

    public HNAnnotationCall setArgs(HNode[] args) {
        this.args = JNodeUtils.bind(this,args,"args");
        return this;
    }

    public HNode getName() {
        return name;
    }

    public HNAnnotationCall setName(HNode name) {
        this.name = JNodeUtils.bind(this, name,"name");
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("@").append(name);
        if(args!=null && args.length>0) {
            sb.append("(");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                String sargi = args[i].toString();
                sb.append(sargi);
            }
            sb.append(")");
        }
        return sb.toString();
    }

    public JNode get(int index) {
        return args[index];
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNAnnotationCall) {
            HNAnnotationCall o = (HNAnnotationCall) node;
            this.name = JNodeUtils.bindCopy(this, copyFactory, o.name);
            this.args = JNodeUtils.bindCopy(this, copyFactory, o.args,HNode.class);
        }
    }

    public static HNAnnotationCall ofModifier(String modifierName){
        return new HNAnnotationCall(
                new HNTypeTokenSpecialAnnotation(HTokenUtils.createToken(modifierName)),
                null, null
        );
    }
}
