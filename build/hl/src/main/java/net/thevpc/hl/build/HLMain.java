package net.thevpc.hl.build;

import net.hl.compiler.HL;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.HTask;
import net.thevpc.nuts.app.*;
import net.thevpc.nuts.artifact.NId;
import net.thevpc.nuts.cmdline.NCmdLine;
import net.thevpc.nuts.command.NCommandConfig;
import net.thevpc.nuts.command.NCustomCmd;
import net.thevpc.nuts.command.NExecutionException;
import net.thevpc.nuts.core.NWorkspace;
import net.thevpc.nuts.text.NMsg;

@NApp
public class HLMain {

    private static final String PREFERRED_ALIAS = "hl";
    boolean noMoreOptions = false;
    HL hl;

    public static void main(String[] args) {
        NApplication.builder(args).run();
    }


    private NCmdLine parseCmdLine() {
        NCmdLine cmdLine = NApplication.of().cmdLine();
        cmdLine.matcher()
                .when("--clean").and(c -> !noMoreOptions).asFlag(a -> hl.setTask(HTask.CLEAN, a.booleanValue()))
                .when("--java").and(c -> !noMoreOptions).asFlag(a -> hl.setTask(HTask.JAVA, a.booleanValue()))
                .when("--c").and(c -> !noMoreOptions).asFlag(a -> hl.setTask(HTask.C, a.booleanValue()))
                .when("--c++", "--cpp").and(c -> !noMoreOptions).asFlag(a -> hl.setTask(HTask.CPP, a.booleanValue()))
                .when("--cs", "--c#").and(c -> !noMoreOptions).asFlag(a -> hl.setTask(HTask.CS, a.booleanValue()))
                .when("--run").and(c -> !noMoreOptions).asFlag(a -> hl.setTask(HTask.RUN, a.booleanValue()))
                .when("--incremental", "-i").and(c -> !noMoreOptions).asFlag(a -> hl.setIncremental(a.booleanValue()))
                .when("--root", "-r").and(c -> !noMoreOptions).asEntry(a -> hl.setProjectRoot(a.stringValue()))
                .whenNonOption().asArg(a -> {
                    String s = a.stringValue();
                    if (isURL(s)) {
                        hl.addSourceFileURL(s);
                    } else {
                        hl.addSourceFile(s);
                    }
                    noMoreOptions = true;
                })
                .withDefaults()
                .requireAll();
        return cmdLine;
    }

    @NAppComplete
    public void complete() {
        parseCmdLine().printCompleteResult();
    }

    @NAppRun
    public void run() {
        hl = HL.create();
        NCmdLine cmdLine = parseCmdLine();
        final HProject e = hl.compile();
        if (!e.isSuccessful()) {
            String m = "compilation failed with ";
            m += e.getErrorCount() > 1 ? (e.getErrorCount() + " errors") : "1 error";
            if (e.getWarningCount() > 0) {
                m += (" and " + (e.getWarningCount() > 1 ? (e.getWarningCount() + " errors") : "1 error"));
            }
            throw new NExecutionException(NMsg.ofP(m), 201);
        }
    }

    private boolean isURL(String s) {
        return s.startsWith("file:")
                || s.startsWith("http:")
                || s.startsWith("https:");
    }

    @NAppUninstall
    public void onUninstallApplication() {
        NWorkspace.of().removeCommandIfExists(PREFERRED_ALIAS);
        NWorkspace.of().saveConfig();
    }

    @NAppUpdate
    public void onUpdateApplication() {
        onInstallApplication();
    }

    @NAppInstall
    public void onInstallApplication() {
        NWorkspace ws = NWorkspace.of();
        NApplication app = NApplication.of();
        NId appId = app.id().get();
        NCustomCmd a = ws.findCommand(PREFERRED_ALIAS, appId, appId);
        boolean update = false;
        boolean add = false;
        if (a != null) {
            update = true;
        } else if (ws.findCommand(PREFERRED_ALIAS) == null) {
            add = true;
        }
        if (update || add) {
            ws
                    .addCommand(new NCommandConfig()
                            .name(PREFERRED_ALIAS)
                            .owner(appId)
                            .command(appId.shortName())
                    );
            ws.saveConfig();
        }
    }


}
