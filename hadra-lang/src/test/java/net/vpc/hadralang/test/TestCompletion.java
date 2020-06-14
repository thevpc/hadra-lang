package net.vpc.hadralang.test;

import net.vpc.hadralang.compiler.core.DefaultHLProjectContext;
import net.vpc.hadralang.compiler.core.HLCompletion;
import net.vpc.hadralang.compiler.core.HadraLanguage;
import net.vpc.hadralang.compiler.index.DefaultHLIndexer;
import net.vpc.hadralang.compiler.index.HLIndexedProject;
import org.junit.jupiter.api.Test;

public class TestCompletion {
    @Test
    public void test1() {
        DefaultHLIndexer indexer = new DefaultHLIndexer();
        indexer.indexProject(new HLIndexedProject(
                "<example>",
                "NoName",
                "<source>",
                new String[0],
                new String[0]
        ));
        HLCompletion c = new HLCompletion(new DefaultHLProjectContext(
                new HadraLanguage(), indexer,"<example>"
        ));
        c.setCompilationUnit("int x=4; x++;println(x);", null);
        c.findProposals(0, 0);
    }

}
