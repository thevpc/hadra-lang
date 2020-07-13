package net.hl.compiler.stages;

import net.vpc.common.jeep.*;
import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;
import net.hl.compiler.ast.HNBlock;
import net.hl.compiler.ast.HNDeclareType;
import net.hl.compiler.index.HIndexedClass;
import net.hl.compiler.index.HIndexedProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Set;
import net.hl.compiler.HL;
import net.hl.compiler.core.HTarget;
import net.hl.compiler.index.HIndexer;

public class HStage03Indexer extends AbstractHStage{
    private static String LIB_HL_LANG_PREFIX="hadra-lang";

    @Override
    public HTarget[] getTargets() {
        return new HTarget[]{HTarget.RESOLVED_AST};
    }

    @Override
    public void processProject(HProject project, HOptions options) {
        boolean incremental = options.isIncremental();
        HIndexer indexer = project.indexer();

        if(incremental && project.getResolvedMetaPackage() == null) {
            if (project.getCompilationUnits().length == 1 && project.rootId().equals(project.getCompilationUnits()[0].getSource().name())) {
                //this is a single file project
            } else {
                Set<HIndexedProject> hlIndexedProjects = indexer.searchProjects();
                if (hlIndexedProjects.isEmpty()) {
                    if (project.getCompilationUnits().length > 0) {
                        project.log().error("X404", null, "unable to resolve project", project.getCompilationUnits()[0].getAst().getStartToken());
                    } else {
                        project.log().error("X404", null, "unable to resolve project", null);
                    }
                }
            }
        }
        if (!project.isSuccessful()) {
            return;
        }
        Set<HIndexedProject> hlIndexedProjects = indexer.searchProjects();
        try {
            indexer.indexSDK(null, !incremental);
        }catch (Exception ex){
            project.log().error("X000", null,"unresolvable SDK : "+ex.toString(),null);
        }
        for (HIndexedProject iproject : hlIndexedProjects) {
            for (String dependencyFile : iproject.getDependencyFiles()) {
                indexer.indexLibrary(new File(dependencyFile), !incremental);
            }
        }
        // check if stdlib is included in the dependencies
        // if not, use compiler's classpath stdlib
        HIndexedClass tupleType = indexer.searchType("net.hl.lang.Tuple");
        URL stdlibUrl=null;
        if(tupleType==null){
            ClassLoader cl = getClass().getClassLoader();
            if(cl instanceof URLClassLoader){
                URLClassLoader ucl=(URLClassLoader)cl;
                for (URL url : ucl.getURLs()) {
                    String s=url.toString();
                    if(s.endsWith("/"+LIB_HL_LANG_PREFIX+"/target/classes/") || s.endsWith("/"+LIB_HL_LANG_PREFIX+"/target/classes")) {
                        //this is dev stdlib, include it
                        stdlibUrl = url;
                        break;
                    }else if(s.matches(".*/"+LIB_HL_LANG_PREFIX+"-.*[.]jar")){
                        stdlibUrl = url;
                        break;
                    }
                }
            }
            if(stdlibUrl==null){
                //this happens when using sunfire under netbeans!
                String cp = System.getProperty("java.class.path");
                if(cp!=null){
                    for (String s : cp.split(File.pathSeparator)) {
                        if(s.endsWith("/"+LIB_HL_LANG_PREFIX+"/target/classes/") || s.endsWith("/"+LIB_HL_LANG_PREFIX+"/target/classes")) {
                            try {
                                //this is dev stdlib, include it
                                stdlibUrl = Paths.get(s).toUri().toURL();
                            } catch (MalformedURLException ex) {
                                //
                            }
                            break;
                        }else if(s.matches(".*/"+LIB_HL_LANG_PREFIX+"-.*[.]jar")){
                            try {
                                stdlibUrl = Paths.get(s).toUri().toURL();
                            } catch (MalformedURLException ex) {
                                //
                            }
                            break;
                        }
                    }
                }
                
            }
            if(stdlibUrl==null){
                project.log().error("X000", null,"unresolvable stdlib",null);
            }else{
                project.log().warn("W000", null,"using default stdlib at "+stdlibUrl,null);
                indexer.indexLibrary(stdlibUrl,false);
            }
        }
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            HNBlock ast = (HNBlock) compilationUnit.getAst();
            for (HNDeclareType classDeclaration : ast.findDeclaredTypes()) {
                classDeclaration.setMetaPackageName(project.getMetaPackageType().getMetaPackage());
            }
            indexer.indexSource(compilationUnit);
        }
        indexer.indexDeclareType(project.rootId(),project.getMetaPackageType());
    }
}
