/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.core;

import net.hl.compiler.utils.StringUtils;
import net.thevpc.jeep.DefaultJCompilerLog;
import net.thevpc.jeep.log.JSourceMessage;
import net.thevpc.jeep.msg.Message;
import net.thevpc.jeep.source.JTextSource;
import net.thevpc.jeep.source.JTextSourceRange;
import net.thevpc.jeep.source.JTextSourceToken;
import net.thevpc.nuts.NMsg;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPrintStream;
import net.thevpc.nuts.io.NPrintStream;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.text.NTextStyle;
import net.thevpc.nuts.text.NTexts;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.logging.Level;

/**
 * @author vpc
 */
public class HCompilerLog extends DefaultJCompilerLog {

    private NSession session;

    public HCompilerLog(NSession session) {
        this.session = session;
    }

    @Override
    public void printFooter() {
        NPrintStream out = session.out();
        String op = getOperationName();
        if (op == null) {
            op = DEFAULT_OPERATION_NAME;
        }
        final int errors = getErrorCount();
        final int warnings = getWarningCount();
        NMsg m;
        if (errors == 0 && warnings == 0) {
            m = NMsg.ofC("%s ##:success:successful##", op, warnings);
        } else if (errors == 0) {
            m = NMsg.ofC("%s ##:success:successful## with ##:warn:%d## warning%s", op, warnings, warnings > 1 ? "s" : "");
        } else if (warnings == 0) {
            m = NMsg.ofC("%s ##:fail:failed## with ##:fail:%d## error%s", op, errors, errors > 1 ? "s" : "");
        } else {
            m = NMsg.ofC("%s ##:fail:failed## with ##:fail:%d## error%s and ##:warn:%d## warning%s", op, errors, errors > 1 ? "s" : "", warnings, warnings > 1 ? "s" : "");
        }

        out.println(
                StringUtils.center2(
                        "[ " + ofTexts().ofText(
                                m
                        ).toString()
                                + " ]",
                        80, '-', session)
        );
    }

    private NTexts ofTexts() {
        return NTexts.of(session);
    }

    @Override
    public void printlnMessage(JSourceMessage jSourceMessage) {
        NPrintStream out = session.out();
        boolean showCompilationSource = true;
        final JTextSourceToken token = jSourceMessage.getToken();
        final Level level = jSourceMessage.getLevel();
        final String id = jSourceMessage.getId();
        final Message message = jSourceMessage.getMessage();
        final NTexts factory = ofTexts();

        JTextSource compilationUnitSource0 = token == null ? null : token.getSource();
        String compilationUnitSource = compilationUnitSource0 == null ? "" : compilationUnitSource0.name();
        String compilationUnitSourceShort = compilationUnitSource;
        if (compilationUnitSourceShort.length() > 0 && (compilationUnitSourceShort.contains("/") || compilationUnitSourceShort.contains("\\"))) {
            compilationUnitSourceShort = compilationUnitSourceShort.replace('\\', '/');
            while (compilationUnitSourceShort.endsWith("/")) {
                compilationUnitSourceShort = compilationUnitSourceShort.substring(0, compilationUnitSourceShort.length() - 1);
            }
            if (compilationUnitSourceShort.contains("/")) {
                compilationUnitSourceShort = compilationUnitSourceShort.substring(
                        compilationUnitSourceShort.lastIndexOf('/') + 1
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
                out.print(NMsg.ofC("%s", StringUtils.left(compilationUnitSourceShort, 13)));
            }
            if (token != null) {
                out.print(NMsg.ofC(" [%s,%s]",
                        factory.ofStyled(StringUtils.right(String.valueOf(token.getStartLineNumber() + 1), 4), NTextStyle.number()),
                        factory.ofStyled(StringUtils.right(String.valueOf(token.getStartColumnNumber() + 1), 3), NTextStyle.number())
                ));
            } else {
                out.print("           ");
            }
        }
        out.print(" ");
        NText n;
        if (level.intValue() >= Level.SEVERE.intValue()) {
            n = factory.ofStyled(StringUtils.left("ERROR  ", 6), NTextStyle.error());
        } else if (level.intValue() >= Level.WARNING.intValue()) {
            n = factory.ofStyled(StringUtils.left("WARNING", 6), NTextStyle.warn());
        } else if (level.intValue() >= Level.INFO.intValue()) {
            n = factory.ofStyled(StringUtils.left("INFO   ", 6), NTextStyle.info());
        } else if (level.intValue() >= Level.CONFIG.intValue()) {
            n = factory.ofStyled(StringUtils.left("CONFIG ", 6), NTextStyle.config());
        } else {
            n = factory.ofStyled(StringUtils.left(level.toString(), 6), NTextStyle.pale());
        }
        out.print(NMsg.ofC("%s [%-5s] : %s",
                n,
                factory.ofStyled(StringUtils.left(id == null ? "" : id, 6), NTextStyle.version()),
                message.getText()
        ));
        boolean includeSourceNameInRange = false;
        if (token != null && compilationUnitSource0 != null) {
            if (includeSourceNameInRange) {
                out.print(NMsg.ofC("%s", factory.ofStyled(compilationUnitSource0.name(), NTextStyle.path())));
                out.print(NMsg.ofC("%s", ":"));
            }
            long cn = token.getStartCharacterNumber();
            int window = 80;
            JTextSourceRange range = compilationUnitSource0.range((int) cn - window, (int) cn + window);
            JTextSourceRange.JRangePointer windowString = range.trim(cn, window);
            out.print("\n   ");
            out.print(NMsg.ofC("%s", factory.ofCode("hadra", windowString.getText())));
            out.print(NMsg.ofC("\n   "));
            for (int i = 0; i < windowString.getOffset(); i++) {
                out.print(" ");
            }
            out.print(NMsg.ofC("%s", factory.ofStyled("^^^", NTextStyle.path())));

//            out.printf("%s", " [Line:");
//            out.printf("%s", text.ofStyled(String.valueOf(token.getStartLineNumber() + 1), NTextStyle.number()));
//            out.printf("%s", ",Column:");
//            out.printf("%s", text.ofStyled(String.valueOf(token.getStartColumnNumber() + 1), NTextStyle.number()));
//            out.printf("%s", "]");
            if (compilationUnitSource.length() > 0 && (compilationUnitSource.contains("/") || compilationUnitSource.contains("\\"))) {
                String s = compilationUnitSource;
                try {
                    s = Paths.get(compilationUnitSource).normalize().toAbsolutePath().toString();
                } catch (Exception ex) {
                    //
                }
                out.print(NMsg.ofC(" %s", factory.ofStyled(s, NTextStyle.path())));
            } else if (compilationUnitSource.length() > 0) {
                out.print(NMsg.ofC(" %s", factory.ofStyled(compilationUnitSource, NTextStyle.path())));
            }
            out.print("\n");
        } else {
            out.print("\n");
        }
    }
}
