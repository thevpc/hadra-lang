package net.hl.compiler.core;

import net.thevpc.jeep.JContext;
import net.hl.compiler.index.HIndexer;

public interface HProjectContext {
    JContext languageContext();
    HIndexer indexer();

    String rootId();
}
