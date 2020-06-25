/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.editor.hl4nb.editor.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.vpc.common.jeep.*;
import net.vpc.common.textsource.JTextSourceToken;
import net.vpc.common.textsource.log.JSourceMessage;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.DefaultError;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.csl.api.Severity;

/**
 *
 * @author vpc
 */
public class HLnbParserResult extends ParserResult {

    private boolean valid = true;
    private final JContext hcontext;
    private final JCompilationUnit compilationUnit;
    private final List<Error> errors = new ArrayList<>();

    public HLnbParserResult(Snapshot snapshot, JContext hcontext, JCompilationUnit compilationUnit) {
        super(snapshot);
        this.hcontext = hcontext;
        this.compilationUnit = compilationUnit;

        JCompilerLog syntaxErrors = hcontext.log();
//        Document document = snapshot.getSource().getDocument(false);
        for (JSourceMessage syntaxError : syntaxErrors) {
            JTextSourceToken token = syntaxError.getToken();
            int start = token.getStartCharacterNumber();
            int end = token.getEndCharacterNumber();
            String desc = syntaxError.toString(false);
            String name = desc.split("\n")[0];
            boolean isLineError = (end == -1) || (start == -1);

            Severity sev
                    = (syntaxError.getLevel().intValue() > Level.SEVERE.intValue()) ? Severity.FATAL
                    : (syntaxError.getLevel().intValue() >= Level.SEVERE.intValue()) ? Severity.ERROR
                    : (syntaxError.getLevel().intValue() >= Level.WARNING.intValue()) ? Severity.WARNING
                    : (syntaxError.getLevel().intValue() >= Level.INFO.intValue()) ? Severity.INFO
                    : null;
            if (sev != null) {
                Error e = DefaultError.createDefaultError("SYNTAX_ERROR", name, desc, snapshot.getSource().getFileObject(), start, end, isLineError, sev);
                errors.add(e);
            }
        }

    }

    @Override
    public List<? extends Error> getDiagnostics() {
        return errors;
    }

    public JContext getHcontext() throws org.netbeans.modules.parsing.spi.ParseException {
        if (!valid) {
            throw new org.netbeans.modules.parsing.spi.ParseException();
        }
        return hcontext;
    }

    public JCompilationUnit getCompilationUnit() throws org.netbeans.modules.parsing.spi.ParseException {
        if (!valid) {
            throw new org.netbeans.modules.parsing.spi.ParseException();
        }
        return compilationUnit;
    }

    //        public JavaParser getJavaParser() throws org.netbeans.modules.parsing.spi.ParseException {
    //
    //            return javaParser;
    //        }
    @Override
    protected void invalidate() {
        valid = false;
    }

}
