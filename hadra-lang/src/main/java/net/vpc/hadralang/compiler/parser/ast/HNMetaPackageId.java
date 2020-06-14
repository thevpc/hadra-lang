/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.hadralang.compiler.core.JModuleId;

import java.util.Arrays;
import java.util.List;

/**
 * @author vpc
 */
public class HNMetaPackageId extends HNode {
    HNMetaPackageGroup group = null;
    HNMetaPackageArtifact artifact = null;
    HNMetaPackageVersion version = null;
    JToken lastDot=null;
    JToken colonToken =null;
    JToken sharpToken =null;

    public HNMetaPackageId() {
        super(HNNodeId.H_META_PACKAGE_ID);
    }

    public HNMetaPackageGroup getGroup() {
        return group;
    }

    public HNMetaPackageId setGroup(HNMetaPackageGroup group) {
        this.group=JNodeUtils.bind(this,group,"group");
        return this;
    }

    public HNMetaPackageArtifact getArtifact() {
        return artifact;
    }

    public HNMetaPackageId setArtifact(HNMetaPackageArtifact artifact) {
        this.artifact=JNodeUtils.bind(this,artifact,"artifact");
        return this;
    }

    public HNMetaPackageVersion getVersion() {
        return version;
    }

    public HNMetaPackageId setVersion(HNMetaPackageVersion version) {
        this.version= JNodeUtils.bind(this,version,"version");
        return this;
    }

    public JToken getLastDot() {
        return lastDot;
    }

    public HNMetaPackageId setLastDot(JToken lastDot) {
        this.lastDot = lastDot;
        return this;
    }

    public JToken getColonToken() {
        return colonToken;
    }

    public HNMetaPackageId setColonToken(JToken colonToken) {
        this.colonToken = colonToken;
        return this;
    }

    public JToken getSharpToken() {
        return sharpToken;
    }

    public HNMetaPackageId setSharpToken(JToken sharpToken) {
        this.sharpToken = sharpToken;
        return this;
    }

    private String getPackageString(){
        StringBuilder sb=new StringBuilder();
        if(group !=null) {
            for (JToken token : group.getTokens()) {
                sb.append(token.sval);
            }
        }
        return sb.toString();
    }

    private String getNameString(){
        StringBuilder sb=new StringBuilder();
        if(artifact !=null) {
            for (JToken token : artifact.getTokens()) {
                sb.append(token.sval);
            }
        }
        return sb.toString();
    }

    private String getVersionString(){
        StringBuilder sb=new StringBuilder();
        if(version!=null) {
            for (JToken token : version.getTokens()) {
                sb.append(token.sval);
            }
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb=new StringBuilder();
        if(group !=null){
            for (JToken token : group.getTokens()) {
                sb.append(token.sval);
            }
        }
        if(artifact !=null){
            if(sb.length()>0){
                sb.append(":");
            }
            for (JToken token : artifact.getTokens()) {
                sb.append(token.sval);
            }
        }
        if(version!=null){
            if(sb.length()>0){
                sb.append("#");
            }
            for (JToken token : version.getTokens()) {
                sb.append(token.sval);
            }
        }
        return sb.toString();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNMetaPackageId) {
            HNMetaPackageId o = (HNMetaPackageId) node;
//            this.XXX=bindCopy(o.XXX);
            this.group = JNodeUtils.bindCopy(this, copyFactory, o.group);
            this.artifact = JNodeUtils.bindCopy(this, copyFactory, o.artifact);
            this.version = JNodeUtils.bindCopy(this, copyFactory, o.version);
            this.colonToken = o.colonToken;
            this.sharpToken = o.sharpToken;
        }
    }


    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(group, artifact,version);
    }
    public JModuleId toJModuleId(){
        return new JModuleId(
                getPackageString(),getNameString(),getVersionString()
        );
    }
}
