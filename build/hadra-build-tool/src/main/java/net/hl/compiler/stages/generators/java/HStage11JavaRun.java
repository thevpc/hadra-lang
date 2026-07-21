package net.hl.compiler.stages.generators.java;

import java.io.IOException;
import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;

import java.util.logging.Logger;
import net.hl.compiler.HL;
import net.hl.compiler.core.HTask;
import net.hl.compiler.stages.AbstractHStage;
import net.thevpc.nuts.command.NExec;
import net.thevpc.nuts.command.NExecutionType;
import net.thevpc.nuts.io.NExecOutput;
import net.thevpc.nuts.time.NDuration;

public class HStage11JavaRun extends AbstractHStage {

    private static final Logger LOG = Logger.getLogger(HStage11JavaRun.class.getName());

    @Override
    public HTask[] getTasks() {
        return new HTask[]{HTask.RUN};
    }

    @Override
    public boolean isEnabled(HProject project, HL options) {
        if ((options.containsAnyTask(HTask.RUN))) {
            if (options.containsAllTasks(HTask.JAVA)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void processProject(HProject project, HOptions options) {

        HJavaContextHelper jn = HJavaContextHelper.of(project);
        if (jn.getOutputJarFile() != null) {
            try {
                NExec.of()
                        .command(jn.getOutputJarFile().getCanonicalPath())
                        .sleepDuration(NDuration.ofSeconds(1))
                        .err(NExecOutput.ofRedirect())
                        .failFast(true)
                        .executionType(NExecutionType.EMBEDDED)
                        .run();
                return;
            } catch (IOException ex) {
                project.log().jerror("RJ01", "run", null, "error executing jar", ex);
            }
        }
    }

}
