/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.ast;

import net.hl.compiler.utils.HTokenUtils;
import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JNodeCopyFactory;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.util.JeepUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNAnnotationList extends HNode {
    private HNAnnotationCall[] children;

    private HNAnnotationList() {
        super(HNNodeId.H_ANNOTATION_LIST);
        children = new HNAnnotationCall[0];
    }

    public HNAnnotationList(HNAnnotationCall[] children, JToken[] separators, JToken startToken, JToken endToken) {
        this();
        setChildren(children);
        setStartToken(startToken);
        setEndToken(endToken);
        setSeparators(separators);
    }

    public static boolean isStatic(HNAnnotationCall[] annotations) {
        return isModifier("static", annotations);
    }

    public static boolean isPublic(HNAnnotationCall[] annotations) {
        return isModifier("public", annotations);
    }

    public static boolean isPrivate(HNAnnotationCall[] annotations) {
        return isModifier("private", annotations);
    }

    public static boolean isProtected(HNAnnotationCall[] annotations) {
        return isModifier("protected", annotations);
    }

    public static boolean isModifier(String modifierName, HNAnnotationList a) {
        return a != null && isModifier(modifierName, a.children);
    }

    public static boolean isModifier(String modifierName, HNAnnotationCall[] a) {
        return a != null && Arrays.stream(a).anyMatch(x -> isModifier(modifierName, x));
    }

    public static boolean isModifier(String modifierName, HNAnnotationCall a) {
        if (a == null) {
            return false;
        }
        if (a.getName() instanceof HNTypeTokenSpecialAnnotation) {
            HNTypeTokenSpecialAnnotation sa = (HNTypeTokenSpecialAnnotation) a.getName();
            return sa.getTypename().name().equals(modifierName);
        }
        return false;
    }

    public static int size(HNAnnotationList a) {
        return a == null ? 0 : a.size();
    }

    public static HNAnnotationList nonNull(HNAnnotationCall[] a) {
        return a == null ? new HNAnnotationList() : new HNAnnotationList(a,null,null,null);
    }

    public static HNAnnotationList nonNull(HNAnnotationList a) {
        return a == null ? new HNAnnotationList() : a;
    }

    public static HNAnnotationCall[] publify(HNAnnotationCall[] a) {
        return new HNAnnotationList(a,null,null,null).publify().toArray();
    }

    public static HNAnnotationList publify(HNAnnotationList a) {
        return nonNull(a).publify();
    }

    public static HNAnnotationCall[] toArray(HNAnnotationList a) {
        return nonNull(a).toArray();
    }

    public static boolean isAbstract(HNAnnotationList a) {
        return isModifier("abstract",a);
    }

    public static boolean isAbstract(HNAnnotationCall[] a) {
        return isModifier("abstract",a);
    }

    public int size() {
        return children == null ? 0 : children.length;
    }

    public HNAnnotationCall[] getChildren() {
        return children;
    }

    public HNAnnotationList setChildren(HNAnnotationCall[] children) {
        this.children = JNodeUtils.bind(this, children, "children");
        return this;
    }

    public HNode addChildren(HNAnnotationCall[] annotations) {
        return setAnnotations(JeepUtils.arrayConcatNonNull(HNAnnotationCall.class, this.children, annotations));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (HNode child : children) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(child);
        }
        return sb.toString();
    }

    public void copyFrom(JNode node, JNodeCopyFactory copyFactory) {
        super.copyFrom(node, copyFactory);
        if (node instanceof HNAnnotationList) {
            HNAnnotationList o = (HNAnnotationList) node;
            this.children = JNodeUtils.bindCopy(this, copyFactory, o.children, HNAnnotationCall.class);
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(children);
    }

    public boolean isModifier(String modifierName) {
        return isModifier(modifierName, this);
    }

    public HNAnnotationList publify() {
        if (!isModifier("public") && !isModifier("private") && !isModifier("protected")) {
            return addModifier("public");
        }
        return this;
    }

    public HNAnnotationList addModifier(String modifierName) {
        if (isModifier(modifierName)) {
            return this;
        }
        return (HNAnnotationList) ((HNAnnotationList) copy()).addChildren(new HNAnnotationCall[]{
                HNAnnotationCall.ofModifier(modifierName)
        });
    }

    public HNAnnotationCall[] toArray() {
        return children == null ? new HNAnnotationCall[0] : children;
    }
}
