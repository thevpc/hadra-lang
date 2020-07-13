package net.hl.compiler.stages.generators.java;

import net.hl.compiler.core.HProject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HGenGlobalContext {
    private HProject project;

    private Set<String> generatedFiles = new HashSet<>();

    public HGenGlobalContext(HProject project) {
        this.project = project;
    }

    public HProject project() {
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
