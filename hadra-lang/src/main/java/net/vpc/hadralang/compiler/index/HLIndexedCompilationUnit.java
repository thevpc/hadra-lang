package net.vpc.hadralang.compiler.index;

import net.vpc.common.jeep.JIndexDocument;

public class HLIndexedCompilationUnit implements HLIndexedElement{
    private String source;

    public HLIndexedCompilationUnit(String source) {
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
