package net.vpc.hadralang.compiler.parser.patterns;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.tokens.AbstractJTokenMatcher;
import net.vpc.common.jeep.core.tokens.JTokenDef;
import net.vpc.common.jeep.core.tokens.JTokenPatternOrder;
import net.vpc.common.jeep.impl.tokens.AbstractTokenPattern;
import net.vpc.common.jeep.impl.tokens.JTokenId;
import net.vpc.hadralang.compiler.core.HadraLanguage;

public class InterpolatedStringVarPattern extends AbstractTokenPattern {

    private JTokenDef I_VAR = new JTokenDef(
            JTokenId.IDENTIFIER,
            "IDENTIFIER",
            JTokenType.TT_IDENTIFIER,
            "TT_IDENTIFIER",
            "$\".${|}.\""
    );


    public InterpolatedStringVarPattern() {
        super(JTokenPatternOrder.ORDER_STRING,"InterpolatedStringVar");
    }

    @Override
    public void bindToState(JTokenizerState state) {
        super.bindToState(state);
        I_VAR=I_VAR.bindToState(state);
    }

    @Override
    public JTokenDef[] tokenDefinitions() {
        return new JTokenDef[]{I_VAR};
    }

    @Override
    public JTokenMatcher matcher() {
        return new InterpolatedStringPartPatternMatcher(InterpolatedStringVarPattern.InterpolatedStringPartPatternMatcher.INIT);
    }

    public class InterpolatedStringPartPatternMatcher extends AbstractJTokenMatcher {

        static final int INIT = 0;
        static final int CONTINUE = 2;
        int status;
        int startStatus;
        int pushStatus;
        private StringBuilder value = new StringBuilder();

        public InterpolatedStringPartPatternMatcher(int status) {
            super(InterpolatedStringVarPattern.this.order());
            this.startStatus=status;
            this.status=status;
        }

        @Override
        public JTokenPattern pattern() {
            return InterpolatedStringVarPattern.this;
        }

        @Override
        public JTokenMatcher reset() {
            status = INIT;
            value.delete(0, value.length());
            return super.reset();
        }

        @Override
        protected void fillToken(JToken token, JTokenizerReader reader) {
            fill(I_VAR,token);
            token.image = image();
            token.sval = value.toString();
            token.pushState =HadraLanguage.POP_STATE;
        }

        @Override
        public boolean matches(char c) {
            switch (status) {
                case INIT: {
                    if(Character.isJavaIdentifierStart(c)){
                        image.append(c);
                        value.append(c);
                        status = CONTINUE;
                        return true;
                    }
                    return false;
                }
                case CONTINUE: {
                    if(Character.isJavaIdentifierPart(c)){
                        image.append(c);
                        value.append(c);
                        return true;
                    }
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
            return true;
        }
    }
}

