/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.test;

import net.vpc.common.jeep.*;
import net.vpc.common.textsource.JTextSourceFactory;
import net.vpc.hadralang.compiler.HL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.vpc.common.jeep.core.tokens.JTokenDef;

/**
 * @author vpc
 */
public class TestTokenizer {
    @Test
    public void testRange() {
        HL c = new HL();
        double a = 1E-5;
        JTokenizer tokens = c.languageContext().tokens().of("1E-1..2", false, false);
        int count = 0;
        Assertions.assertEquals(0.1, tokens.next().sval);
        Assertions.assertEquals("..", tokens.next().image);
        Assertions.assertEquals("2", tokens.next().image);
        Assertions.assertEquals(JTokenType.TT_EOF, tokens.next().def.ttype);
//        for (JToken token : tokens) {
//            System.out.println(token);
//            count++;
//        }
//        Assertions.assertEquals(4,count);
    }

    @Test
    public void test1() {
        JTokens tokens = new HL().languageContext().tokens();
        JTokenizer jTokenizer = tokens.of("public static", true, true);
        int count = 0;
        for (JToken jToken : jTokenizer) {
            System.out.println(jToken);
            count++;
        }
        System.out.println(count);
        Assertions.assertEquals(4, count);
    }

    @Test
    public void testIs() {
        JTokens tokens = new HL().languageContext().tokens();
        JTokenizer jTokenizer = tokens.of("is", true, true);
        for (JTokenPattern pattern : jTokenizer.getPatterns()) {
            System.out.println(pattern.matcher().matches("is") + " :: " + pattern + "  " + pattern.getClass().getSimpleName());
        }
        for (JToken jToken : jTokenizer) {
            System.out.println(jToken);
            if (jToken.image.equals("is")) {
                Assertions.assertEquals(JTokenType.TT_KEYWORD, jToken.def.ttype);
            }
        }
        System.out.println("------------------------------------------");

        jTokenizer = tokens.of("isBlank", true, true);
        for (JTokenPattern pattern : jTokenizer.getPatterns()) {
            System.out.println(pattern.matcher().matches("is") + " :: " + pattern + "  " + pattern.getClass().getSimpleName());
        }
        for (JToken jToken : jTokenizer) {
            System.out.println(jToken);
        }

    }

    @Test
    public void testDollar() {
        JTokens tokens = new HL().languageContext().tokens();
        JTokenizer jTokenizer = tokens.of("($/2+1)..$", false, false);
        for (JTokenPattern pattern : jTokenizer.getPatterns()) {
            System.out.println(pattern.matcher().matches("$") + " :: " + pattern + "  " + pattern.getClass().getSimpleName());
        }
        int count = 0;
        for (JToken jToken : jTokenizer) {
            System.out.println(jToken);
            count++;
        }
        System.out.println(count);
    }

    @Test
    public void testTokenDefinitions() {
        JTokens tokens = new HL().languageContext().tokens();

        JTokenizer jTokenizer = tokens.of("public $\"Hello", false, false);
        JTokenDef[] tokenTemplates = jTokenizer.getTokenDefinitions();
        Arrays.sort(tokenTemplates, Comparator.<JTokenDef>comparingInt(x -> x.stateId)
                .thenComparingInt(x -> x.id)
        );
        for (JTokenDef token : tokenTemplates) {
            System.out.println(JTokenFormat.COLUMNS.format(token));
        }

    }

    @Test
    public void testInvalid() {
        JTokens tokens = new HL().languageContext().tokens();

        JTokenizer jTokenizer = tokens.of("public $\"Hello", false, false);
        System.out.println("========================================================");
        int count = 0;
        for (JToken token : jTokenizer) {
            System.out.println(JTokenFormat.COLUMNS.format(token));
            count++;
        }
        System.out.println(count);
    }

    @Test
    public void testInterpolation1() {
        HL c = new HL();
        JTokenizer tokens = c.languageContext().tokens().of(
                getClass().getResource("/net/vpc/hadralang/test/tokenizer/interpolation1.hl"), false, false);
        for (JToken token : tokens) {
            System.out.println(token.sval + " :: " + token);
            if (token.pushState > 0) {
                System.out.println("PUSH STATE " + token.pushState);
            } else if (token.pushState == Jeep.POP_STATE) {
                System.out.println("POP STATE");
            }
        }
    }

    @Test
    public void testInterpolation2() {
        HL c = new HL();
        JTokenizer tokens = c.languageContext().tokens().of(
                getClass().getResource("/net/vpc/hadralang/test/tokenizer/interpolation2.hl"), false, false);
        for (JToken token : tokens) {
            System.out.println(
                    token.def.ttype + ":" + token.def.id + ":" + token.sval + " :: " + token);
            if (token.pushState > 0) {
                System.out.println("PUSH STATE " + token.pushState);
            } else if (token.pushState == Jeep.POP_STATE) {
                System.out.println("POP STATE");
            }
        }
    }

    @Test
    public void testInterpolation3() {
        HL hl = new HL();
        String str = _stringResource("interpolation3.hl");
        System.out.println(str.substring(25, 26));
        JTokenizer tokens = _tokenizeResource("interpolation3.hl", hl, true);
        String[] a = tokenizerSig(tokens);
        for (String s : a) {
            System.out.println(s);
        }
        Assertions.assertArrayEquals((
                        "DEFAULT:TT_STRING_INTERP/STRING_INTERP_START;PUSH STRING_INTERP_TEXT\n" +
                                "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_TEXT\n" +
                                "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_DOLLAR_START;PUSH STRING_INTERP_VAR\n" +
                                "STRING_INTERP_VAR:TT_IDENTIFIER/IDENTIFIER;POP STRING_INTERP_TEXT\n" +
                                "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_TEXT\n" +
                                "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_DOLLAR_START;PUSH STRING_INTERP_CODE\n" +
                                "STRING_INTERP_CODE:TT_IDENTIFIER/IDENTIFIER\n" +
                                "STRING_INTERP_CODE:TT_OPERATOR/+\n" +
                                "STRING_INTERP_CODE:TT_NUMBER/NUMBER_INT\n" +
                                "STRING_INTERP_CODE:TT_STRING_INTERP/STRING_INTERP_DOLLAR_END;POP STRING_INTERP_TEXT\n" +
                                "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_END;POP DEFAULT\n" +
                                "DEFAULT:TT_EOF/EOF").split("\n"),
                a
        );
    }

    @Test
    public void testInterpolation3WithPeek() {
        HL hl = new HL();
        JTokenizer tokens = _tokenizeResource("interpolation3.hl", hl, true);
        List<String> a = new ArrayList<>();
        while (true) {
            tokens.peek();
            JToken token = tokens.next();
            System.out.println(">> "+JTokenFormat.COLUMNS.format(token));
            if (!token.isEOF()) {
                a.add(tokenSig(token, tokens));
            }else{
                break;
            }
        }
        for (String s : a) {
            System.out.println(s);
        }
        Assertions.assertArrayEquals((
                        "DEFAULT:TT_STRING_INTERP/STRING_INTERP_START;PUSH STRING_INTERP_TEXT\n" +
                                "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_TEXT\n" +
                                "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_DOLLAR_START;PUSH STRING_INTERP_VAR\n" +
                                "STRING_INTERP_VAR:TT_IDENTIFIER/IDENTIFIER;POP STRING_INTERP_TEXT\n" +
                                "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_TEXT\n" +
                                "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_DOLLAR_START;PUSH STRING_INTERP_CODE\n" +
                                "STRING_INTERP_CODE:TT_IDENTIFIER/IDENTIFIER\n" +
                                "STRING_INTERP_CODE:TT_OPERATOR/+\n" +
                                "STRING_INTERP_CODE:TT_NUMBER/NUMBER_INT\n" +
                                "STRING_INTERP_CODE:TT_STRING_INTERP/STRING_INTERP_DOLLAR_END;POP STRING_INTERP_TEXT\n" +
                                "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_END;POP DEFAULT"
                                ).split("\n"),
                a.toArray(new String[0])
        );
    }

    /**
     * incomplete interpolation
     * $"values are x=$
     */
    @Test
    public void testInterpolation4() {
        HL hl = new HL();
        JTokenizer tokens = _tokenizeResource("interpolation4.hl", hl, true);
        String[] a = tokenizerSig(tokens);
        for (String s : a) {
            System.out.println(s);
        }
//        Assertions.assertArrayEquals((
//                "DEFAULT:TT_STRING_INTERP/STRING_INTERP_START;PUSH STRING_INTERP_TEXT\n" +
//                        "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_TEXT\n" +
//                        "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_DOLLAR_START;PUSH STRING_INTERP_VAR\n" +
//                        "STRING_INTERP_VAR:TT_IDENTIFIER/IDENTIFIER;POP STRING_INTERP_TEXT\n" +
//                        "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_TEXT\n" +
//                        "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_DOLLAR_START;PUSH STRING_INTERP_CODE\n" +
//                        "STRING_INTERP_CODE:TT_IDENTIFIER/IDENTIFIER\n" +
//                        "STRING_INTERP_CODE:TT_OPERATOR/+\n" +
//                        "STRING_INTERP_CODE:TT_NUMBER/NUMBER_INT\n" +
//                        "STRING_INTERP_CODE:TT_STRING_INTERP/STRING_INTERP_DOLLAR_END;POP STRING_INTERP_TEXT\n" +
//                        "STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_END;POP DEFAULT\n" +
//                        "DEFAULT:TT_EOF/EOF").split("\n"),
//                a
//        );
    }
    
    @Test
    public void testTokenizer05() {
        HL hl = new HL();
        JTokenizer tokens = _tokenizeResource("testTokenizer05.hl", hl, true);
        String[] a = tokenizerSig(tokens);
        for (String s : a) {
            System.out.println(s);
        }
    }

    @Test
    public void testTokenizer06() {
        HL hl = new HL();
        JTokenizer tokens = _tokenizeResource("testTokenizer06.hl", hl, true);
        String[] a = tokenizerSig(tokens);
        for (String s : a) {
            System.out.println(s);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    // Helpers
    /////////////////////////////////////////////////////////////////////////////////////////
    private JTokenizer _tokenizeResource(String resourceName, HL hl, boolean dump) {
        if (dump) {
            System.out.println("==================================START DUMP");
            dump(_tokenizeResource(resourceName, hl));
            System.out.println("==================================END   DUMP");
        }
        return _tokenizeResource(resourceName, hl);
    }

    private String _stringResource(String resourceName) {
        return JTextSourceFactory.fromURL(getClass().getResource("/net/vpc/hadralang/test/tokenizer/" + resourceName)).text();
    }

    private JTokenizer _tokenizeResource(String resourceName, HL hl) {
        return hl.languageContext().tokens().of(
                getClass().getResource("/net/vpc/hadralang/test/tokenizer/" + resourceName), false, false);
    }

    private void dump(JTokenizer tokens) {
        for (JToken token : tokens) {
            System.out.println(JTokenFormat.COLUMNS.format(token));
            if (token.pushState > 0) {
                System.out.println("PUSH STATE [" + token.pushState + "]" + tokens.getState(token.pushState).getName());
            } else if (token.pushState == Jeep.POP_STATE) {
                System.out.println("POP STATE to [" + tokens.getState().getId() + "]" + tokens.getState().getName());
            }
        }
    }

    private String[] tokenizerSig(JTokenizer tokens) {
        List<String> s = new ArrayList<>();
        for (JToken token : tokens) {
            s.add(tokenSig(token, tokens));
        }
        return s.toArray(new String[0]);
    }

    private String tokenSig(JToken token, JTokenizer tokens) {
        String s = token.def.stateName + ":" + token.def.ttypeName + "/" + token.def.idName;
        if (token.pushState > 0) {
            s += ";PUSH " + tokens.getState(token.pushState).getName();
        } else if (token.pushState == Jeep.POP_STATE) {
            s += ";POP " + tokens.getState().getName();
        }
        return s;
    }
}
