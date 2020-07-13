package net.hl.compiler.stages;

import net.vpc.common.jeep.*;
import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.invokables.HLJCompilerContext;
import net.hl.compiler.ast.HNode;

import java.util.List;

public abstract class HStageType2 extends AbstractHStage {

    private boolean check = true;

    public abstract boolean processCompilerStage(JCompilerContext compilerContextBase);

    protected void processProjectMain(HProject project, HOptions options) {
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            processCompilerStage(project.newCompilerContext(compilationUnit));
        }
    }

    @Override
    public void processProject(HProject project, HOptions options) {
        processProjectMain(project, options);
        if (isRequiredCheck(project, options)) {
            for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
                processCompilerStageCheck(project.newCompilerContext(compilationUnit));
            }
        }
    }

    public boolean isRequiredCheck(HProject project, HOptions options) {
        return isCheck();
    }

    public boolean isCheck() {
        return check;
    }

    public boolean processAllNextCompilerStage(JCompilerContext compilerContextBase) {
        JNode parentNode = compilerContextBase.getNode();
        List<JNode> jNodes = parentNode.getChildrenNodes();
        boolean succeeded = true;
        for (JNode jNode : jNodes) {
            if (jNode != null) {
                if (!processCompilerStage(compilerContextBase.nextNode(jNode))) {
                    succeeded = false;
                }
            }
        }
        return succeeded;
    }

    protected void processProjectCheck(HProject project, HOptions options) {
        //check
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            processCompilerStageCheck(project.newCompilerContext(compilationUnit));
        }
    }

    protected boolean processAllNextCompilerStageCheck(JCompilerContext compilerContextBase) {
        JNode parentNode = compilerContextBase.getNode();
        List<JNode> jNodes = parentNode.getChildrenNodes();
        boolean succeeded = true;
        for (JNode jNode : jNodes) {
            if (jNode != null) {
                if (!processCompilerStageCheck(compilerContextBase.nextNode(jNode))) {
                    succeeded = false;
                }
            }
        }
        return succeeded;
    }

    protected boolean processCompilerStageCurrentCheck(HNode node, HLJCompilerContext compilerContext) {
        return true;
    }

    protected boolean processCompilerStageCheck(JCompilerContext compilerContextBase) {
        JNode node = compilerContextBase.getNode();
        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
        processAllNextCompilerStageCheck(compilerContextBase);
        try {
            processCompilerStageCurrentCheck((HNode) node, compilerContext);
            return true;
        } catch (RuntimeException ex) {
            throw ex;
        }
    }
}
