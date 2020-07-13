package net.hl.ide.hl4nb.editor.semantic;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.JIndexStore;
import net.hl.compiler.index.HIndexerImpl;
import net.hl.ide.hl4nb.HadraLanguageSingleton;
import net.hl.compiler.core.HProjectContext;
import net.hl.compiler.index.HIndexer;

public class LHnbProjectContext implements HProjectContext {
    private final JContext context;
    private final HIndexer indexer;
    private final String rootId;

    public LHnbProjectContext(JIndexStore indexStore,String rootId) {
        this.context = HadraLanguageSingleton.HADRA_LANGUAGE;
        this.indexer = new HIndexerImpl(indexStore);
        this.rootId = rootId;
    }

    public String rootId() {
        return rootId;
    }

    @Override
    public JContext languageContext() {
        return context;
    }

    @Override
    public HIndexer indexer() {
        return indexer;
    }
}
