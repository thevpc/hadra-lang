package net.hl.compiler.stages;

import net.hl.compiler.HL;
import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.HTask;

public interface HStage {
    void processProject(HProject project, HOptions options);

    public HTask[] getTasks();
    
    public boolean isEnabled(HProject project, HL options);

}
