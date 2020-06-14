package net.vpc.hadralang.compiler.stages;

import net.vpc.hadralang.compiler.core.HLCOptions;
import net.vpc.hadralang.compiler.core.HLProject;

public interface HLCStage {
    void processProject(HLProject project, HLCOptions options);

}
