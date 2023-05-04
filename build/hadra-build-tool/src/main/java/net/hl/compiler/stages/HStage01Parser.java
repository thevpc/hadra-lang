package net.hl.compiler.stages;

import net.hl.compiler.HL;
import net.hl.compiler.core.types.HLJTypes;
import net.thevpc.jeep.DefaultJCompilationUnit;
import net.thevpc.jeep.JTypes;
import net.thevpc.jeep.log.LogJTextSourceReport;
import net.thevpc.jeep.source.JTextSource;
import net.thevpc.jeep.source.JTextSourceRoot;
import net.thevpc.jeep.util.JStringUtils;
import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;
import net.hl.compiler.ast.HNBlock;
import net.hl.compiler.core.HTask;

public class HStage01Parser extends AbstractHStage {

    @Override
    public HTask[] getTasks() {
        return new HTask[]{HTask.AST};
    }

    @Override
    public boolean isEnabled(HProject project, HL options) {
        return options.containsAnyTask(HTask.AST);
    }

    @Override
    public void processProject(HProject project, HOptions options) {
        JTextSourceRoot[] inputs = options.sources();
        int foundCompilationUnits = 0;
        if (inputs.length > 0) {
            if (JStringUtils.isBlank(options.getProjectRoot())) {
                if (inputs.length != 1) {
                    project.log().jerror("X405", null, null, "missing source");
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
            project.log().jerror("X403", null, null, "missing units to compile");
        }
        HLJTypes t = (HLJTypes) project.languageContext().types();
        t.setIndexer(project.indexer());
    }
}
