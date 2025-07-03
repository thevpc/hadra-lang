package net.thevpc.hl.build;

import net.hl.compiler.HL;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.HTask;
import net.thevpc.nuts.*;
import net.thevpc.nuts.cmdline.NArg;
import net.thevpc.nuts.cmdline.NCmdLine;
import net.thevpc.nuts.cmdline.NCmdLineRunner;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;

public class HLMain implements NApplication {

    private static final String PREFERRED_ALIAS = "hl";

    public static void main(String[] args) {
        NApp.builder(args).run();
    }

    @Override
    public void run() {
        NApp.of().runCmdLine(new NCmdLineRunner() {
            HL hl = HL.create();
            boolean noMoreOptions = false;


            @Override
            public boolean next(NArg arg, NCmdLine cmdLine) {
                if (arg.isOption()) {
                    if (noMoreOptions) {
                        return false;
                    }
                    switch (arg.key()) {
                        case "--clean": {
                            arg = cmdLine.nextFlag().get();
                            if (arg.isNonCommented()) {
                                if (arg.getBooleanValue().get()) {
                                    hl.addTask(HTask.CLEAN);
                                } else {
                                    hl.removeTask(HTask.CLEAN);
                                }
                            }
                            return true;
                        }
                        case "-i":
                        case "--incremental": {
                            arg = cmdLine.nextFlag().get();
                            if (arg.isNonCommented()) {
                                hl.setIncremental(arg.getBooleanValue().get());
                            }
                            return true;
                        }
                        case "-r":
                        case "--root": {
                            arg = cmdLine.nextEntry().get();
                            if (arg.isNonCommented()) {
                                hl.setProjectRoot(arg.getStringValue().get());
                            }
                            return true;
                        }
                        case "--java": {
                            arg = cmdLine.next().get();
                            if (arg.isNonCommented()) {
                                hl.addTask(HTask.JAVA);
                            }
                            return true;
                        }
                        case "--c": {
                            arg = cmdLine.nextEntry().get();
                            if (arg.isNonCommented()) {
                                hl.addTask(HTask.C);
                            }
                            return true;
                        }
                        case "--c++": {
                            arg = cmdLine.nextEntry().get();
                            if (arg.isNonCommented()) {
                                hl.addTask(HTask.CPP);
                            }
                            return true;
                        }
                        case "--cs": {
                            arg = cmdLine.nextEntry().get();
                            if (arg.isNonCommented()) {
                                hl.addTask(HTask.CS);
                            }
                            return true;
                        }
                        case "--run": {
                            arg = cmdLine.nextEntry().get();
                            if (arg.isNonCommented()) {
                                hl.addTask(HTask.RUN);
                            }
                            return true;
                        }
                    }
                    return false;
                } else {
                    String s = cmdLine.next().get().toString();
                    if (isURL(s)) {
                        hl.addSourceFileURL(s);
                    } else {
                        hl.addSourceFile(s);
                    }
                    noMoreOptions = true;
                    return true;
                }
            }


            private boolean isURL(String s) {
                return s.startsWith("file:")
                        || s.startsWith("http:")
                        || s.startsWith("https:");
            }

            @Override
            public void run(NCmdLine nCmdLine) {
                final HProject e = hl.compile();
                if (!e.isSuccessful()) {
                    String m = "compilation failed with ";
                    m += e.getErrorCount() > 1 ? (String.valueOf(e.getErrorCount()) + " errors") : "1 error";
                    if (e.getWarningCount() > 0) {
                        m += (" and " + (e.getWarningCount() > 1 ? (String.valueOf(e.getWarningCount()) + " errors") : "1 error"));
                    }
                    throw new NExecutionException(NMsg.ofPlain(m), 201);
                }
            }
        });
    }

    @Override
    public void onUninstallApplication() {
        NWorkspace.of().removeCommandIfExists(PREFERRED_ALIAS);
        NWorkspace.of().saveConfig();
    }

    @Override
    public void onUpdateApplication() {
        onInstallApplication();
    }

    @Override
    public void onInstallApplication() {
        NWorkspace ws = NWorkspace.of();
        NApp app = NApp.of();
        NId appId = app.getId().get();
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
                            .setName(PREFERRED_ALIAS)
                            .setOwner(appId)
                            .setCommand(appId.getShortName())
                    );
            ws.saveConfig();
        }
    }


}
