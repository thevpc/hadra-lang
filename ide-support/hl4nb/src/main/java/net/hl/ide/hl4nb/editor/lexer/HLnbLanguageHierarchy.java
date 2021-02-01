/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.ide.hl4nb.editor.lexer;

import java.io.StringReader;
import java.util.*;
import java.util.logging.Logger;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.tokens.JTokenDef;
import net.thevpc.jeep.impl.tokens.DefaultJTokenizerReader;
import net.thevpc.jeep.impl.tokens.JTokenId;
import net.hl.compiler.core.HTokenId;
import net.hl.ide.hl4nb.HadraLanguageSingleton;
import org.netbeans.api.lexer.Language;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author vpc
 */
public class HLnbLanguageHierarchy extends LanguageHierarchy<HLnbToken> {

    private static final Logger LOG = Logger.getLogger(HLnbLanguageHierarchy.class.getName());
    private Map<Integer, HLnbToken> tokens = new HashMap<>();
    private Map<String, HLnbToken> extraTokens = new HashMap<>();
    
    private static final Language<HLnbToken> language = new HLnbLanguageHierarchy().language();

    public static final Language<HLnbToken> getLanguage() {
//        LOG.info("##### GOT LANGUAGE");
        return language;
    }

    public JTokenizer tokenizerTemplate() {
        return tokenizer(new DefaultJTokenizerReader(new StringReader("")));
    }
    
    public JTokenizer tokenizer(JTokenizerReader reader) {
        JTokens tokens = HadraLanguageSingleton.HADRA_LANGUAGE.tokens();
        return tokens.of(reader,new JTokenConfigBuilder(tokens.config()));
    }

    public HLnbLanguageHierarchy() {
        add(90000, "EOF", "eof");
        add(0, "DEFAULT", "default");
        Set<Integer> visitedIds=new HashSet<>();
        for (JTokenDef t : tokenizerTemplate().getTokenDefinitions()) {

            //We may encounter multiple tokens with the same ids
            // coming from distinct Token States
            //this of :
            //  $"this is ${a}"+a;
            // we shall have two instances of WORD token 'a'
            // one is from STRING_INTERPOLATED state
            // ond the other for NORMAL state
            if(visitedIds.contains(t.id)){
               continue;
            }
            visitedIds.add(t.id);

            switch(t.ttype){
                case JTokenType.TT_WHITESPACE:{
                    add(t.id, t.idName,"whitespaces");
                    break;
                }
                case JTokenType.TT_LINE_COMMENTS:
                case JTokenType.TT_BLOCK_COMMENTS:
                {
                    add(t.id, t.idName,"comment");
                    break;
                }
                case JTokenType.TT_NUMBER:
                {
                    add(t.id, t.idName,"number");
                    break;
                }
                case JTokenType.TT_KEYWORD:
                {
                    add(t.id, t.idName,"keyword");
                    break;
                }
                case JTokenType.TT_IDENTIFIER:
                {
                    add(t.id, t.idName,"identifier");
                    break;
                }
                case JTokenType.TT_OPERATOR:
                {
                    add(t.id, t.idName,"operator");
                    break;
                }
                case JTokenType.TT_SEPARATOR:
                {
                    add(t.id, t.idName,"separator");
                    break;
                }
                default:{
                    switch (t.id){
                        case HTokenId.LEFT_CURLY_BRACKET:
                        case HTokenId.STRING_INTERP_DOLLAR_START:
                        case HTokenId.STRING_INTERP_DOLLAR_END:{
                            add(t.id, t.idName,"separator");
                            break;
                        }
                        case HTokenId.STRING_INTERP_START:
                        case HTokenId.STRING_INTERP_END:
                        case HTokenId.STRING_INTERP_TEXT:{
                            add(t.id, t.idName,"string");
                            break;
                        }
                        case HTokenId.IDENTIFIER:{
                            add(t.id, t.idName,"identifier");
                            break;
                        }
                        case JTokenId.ANTI_QUOTES:
                        case HTokenId.SIMPLE_QUOTES:
                        case HTokenId.DOUBLE_QUOTES:{
                            add(t.id, t.idName,"string");
                            break;
                        }
                        case JTokenId.TEMPORAL:{
                            add(t.id, t.idName,"string");
                            break;
                        }
                        case JTokenId.REGEX:{
                            add(t.id, t.idName,"string");
                            break;
                        }
                        default:{
                            add(t.id, t.idName,"default");
                        }
                    }
                }

            }
        }
        extraTokens.put("string",   new HLnbToken(1000, "SpecialWords", "string"));
        extraTokens.put("Int",      new HLnbToken(1001, "SpecialWords", "Int"));
        extraTokens.put("complex",  new HLnbToken(1002, "SpecialWords", "complex"));
        extraTokens.put("Complex",  new HLnbToken(1003, "SpecialWords", "Complex"));
        extraTokens.put("matrix",   new HLnbToken(1004, "SpecialWords", "matrix"));
        extraTokens.put("Matrix",   new HLnbToken(1005, "SpecialWords", "Matrix"));
        extraTokens.put("vector",   new HLnbToken(1006, "SpecialWords", "vector"));
        extraTokens.put("Vector",   new HLnbToken(1007, "SpecialWords", "Vector"));
//        LOG.info("##### HLnbLanguageHierarchy : " + tokens.size());
//        for (HLnbToken createTokenId : createTokenIds()) {
//            LOG.info("#####      " + createTokenId);
//        }
    }

    private void add(int ordinal, String name, String cat) {
        tokens.put(ordinal, new HLnbToken(ordinal, cat, name));
    }

    @Override
    protected Collection<HLnbToken> createTokenIds() {
//        LOG.info("##### HLnbLanguageHierarchy call createTokenIds : " + tokens.size());
        return tokens.values();
    }

    @Override
    protected Lexer<HLnbToken> createLexer(LexerRestartInfo<HLnbToken> lri) {
        return new HLnbLexer(lri, this);
    }

    @Override
    protected String mimeType() {
        return "text/x-hl";
    }

    public HLnbToken getToken(JToken id) {
        HLnbToken t = extraTokens.get(id.image);
        if(t!=null){
            return t;
        }
        HLnbToken a = tokens.get(id.def.id);
        if (a == null) {
            return tokens.get(0);
        }
        return a;
    }
    
    public HLnbToken getToken(int id) {
        HLnbToken a = tokens.get(id);
        if (a == null) {
            return tokens.get(0);
        }
        return a;
    }

}
