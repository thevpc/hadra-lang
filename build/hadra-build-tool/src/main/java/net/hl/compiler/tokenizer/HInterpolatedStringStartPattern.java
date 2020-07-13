package net.hl.compiler.tokenizer;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.tokens.AbstractJTokenMatcher;
import net.vpc.common.jeep.core.tokens.JTokenDef;
import net.vpc.common.jeep.core.tokens.JTokenPatternOrder;
import net.vpc.common.jeep.impl.tokens.AbstractTokenPattern;
import net.hl.compiler.core.HTokenId;
import net.hl.compiler.core.HTokenState;

public class HInterpolatedStringStartPattern extends AbstractTokenPattern {
    char quote = '\"';
    private JTokenDef I_START = new JTokenDef(
            HTokenId.STRING_INTERP_START,
            "STRING_INTERP_START",
            JTokenType.TT_STRING_INTERP,
            "TT_STRING_INTERP",
            "$\""
    );
    public HInterpolatedStringStartPattern() {
        super(JTokenPatternOrder.ORDER_STRING,"InterpolatedString");
    }

    @Override
    public void bindToState(JTokenizerState state) {
        super.bindToState(state);
        I_START=I_START.bindToState(state);
    }
    @Override
    public JTokenDef[] tokenDefinitions() {
        return new JTokenDef[]{I_START};
    }

    @Override
    public JTokenMatcher matcher() {
        return new InterpolatedStringPartPatternMatcher();
    }

    public class InterpolatedStringPartPatternMatcher extends AbstractJTokenMatcher {

        static final int INIT = 0;
        static final int INIT1 = 1;
        static final int END = 5;
        int status = INIT;
        public InterpolatedStringPartPatternMatcher() {
            super(HInterpolatedStringStartPattern.this.order());
        }

        @Override
        public JTokenPattern pattern() {
            return HInterpolatedStringStartPattern.this;
        }

        @Override
        public JTokenMatcher reset() {
            status=INIT;
            return super.reset();
        }

        @Override
        protected void fillToken(JToken token, JTokenizerReader reader) {
            token.image = image();
            token.sval = "";
            token.pushState = HTokenState.STATE_STRING_INTERP_TEXT;
            fill(I_START,token);
        }

        @Override
        public boolean matches(char c) {
            switch (status) {
                case INIT: {
                    if (c == '$') {
                        image.append(c);
                        status = INIT1;
                        return true;
                    }
                    return false;
                }
                case INIT1: {
                    if (c == quote) {
                        image.append(c);
                        status = END;
                        return true;
                    }
                    return false;
                }
                case END:{
                    return false;
                }
            }
            throw new JParseException("Unsupported");
        }

        @Override
        public Object value() {
            return "";
        }

        @Override
        public boolean valid() {
            return (status == END);
        }
    }
}
