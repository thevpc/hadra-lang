/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.stages.generators.java;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.hl.compiler.ast.HNBlock;

import net.hl.compiler.core.HProject;
import net.hl.compiler.ast.HNDeclareType;
import net.hl.compiler.ast.HNode;
import net.thevpc.jeep.source.JTextSource;

/**
 *
 * @author vpc
 */
public class HJavaContextHelper {

    private HNDeclareType metaPackage;
    private List<HNDeclareType> topLevelTypes = new ArrayList<>();
    private Set<JTextSource> metaPackageSources = new HashSet<>();
    private List<String> javaFiles = new ArrayList<>();
    private File outputJarFile;

    public static HJavaContextHelper of(HProject project) {
        return (HJavaContextHelper) project.getUserProperties().computeIfAbsent(HJavaContextHelper.class.getName(), (k) -> new HJavaContextHelper());
    }

    public HJavaContextHelper() {
    }

    public List<String> getJavaFiles() {
        return javaFiles;
    }

    public File getOutputJarFile() {
        return outputJarFile;
    }

    public void setOutputJarFile(File outputJarFile) {
        this.outputJarFile = outputJarFile;
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
