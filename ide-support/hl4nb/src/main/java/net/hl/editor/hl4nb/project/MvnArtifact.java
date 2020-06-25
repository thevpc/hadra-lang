/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.editor.hl4nb.project;

import java.util.Objects;

/**
 *
 * @author vpc
 */
public class MvnArtifact {

    private final String groupId;
    private final String artifactId;
    private final String version;

    public MvnArtifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public MvnArtifact setVersion(String version) {
        return new MvnArtifact(groupId, artifactId, version);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.groupId);
        hash = 19 * hash + Objects.hashCode(this.artifactId);
        hash = 19 * hash + Objects.hashCode(this.version);
        return hash;
    }

    public boolean equalsNoVersion(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MvnArtifact other = (MvnArtifact) obj;
        if (!Objects.equals(this.groupId, other.groupId)) {
            return false;
        }
        if (!Objects.equals(this.artifactId, other.artifactId)) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MvnArtifact other = (MvnArtifact) obj;
        if (!Objects.equals(this.groupId, other.groupId)) {
            return false;
        }
        if (!Objects.equals(this.artifactId, other.artifactId)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        return true;
    }

}
