/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.ide.hl4nb.editor.lexer;

import java.util.logging.Logger;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JTokenizer;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author vpc
 */
public class HLnbLexer implements Lexer<HLnbToken> {

    private static final Logger LOG = Logger.getLogger(HLnbLexer.class.getName());

    private final LexerRestartInfo<HLnbToken> info;
    private final JTokenizer jTokenizer;
    private final HLnbLanguageHierarchy hierarchy;

    public HLnbLexer(LexerRestartInfo<HLnbToken> info, HLnbLanguageHierarchy hierarchy) {
        this.info = info;
        this.hierarchy = hierarchy;
        jTokenizer = hierarchy.tokenizer(new HLnbLexerInputJTokenizerReader(info.input()));
//        LOG.info("new HLnbLexer");
    }

    @Override
    public Token<HLnbToken> nextToken() {
        JToken token = jTokenizer.next();
//        LOG.info("nextToken " + token);
        if (token.isEOF()) {
            return null;
        }
        return info.tokenFactory().createToken(hierarchy.getToken(token));
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
    }

}
