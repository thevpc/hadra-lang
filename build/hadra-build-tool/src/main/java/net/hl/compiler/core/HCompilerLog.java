/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.core;

import java.io.PrintStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.logging.Level;
import net.hl.compiler.utils.StringUtils;
import net.thevpc.common.msg.Message;
import net.thevpc.common.textsource.JTextSource;
import net.thevpc.common.textsource.JTextSourceRange;
import net.thevpc.common.textsource.JTextSourceToken;
import net.thevpc.common.textsource.log.JSourceMessage;
import net.thevpc.jeep.DefaultJCompilerLog;
import net.thevpc.nuts.NutsMessage;
import net.thevpc.nuts.NutsPrintStream;
import net.thevpc.nuts.NutsSession;
import net.thevpc.nuts.NutsTextStyle;
import net.thevpc.nuts.NutsTextManager;
import net.thevpc.nuts.NutsText;

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
        NutsPrintStream out = session.out();
        String op = getOperationName();
        if (op == null) {
            op = DEFAULT_OPERATION_NAME;
        }
        final int errors = getErrorCount();
        final int warnings = getWarningCount();
        NutsMessage m;
        if (errors == 0 && warnings == 0) {
            m = NutsMessage.cstyle("%s ##:success:successful##", op, warnings);
        } else if (errors == 0) {
            m = NutsMessage.cstyle("%s ##:success:successful## with ##:warn:%d## warning%s", op, warnings, warnings > 1 ? "s" : "");
        } else if (warnings == 0) {
            m = NutsMessage.cstyle("%s ##:fail:failed## with ##:fail:%d## error%s", op, errors, errors > 1 ? "s" : "");
        } else {
            m = NutsMessage.cstyle("%s ##:fail:failed## with ##:fail:%d## error%s and ##:warn:%d## warning%s", op, errors, errors > 1 ? "s" : "", warnings, warnings > 1 ? "s" : "");
        }

        out.println(
                StringUtils.center2(
                        "[ " + session.getWorkspace().text().toText(
                                m
                        ).toString()
                        + " ]",
                        80, '-', session.getWorkspace())
        );
    }

    @Override
    public void printlnMessage(JSourceMessage jSourceMessage) {
        NutsPrintStream out = session.out();
        boolean showCompilationSource = true;
        final JTextSourceToken token = jSourceMessage.getToken();
        final Level level = jSourceMessage.getLevel();
        final String id = jSourceMessage.getId();
        final Message message = jSourceMessage.getMessage();
        final NutsTextManager factory = session.getWorkspace().text();

        JTextSource compilationUnitSource0 = token == null ? null : token.getSource();
        String compilationUnitSource = compilationUnitSource0 == null ? "" : compilationUnitSource0.name();
        String compilationUnitSourceShort = compilationUnitSource;
        if (compilationUnitSourceShort.length() > 0 && (compilationUnitSourceShort.contains("/") || compilationUnitSourceShort.contains("\\"))) {
            compilationUnitSourceShort=compilationUnitSourceShort.replace('\\', '/');
            while(compilationUnitSourceShort.endsWith("/")){
                compilationUnitSourceShort=compilationUnitSourceShort.substring(0,compilationUnitSourceShort.length());
            }
            if(compilationUnitSourceShort.contains("/")){
                compilationUnitSourceShort=compilationUnitSourceShort.substring(
                        compilationUnitSourceShort.lastIndexOf('/')+1
                );
            }
        }
        if (showCompilationSource && compilationUnitSourceShort.isEmpty() && token == null) {
            StringBuilder s = new StringBuilder(LocalDateTime.now().toString());
            while (s.length() < 23) {
                s.append(' ');
            }
            out.print(s);
        } else {
            if (showCompilationSource) {
                out.printf("%s", StringUtils.left(compilationUnitSourceShort, 13));
            }
            if (token != null) {
                out.printf(" [%s,%s]",
                        factory.forStyled(StringUtils.right(String.valueOf(token.getStartLineNumber() + 1), 4), NutsTextStyle.number()),
                        factory.forStyled(StringUtils.right(String.valueOf(token.getStartColumnNumber() + 1), 3), NutsTextStyle.number())
                );
            } else {
                out.print("           ");
            }
        }
        out.print(" ");
        NutsText n;
        if (level.intValue() >= Level.SEVERE.intValue()) {
            n = factory.forStyled(StringUtils.left("ERROR  ", 6), NutsTextStyle.error());
        } else if (level.intValue() >= Level.WARNING.intValue()) {
            n = factory.forStyled(StringUtils.left("WARNING", 6), NutsTextStyle.warn());
        } else if (level.intValue() >= Level.INFO.intValue()) {
            n = factory.forStyled(StringUtils.left("INFO   ", 6), NutsTextStyle.info());
        } else if (level.intValue() >= Level.CONFIG.intValue()) {
            n = factory.forStyled(StringUtils.left("CONFIG ", 6), NutsTextStyle.config());
        } else {
            n = factory.forStyled(StringUtils.left(level.toString(), 6), NutsTextStyle.pale());
        }
        out.printf("%s [%-5s] : %s",
                n,
                factory.forStyled(StringUtils.left(id == null ? "" : id, 6), NutsTextStyle.version()),
                message.getText()
        );
        boolean includeSourceNameInRange = false;
        if (token != null && compilationUnitSource0 != null) {
            if (includeSourceNameInRange) {
                out.printf("%s", factory.forStyled(compilationUnitSource0.name(), NutsTextStyle.path()));
                out.printf("%s", ":");
            }
            long cn = token.getStartCharacterNumber();
            int window = 80;
            JTextSourceRange range = compilationUnitSource0.range((int) cn - window, (int) cn + window);
            JTextSourceRange.JRangePointer windowString = range.trim(cn, window);
            out.print("\n   ");
            out.printf("%s", factory.forCode("hadra", windowString.getText()));
            out.append("\n   ");
            for (int i = 0; i < windowString.getOffset(); i++) {
                out.print(" ");
            }
            out.printf("%s", factory.forStyled("^^^", NutsTextStyle.path()));

//            out.printf("%s", " [Line:");
//            out.printf("%s", text.forStyled(String.valueOf(token.getStartLineNumber() + 1), NutsTextStyle.number()));
//            out.printf("%s", ",Column:");
//            out.printf("%s", text.forStyled(String.valueOf(token.getStartColumnNumber() + 1), NutsTextStyle.number()));
//            out.printf("%s", "]");
            if (compilationUnitSource.length() > 0 && (compilationUnitSource.contains("/") || compilationUnitSource.contains("\\"))) {
                String s = compilationUnitSource;
                try {
                    s = Paths.get(compilationUnitSource).normalize().toAbsolutePath().toString();
                } catch (Exception ex) {
                    //
                }
                out.printf(" %s", factory.forStyled(s, NutsTextStyle.path()));
            } else if (compilationUnitSource.length() > 0) {
                out.printf(" %s", factory.forStyled(compilationUnitSource, NutsTextStyle.path()));
            }
            out.printf("%n");
        } else {
            out.print("\n");
        }
    }
}
