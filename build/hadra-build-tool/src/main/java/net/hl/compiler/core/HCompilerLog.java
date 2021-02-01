/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.core;

import java.io.PrintStream;
import java.util.logging.Level;
import net.thevpc.common.msg.Message;
import net.thevpc.common.textsource.JTextSource;
import net.thevpc.common.textsource.JTextSourceRange;
import net.thevpc.common.textsource.JTextSourceToken;
import net.thevpc.common.textsource.log.JSourceMessage;
import net.thevpc.jeep.DefaultJCompilerLog;
import net.thevpc.nuts.NutsSession;
import net.thevpc.nuts.NutsTextNode;
import net.thevpc.nuts.NutsTextNodeFactory;
import net.thevpc.nuts.NutsTextNodeStyle;

/**
 *
 * @author vpc
 */
public class HCompilerLog extends DefaultJCompilerLog {

    private NutsSession session;

    public HCompilerLog(NutsSession session) {
        this.session = session;
    }

    @Override
    public void printFooter() {
        PrintStream out = session.out();
        final NutsTextNodeFactory factory = session.getWorkspace().formats().text().factory();
        out.printf("%s%n", factory.styled("-----------------------------------------------------------------------------------",
                NutsTextNodeStyle.pale())
        );
        String op = getOperationName();
        if (op == null) {
            op = DEFAULT_OPERATION_NAME;
        }
        final int errors = getErrorCount();
        final int warnings = getWarningCount();
        if (errors == 0 && warnings == 0) {
            out.printf("%s ##:success:successful##%n", op, warnings);
        } else if (errors == 0) {
            out.printf("%s ##:success:successful## with ##:warn:%d## warning%s%n", op, warnings, warnings > 1 ? "s" : "");
        } else if (warnings == 0) {
            out.printf("%s ##:fail:failed## with ##:fail:%d## error%s%n", op, errors, errors > 1 ? "s" : "");
        } else {
            out.printf("%s ##:fail:failed## with ##:fail:%d## error%s and ##:warn:%d## warning%s%n", op, errors, errors > 1 ? "s" : "", warnings, warnings > 1 ? "s" : "");
        }
    }

    @Override
    public void printlnMessage(JSourceMessage jSourceMessage) {
        PrintStream out = session.out();
        boolean showCompilationSource = true;
        final JTextSourceToken token = jSourceMessage.getToken();
        final Level level = jSourceMessage.getLevel();
        final String id = jSourceMessage.getId();
        final Message message = jSourceMessage.getMessage();

        JTextSource compilationUnitSource0 = token == null ? null : token.getSource();
        String compilationUnitSource = compilationUnitSource0 == null ? "" : compilationUnitSource0.name();
        if (showCompilationSource) {
            out.printf("%-10s ", compilationUnitSource);
        }
        if (token != null) {
            out.printf("[%4s,%3s] ", (token.getStartLineNumber() + 1), (token.getStartColumnNumber() + 1));
        } else {
            out.print("           ");
        }
        final NutsTextNodeFactory factory = session.getWorkspace().formats().text().factory();
        NutsTextNode n;
        if (level.intValue() >= Level.SEVERE.intValue()) {
            n = factory.styled("ERROR", NutsTextNodeStyle.error());
        } else if (level.intValue() >= Level.WARNING.intValue()) {
            n = factory.styled(level.toString(), NutsTextNodeStyle.warn());
        } else if (level.intValue() >= Level.INFO.intValue()) {
            n = factory.styled(level.toString(), NutsTextNodeStyle.info());
        } else if (level.intValue() >= Level.CONFIG.intValue()) {
            n = factory.styled(level.toString(), NutsTextNodeStyle.config());
        } else {
            n = factory.styled(level.toString(), NutsTextNodeStyle.pale());
        }
        out.printf("%-6s [%-5s] : %s",
                n,
                factory.styled(id == null ? "" : id, NutsTextNodeStyle.version()),
                message.getText()
        );
        boolean includeSourceNameInRange = false;
        if (token != null && compilationUnitSource0 != null) {
            if (includeSourceNameInRange) {
                out.printf("%s", factory.styled(compilationUnitSource0.name(), NutsTextNodeStyle.path()));
                out.printf("%s", ":");
            }
            long cn = token.getStartCharacterNumber();
            int window = 80;
            JTextSourceRange range = compilationUnitSource0.range((int) cn - window, (int) cn + window);
            JTextSourceRange.JRangePointer windowString = range.trim(cn, window);
            out.print("\n   ");
            out.print(windowString.getText());
            out.append("\n   ");
            for (int i = 0; i < windowString.getOffset(); i++) {
                out.print(" ");
            }
            out.printf("%s", factory.styled("^^^", NutsTextNodeStyle.path()));
            out.printf("%s", " [Line:");
            out.printf("%s", factory.styled(String.valueOf(token.getStartLineNumber() + 1), NutsTextNodeStyle.number()));
            out.printf("%s", ",Column:");
            out.printf("%s", factory.styled(String.valueOf(token.getStartColumnNumber() + 1), NutsTextNodeStyle.number()));
            out.printf("%s", "]");
        } else {
            out.print("\n");
        }
    }
}
