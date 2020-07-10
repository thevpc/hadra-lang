package net.hl.compiler.stages;

import net.vpc.app.nuts.Nuts;
import net.vpc.app.nuts.NutsDefinition;
import net.vpc.app.nuts.NutsSearchCommand;
import net.vpc.app.nuts.NutsWorkspace;
import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.DefaultJTypedValue;
import net.vpc.common.jeep.core.eval.JEvaluableValue;
import net.vpc.common.jeep.impl.functions.DefaultJInvokeContext;
import net.vpc.common.jeep.util.JStringUtils;
import net.hl.compiler.core.*;
import net.hl.compiler.core.elements.HNElementMetaPackageArtifact;
import net.hl.compiler.core.elements.HNElementMetaPackageGroup;
import net.hl.compiler.core.elements.HNElementMetaPackageVersion;
import net.hl.compiler.core.elements.HNElementNonExpr;
import net.hl.compiler.core.invokables.JNodeHBlocJInvoke;
import net.hl.compiler.index.HLIndexedProject;
import net.hl.compiler.ast.*;
import net.hl.compiler.stages.generators.java.HLCStage08JavaTransform;
import net.hl.compiler.utils.HNodeUtils;
import net.hl.compiler.utils.HTokenUtils;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HLCStage02Preprocessor implements HLCStage {
    public static final Logger LOG=Logger.getLogger(HLCStage02Preprocessor.class.getName());
    @Override
    public void processProject(HLProject project, HLOptions options) {
        HNDeclareMetaPackage currentMetaPackage = null;
        String currentMetaPackageSource = null;
        JToken anyToken = null;
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            HNBlock.CompilationUnitBlock ast = (HNBlock.CompilationUnitBlock) compilationUnit.getAst();
            if (anyToken == null) {
                anyToken = ast.getStartToken();
            }
            for (HNDeclareMetaPackage metaPackage : ast.findDeclaredMetaPackages()) {
                if (currentMetaPackage != null) {
                    project.log().error("X400", null, "multiple package declarations detected", metaPackage.getStartToken());
                } else {
                    currentMetaPackage = metaPackage;
                    currentMetaPackageSource = compilationUnit.getSource().name();
                    project.setResolvedMetaPackage(currentMetaPackage);
                }
            }
        }
        HNDeclareMetaPackage metaPackage = project.getResolvedMetaPackage();
        HLIndexedProject ip = null;
        String projectRoot = options.getProjectRoot();
        if (!options.isIncremental() || metaPackage != null) {
            ip = parsePreProcessorResult(metaPackage, projectRoot, currentMetaPackageSource, project, options);
            if (ip == null) {
                if (currentMetaPackageSource == null) {
                    currentMetaPackageSource = projectRoot;
                }
                ip = new HLIndexedProject(projectRoot, "NoName",
                        currentMetaPackageSource, new String[0], new String[0]);
            } else {
                if (JStringUtils.isBlank(JModuleId.valueOf(ip.getModuleId()).getArtifactId())) {
                    project.log().error("X000", null, "missing artifact name", metaPackage == null ? anyToken : metaPackage.getStartToken());
                }
            }
            project.indexer().indexProject(ip);
        } else {
            ip = project.indexer().searchProject(projectRoot);
            if (ip == null) {
                project.log().error("X000", null, "unable to resolve project in incremental node : " + projectRoot, anyToken);
            }
        }
        if (ip == null) {
            ip = new HLIndexedProject(projectRoot, "NoName", currentMetaPackageSource, new String[0], new String[0]);
        }
        project.setIndexedProject(ip);
        HNDeclareType mpt = project.getMetaPackageType();
        JModuleId jModuleId = JModuleId.valueOf(ip.getModuleId());
        if (JStringUtils.isBlank(jModuleId.getArtifactId())) {
            jModuleId = new JModuleId(jModuleId.getGroupId(), "NoName", jModuleId.getVersion());
        }
        mpt.setMetaPackageName(resolvePackageName(jModuleId.getGroupId()));
        mpt.setPackageName(null);
        mpt.setNameToken(HTokenUtils.createToken(resolveClassName(jModuleId.getArtifactId())));
    }

    protected String resolvePackageName(String groupId) {
        StringBuilder sb = new StringBuilder(groupId.toLowerCase());
        return sb.toString();
    }

    protected String resolveClassName(String artifactId) {
        StringBuilder sb = new StringBuilder();
        boolean wasSpace = true;
        for (char c : artifactId.toCharArray()) {
            if (c == '-') {
                wasSpace = true;
            } else if (c == '_') {
                wasSpace = true;
                sb.append(c);
            } else {
                if (wasSpace) {
                    wasSpace=false;
                    sb.append(Character.toUpperCase(c));
                } else {
                    wasSpace=false;
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }


    public HLIndexedProject parsePreProcessorResult(HNDeclareMetaPackage metaPackage,
                                                    String projectId,
                                                    String currentMetaPackageSource, HLProject project, HLOptions options) {
        JContext context = project.languageContext();
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
            moduleId = JModuleId.replaceBlanks(moduleId, JModuleId.DEFAULT_MODULE_ID);
            if (!requirePreprocessor) {
                HLIndexedProject i = new HLIndexedProject(projectId,
                        moduleId.toString(),
                        currentMetaPackageSource,
                        new String[0],
                        new String[0]
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
                        block.getStartToken(),block.getEndToken()
                        );
                mainMethod.addAnnotations(HNAnnotationCall.ofModifier("static"));
                mainMethod.setArguments(new ArrayList<>(
                        Arrays.asList(
                                new HNDeclareIdentifier(
                                        new HNDeclareTokenIdentifier(HTokenUtils.createToken("args")),
                                        null,
                                        HNodeUtils.createTypeToken("String"),
                                        null,
                                        null,null
                                )
                        )
                ));
                mainMethod.setBody(block.setBlocType(HNBlock.BlocType.LOCAL_BLOC));
                mainMethod.setReturnTypeName(HNodeUtils.createTypeToken("void"));
                HNBlock preprocessorRootNode=new HNBlock.CompilationUnitBlock(new HNode[]{
                        mainMethod
                },block.getStartToken(),block.getEndToken());

//                System.out.println("### START PREPROCESSOR");
                JContext preProcessorContext = context.newContext();
                preProcessorContext.log(context.log());//inherit logger
                JNode nn = metaPackage;
                while (nn != null && !(nn instanceof HNBlock.CompilationUnitBlock)) {
                    nn = nn.getParentNode();
                }
                if (nn == null) {
                    project.log().error("X000", "unexpected error", "missing root CompilationUnitBlock", metaPackage.getStartToken());
                    return null;
                }
                HNBlock.CompilationUnitBlock cub = (HNBlock.CompilationUnitBlock) nn;
                HLProject preProcessorProgram = new HLProject(preProcessorContext, project.indexer());
                preProcessorProgram.setIndexedProject(
                        new HLIndexedProject(projectId,
                                "HLPreprocessor#0.1.0",
                                currentMetaPackageSource,
                                new String[0],
                                new String[0]
                        )
                );
                preProcessorProgram.getMetaPackageType().setNameToken(HTokenUtils.createToken("HLPreprocessor"));
                LOG.log(Level.INFO, "Running Preprocessor with code \n"+preprocessorRootNode);
                preProcessorProgram.addCompilationUnit(new DefaultJCompilationUnit(cub.getCompilationUnit().getSource(), preprocessorRootNode));
                preProcessorProgram.setRootId("<preprocessor>:"+projectId);
                for (HLCStage hlcStage : new HLCStage[]{
                        new HLCStage03Indexer(),
                        new HLCStage04DefinitionResolver(true),
                        new HLCStage05CallResolver(true),
                        new HLCStage08JavaTransform(true) //transform to java nodes to help evaluation!
                }) {
                   hlcStage.processProject(preProcessorProgram, options);
                }
//                System.out.println("PREPROCESSOR CODE");
//                System.out.println(JeepUtils.indent(preProcessorProgram.getMetaPackageType().toString()));

                HLCompilerEnv env = new HLCompilerEnv(project, options, context);
                new JNodeHBlocJInvoke((HNBlock) mainMethod.getBody())
                        .invoke(new DefaultJInvokeContext(
                                context,
                                preProcessorContext.evaluators().newEvaluator(),
                                new DefaultJTypedValue(env, context.types().forName(HLCompilerEnv.class.getName())),
                                new JEvaluable[]{new JEvaluableValue(
                                        new String[0],
                                        preProcessorContext.types().forName("String[]")
                                )},
                                "compile",null
                        ));
                Set<String> foundIds = new HashSet<>();
                Set<String> dfiles = new HashSet<>();
                NutsWorkspace ws = Nuts.openWorkspace("-y"/*,"-z"*/);
                NutsSearchCommand search = ws.search().setDependencies(true)
                        .setLatest(true).setContent(true);
                Set<String> classPath = new LinkedHashSet<>();

                boolean someSearch = false;
                for (HLDependency effectiveDependency : env.dependencies()) {
                    someSearch = true;
                    search.addId(effectiveDependency.getName());
                    classPath.add(effectiveDependency.toString());
                }

                if (someSearch) {
                    for (NutsDefinition resultDefinition : search.getResultDefinitions()) {
                        dfiles.add(resultDefinition.getContent().getPath().toString());
                        foundIds.add(resultDefinition.getId().getShortName());
                    }
                    for (HLDependency effectiveDependency : env.dependencies()) {
                        if(!foundIds.contains(ws.id().parse(effectiveDependency.getName()).getShortName())){
                            if(!effectiveDependency.isOptional()){
                                project.log().error("X000","pre-processor","unresolvable dependency "+effectiveDependency.getName(),block.getStartToken());
                            }
                        }
                    }
                }
                HLIndexedProject ip = new HLIndexedProject(
                        projectId, moduleId.toString(), currentMetaPackageSource,
                        classPath.toArray(new String[0]),
                        dfiles.toArray(new String[0])
                );
                metaPackage.setIndex(ip);

//                System.out.println("### END PREPROCESSOR");
                return ip;
            }
        }
        return null;
    }


}
