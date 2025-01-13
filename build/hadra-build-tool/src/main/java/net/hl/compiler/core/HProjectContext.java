package net.hl.compiler.core;

import net.hl.compiler.index.HIndexer;
import net.thevpc.nuts.NSession;

public interface HProjectContext {

    HadraContext languageContext();

    HIndexer indexer();

    String rootId();
}
