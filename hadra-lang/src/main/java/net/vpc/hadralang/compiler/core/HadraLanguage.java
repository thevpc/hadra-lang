/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.DefaultJeep;
import net.vpc.common.jeep.core.tokens.*;
import net.vpc.common.jeep.impl.tokens.JTokenizerImpl;
import net.vpc.hadralang.compiler.parser.*;
import net.vpc.hadralang.compiler.parser.patterns.HLSuperscriptPattern;
import net.vpc.hadralang.compiler.parser.patterns.InterpolatedStringPartPattern;
import net.vpc.hadralang.compiler.parser.patterns.InterpolatedStringStartPattern;
import net.vpc.hadralang.compiler.parser.patterns.InterpolatedStringVarPattern;
import net.vpc.hadralang.compiler.stages.runtime.HLEvaluator;
import net.vpc.hadralang.stdlib.HDefaults;
import net.vpc.hadralang.stdlib.JExports;
import net.vpc.hadralang.stdlib.JSignature;
import net.vpc.hadralang.stdlib.ext.*;

/**
 *
 * @author vpc
 */
public final class HadraLanguage extends DefaultJeep {

    public static final String MIME_TYPE = "text/x-hl";

    static {
        JTokenType.JTOKEN_TYPES.addIntField("TT_SUPERSCRIPT", HTokenTTypes.TT_SUPERSCRIPT);
    }

    public HadraLanguage() {
        this(null);
    }

    public HadraLanguage(ClassLoader classLoader) {
        super(new HLJeepFactory(), classLoader);
        prepare();
    }

    protected void prepare() {
        /*
         * Lexical Parsing...
         */
        JTokenConfigBuilder config = new JTokenConfigBuilder(this.tokens().config());
        config.setIdPattern(new JavaIdPattern());
        //date/datetime/timestamp
        config.addPattern(new TemporalPattern("t\"", "\""));
        //pattern
        config.addPattern(new RegexPattern("p\"", "\""));

        config.addPatterns(new SeparatorsPattern("Separators1", HTokenIdOffsets.OFFSET_LEFT_PARENTHESIS, JTokenType.Enums.TT_GROUP_SEPARATOR,
                "(", ")", "[", "]", "{")
        );
        //this will be handled in a special way
        config.addPatterns(new SeparatorsPattern("Separators2",
                HTokenIdOffsets.OFFSET_RIGHT_CURLY_BRACKET,
                JTokenType.Enums.TT_GROUP_SEPARATOR,
                "}")
        );

        config.addPatterns(new SeparatorsPattern("Separators3", HTokenIdOffsets.OFFSET_COMMA,
                JTokenType.Enums.TT_SEPARATOR,
                ",", ";")
        );

        //superscript powers
        config.addPattern(new HLSuperscriptPattern());

        //numbers
        config.setNumberEvaluator(HNumberEvaluator.H_NUMBER);
        config.setNumberSuffixes(new char[]{'b', 'B', 'd', 'D', 'f', 'F', 'l', 'L', 's', 'S'});

        config.addKeywords("public", "private", "protected", "abstract", "static", "final", "package", "import");
        config.addKeywords("void", "var", "val", "class", "record", "interface", "extends", "return", "default");
        config.addKeywords("if", "else", "switch", "case", "break", "continue", "for", "do", "while");
        config.addKeywords("double", "float", "long", "int", "short", "byte", "float", "char", "boolean");
        config.addKeywords("null");
        config.addKeywords("def", "struct", "implicit", "const", "is"); //"set", "get"
        config.addKeywords("super", "this", "constructor", "operator", "true", "false"); //"set", "get"
//        this.tokens().config().addKeywords("decimal", "bigint", "bigdecimal", "date", "string");
        config.setCStyleComments();

        tokens().setConfig(config);

        this.operators().declareCStyleOperators();

//        this.tokens().config().addPattern(new OperatorsPattern(200, JTokenPattern.ORDER_OPERATOR-1,
//                new JTypedImage("...",THREE_DOTS_TOKEN_TYPE))
//        );
        //        this.operators().declareBinaryOperators(".","...", "....");
//        this.operators().declareBinaryOperators("?", "??", "???", "????");
//        this.operators().declareBinaryOperators(":", "::", ":::", "::::");
//        this.operators().declareBinaryOperators(".?");
//        this.operators().declareBinaryOperators("**", "***", "****");
//        this.operators().declareBinaryOperators("<<", "<<<", "<<<<");
//        this.operators().declareBinaryOperators(">>", ">>>", ">>>>");
//        this.operators().declareBinaryOperators("->", "=>", "<-", "<=");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_0, "->");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_1, "=", "+=", "-=", "*=", "|=", "&=", "~=", "^=", "%=");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_2, "=>", "=<", "<-", ":=");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_2, ":");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_3, "||");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_4, "&&");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_5, "|");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_6, "^", "^^");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_7, "&");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_8, "==", "!=", "===", "!==", "<>");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_9, "<", ">", "<=", ">=", "is");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_10, "<<", "<<<", ">>", ">>>");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_10, "..", "<..", "..<");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_10, ":+", ":-");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_11, "+", "-");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_12, "*", "/", "%");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_13, "**", "***");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_13, ":*", ":**", ":***");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_13, "++", "--", "~");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_13, ":++", ":--", ":~");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_15, ".", "?", ".?", "??");

        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_13, "∘", "±", "∓", "∔", "∴", "∵", "∷");
        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_13, "×", "÷");

        this.operators().declarePrefixUnaryOperators(JOperatorPrecedences.PRECEDENCE_16, "√", "∛", "∜", "∑", "∐", "∀", "∂", "∃", "∄", "∆", "∇");

        this.operators().declarePrefixUnaryOperators(JOperatorPrecedences.PRECEDENCE_16, "∈", "∉", "∊", "∋", "∌", "∍");
        this.operators().declarePrefixUnaryOperators(JOperatorPrecedences.PRECEDENCE_16, "∏", "∐", "∑");
        this.operators().declarePostfixUnaryOperators(JOperatorPrecedences.PRECEDENCE_16, "∎");

        this.operators().declareSpecialOperators("...");

        //reserved operators...
//        this.operators().declareBinaryOperators(JOperatorPrecedences.PRECEDENCE_20, "↓","→","←","↑");

        /*
         * this is the default state, handling HL tokens
         */
        JTokenConfigBuilder config_DEFAULT = new JTokenConfigBuilder(tokens().config());
        //interpolated string
        config_DEFAULT.addPattern(new InterpolatedStringStartPattern());

        /*
         * this is the state of expressions in a interpolated string
         */
        JTokenConfigBuilder config_INTERP_STRING = new JTokenConfigBuilder().unsetAll();
        config_INTERP_STRING.addPattern(new InterpolatedStringPartPattern());

        /*
         * this is the state of $var an interpolated strng
         */
        JTokenConfigBuilder config_INTERP_VAR = new JTokenConfigBuilder().unsetAll();
        config_INTERP_VAR.addPattern(new InterpolatedStringVarPattern());

        /*
         * this is the state of ${expressions} in an interpolated string
         */
        JTokenConfigBuilder config_INTERP_CODE = new JTokenConfigBuilder(tokens().config());
        //this will be handled in a special way
        JTokenPattern a = config_INTERP_CODE.getPattern("Separators2");
        config_INTERP_CODE.removePattern(a);
        config_INTERP_CODE.addPattern(new OperatorsPattern("Operators2b", HTokenIdOffsets.OFFSET_CLOSE_BRACE2, 
                JTokenType.Enums.TT_GROUP_SEPARATOR, 
                "\\}"
        ));
        //PopS(89/*distinct id"*/,JToken.TT_GROUP_SEPRATOR, "GROUP_SEPARATORS","\\}")
        config_INTERP_CODE.addPattern(new PushStatePattern(
                HTokenIdOffsets.OFFSET_STRING_INTERP_DOLLAR_END, "STRING_INTERP_DOLLAR_END",
                JTokenType.TT_STRING_INTERP, "TT_STRING_INTERP", "}",
                JTokenPattern.ORDER_OPERATOR, "}",
                Jeep.POP_STATE));

        tokens().setFactory((reader, config1, skipComments, skipSpaces, context) -> {
            JTokenizerImpl t = new JTokenizerImpl(reader, skipComments, skipSpaces);
            t.addState(HTokenState.Enums.STATE_DEFAULT, config_DEFAULT);
            t.addState(HTokenState.Enums.STATE_STRING_INTERP_TEXT, config_INTERP_STRING);
            t.addState(HTokenState.Enums.STATE_STRING_INTERP_CODE, config_INTERP_CODE);
            t.addState(HTokenState.Enums.STATE_STRING_INTERP_VAR, config_INTERP_VAR);
            t.pushState(HTokenState.Enums.STATE_DEFAULT);
            return t;
        });

        /*
         * Syntactic/Semantic Parsing...
         */
        this.parsers().setFactory(HLParser::new);

        /**
         * Java binding... to help overloading of methods with erasure
         * equivalent arguments : method(List&lt;Integer>);
         * method(List&lt;String>);
         */
        this.types().addResolver(new JTypesResolver() {
            @Override
            public String resolveMethodSignature(Method method) {
                JSignature s = method.getAnnotation(JSignature.class);
                if (s != null) {
                    return s.value();
                }
                return null;
            }

            @Override
            public String resolveConstructorSignature(Constructor method) {
                JSignature s = (JSignature) method.getAnnotation(JSignature.class);
                if (s != null) {
                    return s.value();
                }
                return null;
            }

            @Override
            public String[] resolveTypeExports(Class clazz) {
                JExports s = (JExports) clazz.getAnnotation(JExports.class);
                if (s != null) {
                    return s.value();
                }
                return new String[0];
            }
        });

        /*
         * Evaluation
         */
        this.evaluators().setFactory(context -> HLEvaluator.INSTANCE);
        this.resolvers().importType(HJavaDefaultOperators.class);
        this.resolvers().importType(ArrayExtensions.class);
        this.resolvers().importType(BigDecimalExtensions.class);
        this.resolvers().importType(BigIntegerExtensions.class);
        this.resolvers().importType(CharArrayExtensions.class);
        this.resolvers().importType(PatternExtensions.class);
        this.resolvers().importType(CharSequenceExtensions.class);
        this.resolvers().importType(IntArrayExtensions.class);
        this.resolvers().importType(ListExtensions.class);
        this.resolvers().importType(MapExtensions.class);
        this.resolvers().importType(RangeExtensions.class);
        this.resolvers().importType(StringBufferExtensions.class);
        this.resolvers().importType(StringBuilderExtensions.class);
        this.resolvers().importType(StringExtensions.class);
        this.resolvers().importType(HDefaults.class);
    }

}
