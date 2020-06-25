package net.hl.compiler.stages;

import net.hl.compiler.core.HLOptions;
import net.hl.compiler.core.HLProject;

public interface HLCStage {
    void processProject(HLProject project, HLOptions options);

}
