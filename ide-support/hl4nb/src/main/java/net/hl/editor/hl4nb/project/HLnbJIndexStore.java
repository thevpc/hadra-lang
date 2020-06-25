package net.hl.editor.hl4nb.project;

import net.vpc.common.jeep.JIndexStoreMemory;

//should replace this with Netbeans Index Mechanism based on Lucene
public class HLnbJIndexStore extends JIndexStoreMemory {
    private String projectPath;

    public HLnbJIndexStore(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getProjectPath() {
        return projectPath;
    }
}
