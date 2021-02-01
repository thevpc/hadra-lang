/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.utils;

import java.util.Objects;

/**
 *
 * @author vpc
 */
public class DepIdAndFile {
    private String id;
    private String file;

    public DepIdAndFile(String id, String file) {
        this.id = id;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public String getFile() {
        return file;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        hash = 97 * hash + Objects.hashCode(this.file);
        return hash;
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
        final DepIdAndFile other = (DepIdAndFile) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.file, other.file)) {
            return false;
        }
        return true;
    }
    
    
}
