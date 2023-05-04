package net.thevpc.hl.test;

import java.util.List;
import net.hl.compiler.core.DefaultHLProjectContext;
import net.hl.compiler.core.HCompletion;
import net.hl.compiler.core.HadraLanguage;
import net.hl.compiler.index.HIndexerImpl;
import net.hl.compiler.index.HIndexedProject;
import net.hl.compiler.utils.DepIdAndFile;
import net.thevpc.jeep.JCompletionProposal;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.NSession;
import org.junit.jupiter.api.Test;

public class TestCompletion {

    @Test
    public void test1() {
        HIndexerImpl indexer = new HIndexerImpl();
        indexer.indexProject(new HIndexedProject(
                "<example>",
                "NoName",
                "<source>",
                new DepIdAndFile[0]
        ));
        HCompletion c = new HCompletion(new DefaultHLProjectContext(
                HadraLanguage.getSingleton(), indexer, "<example>"
        ));
        c.setCompilationUnit("int x=4; x++;println(x);", null);
        final List<JCompletionProposal> p = c.findProposals(0, 0);
        for (JCompletionProposal prop : p) {
            System.out.println(prop);
        }
    }

}
