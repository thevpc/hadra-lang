package net.hl.compiler.stages;

import net.thevpc.nuts.*;
import net.thevpc.jeep.*;
import net.thevpc.jeep.core.DefaultJTypedValue;
import net.thevpc.jeep.core.eval.JEvaluableValue;
import net.thevpc.jeep.impl.functions.DefaultJInvokeContext;
import net.thevpc.jeep.util.JStringUtils;
import net.hl.compiler.core.*;
import net.hl.compiler.core.elements.HNElementMetaPackageArtifact;
import net.hl.compiler.core.elements.HNElementMetaPackageGroup;
import net.hl.compiler.core.elements.HNElementMetaPackageVersion;
import net.hl.compiler.core.elements.HNElementNonExpr;
import net.hl.compiler.core.invokables.JNodeHBlocJInvoke;
import net.hl.compiler.index.HIndexedProject;
import net.hl.compiler.ast.*;
import net.hl.compiler.stages.generators.java.HStage08JavaTransform;
import net.hl.compiler.utils.HNodeUtils;
import net.hl.compiler.utils.HTokenUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.hl.compiler.HL;
import net.hl.compiler.utils.DepIdAndFile;
import net.hl.compiler.utils.HSharedUtils;

public class HStage02Preprocessor extends AbstractHStage {

    public static final Logger LOG = Logger.getLogger(HStage02Preprocessor.class.getName());

    @Override
    public HTask[] getTasks() {
        return new HTask[]{HTask.RESOLVED_AST};
    }

    @Override
    public boolean isEnabled(HProject project, HL options) {
        return options.containsAnyTask(HTask.RESOLVED_AST);
    }

    @Override
    public void processProject(HProject project, HOptions options) {
        HNDeclareMetaPackage currentMetaPackage = null;
        String currentMetaPackageSource = null;
        JToken anyToken = null;
        List<HNMetaImportPackage> leadingImportPackages = new ArrayList<>();
        Set<String> metaSources=new HashSet<>();
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            HNBlock.CompilationUnitBlock ast = (HNBlock.CompilationUnitBlock) compilationUnit.getAst();
            if (anyToken == null) {
                anyToken = ast.getStartToken();
            }
            for (HNDeclareMetaPackage metaPackage : ast.findDeclaredMetaPackages()) {
                if (currentMetaPackage != null) {
                    project.log().jerror("X400", null, metaPackage.getStartToken(), "multiple package declarations detected");
                } else {
                    currentMetaPackage = metaPackage;
                    currentMetaPackageSource = compilationUnit.getSource().name();
                    metaSources.add(currentMetaPackageSource);
                    project.setResolvedMetaPackage(currentMetaPackage);
                }
            }
            for (HNMetaImportPackage leadingImportPackage : ast.findLeadingImportPackages()) {
                metaSources.add(compilationUnit.getSource().name());
                leadingImportPackages.add(leadingImportPackage);
            }
        }

        HNDeclareMetaPackage metaPackage = project.getResolvedMetaPackage();
        HIndexedProject ip = null;
        String projectRoot = options.getProjectRoot();
        JModuleId defaultModuleId = JModuleId.DEFAULT_MODULE_ID();
        DepIdAndFile[] defaultLangPaths = HStageUtils.resolveLangPaths(null, null, true, true, true);
        if (defaultLangPaths.length == 0) {
            project.log().jerror("X000", null, null, "unresolvable hadra-lang library");
        }
        if (!options.isIncremental() || metaPackage != null || leadingImportPackages.size() > 0) {
            ip = parsePreProcessorResult(metaPackage, projectRoot, currentMetaPackageSource, project, options, leadingImportPackages,metaSources);
            if (ip == null) {
                if (currentMetaPackageSource == null) {
                    currentMetaPackageSource = projectRoot;
                }
                ip = new HIndexedProject(projectRoot, defaultModuleId.toString(),
                        currentMetaPackageSource,
                        defaultLangPaths.length > 0
                                ? new DepIdAndFile[]{defaultLangPaths[0]} : new DepIdAndFile[0]
                );
            } else {
                if (JStringUtils.isBlank(JModuleId.valueOf(ip.getModuleId()).getArtifactId())) {
                    project.log().jerror("X000", null, metaPackage == null ? anyToken : metaPackage.getStartToken(), "missing artifact name");
                }
                DepIdAndFile[] u = HStageUtils.resolveLangPaths(ip.getDependencies(), null, false, false, true);
                if (u.length > 0) {
                    List<DepIdAndFile> depIds = new ArrayList<>(Arrays.asList(ip.getDependencies()));
                    depIds.add(u[0]);
                    ip = new HIndexedProject(ip.getId(), ip.getModuleId(), ip.getSource(), depIds.toArray(new DepIdAndFile[0]));
                }
            }
            project.log().jinfo("X000", null, null, "start indexing project...");
            project.indexer().indexProject(ip);
        } else {
            ip = project.indexer().searchProject(projectRoot);
            if (ip == null) {
                project.log().jerror("X000", null, anyToken, "unable to resolve project in incremental node : " + projectRoot);
                ip = new HIndexedProject(projectRoot, defaultModuleId.toString(), currentMetaPackageSource,
                        defaultLangPaths.length > 0 ? new DepIdAndFile[]{defaultLangPaths[0]} : new DepIdAndFile[0]
                );
            }
        }

        project.setIndexedProject(ip);
        HNDeclareType mpt = project.getMetaPackageType();
        JModuleId jModuleId = JModuleId.replaceBlanks(JModuleId.valueOf(ip.getModuleId()), defaultModuleId);
        mpt.setMetaPackageName(HSharedUtils.resolvePackageName(jModuleId.getGroupId()));
        mpt.setPackageName(null);
        mpt.setNameToken(HTokenUtils.createToken(resolveClassName(jModuleId.getArtifactId())));
    }

    protected String resolveClassName(String artifactId) {
        StringBuilder sb = new StringBuilder();
        boolean wasSpace = true;
        for (char c : artifactId.toCharArray()) {
            switch (c) {
                case '-':
                    wasSpace = true;
                    break;
                case '_':
                    wasSpace = true;
                    sb.append(c);
                    break;
                default:
                    if (wasSpace) {
                        wasSpace = false;
                        sb.append(Character.toUpperCase(c));
                    } else {
                        wasSpace = false;
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }

    public HIndexedProject parsePreProcessorResult(HNDeclareMetaPackage metaPackage,
                                                   String projectId,
                                                   String currentMetaPackageSource, HProject project, HOptions options,
                                                   List<HNMetaImportPackage> leadingImportPackages,
                                                   Set<String> metaSources
    ) {
        String goodSource=currentMetaPackageSource;
        if(goodSource==null){
            for (String metaSource : metaSources) {
                goodSource=metaSource;
            }
        }
        if(goodSource==null) {
            goodSource = "unknown-source";
        }
        HadraContext context = project.languageContext();
        HCompilerEnv env = new HCompilerEnv(project, options, context);
        if (metaPackage != null) {
            HNBlock body = metaPackage.getBody();
            boolean requirePreprocessor = false;
            if (body != null) {
                if (body.getStatements().size() > 0) {
                    requirePreprocessor = true;
                }
            }
            HNMetaPackageId hmoduleId = metaPackage.getModuleId();
            JModuleId moduleId = null;
            if (hmoduleId == null) {
                moduleId = new JModuleId("", "", "");
            } else {
                HNMetaPackageGroup g = hmoduleId.getGroup();
                if (g != null) {
                    g.setElement(new HNElementMetaPackageGroup(g.getValue()));
                }
                HNMetaPackageArtifact a = hmoduleId.getArtifact();
                if (a != null) {
                    a.setElement(new HNElementMetaPackageArtifact(a.getValue()));
                }
                HNMetaPackageVersion v = hmoduleId.getVersion();
                if (v != null) {
                    v.setElement(new HNElementMetaPackageVersion(v.getValue()));
                }
                hmoduleId.setElement(new HNElementNonExpr());
                moduleId = hmoduleId.toJModuleId();
            }
            metaPackage.setElement(new HNElementNonExpr());
            moduleId = JModuleId.replaceBlanks(moduleId, JModuleId.DEFAULT_MODULE_ID());
            DepIdAndFile[] defaultLangPaths = HStageUtils.resolveLangPaths(null, null, true, true, true);
            if (defaultLangPaths.length == 0) {
                project.log().jerror("X000", null, null, "unresolvable hadra-lang library");
            }

            if (!requirePreprocessor) {
                HIndexedProject i = new HIndexedProject(projectId,
                        moduleId.toString(),
                        currentMetaPackageSource,
                        defaultLangPaths.length == 0 ? new DepIdAndFile[0] : new DepIdAndFile[]{defaultLangPaths[0]}
                );
                metaPackage.setIndex(i);
                return i;
            }
            HNBlock block = metaPackage.getBody();//.copy();
            if (block != null && !block.isEmpty()) {
                //create a root block so that  content will be considered as local vars and
                // no fields will be defined
                HNDeclareInvokable mainMethod = new HNDeclareInvokable(
                        HTokenUtils.createToken("main"),
                        block.getStartToken(), block.getEndToken()
                );
                mainMethod.addAnnotations(HNAnnotationCall.ofModifier("static"));
                mainMethod.setArguments(new ArrayList<>(
                        Arrays.asList(
                                new HNDeclareIdentifier(
                                        new HNDeclareTokenIdentifier(HTokenUtils.createToken("args")),
                                        null,
                                        HNodeUtils.createTypeToken("String"),
                                        null,
                                        null, null
                                )
                        )
                ));
                mainMethod.setBody(block.setBlocType(HNBlock.BlocType.LOCAL_BLOC));
                mainMethod.setReturnTypeName(HNodeUtils.createTypeToken("void"));
                HNBlock preprocessorRootNode = new HNBlock.CompilationUnitBlock(new HNode[]{
                        mainMethod
                }, block.getStartToken(), block.getEndToken());

//                System.out.println("### START PREPROCESSOR");
                HadraContext preProcessorContext = context.newContext();
                preProcessorContext.log(context.log());//inherit logger
                JNode nn = metaPackage;
                while (nn != null && !(nn instanceof HNBlock.CompilationUnitBlock)) {
                    nn = nn.getParentNode();
                }
                if (nn == null) {
                    project.log().jerror("X000", "unexpected error", metaPackage.getStartToken(), "missing root CompilationUnitBlock");
                    return null;
                }
                HNBlock.CompilationUnitBlock cub = (HNBlock.CompilationUnitBlock) nn;
                HProject preProcessorProgram = new HProject(preProcessorContext, project.indexer());
                preProcessorProgram.setIndexedProject(new HIndexedProject(projectId,
                                "HLPreprocessor#0.1.0",
                                currentMetaPackageSource,
                                defaultLangPaths.length == 0 ? new DepIdAndFile[0] : new DepIdAndFile[]{defaultLangPaths[0]}
                        )
                );
                preProcessorProgram.getMetaPackageType().setNameToken(HTokenUtils.createToken("HLPreprocessor"));
                LOG.log(Level.FINE, "running Preprocessor with code \n{0}", preprocessorRootNode);
                preProcessorProgram.addCompilationUnit(new DefaultJCompilationUnit(cub.getCompilationUnit().getSource(), preprocessorRootNode));
                preProcessorProgram.setRootId("<preprocessor>:" + projectId);
                for (HStage hlcStage : new HStage[]{
                        new HStage03Indexer(true),
                        new HStage04DefinitionResolver(true),
                        new HStage05CallResolver(true),
                        new HStage08JavaTransform(true) //transform to java nodes to help evaluation!
                }) {
                    hlcStage.processProject(preProcessorProgram, options);
                }
//                System.out.println("PREPROCESSOR CODE");
//                System.out.println(JeepUtils.indent(preProcessorProgram.getMetaPackageType().toString()));


                new JNodeHBlocJInvoke((HNBlock) mainMethod.getBody())
                        .invoke(new DefaultJInvokeContext(
                                context,
                                preProcessorContext.evaluators().newEvaluator(),
                                new DefaultJTypedValue(env, context.types().forName(HCompilerEnv.class.getName())),
                                new JEvaluable[]{new JEvaluableValue(
                                        new String[0],
                                        preProcessorContext.types().forName("String[]")
                                )},
                                "compile", null, null
                        ));
//                Set<String> foundIds = new HashSet<>();
                Set<DepIdAndFile> classPath = buildClassPath(project, env, leadingImportPackages, block.getStartToken());
                HIndexedProject ip = new HIndexedProject(
                        projectId, moduleId.toString(), goodSource,
                        classPath.toArray(new DepIdAndFile[0])
                );
                metaPackage.setIndex(ip);

//                System.out.println("### END PREPROCESSOR");
                return ip;
            }
        } else if (leadingImportPackages.size() > 0) {
            Set<DepIdAndFile> classPath = buildClassPath(project, env,
                    leadingImportPackages,
                    leadingImportPackages.get(0).getStartToken());
            HIndexedProject ip = new HIndexedProject(
                    projectId, "snapshot", goodSource,
                    classPath.toArray(new DepIdAndFile[0])
            );
            return ip;
        }
        return null;
    }

    private Set<DepIdAndFile> buildClassPath(HProject project, HCompilerEnv env, List<HNMetaImportPackage> leadingImportPackages, JToken startToken) {
        for (HNMetaImportPackage lip : leadingImportPackages) {
            HNIdentifier pn = (HNIdentifier) lip.getImportedPackageNode();
            String name = pn.getName();
            env.addDependency(
                    new HDependency(
                            name,
                            lip.getScope(),
                            lip.isOptional(),
                            lip.getExclusions().stream().map(x -> ((HNIdentifier) x).getName()).toArray(String[]::new)
                    )
            );
        }
        NSearchCmd search = NSearchCmd.of()
//                .setDependencies(true)
                .setInlineDependencies(true)
                .setLatest(true)
                .setDependencyFilter(NDependencyFilters.of().byRunnable())
                ;
        boolean someSearch = false;
        for (HDependency d : env.dependencies()) {
            someSearch = true;
            search.addId(d.toString());
        }
        Set<DepIdAndFile> classPath = new LinkedHashSet<>();
        for (HDependency dep : env.dependencies()) {
            NDefinition def = null;
            try {
                def = NFetchCmd.of().setId(dep.getName()).setDependencyFilter(NDependencyFilters.of().byRunnable()).getResultDefinition();
            } catch (NNotFoundException ex) {
                //
            }
            if (def == null || def.getContent().isEmpty()) {
                project.log().jerror("X000", "pre-processor", startToken, "unresolvable dependency " + dep);
            } else {
                classPath.add(new DepIdAndFile(dep.toString(), def.getContent().get().toString()));
            }
        }
        if (someSearch) {
            for (NDependencies depd : search.getResultDependencies()) {
                for (NDependency dep : depd.transitive().toList()) {
                    NDefinition def = null;
                    try {
                        def = NFetchCmd.of(dep.toId()).setDependencyFilter(NDependencyFilters.of().byRunnable()).getResultDefinition();
                    } catch (NNotFoundException ex) {
                        //
                    }
                    if (def == null || def.getContent().isEmpty()) {
                        project.log().jerror("X000", "pre-processor", startToken, "unresolvable dependency " + dep);
                    } else {
                        classPath.add(new DepIdAndFile(dep.toString(), def.getContent().get().toString()));
                    }
                }
            }
        }
        return classPath;
    }

}
