package net.hl.compiler.core;

import net.hl.compiler.index.HIndexer;

public class DefaultHLProjectContext implements HProjectContext {

    HadraContext languageContext;
    HIndexer indexer;
    String rootId;

    public DefaultHLProjectContext(HadraContext languageContext, HIndexer indexer, String rootId) {
        this.languageContext = languageContext;
        this.indexer = indexer;
        this.rootId = rootId;
    }

    public String rootId() {
        return rootId;
    }

    public HadraContext languageContext() {
        return languageContext;
    }

    public HIndexer indexer() {
        return indexer;
    }
}
