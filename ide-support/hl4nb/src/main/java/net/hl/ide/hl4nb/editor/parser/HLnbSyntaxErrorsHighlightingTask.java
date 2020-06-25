package net.hl.ide.hl4nb.editor.parser;

///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package net.hl.ide.hl4nb.parser;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Level;
//import javax.swing.text.BadLocationException;
//import javax.swing.text.Document;
//import net.vpc.common.jeep.JCompilerLog;
//import net.vpc.common.textsource.log.JSourceMessage;
//import net.vpc.common.jeep.JToken;
//import org.netbeans.modules.parsing.spi.Parser.Result;
//import org.netbeans.modules.parsing.spi.ParserResultTask;
//import org.netbeans.modules.parsing.spi.Scheduler;
//import org.netbeans.modules.parsing.spi.SchedulerEvent;
//import org.netbeans.spi.editor.hints.ErrorDescription;
//import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
//import org.netbeans.spi.editor.hints.HintsController;
//import org.netbeans.spi.editor.hints.Severity;
//import org.openide.util.Exceptions;
//
///**
// *
// * @author vpc
// */
//public class HLnbSyntaxErrorsHighlightingTask extends ParserResultTask {
//
//    public HLnbSyntaxErrorsHighlightingTask() {
//    }
//
//    @Override
//    public void run(Result result, SchedulerEvent event) {
//        try {
//            HLnbParserResult sjResult = (HLnbParserResult) result;
//            JCompilerLog syntaxErrors = sjResult.getHcontext().log();
//            Document document = result.getSnapshot().getSource().getDocument(false);
//            List<ErrorDescription> errors = new ArrayList<ErrorDescription>();
//            for (JSourceMessage syntaxError : syntaxErrors) {
//                JToken token = syntaxError.token();
//
////                int start = NbDocument.findLineOffset ((StyledDocument) document, token.startLineNumber) + token.startColumnNumber;
////                int end = NbDocument.findLineOffset ((StyledDocument) document, token.endLineNumber) + token.endColumnNumber;
//                int start = (int) token.startCharacterNumber;
//                int end = (int) token.endCharacterNumber;
//                Severity sev
//                        = (syntaxError.level().intValue() >= Level.SEVERE.intValue()) ? Severity.ERROR
//                        : (syntaxError.level().intValue() >= Level.WARNING.intValue()) ? Severity.WARNING
//                        : null;
//                if (sev != null) {
//                    ErrorDescription errorDescription = ErrorDescriptionFactory.createErrorDescription(
//                            sev,
//                            syntaxError.toString(),
//                            document,
//                            document.createPosition(start),
//                            document.createPosition(end)
//                    );
//                    errors.add(errorDescription);
//                }
//            }
//            HintsController.setErrors(document, "x-hl", errors);
//        } catch (BadLocationException ex1) {
//            Exceptions.printStackTrace(ex1);
//        } catch (org.netbeans.modules.parsing.spi.ParseException ex1) {
//            Exceptions.printStackTrace(ex1);
//        }
//    }
//
//    @Override
//    public int getPriority() {
//        return 100;
//    }
//
//    @Override
//    public Class<? extends Scheduler> getSchedulerClass() {
//        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
//    }
//
//    @Override
//    public void cancel() {
//    }
//}
