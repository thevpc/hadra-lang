package net.hl.compiler.parser.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.vpc.common.jeep.JNodeCopyFactory;

public class HNMetaImportPackage extends HNode {
    private HNode importedPackageNode;
    private String scope;
    private List<HNode> exclusions=new ArrayList<>();
    private boolean optional;
    public HNMetaImportPackage() {
        super(HNNodeId.H_META_IMPORT_PACKAGE);
    }
    public HNMetaImportPackage(JToken token) {
        this();
        setStartToken(token);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getImportedPackageNode,this::setImportedPackageNode);
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this.getExclusions());
    }


    @Override
    public List<JNode> childrenNodes() {
        List<JNode> li=new ArrayList<>();
        li.add(this.importedPackageNode);
        li.addAll(this.exclusions);
        return li;
    }

    public boolean isOptional() {
        return optional;
    }

    public HNMetaImportPackage setOptional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public HNode getImportedPackageNode() {
        return importedPackageNode;
    }

    public HNMetaImportPackage setImportedPackageNode(HNode importedPackageNode) {
        this.importedPackageNode=JNodeUtils.bind(this,importedPackageNode,"importedPackage");
        return this;
    }

    public String getScope() {
        return scope;
    }

    public HNMetaImportPackage setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public void addExclusion(HNode node) {
        exclusions.add(JNodeUtils.bind(this,node,"exclusions",exclusions.size()));
    }

    public List<HNode> getExclusions() {
        return exclusions;
    }

    public HNMetaImportPackage setExclusions(List<HNode> exclusions) {
        this.exclusions = JNodeUtils.bind(this,exclusions,"exclusions");
        return this;
    }

    @Override
    public void copyFrom(JNode other,JNodeCopyFactory copyFactory) {
        super.copyFrom(other,copyFactory);
        if(other instanceof HNMetaImportPackage){
            HNMetaImportPackage o=(HNMetaImportPackage)other;
            this.setImportedPackageNode(JNodeUtils.bindCopy(this, copyFactory, o.getImportedPackageNode()));
            this.setScope(o.getScope());
            this.setExclusions(new ArrayList<>(JNodeUtils.bindCopy(this, copyFactory, o.getExclusions())));
        }
    }

    @Override
    public String toString() {
        return
                "import package "
                        +importedPackageNode
                        +" for "
                                +(optional?"optional ":"")
                                +(scope==null?"compile":scope)
                        +((exclusions.size()>0)?
                        (" "+exclusions.stream().map(x->x.toString()).collect(Collectors.joining(","))) :
                            "");
    }
}
