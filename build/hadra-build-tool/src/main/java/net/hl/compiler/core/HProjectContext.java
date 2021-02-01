package net.hl.compiler.core;

import net.hl.compiler.index.HIndexer;
import net.thevpc.nuts.NutsSession;

public interface HProjectContext {

    NutsSession getSession();

    HadraContext languageContext();

    HIndexer indexer();

    String rootId();
}
