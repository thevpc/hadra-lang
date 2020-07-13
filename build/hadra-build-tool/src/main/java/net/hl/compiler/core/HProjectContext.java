package net.hl.compiler.core;

import net.vpc.common.jeep.JContext;
import net.hl.compiler.index.HIndexer;

public interface HProjectContext {
    JContext languageContext();
    HIndexer indexer();

    String rootId();
}
