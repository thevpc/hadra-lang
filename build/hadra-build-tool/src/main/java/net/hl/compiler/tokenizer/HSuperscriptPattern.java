package net.hl.compiler.tokenizer;

import net.thevpc.jeep.core.tokens.JTokenDef;
import net.thevpc.jeep.core.tokens.SimpleTokenPattern;
import net.hl.compiler.core.HTokenId;
import net.hl.compiler.core.HTokenTTypes;

public class HSuperscriptPattern extends SimpleTokenPattern {

    private static final JTokenDef INFO = new JTokenDef(
            HTokenId.SUPERSCRIPT,
            "SUPERSCRIPT",
            HTokenTTypes.TT_SUPERSCRIPT,
            "TT_SUPERSCRIPT",
            "¹²³"
    );

    public HSuperscriptPattern() {
        super(INFO);
    }
    @Override
    public boolean accept(CharSequence prefix, char c) {
        return "¹²³⁴⁵⁶⁷⁸⁹⁰".indexOf(c)>=0;
    }
}
