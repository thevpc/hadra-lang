package net.hl.compiler.stages;

import net.vpc.common.jeep.DefaultJCompilationUnit;
import net.vpc.common.textsource.JTextSource;
import net.vpc.common.textsource.JTextSourceRoot;
import net.vpc.common.jeep.log.LogJTextSourceReport;
import net.vpc.common.jeep.util.JStringUtils;
import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;
import net.hl.compiler.ast.HNBlock;
import net.hl.compiler.core.HTarget;

public class HStage01Parser extends AbstractHStage {

    @Override
    public HTarget[] getTargets() {
        return new HTarget[]{HTarget.AST};
    }

    @Override
    public void processProject(HProject project, HOptions options) {
        JTextSourceRoot[] inputs = options.roots();
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

            for (JTextSourceRoot input : inputs) {
                for (JTextSource inputItem : input.iterate(new LogJTextSourceReport(project.log()))) {
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
