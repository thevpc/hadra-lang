/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author vpc
 */
public class HFileUtils {

    public static <T> T coalesce(T ... all) {
        for (T t : all) {
            if(t!=null){
                if(t instanceof String){
                    if(!t.toString().trim().isEmpty()){
                        return t;
                    }
                }else{
                    return t;
                }
            }
        }
        return null;
    }
    
    public static Path getTarget(String s) {
         if (s == null || s.isEmpty()) {
            return getPath("target");
        }
        return getPath("target", getPath(s));
    }

    public static Path getPath(String p) {
        if (p == null || p.isEmpty()) {
            return new File(".").toPath();
        }
        return Paths.get(p);
    }

    public static Path getPath(String s, Path parent) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("error");
        }
        Path p = Paths.get(s);
        if (p.isAbsolute()) {
            return p;
        }
        return parent.resolve(p);
    }
}
