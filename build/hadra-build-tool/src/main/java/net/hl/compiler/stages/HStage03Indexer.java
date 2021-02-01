package net.hl.compiler.stages;

import net.thevpc.jeep.*;
import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;
import net.hl.compiler.ast.HNBlock;
import net.hl.compiler.ast.HNDeclareType;
import net.hl.compiler.index.HIndexedClass;
import net.hl.compiler.index.HIndexedProject;

import java.io.File;
import java.util.Set;
import net.hl.compiler.HL;
import net.hl.compiler.core.HTask;
import net.hl.compiler.index.HIndexer;
import net.hl.compiler.utils.DepIdAndFile;

public class HStage03Indexer extends AbstractHStage {

    private static String LIB_HL_LANG_PREFIX = "hadra-lang";
    private boolean inPreprocessor;

    public HStage03Indexer(boolean inPreprocessor) {
        this.inPreprocessor = inPreprocessor;
    }

    @Override
    public boolean isEnabled(HProject project, HL options) {
        return options.containsAnyTask(HTask.RESOLVED_AST,
                HTask.COMPILE,
                HTask.RUN
        );
    }
    

    @Override
    public HTask[] getTasks() {
        return new HTask[]{HTask.RESOLVED_AST};
    }

    @Override
    public void processProject(HProject project, HOptions options) {
        boolean incremental = options.isIncremental();
        HIndexer indexer = project.indexer();

        final JCompilerLog clog = project.log(); //this is a single file project
        if (!project.isSuccessful()) {
            return;
        }
        Set<HIndexedProject> hlIndexedProjects = indexer.searchProjects();
        try {
            indexer.indexSDK(null, !incremental, clog);
        } catch (Exception ex) {
            clog.jerror("X000", null, null, "unresolvable SDK : " + ex.toString());
        }
        for (HIndexedProject iproject : hlIndexedProjects) {
            for (DepIdAndFile dependencyFile : iproject.getDependencies()) {
                indexer.indexLibrary(new File(dependencyFile.getFile()), !incremental, clog);
            }
        }
        // check if stdlib is included in the dependencies
        // if not, use compiler's classpath stdlib
        HIndexedClass tupleType = indexer.searchType("net.hl.lang.Tuple");
        if (tupleType == null) {
            if (inPreprocessor) {
                DepIdAndFile[] u = HStageUtils.resolveLangPaths(null,null, true, true, true);
                if (u.length > 0) {
                    indexer.indexLibrary(new File(u[0].getFile()), !incremental, clog);
                } else {
                    clog.jerror("X000", null, null, "unresolvable hadra-lang library");
                }
            } else {
                clog.jerror("X000", null, null, "unresolvable hadra-lang library : unable to load classes");
            }
        }
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            HNBlock ast = (HNBlock) compilationUnit.getAst();
            for (HNDeclareType classDeclaration : ast.findDeclaredTypes()) {
                classDeclaration.setMetaPackageName(project.getMetaPackageType().getMetaPackage());
            }
            indexer.indexSource(compilationUnit, clog);
        }
        indexer.indexDeclareType(project.rootId(), project.getMetaPackageType());
    }
}
