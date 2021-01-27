package net.hl.compiler.tokenizer;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.tokens.AbstractJTokenMatcher;
import net.thevpc.jeep.core.tokens.JTokenDef;
import net.thevpc.jeep.core.tokens.JTokenPatternOrder;
import net.thevpc.jeep.impl.tokens.AbstractTokenPattern;
import net.hl.compiler.core.HTokenId;
import net.hl.compiler.core.HTokenState;

public class HInterpolatedStringPartPattern extends AbstractTokenPattern {

    char quote = '\"';

    private JTokenDef I_CONTINUE = new JTokenDef(
            HTokenId.STRING_INTERP_TEXT,
            "STRING_INTERP_TEXT",
            JTokenType.TT_STRING_INTERP,
            "TT_STRING_INTERP",
            "$\".|.\""
    );
    private JTokenDef I_END = new JTokenDef(
            HTokenId.STRING_INTERP_END,
            "STRING_INTERP_END",
            JTokenType.TT_STRING_INTERP,
            "TT_STRING_INTERP",
            "$\"..|\""
    );
    private JTokenDef I_DOLLAR_START = new JTokenDef(
            HTokenId.STRING_INTERP_DOLLAR_START,
            "STRING_INTERP_DOLLAR_START",
            JTokenType.TT_STRING_INTERP,
            "TT_STRING_INTERP",
            "$\".$|.\""
    );
    
    @Override
    public void bindToState(JTokenizerState state) {
        super.bindToState(state);
        I_CONTINUE=I_CONTINUE.bindToState(state);
        I_END=I_END.bindToState(state);
        I_DOLLAR_START=I_DOLLAR_START.bindToState(state);
    }


    public HInterpolatedStringPartPattern() {
        super(JTokenPatternOrder.ORDER_STRING,"InterpolatedString");
    }


    @Override
    public JTokenDef[] tokenDefinitions() {
        return new JTokenDef[]{I_CONTINUE,I_END,I_DOLLAR_START};
    }

    @Override
    public JTokenMatcher matcher() {
        return new InterpolatedStringPartPatternMatcher();
    }

    public class InterpolatedStringPartPatternMatcher extends AbstractJTokenMatcher {

        static final int INIT = 1;
        static final int CONTINUE = 2;
        static final int ESCAPED = 3;
        static final int DOLLAR = 4;
        static final int END_STRING = 5;
        static final int END_QUOTE = 6;
        static final int END_DOLLAR = 7;
        int status;
        int pushState;
        private StringBuilder value = new StringBuilder();

        public InterpolatedStringPartPatternMatcher() {
            super(HInterpolatedStringPartPattern.this.order());
        }

        @Override
        public JTokenPattern pattern() {
            return HInterpolatedStringPartPattern.this;
        }

        @Override
        public JTokenMatcher reset() {
            status = INIT;
            pushState = 0;
            value.delete(0, value.length());
            return super.reset();
        }

        @Override
        protected void fillToken(JToken token, JTokenizerReader reader) {
            token.image = image();
            token.sval = value.toString();
            token.pushState = pushState;
            switch (status) {
                case END_DOLLAR:{
                    fill(I_DOLLAR_START,token);
                    break;
                }
                case END_STRING:{
                    fill(I_CONTINUE,token);
                    break;
                }
                case CONTINUE:{
                    fill(I_CONTINUE,token);
                    token.setError(1,"EOF");
                    break;
                }

                case END_QUOTE:{
                    fill(I_END,token);
                    token.pushState=Jeep.POP_STATE;
                    break;
                }
                case DOLLAR:{
                    fill(I_DOLLAR_START,token);
                    token.pushState=Jeep.POP_STATE;
                    token.setError(1,"EOF");
                    break;
                }
                case ESCAPED:{
                    fill(I_END,token);
                    token.pushState=Jeep.POP_STATE;
                    token.setError(1,"EOF");
                    break;
                }
                default:{
                    throw new JShouldNeverHappenException();
                }
            }
        }

        @Override
        public boolean matches(char c) {
            switch (status) {
                case INIT: {
                    if (c == quote) {
                        image.append(c);
                        status = END_QUOTE;
                        return true;
                    }else if (c == '\\') {
                        status = ESCAPED;
                        image.append(c);
                        return true;
                    } else if (c == '$') {
                        image.append(c);
                        status = DOLLAR;
                        return true;
                    } else {
                        image.append(c);
                        value.append(c);
                        status = CONTINUE;
                        return true;
                    }
                }
                case CONTINUE: {
                    if (c == '\\') {
                        status = ESCAPED;
                        image.append(c);
                    } else if (c == quote) {
                        status=END_STRING;
                        return false;
                    } else if (c == '$') {
                        status=END_STRING;
                        return false;
                    } else {
                        image.append(c);
                        value.append(c);
                    }
                    return true;
                }
                case ESCAPED: {
                    if (c == quote) {
                        image.append(c);
                        value.append(c);
                        status = CONTINUE;
                        return true;
                    } else {
                        boolean processed = false;
                        if (true/*cstyleEscape*/) {
                            switch (c) {
                                case 'n': {
                                    image.append(c);
                                    value.append('\n');
                                    status = CONTINUE;
                                    processed = true;
                                    break;
                                }
                                case 't': {
                                    image.append(c);
                                    value.append('\t');
                                    status = CONTINUE;
                                    processed = true;
                                    break;
                                }
                                case 'f': {
                                    image.append(c);
                                    value.append('\f');
                                    status = CONTINUE;
                                    processed = true;
                                    break;
                                }
                            }
                        }
                        if (!processed) {
                            image.append(c);
                            value.append(c);
                            status = CONTINUE;
                            processed = true;
                        }
                        return true;
                    }
                }
                case DOLLAR: {
                    if(c=='{'){
                        image.append(c);
                        status=END_DOLLAR;
                        pushState = HTokenState.STATE_STRING_INTERP_CODE;
                        return true;
                    }else if(Character.isJavaIdentifierStart(c)){
                        status=END_DOLLAR;
                        pushState = HTokenState.STATE_STRING_INTERP_VAR;
                        return false;
                    }else{
                        status=END_DOLLAR;
                        return false;
                    }
                }
                case END_QUOTE:
                case END_DOLLAR:
                case END_STRING:{
                    return false;
                }
            }
            throw new JParseException("Unsupported");
        }

        @Override
        public Object value() {
            return value.toString();
        }

        @Override
        public boolean valid() {
            return image.length()>0;
        }
    }
}
