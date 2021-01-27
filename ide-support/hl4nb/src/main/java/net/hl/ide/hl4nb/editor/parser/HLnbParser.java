/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.ide.hl4nb.editor.parser;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import net.thevpc.jeep.DefaultJCompilationUnit;
import net.thevpc.jeep.JCompilationUnit;
import net.thevpc.jeep.JContext;
import net.thevpc.common.textsource.JTextSourceFactory;
import net.hl.ide.hl4nb.HadraLanguageSingleton;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

/**
 *
 * @author vpc
 */
public class HLnbParser extends Parser {

    private static final Logger LOG = Logger.getLogger(HLnbParser.class.getName());
    private Snapshot snapshot;
    private JContext hcontext;
    private JCompilationUnit compilationUnit;

    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) {
        this.snapshot = snapshot;
        hcontext = HadraLanguageSingleton.HADRA_LANGUAGE.newContext();
        try {
            compilationUnit = new DefaultJCompilationUnit(
                    JTextSourceFactory.fromString(snapshot.getText().toString(),
                            snapshot.getSource().getFileObject().getPath()
                    ), hcontext
            );
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "parse "+snapshot.getSource().getFileObject().getPath()+" failed : " + ex.toString(), ex);
        }
    }

    @Override
    public ParserResult getResult(Task task) {
        return new HLnbParserResult(snapshot, hcontext, compilationUnit);
    }

    @Override
    public void cancel() {
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
    }

}
