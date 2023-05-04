package net.hl.compiler.core;

import net.hl.compiler.index.HIndexer;
import net.thevpc.nuts.NSession;

public class DefaultHLProjectContext implements HProjectContext {

    HadraContext languageContext;
    HIndexer indexer;
    String rootId;

    public DefaultHLProjectContext(HadraContext languageContext, HIndexer indexer, String rootId) {
        this.languageContext = languageContext;
        this.indexer = indexer;
        this.rootId = rootId;
    }

    public NSession getSession() {
        return languageContext.getSession();
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
