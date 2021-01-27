/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.stages.generators.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.hl.compiler.ast.HNBlock;

import net.thevpc.common.textsource.JTextSource;
import net.hl.compiler.core.HProject;
import net.hl.compiler.ast.HNDeclareType;
import net.hl.compiler.ast.HNode;

/**
 *
 * @author vpc
 */
public class HJavaNodes {

    private HNDeclareType metaPackage;
    private List<HNDeclareType> topLevelTypes = new ArrayList<>();
    private Set<JTextSource> metaPackageSources = new HashSet<JTextSource>();
    private List<String> javaFiles = new ArrayList<>();

    public static HJavaNodes of(HProject project) {
        return (HJavaNodes) project.getUserProperties().computeIfAbsent(HJavaNodes.class.getName(), (k) -> new HJavaNodes());
    }

    public HJavaNodes() {
    }

    public List<String> getJavaFiles() {
        return javaFiles;
    }

    public Set<JTextSource> getMetaPackageSources() {
        return metaPackageSources;
    }

    public HNBlock getMetaPackageBody() {
        HNBlock metaBody = (HNBlock) getMetaPackage().getBody();
        if (metaBody == null) {
            metaBody = new HNBlock(HNBlock.BlocType.CLASS_BODY, new HNode[0], null, null);
            getMetaPackage().setBody(metaBody);
        }
        return metaBody;
    }

    public HNDeclareType getMetaPackage() {
        return metaPackage;
    }

    public void setMetaPackage(HNDeclareType metaPackage) {
        this.metaPackage = metaPackage;
    }

    public List<HNDeclareType> getTopLevelTypes() {
        return topLevelTypes;
    }

    public void setTopLevelTypes(List<HNDeclareType> topLevelTypes) {
        this.topLevelTypes = topLevelTypes;
    }

}
