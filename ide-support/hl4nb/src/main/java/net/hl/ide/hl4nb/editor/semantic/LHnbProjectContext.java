package net.hl.ide.hl4nb.editor.semantic;

import net.thevpc.jeep.JIndexStore;
import net.hl.compiler.index.HIndexerImpl;
import net.hl.ide.hl4nb.HadraLanguageSingleton;
import net.hl.compiler.core.HProjectContext;
import net.hl.compiler.core.HadraContext;
import net.hl.compiler.index.HIndexer;
import net.thevpc.nuts.NutsSession;

public class LHnbProjectContext implements HProjectContext {

    private final HadraContext context;
    private final HIndexer indexer;
    private final String rootId;

    public LHnbProjectContext(JIndexStore indexStore, String rootId) {
        this.context = HadraLanguageSingleton.HADRA_LANGUAGE;
        this.indexer = new HIndexerImpl(indexStore);
        this.rootId = rootId;
    }

    @Override
    public NutsSession getSession() {
        return context.getSession();
    }

    public String rootId() {
        return rootId;
    }

    @Override
    public HadraContext languageContext() {
        return context;
    }

    @Override
    public HIndexer indexer() {
        return indexer;
    }
}
