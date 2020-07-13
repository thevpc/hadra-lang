package net.hl.compiler;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.util.Chronometer;
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

/**
 * Hadra Language Compiler
 */
public class HL extends HOptions<HL> {

    private static final Logger LOG = Logger.getLogger(HL.class.getName());
    //    public static final String NON_ID_CHARS = "\\[]{}()<>*+/~&|=£%'\"`;,.:#@!?-";
    private HProjectContext projectContext;

    public HL() {
        this((ClassLoader) null, null);
    }

    public HL(ClassLoader classLoader, HIndexer indexer) {
        this(new HadraLanguage(classLoader), indexer);
    }

    public HL(JContext language, HIndexer indexer) {
        this(new DefaultHLProjectContext(
                language == null ? new HadraLanguage() : language,
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

    public static HL create() {
        return new HL();
    }

    public JContext languageContext() {
        return projectContext.languageContext();
    }

    public HProject compile() {
        HL options = this;
        JContext context = languageContext().newContext();
        HProject project = new HProject(context, projectContext.indexer());
        Chronometer globalChronometer = Chronometer.start();
        if (getTargets().isEmpty()) {
            project.log().error("X000", "options", "mising target", null);
        }
        try {
            if (project.isSuccessful()) {
                List<HStage> stages = new ArrayList<>();
                stages.add(new HStage01Parser());
                stages.add(new HStage02Preprocessor());
                stages.add(new HStage03Indexer());
                stages.add(new HStage04DefinitionResolver(false));
                stages.add(new HStage05CallResolver(false));
                stages.add(new HStage08JavaTransform(false));
                stages.add(new HStage09JavaGenerator());
                stages.add(new HStage10JavaCompiler());
                EnumSet<HTarget> toProcessTargets = EnumSet.copyOf(options.getTargets());
                for (int i = 0; i < stages.size(); i++) {
                    HStage stage = stages.get(i);
                    String stageName = stage.getClass().getSimpleName();
                    if (stage.isEnabled(project, options)) {
                        for (HTarget target : stage.getTargets()) {
                            toProcessTargets.remove(target);
                        }
                        LOG.log(Level.INFO, "{0} ({1}/{2}) starting...", new Object[]{stageName, i + 1, stages.size()});
                        Chronometer chronometer = Chronometer.start(stageName);
                        stage.processProject(project, options);
                        LOG.log(Level.INFO, "{0} ({1}/{2}) took {3}", new Object[]{stageName, i + 1, stages.size(), chronometer.stop().getDuration()});
                        if (!project.isSuccessful()) {
                            break;
                        }
                    } else {
                        stage.isEnabled(project, options);
                    }
                }
                if (toProcessTargets.size() > 0) {
                    LOG.log(Level.SEVERE, "unsupported targets : {0}", toProcessTargets);
                    project.log().error("X000", null, "unsupported targets : " + toProcessTargets, null);
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.INFO, "unexpected error : " + ex.toString(), ex);
            project.log().error("X000", null, "unexpected error : " + ex.toString(), null);
        }
        LOG.log(Level.INFO, "compilation finished with {0} errors and {1} warnings in {2}", new Object[]{
            project.errorCount(),
            project.warningCount(),
            globalChronometer.stop().getDuration()
        });
        project.log().printFooter();
        return project;
    }

}
