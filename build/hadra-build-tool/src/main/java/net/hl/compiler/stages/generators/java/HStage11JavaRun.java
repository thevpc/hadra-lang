package net.hl.compiler.stages.generators.java;

import java.io.IOException;
import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;

import java.util.logging.Logger;
import net.hl.compiler.HL;
import net.hl.compiler.core.HTarget;
import net.hl.compiler.stages.AbstractHStage;
import net.thevpc.nuts.NutsWorkspace;

public class HStage11JavaRun extends AbstractHStage {

    private static final Logger LOG = Logger.getLogger(HStage11JavaRun.class.getName());

    @Override
    public HTarget[] getTargets() {
        return new HTarget[]{HTarget.RUN};
    }

    @Override
    public boolean isEnabled(HProject project, HL options) {
        if ((options.containsAnyTargets(HTarget.RUN))) {
            if (options.containsAllTargets(HTarget.JAVA)) {
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
                NutsWorkspace ws = project.getSession().getWorkspace();
                ws.exec()
                        .addCommand(jn.getOutputJarFile().getCanonicalPath())
                        .setSleepMillis(1000)
                        .setInheritSystemIO(true)
                        .setRedirectErrorStream(true)
                        .setFailFast(true)
                        .getResult();
                return;
            } catch (IOException ex) {
                project.log().jerror("RJ01", "run", null, "error executing jar", ex);
            }
        }
    }

}
