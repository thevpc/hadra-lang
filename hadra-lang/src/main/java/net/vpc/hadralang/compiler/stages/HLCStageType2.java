package net.vpc.hadralang.compiler.stages;

import net.vpc.common.jeep.*;
import net.vpc.hadralang.compiler.core.HLCOptions;
import net.vpc.hadralang.compiler.core.HLProject;
import net.vpc.hadralang.compiler.core.invokables.HLJCompilerContext;
import net.vpc.hadralang.compiler.parser.ast.HNode;

import java.util.List;

public abstract class HLCStageType2 implements HLCStage {

    private boolean check = true;

    public abstract boolean processCompilerStage(JCompilerContext compilerContextBase);

    protected void processProjectMain(HLProject project, HLCOptions options) {
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            processCompilerStage(project.newCompilerContext(compilationUnit));
        }
    }

    @Override
    public void processProject(HLProject project, HLCOptions options) {
        processProjectMain(project, options);
        if (check) {
            for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
                processCompilerStageCheck(project.newCompilerContext(compilationUnit));
            }
        }
    }

    public boolean processAllNextCompilerStage(JCompilerContext compilerContextBase) {
        JNode parentNode = compilerContextBase.node();
        List<JNode> jNodes = parentNode.childrenNodes();
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

    protected void processProjectCheck(HLProject project, HLCOptions options) {
        //check
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            processCompilerStageCheck(project.newCompilerContext(compilationUnit));
        }
    }

    protected boolean processAllNextCompilerStageCheck(JCompilerContext compilerContextBase) {
        JNode parentNode = compilerContextBase.node();
        List<JNode> jNodes = parentNode.childrenNodes();
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
        JNode node = compilerContextBase.node();
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
