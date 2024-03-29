package net.hl.compiler.ast;

import net.hl.compiler.core.elements.HNElement;
import net.thevpc.jeep.JImportInfo;
import net.thevpc.jeep.JNode;
import net.thevpc.jeep.JNodeCopyFactory;
import net.thevpc.jeep.core.nodes.AbstractJNode;
import net.thevpc.jeep.util.JNodeUtils;
import net.thevpc.jeep.util.JeepUtils;

import java.util.*;

public abstract class HNode extends AbstractJNode {
    private HNAnnotationCall[] annotations = new HNAnnotationCall[0];
    
    /**
     * used by generators/transpillers for languages that support modifiers (such as java and C#)
     */
    private Set<String> modifierKeys = new LinkedHashSet<>();
    /**
     * relevant when this expression is passed as a named argument.
     * defaults to null
     */
    private HNNodeId id;
    private Set<JImportInfo> imports = new LinkedHashSet<>();
    private HNElement element;

    public HNode(HNNodeId id) {
        this.id = id;
    }

    public final HNNodeId id() {
        return id;
    }

    public Set<String> getModifierKeys() {
        return modifierKeys;
    }
    

    public HNAnnotationCall[] getAnnotations() {
        return annotations;
    }

    public HNode setAnnotations(HNAnnotationCall[] annotations) {
        this.annotations = JNodeUtils.bind(this, annotations, "annotations");
        return this;
    }

    public HNode addAnnotationsNoDuplicates(HNAnnotationCall ... annotations) {
        for (HNAnnotationCall annotation : annotations) {
            addAnnotationNoDuplicates(annotation);
        }
        return this;
    }

    public HNode addAnnotationNoDuplicates(HNAnnotationCall annotation) {
        for (HNAnnotationCall hnAnnotationCall : annotations) {
            if(hnAnnotationCall.equals(annotation)){
                return this;
            }
        }
        return addAnnotations(annotation);
    }

    public HNode addModifierKeys(String ... mod) {
        getModifierKeys().addAll(Arrays.asList(mod));
        return this;
    }
    
    public HNode addAnnotations(HNAnnotationCall ... annotations) {
        return setAnnotations(JeepUtils.arrayConcatNonNull(HNAnnotationCall.class, this.annotations, annotations));
    }
    public HNode removeAnnotations(HNAnnotationCall ... annotations) {
        List<HNAnnotationCall> a= new ArrayList<>(Arrays.asList(getAnnotations()));
        a.removeAll(Arrays.asList(annotations));
        return setAnnotations(a.toArray(new HNAnnotationCall[0]));
    }

    public HNode[] getExitPoints() {
        return new HNode[]{this};
    }

    public Set<JImportInfo> getEffectiveImports() {
        Set<JImportInfo> s = new LinkedHashSet<>();
        s.addAll(getParentNode().getEffectiveImports());
        Set<JImportInfo> i = getImports();
        if (i != null) {
            for (JImportInfo jImportInfo : i) {
                if (jImportInfo != null) {
                    s.add(jImportInfo);
                }
            }
        }
        return s;
    }

    public Set<JImportInfo> getImports() {
        return imports;
    }

    public HNode setImports(Set<JImportInfo> imports) {
        this.imports = imports;
        return this;
    }

    public HNElement getElement() {
        return element;
    }

    public HNode setElement(HNElement element) {
        this.element = element;
        return this;
    }

    public String fullChildInfo() {
        JNode pn = getParentNode();
        if (pn == null) {
            return String.valueOf(getChildInfo());
        }
        return pn.getClass().getSimpleName() + ":" + getChildInfo();
    }

    @Override
    public HNode getParentNode() {
        return (HNode) super.getParentNode();
    }

    public void copyFrom(JNode other, JNodeCopyFactory copyFactory) {
        super.copyFrom(other, copyFactory);
        if (other instanceof HNode) {
            HNode hnode = (HNode) other;
            setElement(hnode.getElement());
            setImports(new LinkedHashSet<>(hnode.getImports()));
            this.annotations = JNodeUtils.bindCopy(this, copyFactory, hnode.annotations, HNAnnotationCall.class);
        }
    }

    @Override
    public HNode copy() {
        return (HNode) super.copy();
    }

    @Override
    public HNode copy(JNodeCopyFactory copyFactory) {
        return (HNode) super.copy(copyFactory);
    }
}
