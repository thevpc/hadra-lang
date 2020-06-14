package net.vpc.hadralang.compiler.core;

import net.vpc.hadralang.compiler.HL;

public class HLCWithOptions extends HLCOptionsBase<HLCWithOptions> {
    private HL compiler;

    public HLCWithOptions(HL compiler) {
        this.compiler = compiler;
    }

    public HLProject compile() {
        HLCOptions o = new HLCOptions();
        o.setAll(this);
        return compiler.compile(o);
    }
}
