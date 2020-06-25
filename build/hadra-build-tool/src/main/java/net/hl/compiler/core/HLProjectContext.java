package net.hl.compiler.core;

import net.vpc.common.jeep.JContext;
import net.hl.compiler.index.HLIndexer;

public interface HLProjectContext {
    JContext languageContext();
    HLIndexer indexer();

    String rootId();
}
