package net.vpc.hadralang.compiler.core;

import net.vpc.common.jeep.JContext;
import net.vpc.hadralang.compiler.index.HLIndexer;

public interface HLProjectContext {
    JContext languageContext();
    HLIndexer indexer();

    String rootId();
}
