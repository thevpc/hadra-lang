package net.thevpc.hl.build;

import net.hl.compiler.HL;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.HTask;
import net.thevpc.nuts.*;
import net.thevpc.nuts.cmdline.NArg;
import net.thevpc.nuts.cmdline.NCmdLine;
import net.thevpc.nuts.cmdline.NCmdLineContext;
import net.thevpc.nuts.cmdline.NCmdLineRunner;
import net.thevpc.nuts.util.NMsg;

public class HLMain implements NApplication {

    private static final String PREFERRED_ALIAS = "hl";

    public static void main(String[] args) {
        new HLMain().runAndExit(args);
    }

    @Override
    public void run(NSession session) {
        session.runAppCmdLine(new NCmdLineRunner() {
            HL hl = HL.create(session);
            boolean noMoreOptions = false;

            @Override
            public boolean nextOption(NArg option, NCmdLine cmdLine, NCmdLineContext context) {
                if (noMoreOptions) {
                    return false;
                }
                switch (option.key()) {
                    case "--clean": {
                        NArg arg = cmdLine.nextFlag().get();
                        if (arg.isActive()) {
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
                        NArg arg = cmdLine.nextFlag().get();
                        if (arg.isActive()) {
                            hl.setIncremental(arg.getBooleanValue().get());
                        }
                        return true;
                    }
                    case "-r":
                    case "--root": {
                        NArg arg = cmdLine.nextEntry().get();
                        if (arg.isActive()) {
                            hl.setProjectRoot(arg.getStringValue().get());
                        }
                        return true;
                    }
                    case "--java": {
                        NArg arg = cmdLine.next().get();
                        if (arg.isActive()) {
                            hl.addTask(HTask.JAVA);
                        }
                        return true;
                    }
                    case "--c": {
                        NArg arg = cmdLine.nextEntry().get();
                        if (arg.isActive()) {
                            hl.addTask(HTask.C);
                        }
                        return true;
                    }
                    case "--c++": {
                        NArg arg = cmdLine.nextEntry().get();
                        if (arg.isActive()) {
                            hl.addTask(HTask.CPP);
                        }
                        return true;
                    }
                    case "--cs": {
                        NArg arg = cmdLine.nextEntry().get();
                        if (arg.isActive()) {
                            hl.addTask(HTask.CS);
                        }
                        return true;
                    }
                    case "--run": {
                        NArg arg = cmdLine.nextEntry().get();
                        if (arg.isActive()) {
                            hl.addTask(HTask.RUN);
                        }
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean nextNonOption(NArg nonOption, NCmdLine cmdLine, NCmdLineContext context) {
                String s = cmdLine.next().get().toString();
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
            public void run(NCmdLine nCmdLine, NCmdLineContext nCmdLineContext) {
                final HProject e = hl.compile();
                if (!e.isSuccessful()) {
                    String m = "compilation failed with ";
                    m += e.getErrorCount() > 1 ? (String.valueOf(e.getErrorCount()) + " errors") : "1 error";
                    if (e.getWarningCount() > 0) {
                        m += (" and " + (e.getWarningCount() > 1 ? (String.valueOf(e.getWarningCount()) + " errors") : "1 error"));
                    }
                    throw new NExecutionException(session, NMsg.ofPlain(m), 201);
                }
            }
        });
    }

    @Override
    public void onUninstallApplication(NSession session) {
        NCommands.of(session).removeCommandIfExists(PREFERRED_ALIAS);
        NConfigs.of(session).save();
    }

    @Override
    public void onUpdateApplication(NSession session) {
        onInstallApplication(session);
    }

    @Override
    public void onInstallApplication(NSession session) {
        NCommands commands = NCommands.of(session);
        NCustomCmd a = commands.findCommand(PREFERRED_ALIAS, session.getAppId(), session.getAppId());
        boolean update = false;
        boolean add = false;
        if (a != null) {
            update = true;
        } else if (commands.findCommand(PREFERRED_ALIAS) == null) {
            add = true;
        }
        if (update || add) {
            commands
                    .setSession((update ? session.copy().setConfirm(NConfirmationMode.YES) : session))
                    .addCommand(new NCommandConfig()
                            .setName(PREFERRED_ALIAS)
                            .setOwner(session.getAppId())
                            .setCommand(session.getAppId().getShortName())
                    );
            NConfigs.of(session).save();
        }
    }


}
