package net.vpc.hadralang.compiler.stages;

import net.vpc.common.jeep.DefaultJCompilationUnit;
import net.vpc.common.jeep.JSource;
import net.vpc.common.jeep.core.compiler.JSourceRoot;
import net.vpc.common.jeep.util.JStringUtils;
import net.vpc.hadralang.compiler.core.HLCOptions;
import net.vpc.hadralang.compiler.core.HLProject;
import net.vpc.hadralang.compiler.parser.ast.HNBlock;

public class HLCStage01Parser implements HLCStage {
    @Override
    public void processProject(HLProject project, HLCOptions options) {
        JSourceRoot[] inputs = options.roots();
        int foundCompilationUnits = 0;
        if (inputs.length > 0) {
            if (JStringUtils.isBlank(options.getProjectRoot())) {
                if (inputs.length != 1) {
                    project.log().error("X405", null, "missing project root", null);
                    return;
                } else {
                    options.setProjectRoot(inputs[0].getId());
                }
            }
            project.setRootId(options.getProjectRoot());

            for (JSourceRoot input : inputs) {
                for (JSource inputItem : input.iterate(project.log())) {
                    foundCompilationUnits++;
                    DefaultJCompilationUnit c = new DefaultJCompilationUnit(inputItem, project.languageContext());
                    ((HNBlock.CompilationUnitBlock) c.getAst()).setCompilationUnit(c);
                    project.addCompilationUnit(c);
                }
            }
        }
        if (foundCompilationUnits == 0 && project.log().isSuccessful()) {
            project.log().error("X403", null, "missing units to compile", null);
        }
    }
}
