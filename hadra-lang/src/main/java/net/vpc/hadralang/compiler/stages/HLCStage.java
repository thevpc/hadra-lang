package net.vpc.hadralang.compiler.stages;

import net.vpc.hadralang.compiler.core.HLOptions;
import net.vpc.hadralang.compiler.core.HLProject;

public interface HLCStage {
    void processProject(HLProject project, HLOptions options);

}
