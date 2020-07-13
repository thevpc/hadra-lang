package net.hl.compiler.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.hl.compiler.index.HIndexedProject;

import java.util.Arrays;
import java.util.List;

import net.vpc.common.jeep.JNodeCopyFactory;

public class HNDeclareMetaPackage extends HNode {
    private HNMetaPackageId moduleId;
//    private JModuleId effectiveModuleId;
//    private List<HLDependency> effectiveDependencies;
//    private Set<String> effectiveDependencyFiles;
    private HNBlock body;
    private HIndexedProject index;

    public HNDeclareMetaPackage() {
        super(HNNodeId.H_DECLARE_META_PACKAGE);
    }

    public HNDeclareMetaPackage(JToken token) {
        this();
        setStartToken(token);
    }

    public HIndexedProject getIndex() {
        return index;
    }

    public HNDeclareMetaPackage setIndex(HIndexedProject index) {
        this.index = index;
        return this;
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
        JNodeUtils.findAndReplaceNext(this,findAndReplace,this::getBody,this::setBody);
    }

//    public JModuleId getEffectiveModuleId() {
//        return effectiveModuleId;
//    }
//
//    public HNDeclareMetaPackage setEffectiveModuleId(JModuleId effectiveModuleId) {
//        this.effectiveModuleId = effectiveModuleId;
//        return this;
//    }
//
//    public List<HLDependency> getEffectiveDependencies() {
//        return effectiveDependencies;
//    }
//
//    public HNDeclareMetaPackage setEffectiveDependencies(List<HLDependency> effectiveDependencies) {
//        this.effectiveDependencies = effectiveDependencies;
//        return this;
//    }
//
//    public Set<String> getEffectiveDependencyFiles() {
//        return effectiveDependencyFiles;
//    }
//
//    public HNDeclareMetaPackage setEffectiveDependencyFiles(Set<String> effectiveDependencyFiles) {
//        this.effectiveDependencyFiles = effectiveDependencyFiles;
//        return this;
//    }

    @Override
    public List<JNode> getChildrenNodes() {
        return Arrays.asList(moduleId, body);
    }

    public HNMetaPackageId getModuleId() {
        return moduleId;
    }

    public HNDeclareMetaPackage setModuleId(HNMetaPackageId moduleId) {
        this.moduleId=JNodeUtils.bind(this,moduleId,"moduleId");
        return this;
    }

    public HNBlock getBody() {
        return body;
    }

    public HNDeclareMetaPackage setBody(HNBlock body) {
        this.body=JNodeUtils.bind(this,body,"body");
        return this;
    }

    @Override
    public String toString() {
        return "package "+moduleId
                +(body !=null?(" "+ body):"")
                ;
    }

    @Override
    public void copyFrom(JNode other,JNodeCopyFactory copyFactory) {
        super.copyFrom(other,copyFactory);
        if (other instanceof HNDeclareMetaPackage) {
            HNDeclareMetaPackage o = (HNDeclareMetaPackage) other;
            this.setModuleId((HNMetaPackageId) JNodeUtils.bindCopy(this, copyFactory, o.getModuleId()));
            this.setBody((HNBlock) JNodeUtils.bindCopy(this, copyFactory, o.getBody()));
        }
    }

}
