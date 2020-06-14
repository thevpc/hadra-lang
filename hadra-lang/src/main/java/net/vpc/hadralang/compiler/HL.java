package net.vpc.hadralang.compiler;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.util.Chronometer;
import net.vpc.hadralang.compiler.core.*;
import net.vpc.hadralang.compiler.index.DefaultHLIndexer;
import net.vpc.hadralang.compiler.index.HLIndexer;
import net.vpc.hadralang.compiler.stages.*;
import net.vpc.hadralang.compiler.stages.generators.java.HLCStage09JavaGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.hadralang.compiler.stages.generators.java.HLCStage08JavaTransform;

/**
 * Hadra Language Compiler
 */
public class HL {
    public static final Logger LOG = Logger.getLogger(HL.class.getName());
    //    public static final String NON_ID_CHARS = "\\[]{}()<>*+/~&|=£%'\"`;,.:#@!?-";
    private HLProjectContext projectContext;

    public HL() {
        this((ClassLoader) null, null);
    }

    public HL(ClassLoader classLoader, HLIndexer indexer) {
        this(new HadraLanguage(classLoader), indexer);
    }

    public HL(JContext language, HLIndexer indexer) {
        this(
                new DefaultHLProjectContext(
                        language == null ? new HadraLanguage() : language,
                        indexer == null ? new DefaultHLIndexer() : indexer,
                        null
                )
        );
    }

    public HL(HLProjectContext projectContext) {
        if (projectContext == null || projectContext.indexer() == null || projectContext.languageContext() == null) {
            throw new NullPointerException();
        }
        this.projectContext = projectContext;
    }

    public JContext languageContext() {
        return projectContext.languageContext();
    }

    public HLCWithOptions withOptions() {
        return new HLCWithOptions(this);
    }

    public HLProject compile(HLCOptions options) {
        JContext context = languageContext().newContext();
        HLProject project = new HLProject(context, projectContext.indexer());
        Chronometer globalChronometer = Chronometer.start();
        try {
            if (project.isSuccessful()) {
                List<HLCStage> stages = new ArrayList<>();
                stages.add(new HLCStage01Parser());
                stages.add(new HLCStage02Preprocessor());
                stages.add(new HLCStage03Indexer());
                stages.add(new HLCStage04DefinitionResolver(false));
                stages.add(new HLCStage05CallResolver(false));
                if (options.generateJavaFolder() != null) {
                    stages.add(new HLCStage08JavaTransform(false));
                    stages.add(new HLCStage09JavaGenerator());
                }
                for (int i = 0; i < stages.size(); i++) {
                    HLCStage stage = stages.get(i);
                    String stageName = stage.getClass().getSimpleName();
                    LOG.log(Level.INFO, "{0} ({1}/{2}) starting...",new Object[]{stageName,i+1,stages.size()});
                    Chronometer chronometer = Chronometer.start(stageName);
                    stage.processProject(project, options);
                    LOG.log(Level.INFO, "{0} ({1}/{2}) took {3}",new Object[]{stageName,i+1,stages.size(),chronometer.stop().getDuration()});
                    if (!project.isSuccessful()) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.INFO, "unexpected error : " + ex.toString(), ex);
            project.log().error("X000", null, "unexpected error : " + ex.toString(), null);
        }
        LOG.log(Level.INFO, "compilation finished with {0} errors and {1} warnings in {2}",new Object[]{
                project.errorCount(),
                project.warningCount(),
                globalChronometer.stop().getDuration()
        });
        project.log().printFooter();
        return project;
    }


}
