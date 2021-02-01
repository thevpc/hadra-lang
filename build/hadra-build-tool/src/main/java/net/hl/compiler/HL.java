package net.hl.compiler;

import java.io.File;
import net.thevpc.jeep.util.Chronometer;
import net.hl.compiler.core.*;
import net.hl.compiler.index.HIndexerImpl;
import net.hl.compiler.stages.*;
import net.hl.compiler.stages.generators.java.HStage08JavaTransform;
import net.hl.compiler.stages.generators.java.HStage09JavaGenerator;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.hl.compiler.stages.generators.java.HStage10JavaCompiler;
import net.hl.compiler.index.HIndexer;
import net.hl.compiler.stages.generators.java.HStage11JavaRun;
import net.thevpc.nuts.NutsSession;
import net.thevpc.nuts.NutsTextNodeStyle;

/**
 * Hadra Language Build Tool
 */
public class HL extends HOptions<HL> {

    private static final Logger LOG = Logger.getLogger(HL.class.getName());
    //    public static final String NON_ID_CHARS = "\\[]{}()<>*+/~&|=£%'\"`;,.:#@!?-";
    private HProjectContext projectContext;

    public HL() {
        this((NutsSession) null);
    }

    public HL(NutsSession session) {
        this((ClassLoader) null, null, session);
    }

    public HL(ClassLoader classLoader, HIndexer indexer, NutsSession session) {
        this(new HadraLanguage(session, classLoader), indexer, session);
    }

    public HL(HadraContext language, HIndexer indexer, NutsSession session) {
        this(new DefaultHLProjectContext(
                language == null ? session == null ? HadraLanguage.getSingleton() : new HadraLanguage(session) : language,
                indexer == null ? new HIndexerImpl() : indexer,
                null
        )
        );
    }

    public HL(HProjectContext projectContext) {
        if (projectContext == null || projectContext.indexer() == null || projectContext.languageContext() == null) {
            throw new NullPointerException();
        }
        this.projectContext = projectContext;
    }

    public static HL create(NutsSession session) {
        return new HL(session);
    }

    public static HL create() {
        return new HL();
    }

    public HadraContext languageContext() {
        return projectContext.languageContext();
    }

    public HProject compile() {
        HL options = this;
        HadraContext context = languageContext().newContext();
        HProject project = new HProject(context, projectContext.indexer(), projectContext.getSession());
        Chronometer globalChronometer = Chronometer.start();
        project.getSession().out().printf("----------------------------------- [%s] -------------------------------------------\n",
                project.getWorkspace().formats().text().factory().styled("Hadra Lang Build Tool", NutsTextNodeStyle.success())
        );
        project.log().jinfo("X000", null, null, "compiler started");

        if (sources().length == 0) {
            if (new File(".", "pom.xml").exists()) {
                addSourceMavenProject(".");
            } else {
                addSourceFile(".");
            }
        }

        if (getTargets().contains(HTarget.COMPILE) || getTargets().contains(HTarget.RUN)) {
            if (!getTargets().stream().anyMatch(x -> HTarget.getLanguagePorts().contains(x))) {
                addTarget(HTarget.JAVA);
            }
        }

        if (getTargets().isEmpty()) {
            addTarget(HTarget.RUN);
            addTarget(HTarget.COMPILE);
            addTarget(HTarget.JAVA);
        }

        if (containsAnyTargets(HTarget.RUN)) {
            addTarget(HTarget.COMPILE);
        }
        if (containsAnyTargets(HTarget.COMPILE)) {
            addTarget(HTarget.RESOLVED_AST);
        }
        if (containsAnyTargets(HTarget.RESOLVED_AST)) {
            addTarget(HTarget.AST);
        }

        try {
            if (project.isSuccessful()) {
                List<HStage> stages = new ArrayList<>();
                stages.add(new HStage01Parser());
                stages.add(new HStage02Preprocessor());
                stages.add(new HStage03Indexer(false));
                stages.add(new HStage04DefinitionResolver(false));
                stages.add(new HStage05CallResolver(false));
                stages.add(new HStage08JavaTransform(false));
                stages.add(new HStage09JavaGenerator());
                stages.add(new HStage10JavaCompiler());
                stages.add(new HStage11JavaRun());
                EnumSet<HTarget> toProcessTargets = EnumSet.copyOf(options.getTargets());
                for (int i = 0; i < stages.size(); i++) {
                    HStage stage = stages.get(i);
                    String stageName = stage.getClass().getSimpleName();
                    if (stage.isEnabled(project, options)) {
                        for (HTarget target : HTarget.expandDependencies(stage.getTargets())) {
                            toProcessTargets.remove(target);
                        }
                        LOG.log(Level.FINE, "{0} ({1}/{2}) starting...", new Object[]{stageName, i + 1, stages.size()});
                        Chronometer chronometer = Chronometer.start(stageName);
                        stage.processProject(project, options);
                        LOG.log(Level.FINE, "{0} ({1}/{2}) took {3}", new Object[]{stageName, i + 1, stages.size(), chronometer.stop().getDuration()});
                        if (!project.isSuccessful()) {
                            break;
                        }
                    } else {
                        stage.isEnabled(project, options);
                    }
                }
                if (toProcessTargets.size() > 0 && project.isSuccessful()) {
                    LOG.log(Level.SEVERE, "unsupported targets : {0}", toProcessTargets);
                    project.log().jerror("X000", null, null, "unsupported targets : " + toProcessTargets);
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.FINE, "unexpected error : " + ex.toString(), ex);
            project.log().jerror("X000", null, null, "unexpected error : " + ex.toString());
        }
        LOG.log(Level.FINE, "compilation finished with {0} errors and {1} warnings in {2}", new Object[]{
            project.getErrorCount(),
            project.getWarningCount(),
            globalChronometer.stop().getDuration()
        });
        project.log().printFooter();
        return project;
    }

}
