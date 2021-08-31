package net.thevpc.hl.build;

import net.hl.compiler.HL;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.HTask;
import net.thevpc.nuts.*;

public class HLMain implements NutsApplication {

    private static final String PREFERRED_ALIAS = "hl";

    public static void main(String[] args) {
        new HLMain().runAndExit(args);
    }

    @Override
    public void run(NutsApplicationContext applicationContext) {
        applicationContext.processCommandLine(new NutsCommandLineProcessor() {
            HL hl = HL.create(applicationContext.getSession());
            boolean noMoreOptions = false;

            @Override
            public boolean onNextOption(NutsArgument argument, NutsCommandLine cmdLine) {
                if (noMoreOptions) {
                    return false;
                }
                switch (argument.getStringKey()) {
                    case "--clean": {
                        if (cmdLine.nextBoolean().getBooleanValue()) {
                            hl.addTask(HTask.CLEAN);
                        } else {
                            hl.removeTask(HTask.CLEAN);
                        }
                        return true;
                    }
                    case "-i":
                    case "--incremental": {
                        hl.setIncremental(cmdLine.nextBoolean().getBooleanValue());
                        return true;
                    }
                    case "-r":
                    case "--root": {
                        hl.setProjectRoot(cmdLine.nextString().getStringValue());
                        return true;
                    }
                    case "--java": {
                        cmdLine.skip();
                        hl.addTask(HTask.JAVA);
                        return true;
                    }
                    case "--c": {
                        cmdLine.skip();
                        hl.addTask(HTask.C);
                        return true;
                    }
                    case "--c++": {
                        cmdLine.skip();
                        hl.addTask(HTask.CPP);
                        return true;
                    }
                    case "--cs": {
                        cmdLine.skip();
                        hl.addTask(HTask.CS);
                        return true;
                    }
                    case "--run": {
                        cmdLine.skip();
                        hl.addTask(HTask.RUN);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean onNextNonOption(NutsArgument argument, NutsCommandLine cmdLine) {
                String s = cmdLine.next().getString();
                if (isURL(s)) {
                    hl.addSourceFileURL(s);
                } else {
                    hl.addSourceFile(s);
                }
                noMoreOptions = true;
                return true;
            }

            private boolean isURL(String s) {
                return s.startsWith("file:")
                        || s.startsWith("http:")
                        || s.startsWith("https:");
            }

            @Override
            public void onExec() {
                final HProject e = hl.compile();
                if (!e.isSuccessful()) {
                    String m = "compilation failed with ";
                    m += e.getErrorCount() > 1 ? (String.valueOf(e.getErrorCount()) + " errors") : "1 error";
                    if (e.getWarningCount() > 0) {
                        m += (" and " + (e.getWarningCount() > 1 ? (String.valueOf(e.getWarningCount()) + " errors") : "1 error"));
                    }
                    throw new NutsExecutionException(applicationContext.getSession(), NutsMessage.plain(m), 201);
                }
            }
        });
    }

    @Override
    public void onUninstallApplication(NutsApplicationContext applicationContext) {
        NutsWorkspace ws = applicationContext.getWorkspace();
        ws.commands().removeCommandIfExists(PREFERRED_ALIAS);
        ws.config().save();
    }

    @Override
    public void onUpdateApplication(NutsApplicationContext applicationContext) {
        onInstallApplication(applicationContext);
    }

    @Override
    public void onInstallApplication(NutsApplicationContext applicationContext) {
        NutsWorkspace ws = applicationContext.getWorkspace();
        NutsSession session = applicationContext.getSession();
        NutsWorkspaceCustomCommand a = ws.commands().findCommand(PREFERRED_ALIAS, applicationContext.getAppId(), applicationContext.getAppId());
        boolean update = false;
        boolean add = false;
        if (a != null) {
            update = true;
        } else if (ws.commands().findCommand(PREFERRED_ALIAS) == null) {
            add = true;
        }
        if (update || add) {
            ws.commands()
                    .setSession((update ? session.copy().setConfirm(NutsConfirmationMode.YES) : session))
                    .addCommand(new NutsCommandConfig()
                    .setName(PREFERRED_ALIAS)
                    .setOwner(applicationContext.getAppId())
                    .setCommand(applicationContext.getAppId().getShortName())
                    );
            ws.config().save();
        }
    }



}
