package net.hl.compiler.core;

import net.hl.compiler.index.HIndexer;

public interface HProjectContext {

    HadraContext languageContext();

    HIndexer indexer();

    String rootId();
}
