package net.hl.editor.hl4nb.editor.semantic;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.JIndexStore;
import net.hl.compiler.core.HLProjectContext;
import net.hl.compiler.index.DefaultHLIndexer;
import net.hl.compiler.index.HLIndexer;
import net.hl.editor.hl4nb.HadraLanguageSingleton;

public class LHnbProjectContext implements HLProjectContext {
    private final JContext context;
    private final HLIndexer indexer;
    private final String rootId;

    public LHnbProjectContext(JIndexStore indexStore,String rootId) {
        this.context = HadraLanguageSingleton.HADRA_LANGUAGE;
        this.indexer = new DefaultHLIndexer(indexStore);
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
    public HLIndexer indexer() {
        return indexer;
    }
}
