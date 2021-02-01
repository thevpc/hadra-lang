package net.thevpc.hl.build;

import net.hl.compiler.HL;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.HTarget;
import net.thevpc.nuts.*;

public class HLMain extends NutsApplication {

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
            public boolean nextOption(NutsArgument argument, NutsCommandLine cmdLine) {
                if (noMoreOptions) {
                    return false;
                }
                switch (argument.getStringKey()) {
                    case "--clean": {
                        hl.clean(cmdLine.nextBoolean().getBooleanValue());
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
                        hl.addTarget(HTarget.JAVA);
                        return true;
                    }
                    case "--c": {
                        cmdLine.skip();
                        hl.addTarget(HTarget.C);
                        return true;
                    }
                    case "--c++": {
                        cmdLine.skip();
                        hl.addTarget(HTarget.CPP);
                        return true;
                    }
                    case "--cs": {
                        cmdLine.skip();
                        hl.addTarget(HTarget.CS);
                        return true;
                    }
                    case "--run": {
                        cmdLine.skip();
                        hl.addTarget(HTarget.RUN);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean nextNonOption(NutsArgument argument, NutsCommandLine cmdLine) {
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
            public void exec() {
                final HProject e = hl.compile();
                if (!e.isSuccessful()) {
                    String m = "compilation failed with ";
                    m += e.getErrorCount() > 1 ? (String.valueOf(e.getErrorCount()) + " errors") : "1 error";
                    if (e.getWarningCount() > 0) {
                        m += (" and " + (e.getWarningCount() > 1 ? (String.valueOf(e.getWarningCount()) + " errors") : "1 error"));
                    }
                    throw new NutsExecutionException(applicationContext.getWorkspace(), m, 201);
                }
            }
        });
    }

    @Override
    protected void onUninstallApplication(NutsApplicationContext applicationContext) {
        NutsWorkspace ws = applicationContext.getWorkspace();
        NutsSession session = applicationContext.getSession();
        NutsWorkspaceCommandAlias a = findDefaultAlias(applicationContext);
        if (a != null) {
            ws.aliases().remove(PREFERRED_ALIAS, new NutsRemoveOptions().setSession(session));
            ws.config().save(session);
        }
    }

    @Override
    protected void onUpdateApplication(NutsApplicationContext applicationContext) {
        onInstallApplication(applicationContext);
    }

    @Override
    protected void onInstallApplication(NutsApplicationContext applicationContext) {
        NutsWorkspace ws = applicationContext.getWorkspace();
        NutsSession session = applicationContext.getSession();
        NutsWorkspaceCommandAlias a = findDefaultAlias(applicationContext);
        boolean update = false;
        boolean add = false;
        if (a != null) {
            update = true;
        } else if (ws.aliases().find(PREFERRED_ALIAS, session) == null) {
            add = true;
        }
        if (update || add) {
            ws.aliases().add(new NutsCommandAliasConfig()
                    .setName(PREFERRED_ALIAS)
                    .setOwner(applicationContext.getAppId())
                    .setCommand(applicationContext.getAppId().getShortName()),
                    new NutsAddOptions().setSession(update ? session.copy().setConfirm(NutsConfirmationMode.YES) : session));
            ws.config().save(session);
        }
    }

    private NutsWorkspaceCommandAlias findDefaultAlias(NutsApplicationContext applicationContext) {
        NutsWorkspace ws = applicationContext.getWorkspace();
        NutsSession session = applicationContext.getSession();
        NutsId appId = applicationContext.getAppId();
        return ws.aliases().find(PREFERRED_ALIAS, appId, appId, session);
    }

}
