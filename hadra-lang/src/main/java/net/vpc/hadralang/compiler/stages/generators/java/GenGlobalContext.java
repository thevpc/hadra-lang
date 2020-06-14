package net.vpc.hadralang.compiler.stages.generators.java;

import net.vpc.hadralang.compiler.core.HLProject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GenGlobalContext {
    private HLProject project;

    private Set<String> generatedFiles = new HashSet<>();

    public GenGlobalContext(HLProject project) {
        this.project = project;
    }

    public HLProject project() {
        return project;
    }

    public File nextFile(String name, String suffix, File folder) {
        int i = 1;
        while (true) {
            File f = new File(folder, (i == 1 ? name : (name + "$" + i)) + suffix);
            String cp = null;
            try {
                cp = f.getCanonicalPath();
            } catch (IOException e) {
                cp = f.getAbsolutePath();
            }
            if (!generatedFiles.contains(cp)) {
                generatedFiles.add(cp);
                return f;
            }
            i++;
        }
    }
}
