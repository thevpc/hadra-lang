/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.hl.test;

import net.thevpc.jeep.*;
import net.hl.compiler.HL;
import net.thevpc.jeep.source.JTextSourceFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.thevpc.jeep.core.tokens.JTokenDef;

/**
 * @author vpc
 */
public class TestTokenizer {

    @Test
    public void testTok1() {
        HL c = new HL();
        JTokenizer tokens = c.languageContext().tokens().of("println(\"Hello World\");");
        int count=0;
        for (JToken token : tokens) {
            System.out.println(token);
            count++;
        }
    }

    @Test
    public void testRange() {
        HL c = new HL();
        double a = 1E-5;
        JTokenizer tokens = c.languageContext().tokens().of("1E-1..2");
        int count = 0;
        Assertions.assertEquals("1E-1", tokens.next().image);
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
        JTokenizer jTokenizer = tokens.of("public static");
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
        JTokenizer jTokenizer = tokens.of("is");
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

        jTokenizer = tokens.of("isBlank");
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
        JTokenizer jTokenizer = tokens.of("($/2+1)..$");
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

        JTokenizer jTokenizer = tokens.of("public $\"Hello");
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

        JTokenizer jTokenizer = tokens.of("public $\"Hello");
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
                getClass().getResource("/net/hl/test/tokenizer/interpolation1.hl"));
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
                getClass().getResource("/net/hl/test/tokenizer/interpolation2.hl"));
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
        Assertions.assertArrayEquals(("STATE_DEFAULT:TT_STRING_INTERP/STRING_INTERP_START;PUSH STATE_STRING_INTERP_TEXT\n"
                + "STATE_STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_TEXT\n"
                + "STATE_STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_DOLLAR_START;PUSH STATE_STRING_INTERP_VAR\n"
                + "STATE_STRING_INTERP_VAR:TT_IDENTIFIER/IDENTIFIER;POP STATE_STRING_INTERP_TEXT\n"
                + "STATE_STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_TEXT\n"
                + "STATE_STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_DOLLAR_START;PUSH STATE_STRING_INTERP_CODE\n"
                + "STATE_STRING_INTERP_CODE:TT_IDENTIFIER/IDENTIFIER\n"
                + "STATE_STRING_INTERP_CODE:TT_OPERATOR/PLUS\n"
                + "STATE_STRING_INTERP_CODE:TT_NUMBER/NUMBER_INT\n"
                + "STATE_STRING_INTERP_CODE:TT_STRING_INTERP/STRING_INTERP_DOLLAR_END;POP STATE_STRING_INTERP_TEXT\n"
                + "STATE_STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_END;POP STATE_DEFAULT\n"
                + "STATE_DEFAULT:EOF/EOF").split("\n"),
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
            System.out.println(">> " + JTokenFormat.COLUMNS.format(token));
            if (!token.isEOF()) {
                a.add(tokenSig(token, tokens));
            } else {
                break;
            }
        }
        System.out.println("FOUND::");
        for (String s : a) {
            System.out.println(s);
        }
        System.out.println("EXPECTED::");
        final String expected = "STATE_DEFAULT:TT_STRING_INTERP/STRING_INTERP_START;PUSH STATE_STRING_INTERP_TEXT\n"
                + "STATE_STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_TEXT\n"
                + "STATE_STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_DOLLAR_START;PUSH STATE_STRING_INTERP_VAR\n"
                + "STATE_STRING_INTERP_VAR:TT_IDENTIFIER/IDENTIFIER;POP STATE_STRING_INTERP_TEXT\n"
                + "STATE_STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_TEXT\n"
                + "STATE_STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_DOLLAR_START;PUSH STATE_STRING_INTERP_CODE\n"
                + "STATE_STRING_INTERP_CODE:TT_IDENTIFIER/IDENTIFIER\n"
                + "STATE_STRING_INTERP_CODE:TT_OPERATOR/PLUS\n"
                + "STATE_STRING_INTERP_CODE:TT_NUMBER/NUMBER_INT\n"
                + "STATE_STRING_INTERP_CODE:TT_STRING_INTERP/STRING_INTERP_DOLLAR_END;POP STATE_STRING_INTERP_TEXT\n"
                + "STATE_STRING_INTERP_TEXT:TT_STRING_INTERP/STRING_INTERP_END;POP STATE_DEFAULT";
        System.out.println(expected);
        Assertions.assertArrayEquals((expected).split("\n"),
                a.toArray(new String[0])
        );
    }

    /**
     * incomplete interpolation $"values are x=$
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

    @Test
    public void testTokenizer07() {
        HL hl = new HL();
        JTokenizer tokens = _tokenizeResource("testTokenizer07.hl", hl, true);
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
        return JTextSourceFactory.fromURL(getClass().getResource("/net/hl/test/tokenizer/" + resourceName)).text();
    }

    private JTokenizer _tokenizeResource(String resourceName, HL hl) {
        return hl.languageContext().tokens().of(
                getClass().getResource("/net/hl/test/tokenizer/" + resourceName));
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
