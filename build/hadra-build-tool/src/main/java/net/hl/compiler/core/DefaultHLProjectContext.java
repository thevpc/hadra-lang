package net.hl.compiler.core;

import net.thevpc.jeep.JContext;
import net.hl.compiler.index.HIndexer;

public class DefaultHLProjectContext implements HProjectContext{
    JContext languageContext;
    HIndexer indexer;
    String rootId;

    public DefaultHLProjectContext(JContext languageContext, HIndexer indexer,String rootId) {
        this.languageContext = languageContext;
        this.indexer = indexer;
        this.rootId = rootId;
    }

    public String rootId() {
        return rootId;
    }

    public JContext languageContext() {
        return languageContext;
    }

    public HIndexer indexer() {
        return indexer;
    }
}
