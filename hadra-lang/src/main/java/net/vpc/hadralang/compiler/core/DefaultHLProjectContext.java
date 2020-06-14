package net.vpc.hadralang.compiler.core;

import net.vpc.common.jeep.JContext;
import net.vpc.hadralang.compiler.index.HLIndexer;

public class DefaultHLProjectContext implements HLProjectContext{
    JContext languageContext;
    HLIndexer indexer;
    String rootId;

    public DefaultHLProjectContext(JContext languageContext, HLIndexer indexer,String rootId) {
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

    public HLIndexer indexer() {
        return indexer;
    }
}
