/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.stages.generators.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JSource;
import net.vpc.hadralang.compiler.core.HLProject;
import net.vpc.hadralang.compiler.parser.ast.HNDeclareType;

/**
 *
 * @author vpc
 */
public class JavaNodes {

    private HNDeclareType metaPackage;
    private List<HNDeclareType> topLevelTypes = new ArrayList<>();
    private Set<JSource> metaPackageSources = new HashSet<JSource>();

    public static JavaNodes of(HLProject project) {
        return (JavaNodes) project.getUserProperties().computeIfAbsent(JavaNodes.class.getName(), (k) -> new JavaNodes());
    }

    public JavaNodes() {
    }

    public Set<JSource> getMetaPackageSources() {
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
