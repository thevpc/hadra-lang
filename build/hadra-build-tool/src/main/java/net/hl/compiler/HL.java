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
import net.hl.compiler.utils.StringUtils;
import net.thevpc.nuts.NutsSession;
import net.thevpc.nuts.NutsTextStyle;

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
        project.getSession().out().println(
                StringUtils.center2(
                        "[ " + project.getWorkspace().text().forStyled("Hadra Lang Build Tool", NutsTextStyle.primary(1)).toString()
                        + " ]",
                         80, '-', context.getSession().getWorkspace())
        );
        project.log().jinfo("X000", null, null, "compiler started");

        if (sources().length == 0) {
            if (new File(".", "pom.xml").exists()) {
                addSourceMavenProject(".");
            } else {
                addSourceFile(".");
            }
        }

        if (containsAnyTask(HTask.COMPILE, HTask.RUN)) {
            if (!containsAnyTask(HTask.getLanguagePorts().toArray(new HTask[0]))) {
                addTask(HTask.JAVA);
            }
        }

        if (getTasks().isEmpty()) {
            addTask(HTask.CLEAN);
            addTask(HTask.COMPILE);
            addTask(HTask.JAVA);
            addTask(HTask.RUN);
        }

        if (containsAnyTask(HTask.RUN)) {
            addTask(HTask.COMPILE);
        }
        if (containsAnyTask(HTask.COMPILE)) {
            addTask(HTask.RESOLVED_AST);
        }
        if (containsAnyTask(HTask.RESOLVED_AST)) {
            addTask(HTask.AST);
        }

        try {
            if (project.isSuccessful()) {
                List<HStage> stages = new ArrayList<>();
                stages.add(new HStage00CleanCompiler());
                stages.add(new HStage01Parser());
                stages.add(new HStage02Preprocessor());
                stages.add(new HStage03Indexer(false));
                stages.add(new HStage04DefinitionResolver(false));
                stages.add(new HStage05CallResolver(false));
                stages.add(new HStage08JavaTransform(false));
                stages.add(new HStage09JavaGenerator());
                stages.add(new HStage10JavaCompiler());
                stages.add(new HStage11JavaRun());
                EnumSet<HTask> toProcessTarsks = EnumSet.copyOf(options.getTasks());
                for (int i = 0; i < stages.size(); i++) {
                    HStage stage = stages.get(i);
                    String stageName = stage.getClass().getSimpleName();
                    if (stage.isEnabled(project, options)) {
                        for (HTask task : HTask.expandDependencies(stage.getTasks())) {
                            toProcessTarsks.remove(task);
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
                if (toProcessTarsks.size() > 0 && project.isSuccessful()) {
                    LOG.log(Level.SEVERE, "unsupported tasks : {0}", toProcessTarsks);
                    project.log().jerror("X000", null, null, "unsupported tasks : " + toProcessTarsks);
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
