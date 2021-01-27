package net.hl.compiler.index;

import net.thevpc.jeep.JIndexDocument;

public class HIndexedCompilationUnit implements HIndexedElement{
    private String source;

    public HIndexedCompilationUnit(String source) {
        this.source = source;
    }

    @Override
    public JIndexDocument toDocument() {
        return null;
    }

    @Override
    public String getId() {
        return null;
    }
}
