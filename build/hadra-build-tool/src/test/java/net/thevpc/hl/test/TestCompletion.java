package net.thevpc.hl.test;

import net.hl.compiler.core.DefaultHLProjectContext;
import net.hl.compiler.core.HCompletion;
import net.hl.compiler.core.HadraLanguage;
import net.hl.compiler.index.HIndexerImpl;
import net.hl.compiler.index.HIndexedProject;
import org.junit.jupiter.api.Test;

public class TestCompletion {
    @Test
    public void test1() {
        HIndexerImpl indexer = new HIndexerImpl();
        indexer.indexProject(new HIndexedProject(
                "<example>",
                "NoName",
                "<source>",
                new String[0],
                new String[0]
        ));
        HCompletion c = new HCompletion(new DefaultHLProjectContext(
                new HadraLanguage(), indexer,"<example>"
        ));
        c.setCompilationUnit("int x=4; x++;println(x);", null);
        c.findProposals(0, 0);
    }

}
