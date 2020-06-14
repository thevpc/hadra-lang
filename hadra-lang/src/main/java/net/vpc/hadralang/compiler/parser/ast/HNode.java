package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.nodes.AbstractJNode;
import net.vpc.hadralang.compiler.core.elements.HNElement;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class HNode extends AbstractJNode {

    ///////////////////////////////////////////////////////////////////////////////////////
    public static final int STAGE_1_DECLARATIONS = 1;
    public static final int STAGE_2_WIRE_TYPES = 2;
    public static final int STAGE_3_WIRE_CALLS = 3;
    private HNNodeId id;
    private Set<JImportInfo> imports = new LinkedHashSet<>();
    private HNElement element;

    public HNode(HNNodeId id) {
        this.id = id;
    }

    public final HNNodeId id() {
        return id;
    }

    public void copyFrom(JNode other,JNodeCopyFactory copyFactory) {
        super.copyFrom(other,copyFactory);
        if (other instanceof HNode) {
            HNode hnode = (HNode) other;
            setElement(hnode.getElement());
            setImports(new LinkedHashSet<>(hnode.getImports()));
        }
    }

    public HNode[] getExitPoints() {
        return new HNode[]{this};
    }

    public Set<JImportInfo> getEffectiveImports() {
        Set<JImportInfo> s = new LinkedHashSet<>();
        s.addAll(((HNode) parentNode()).getEffectiveImports());
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
        JNode pn = parentNode();
        if (pn == null) {
            return String.valueOf(childInfo());
        }
        return pn.getClass().getSimpleName() + ":" + childInfo();
    }

    @Override
    public HNode copy() {
        return (HNode)super.copy();
    }

    @Override
    public HNode copy(JNodeCopyFactory copyFactory) {
        return (HNode)super.copy(copyFactory);
    }

    @Override
    public HNode parentNode() {
        return (HNode) super.parentNode();
    }
}
