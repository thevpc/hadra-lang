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

import net.vpc.common.textsource.JTextSource;
import net.hl.compiler.core.HLProject;
import net.hl.compiler.parser.ast.HNDeclareType;

/**
 *
 * @author vpc
 */
public class JavaNodes {

    private HNDeclareType metaPackage;
    private List<HNDeclareType> topLevelTypes = new ArrayList<>();
    private Set<JTextSource> metaPackageSources = new HashSet<JTextSource>();

    public static JavaNodes of(HLProject project) {
        return (JavaNodes) project.getUserProperties().computeIfAbsent(JavaNodes.class.getName(), (k) -> new JavaNodes());
    }

    public JavaNodes() {
    }

    public Set<JTextSource> getMetaPackageSources() {
        return metaPackageSources;
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
