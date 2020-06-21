package net.vpc.hadralang.compiler.parser;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.*;
import net.vpc.common.jeep.core.nodes.JNodeTokens;
import net.vpc.common.jeep.core.tokens.JTokenDef;
import net.vpc.common.jeep.core.types.DefaultTypeName;
import net.vpc.common.jeep.core.types.JTypeNameBounded;
import net.vpc.common.jeep.impl.functions.JNameSignature;
import net.vpc.common.jeep.impl.tokens.JTokenId;
import net.vpc.common.jeep.util.JTokenUtils;
import net.vpc.common.jeep.util.JeepUtils;
import net.vpc.common.textsource.log.JMessageList;
import net.vpc.common.textsource.log.JSourceMessage;
import net.vpc.common.textsource.log.impl.DefaultJMessageList;
import net.vpc.hadralang.compiler.core.HTokenId;
import net.vpc.hadralang.compiler.core.HTokenIdOffsets;
import net.vpc.hadralang.compiler.core.elements.HNElementMetaPackageArtifact;
import net.vpc.hadralang.compiler.core.elements.HNElementMetaPackageGroup;
import net.vpc.hadralang.compiler.core.elements.HNElementMetaPackageVersion;
import net.vpc.hadralang.compiler.parser.ast.*;
import net.vpc.hadralang.compiler.utils.HNodeUtils;
import net.vpc.hadralang.compiler.utils.HTokenUtils;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HLParser extends DefaultJParser<HNode> {
    public static final HLDeclarationOptions DECL_ANY_1 = new HLDeclarationOptions()
            .setAcceptInValue(false)
            .setAcceptMultiVars(true)
            .setAcceptVarArg(false)
            .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.ERROR);
    public static final HLDeclarationOptions DECL_ANY_2 = new HLDeclarationOptions()
            .setAcceptInValue(false)
            .setAcceptVarArg(false)
            .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.ERROR);
    public static final HLDeclarationOptions DECL_ANY_3 = new HLDeclarationOptions()
            .setAcceptInValue(false)
            .setAcceptMultiVars(true)
            .setAcceptVarArg(false)
            .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.ERROR);
    public static final JTokenDef DEF_GT = new JTokenDef(HTokenId.GT, "GT", JTokenType.TT_OPERATOR, "TT_OPERATOR", ">");
    public static final JTokenDef DEF_GT2 = new JTokenDef(HTokenId.GT2, "GT2", JTokenType.TT_OPERATOR, "TT_OPERATOR", ">>");
    public static final JTokenDef DEF_GT3 = new JTokenDef(HTokenId.GT3, "GT3", JTokenType.TT_OPERATOR, "TT_OPERATOR", ">>>");
    private static final HLDeclarationOptions DECLARE_CONSTR_ARG = new HLDeclarationOptions()
            .setAcceptModifiers(false)
            .setAcceptVar(false).setAcceptDotName(false)
            .setAcceptFunction(false).setAcceptClass(false)
            .setAcceptEqValue(true).setAcceptInValue(false)
            .setAcceptVarArg(true).setAcceptMultiVars(false)
            .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.ERROR);
    private static final HLDeclarationOptions DECLARE_DEFAULT_CONSTR_ARG = new HLDeclarationOptions()
            .setAcceptModifiers(true)
            .setAcceptVar(true).setAcceptDotName(false)
            .setAcceptFunction(false).setAcceptClass(false)
            .setAcceptEqValue(true).setAcceptInValue(false)
            .setAcceptVarArg(true).setAcceptMultiVars(false)
            .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.TYPE);
    private static final HLDeclarationOptions DECLARE_FUNCTION_ARG = new HLDeclarationOptions()
            .setAcceptModifiers(true)
            .setAcceptVar(false).setAcceptDotName(false)
            .setAcceptFunction(false).setAcceptClass(false)
            .setAcceptEqValue(false).setAcceptInValue(false)
            .setAcceptVarArg(true).setAcceptMultiVars(false)
            .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.TYPE);
    private static final HLDeclarationOptions DECLARE_LAMBDA_ARG = new HLDeclarationOptions()
            .setAcceptModifiers(false)
            .setAcceptVar(false).setAcceptDotName(false)
            .setAcceptFunction(false).setAcceptClass(false)
            .setAcceptEqValue(false).setAcceptInValue(false)
            .setAcceptVarArg(true).setAcceptMultiVars(false)
            .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.NAME);
    private static final HLDeclarationOptions DECLARE_ASSIGN_EQ = new HLDeclarationOptions().setAcceptClass(false).setAcceptFunction(false)
            .setAcceptModifiers(false)
            .setAcceptVar(true).setAcceptDotName(false)
            .setAcceptFunction(false).setAcceptClass(false)
            .setAcceptEqValue(true).setAcceptInValue(false)
            .setAcceptVarArg(false).setAcceptMultiVars(false)
            .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.NAME);
    private static final HLDeclarationOptions DECLARE_ASSIGN_COLON = new HLDeclarationOptions().setAcceptClass(false).setAcceptFunction(false)
            .setAcceptModifiers(false)
            .setAcceptVar(true).setAcceptDotName(false)
            .setAcceptFunction(false).setAcceptClass(false)
            .setAcceptEqValue(false).setAcceptInValue(true)
            .setAcceptVarArg(false).setAcceptMultiVars(false)
            .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.NAME);
    private static final HLDeclarationOptions DECLARE_ASSIGN_EQ_OR_COLON = new HLDeclarationOptions().setAcceptClass(false).setAcceptFunction(false)
            .setAcceptModifiers(false)
            .setAcceptVar(true).setAcceptDotName(false)
            .setAcceptFunction(false).setAcceptClass(false)
            .setAcceptEqValue(true).setAcceptInValue(true)
            .setAcceptVarArg(false).setAcceptMultiVars(false)
            .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.NAME);
    //    private static final HashSet<String> unacceptableWordSuffixedForNumbers = new HashSet<>();
//
//    static {
//        unacceptableWordSuffixedForNumbers.add("(");
//        unacceptableWordSuffixedForNumbers.add("[");
//        unacceptableWordSuffixedForNumbers.add("{");
//    }
    private boolean metaParsingMode = false;
    private JExpressionOptions simpleExpressionOptions;
    private JExpressionOptions noBracesExpressionOptions;

    public HLParser(JTokenizer tokenizer, JCompilationUnit compilationUnit, JContext context) {
        super(tokenizer, compilationUnit, context, new HLFactory(compilationUnit, context));

        {
            JExpressionOptions o = new JExpressionOptions();
            o.binary = new JExpressionBinaryOptions();
            o.binary.excludedListOperator = true;
            o.binary.excludedImplicitOperator = true;
            o.binary.excludedBinaryOperators = null;

            o.unary = new JExpressionUnaryOptions();
            o.unary.excludedPrefixParenthesis = false;
            o.unary.excludedPrefixBrackets = true;
            o.unary.excludedPrefixBraces = true;
            o.unary.excludedPostfixParenthesis = false;
            o.unary.excludedPostfixBrackets = false;
            o.unary.excludedPostfixBraces = true;
            o.unary.excludedPrefixUnaryOperators = null;
            o.unary.excludedPostfixUnaryOperators = null;
            setDefaultExpressionOptions(o);
        }

        {
            JExpressionOptions o = new JExpressionOptions();
            o.binary = new JExpressionBinaryOptions();
            o.binary.excludedListOperator = true;
            o.binary.excludedImplicitOperator = true;
            o.binary.excludedBinaryOperators = new HashSet<>(Arrays.asList(":", "->"));
            o.unary = new JExpressionUnaryOptions();
            o.unary.excludedPrefixParenthesis = true;
            o.unary.excludedPrefixBrackets = true;
            o.unary.excludedPrefixBraces = true;
            o.unary.excludedPostfixParenthesis = true;
            o.unary.excludedPostfixBrackets = true;
            o.unary.excludedPostfixBraces = true;
            o.unary.excludedPrefixUnaryOperators = new HashSet<>(Arrays.asList("++", "--"));
            o.unary.excludedPostfixUnaryOperators = new HashSet<>(Arrays.asList("++", "--"));
            simpleExpressionOptions = o;
        }

        {
            JExpressionOptions o = getDefaultExpressionOptions().copy();
            o.unary.excludedPrefixBraces=true;
            o.unary.excludedPostfixBraces=true;
            o.unary.excludedTerminalBraces=true;
            noBracesExpressionOptions = o;
        }
    }

    protected static HNTypeToken createNullTypeToken(JToken nullToken) {
        return new HNTypeToken(nullToken, new DefaultTypeName("null"), null, null, null, nullToken, nullToken);
    }

    @Override
    public HLFactory getNodeFactory() {
        return (HLFactory) super.getNodeFactory();
    }

    @Override
    public HNode parse() {
        return parseDocument();
    }

    //    protected String readLongWord() {
//        JToken i = peek();
//        if (i.isWord()) {
//            StringBuilder sb = new StringBuilder();
//            sb.append(i.sval);
//            skip();
//            while (true) {
//                JToken[] peek = peek(2);
//                if (peek.length >= 2 && peek[0].isImage(".") && peek[1].isWord()) {
//                    sb.append(peek[0].image);
//                    sb.append(peek[1].sval);
//                    skip(2);
//                } else {
//                    break;
//                }
//            }
//            return sb.toString();
//        }
//        return null;
//    }
    protected HNode parseExpressionPars() {
        try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
            ArrayList<JSourceMessage> silencedMessages = new ArrayList<>();
            DefaultJMessageList err = errorList();
            JListWithSeparators<HNDeclareIdentifier> li = parseGroupedList("lambda expression", "lambda argument declaration",
                    () -> parseDeclareArgument(DECLARE_LAMBDA_ARG, err), "(", ",", ")", silencedMessages);
            if (li!=null && li.getEndToken() != null) {
                JToken endToken = li.getEndToken();
                JToken op = peek();
                if (op.id() == HTokenId.MINUS_GT) {
                    //this is for sure a lambda expression so if any error was tracked, reported it now...
                    log().addAll(err);
                    for (JSourceMessage silencedMessage : silencedMessages) {
                        log().add(silencedMessage);
                    }
                    endToken = op = next();
                    //this is a lambda expression
                    HNode e = parseExpression();
                    if (e == null) {
                        log().error("X127", "lambda expression", "expected body", peek());
                    } else {
                        endToken = e.endToken();
                    }
                    HNPars decl = new HNPars(
                            li.getItems().toArray(new HNode[0]),
                            li.getStartToken(),
                            li.getSeparatorTokensArray(),
                            li.getEndToken()
                    );
                    return getNodeFactory().createLambdaExpression(decl,
                            op, e,
                            new JNodeTokens()
                                    .setStart(li.getStartToken()).setEnd(endToken)
                                    .addSeparator(op)
                    );
                }
            }
            snapshot.rollback();
        }
        return parseParenthesis("parenthesis");
    }

    @Override
    protected HNode parseExpressionUnarySuffix(int opPrecedence, HNode middle, JExpressionOptions options, ParseExpressionUnaryContext ucontext) {
        JToken o = peek();
        if (o.id() == HTokenId.SUPERSCRIPT) {
            // check ofr superscript exponents
            // such as : abc³¹
            // which will be replaced by
            //           abc^31
            JToken next = next();
            StringBuilder sb = new StringBuilder();
            for (char c : next.image.toCharArray()) {
                switch (c) {
                    case '⁰': {
                        sb.append('0');
                        break;
                    }
                    case '¹': {
                        sb.append('1');
                        break;
                    }
                    case '²': {
                        sb.append('2');
                        break;
                    }
                    case '³': {
                        sb.append('3');
                        break;
                    }
                    case '⁴': {
                        sb.append('4');
                        break;
                    }
                    case '⁵': {
                        sb.append('5');
                        break;
                    }
                    case '⁶': {
                        sb.append('6');
                        break;
                    }
                    case '⁷': {
                        sb.append('7');
                        break;
                    }
                    case '⁸': {
                        sb.append('8');
                        break;
                    }
                    case '⁹': {
                        sb.append('9');
                        break;
                    }
                    default: {
                        log().error("X101", null, "superscript: invalid superscript characters " + next.image, next);
                    }
                }
            }
            int intv = 1;
            try {
                intv = Integer.parseInt(sb.toString());
            } catch (Exception any) {
                log().error("X101", null, "superscript: invalid superscript characters " + next.image, next);
            }
            return new HNOpBinaryCall(HTokenUtils.createToken("^"), middle, new HNLiteral(intv, next), middle.startToken(), middle.endToken());
        }
        if (middle instanceof HNLiteral) {
            //support for suffixed numbers
            //like 1.2 GHz
            //and replace them with a multiplication!

            HNLiteral lit = (HNLiteral) middle;
            if (lit.getValue() instanceof Number) {
                //check if pure number that is does not terminate with suffix
                char lastChar = lit.startToken().image.charAt(lit.startToken().image.length() - 1);
                if (lastChar >= '0' && lastChar <= '9') {
                    //in that case we will check for word suffixed of the number
                    JToken p = peek();
                    if (p.isIdentifier()) {
                        HNode suffix = parseExpressionUnaryTerminal(options);
                        return new HNOpBinaryCall(HTokenUtils.createToken("*"), middle, suffix, middle.startToken(), middle.endToken());
                    }
                }
            }
        }
        return super.parseExpressionUnarySuffix(opPrecedence, middle, options, ucontext);
    }

    @Override
    public HNode parsePrefixParsNodePars() {
        return super.parsePrefixParsNodePars();
    }

    public HNode parsePrefixParsNode(JExpressionOptions options) {
        JToken t = peek().copy();
        if (t.id() != HTokenId.LEFT_PARENTHESIS) {
            return null;
        }
        try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
            JToken startToken = next();
            HNode expr = parseTypeName();
            if (expr != null) {
                JToken endToken = next();
                if (endToken.id() == HTokenId.RIGHT_PARENTHESIS) {
                    JToken parsEnd = peek();
                    HNode toCast = parseExpression(options);
                    if (toCast != null) {
                        return getNodeFactory().createPrefixParenthesisNode(expr, toCast,
                                new JNodeTokens()
                                        .setStart(startToken)
                                        .setEnd(toCast.endToken())
                                        .addSeparator(startToken)
                                        .addSeparator(parsEnd)
                        );
                    }
                }
            }
            snapshot.rollback();
        }
        return null;
    }

    @Override
    public HNode parsePostfixParsNodePars() {
        return super.parsePostfixParsNodePars();
    }

    public HNode parsePostfixParsNode(HNode left, JToken startToken) {
        JListWithSeparators<HNode> li = parseParsList("postfix pars", "item", this::parseExpression);
        return new HNParsPostfix(
                left,
                li.getItems(),
                left.startToken(), li.getStartToken(), li.getSeparatorTokens(), li.getEndToken()
        );
    }

    @Override
    public HNode parsePostfixBracketsNode(HNode left, JToken startToken) {
        JListWithSeparators<HNode> li = parseBracketsList("postfix brackets", "item", this::parseExpression);
        return new HNBracketsPostfix(
                left,
                li.getItems(),
                left.startToken(), li.getStartToken(), li.getSeparatorTokens(), li.getEndToken()
        );
    }

    @Override
    public HNode parsePostfixBracesNode(HNode middle, JToken copy) {
        return super.parsePostfixBracesNode(middle, copy);
    }

    @Override
    protected HNode parseBrackets() {
        return super.parseBrackets();
    }

    public HNode parseBraces() {
        return parseBraces(HNBlock.BlocType.LOCAL_BLOC);
    }

    protected HNode parseAndBuildExpressionBinary(JToken op, HNode o1, int opPrecedence, JExpressionOptions options) {
        JToken next = peek();
        if (op.id() == HTokenId.DOT && next.isKeyword()) {
            skip();
            switch (next.sval) {
                case "this":
                case "class": {
                    HNTypeToken typeToken = null;
                    if (o1 instanceof HNTypeToken) {
                        //okkay
                    } else {
                        StringBuilder sb = new StringBuilder();
                        HNode n = o1;
                        while (n != null) {
                            if (n instanceof HNIdentifier) {
                                if (sb.length() > 0) {
                                    sb.insert(0, ".");
                                }
                                sb.insert(0, ((HNIdentifier) n).getName());
                                break;
                            } else if (n instanceof HNOpDot) {
                                if (sb.length() > 0) {
                                    sb.insert(0, ".");
                                }
                                sb.insert(0, ((HNOpDot) n).getRight());
                                n = ((HNOpDot) n).getLeft();
                            } else {
                                log().error("X105", null, "invalid '." + next.sval + "' operator on " + o1, n.startToken());
                                return o1;
                            }
                        }
                        typeToken = new HNTypeToken(
                                JTokenUtils.createTokenIdPointer(o1.startToken(), sb.toString()),
                                new DefaultTypeName(sb.toString()),
                                new HNTypeToken[0],
                                new HNTypeToken[0],
                                new HNTypeToken[0],
                                o1.startToken(),
                                o1.endToken()
                        );
                    }
                    if (next.sval.equals("class")) {
                        return new HNDotClass(typeToken, op.copy(), o1.startToken(), next);
                    } else {
                        return new HNDotThis(typeToken, op.copy(), o1.startToken(), next);
                    }
                }

            }
            log().error("X106", null, "unexpected '." + next.sval + "'", next);
            return o1;
        } else if (op.id() == HTokenId.KEYWORD_IS) {
            JToken n = peek();
            if (n.id() == HTokenId.KEYWORD_NULL) {
                JToken nullToken = next();
                return new HNIs(
                        createNullTypeToken(nullToken), o1,
                        null,
                        o1.startToken(),
                        n
                );
            }
            HNTypeToken tn = parseTypeName();
            if (tn == null) {
                log().error("X107", null, "expected type name", next);
                return new HNIs(
                        null, o1,
                        null,
                        o1.startToken(), n
                );
            }
            n = peek();
            if (n.isIdentifier()) {
                next();
                return new HNIs(
                        tn, o1,
                        new HNDeclareTokenIdentifier(n),
                        o1.startToken(),
                        n
                );
            } else {
                return new HNIs(
                        tn, o1,
                        null,
                        o1.startToken(),
                        n
                );
            }
        } else {
            return super.parseAndBuildExpressionBinary(op, o1, opPrecedence, options);
        }
    }

    protected HNode parseAndBuildListOpNodeElement(HNode o1, int opPrecedence, JToken token, JExpressionOptions options) {
        log().error("X108", null, "list operator not supported in this context", peek());
        token = token.copy();
        HNode o2 = parseExpression(opPrecedence, options);
        JToken s = o1 != null && o1.startToken() != null ? o1.startToken() : token;
        return new HNTuple(new HNode[]{o1, o2}, s, new JToken[0], o2.endToken());
//        if (o2 instanceof HXInvokableCall && (((HXInvokableCall) o2).getName()).equals(token.image)) {
//            HXInvokableCall o21 = (HXInvokableCall) o2;
//            List<HNode> aa = new ArrayList<>();
//            aa.add(o1);
//            aa.addAll(Arrays.asList(o21.getArgs()));
//            o1 = getNodeFactory().createListOperatorNode(token, aa.toArray(new HNode[0]), token, o2.endToken());
//            o1.setStartToken(s);
//        } else {
//            o1 = getNodeFactory().createListOperatorNode(token, new HNode[]{o1, o2}, token, o2.endToken());
//            o1.setStartToken(s);
//        }
//        return o1;
    }

//
//    public HNode parseExpression() {
//        return super.parseExpression();
//    }

    @Override
    protected HNode parseParenthesis(String name) {
        JToken startToken = peek();
        if (name == null) {
            name = "parenthesis";
        }
        JListWithSeparators<HNode> expression = parseGroupedList(name, "expression", () -> parseExpression(), "(", ",", ")", null);
        if (expression == null) {
            return null;
        }
        return getNodeFactory().createParsNode(
                expression.getItems(),
                new JNodeTokens()
                        .setStart(expression.getStartToken())
                        .setEnd(expression.getEndToken())
                        .setSeparators(expression.getSeparatorTokens())
        );
    }

    @Override
    public HNode parseExpressionUnaryTerminal(JExpressionOptions options) {
        JToken n = peek();
        switch (n.id()) {
            case HTokenId.KEYWORD_FOR: {
                return parseFor(true);
            }
            case HTokenId.KEYWORD_TRY: {
                return parseTryCatch(true);
            }
            case HTokenId.KEYWORD_IF: {
                return parseIf(true);
            }
            case HTokenId.KEYWORD_WHILE: {
                return parseWhile(true);
            }
            case HTokenId.KEYWORD_SWITCH: {
                return parseSwitch(true);
            }
            case HTokenId.KEYWORD_BOOLEAN:
            case HTokenId.KEYWORD_BYTE:
            case HTokenId.KEYWORD_SHORT:
            case HTokenId.KEYWORD_CHAR:
            case HTokenId.KEYWORD_INT:
            case HTokenId.KEYWORD_LONG:
            case HTokenId.KEYWORD_FLOAT:
            case HTokenId.KEYWORD_DOUBLE: {
                return parseTypeConstructorCall();
            }
            case HTokenId.KEYWORD_STATIC: {
                JToken[] s = nextIds(HTokenId.KEYWORD_STATIC, HTokenId.DOT);
                if (s != null) {
                    JToken p = peek();
                    switch (p.id()) {
                        case HTokenId.KEYWORD_CLASS:
                        case HTokenId.KEYWORD_PACKAGE: {
                            p = next();
                            return new HNStaticEval(s[0], s[1], p, s[0], p);
                        }
                        case HTokenId.IDENTIFIER: {
                            switch (p.sval) {
                                case "log":
                                case "method":
                                case "methodName":
                                case "functionName":
                                case "line":
                                case "filePath":
                                case "fileName": {
                                    return new HNStaticEval(s[0], s[1], p, s[0], p);
                                }
                                default: {
                                    log().warn("X000", null, "invalid static postfix : " + p.sval, p);
                                }
                            }
                        }
                        default: {
                            log().warn("X000", null, "invalid static postfix : " + p.sval, p);
                        }
                    }
                }
                break;
            }
            case HTokenId.IDENTIFIER: {
                //no pars Lambda expression
                //example : x->x*2
                JToken[] s = nextIds(HTokenId.IDENTIFIER, HTokenId.MINUS_GT);
                if (s != null) {
                    JNodeTokens jNodeTokens = new JNodeTokens();
                    HNIdentifier id = new HNIdentifier(s[0]);
                    JToken op = s[1];
                    jNodeTokens.addSeparator(op);
                    HNode e = parseExpression();
                    return getNodeFactory().createLambdaExpression(id,
                            op, e,
                            jNodeTokens
                                    .setStart(id.startToken())
                                    .setEnd(e == null ? op : e.endToken())
                                    .addSeparator(op)
                    );
                }
                break;
            }
            case HTokenId.KEYWORD_THIS: {
                JToken next = next();
                return new HNThis(null, next);
            }
            case HTokenId.KEYWORD_SUPER: {
                JToken next = next();
                return new HNSuper(null, next);
            }
            case HTokenId.TEMPORAL:{
                JToken token = next();
                if (token.isError()) {
                    log().warn("X000", null, "token never terminated", token);
                }
                Object parsed = null;
                try {
                    parsed = HUtils.parseTemporal(token.sval);
                } catch (Exception ex) {
                    log().warn("X013", null, ex.getMessage(), token);
                    parsed = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0);
                }
                return getNodeFactory().createLiteralNode(parsed, new JNodeTokens().setStart(token).setEnd(token));
            }
            case HTokenId.REGEX:{
                JToken token = next();
                if (token.isError()) {
                    log().warn("X000", null, "token never terminated", token);
                }
                return getNodeFactory().createLiteralNode(Pattern.compile(token.sval), new JNodeTokens().setStart(token).setEnd(token));
            }
            case HTokenId.STRING_INTERP_START:{
                return parseStringInterp();
            }
            case HTokenId.DOUBLE_QUOTES:{
                JToken token = next();
                if (token.isError()) {
                    log().warn("X000", null, "token never terminated", token);
                }
                return getNodeFactory().createLiteralNode(token.sval, new JNodeTokens().setStart(token).setEnd(token));
            }
            case HTokenId.SIMPLE_QUOTES:{
                JToken token = next();
                if (token.isError()) {
                    log().warn("X000", null, "token never terminated", token);
                }
                if (token.sval.length() == 1) {
                    return getNodeFactory().createLiteralNode(token.sval.charAt(0), new JNodeTokens().setStart(token).setEnd(token));
                } else {
//                            log().error("X110", "Invalid character token", token);
                    return getNodeFactory().createLiteralNode(token.sval, new JNodeTokens().setStart(token).setEnd(token));
                }
            }
            case JTokenId.ANTI_QUOTES:{//TODO FIX ME
                JToken token = next();
                if (token.isError()) {
                    log().warn("X000", null, "token never terminated", token);
                }
                return getNodeFactory().createIdentifierNode(token.sval, new JNodeTokens().setStart(token).setEnd(token));
            }
            case HTokenId.NUMBER_INT: {
                JToken token = next();
                return getNodeFactory().createLiteralNode(HNumberEvaluator.H_NUMBER.eval(token.id(), token.image, token.sval, "int"), new JNodeTokens().setStart(token).setEnd(token));
            }
            case HTokenId.NUMBER_FLOAT: {
                JToken token = next();
                return getNodeFactory().createLiteralNode(HNumberEvaluator.H_NUMBER.eval(token.id(), token.image, token.sval, "float"), new JNodeTokens().setStart(token).setEnd(token));
            }
            case HTokenId.NUMBER_INFINITY: {
                JToken token = next();
                return getNodeFactory().createLiteralNode(HNumberEvaluator.H_NUMBER.eval(token.id(), token.image, token.sval, "float"), new JNodeTokens().setStart(token).setEnd(token));
            }
        }
        return super.parseExpressionUnaryTerminal(options);
    }

    public HNode parseBraces(HNBlock.BlocType type) {
        JToken n = peek();
        JToken endToken = n;
        if (n.id() != HTokenId.LEFT_CURLY_BRACKET) {
            pushBack(n);
            return null;
        }
        endToken = next();
        JListWithSeparators<HNode> elements = parseStatements();
        if (elements.getEndToken() != null) {
            endToken = elements.getEndToken();
        }
        JToken p = peek();
        if (p.id() == HTokenId.RIGHT_CURLY_BRACKET || p.id() == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
            endToken = next();
        } else {
            log().error("X102", null, "block statement: expected '}'", peek());
        }
        return new HNBlock(type,
                elements.getItems().toArray(new HNode[0]),
                n, endToken);
    }

    public JListWithSeparators<HNode> parseStatements() {
        JToken startToken = null;
        JToken endToken = null;
        List<HNode> elements = new ArrayList<>();
        List<JToken> separators = new ArrayList<>();
        while (true) {
            JToken n1 = next();
            if (n1.isEOF()) {
                break;
            } else if (n1.image.equals(";")) {
                //read next. multiple consecutive ';' are allowed.
                if (startToken == null) {
                    startToken = n1;
                }
                endToken = n1;
                separators.add(n1);
            } else if (n1.id() == HTokenId.RIGHT_CURLY_BRACKET || n1.id() == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
                pushBack(n1);
                break;
            } else {
                pushBack(n1);
                HNode t = null;
                while (true) {
                    t = parseStatement(log());
                    if (t != null) {
                        endToken = t.endToken();
                        if (startToken == null) {
                            startToken = endToken;
                        }
                        break;
                    }
                    JToken toSkip = next();
                    log().error("X103", null, "block statement: invalid statement", toSkip);
                    if (toSkip.isEOF()) {
                        break;
                    }
                }
                if (t != null) {
                    elements.add(t);
//                    boolean requireSemiColumn = false;
//                    if (t instanceof HNIf
//                            || t instanceof HNWhile
//                            || t instanceof HNFor
//                            || t instanceof HNDeclareInvokable
//                            || t instanceof HNDeclareType
//                            || t instanceof HNDeclareMetaPackage
//                            || t instanceof HNSwitch
//                            || t instanceof HNBlock) {
//                        //this will not required ';'
//                    } else {
//                        requireSemiColumn = true;
//                    }
//                    if (requireSemiColumn) {
//                        JToken peek = peek();
//                        if (peek.image.equals(";")) {
//                            endToken = next();
//                            if (startToken == null) {
//                                startToken = endToken;
//                            }
//                            separators.add(n1);
//                        } else {
//                            log().error("X104", null, "block statement: expected ';' at the end of a statement", peek);
//                        }
//                    }
                }
            }
        }
//        for (HNode element : elements) {
//            if(element.endToken().endCharacterNumber>endToken.endCharacterNumber){
//                System.out.println("");
//            }
//        }
        return new DefaultJListWithSeparators<>(
                elements, startToken, separators, endToken
        );
    }

    private HNode parseStringInterp() {
        List<JToken> tokens = new ArrayList<>();
        List<HNode> expressions = new ArrayList<>();
        JToken n = peek();
        JToken startToken;
        JToken endToken;
        if (n.id() == HTokenId.STRING_INTERP_START) {
            startToken = next();
            tokens.add(startToken);
            endToken = startToken;
        } else {
            return null;
        }
        boolean end = false;
        while (!end) {
            n = peek();
            switch (n.id()) {
                case JTokenType.TT_EOF: {
                    end = true;
                    log().error("X000", null, "expected '\"'", n);
                    break;
                }
                case HTokenId.STRING_INTERP_TEXT: {
                    JToken next = next();
                    tokens.add(endToken = next);
                    if (n.isError()) {
                        log().error("X000", null, "expected '\"'", n);
                    }
                    break;
                }
                case HTokenId.STRING_INTERP_END: {
                    end = true;
                    JToken next = next();
                    tokens.add(endToken = next);
                    if (n.isError()) {
                        log().error("X000", null, "expected '\"'", n);
                    }
                    break;
                }
                case HTokenId.STRING_INTERP_DOLLAR_END: {
                    JToken next = next();
                    tokens.add(endToken = next);
                    if (n.isError()) {
                        log().error("X000", null, "expected '\"'", n);
                    }
                    break;
                }
                case HTokenId.STRING_INTERP_DOLLAR_START: {
                    JToken next = next();
                    tokens.add(endToken = next);
                    if (next.isImage("${")) {
                        HNode e = parseExpression();
                        if (e != null) {
                            endToken = e.endToken();
                            expressions.add(e);
                            if (next.id() == HTokenId.LEFT_CURLY_BRACKET) {
                                next = peek();
                                if (next.id() == HTokenId.RIGHT_CURLY_BRACKET || next.id() == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
                                    tokens.add(endToken = next());
                                } else {
                                    log().error("X000", null, "expected '}'", next);
                                }
                            }
                        } else {
                            end = true;
                            log().error("X000", null, "expected valid expression", next);
                            while (true) {
                                next = peek();
                                if (next.isEOF() || next.def.ttype == JTokenType.TT_STRING_INTERP) {
                                    break;
                                }
                                next();
                            }
                        }
                    } else {
                        next = next();
                        if (next.id() == JTokenId.IDENTIFIER) {
                            HNIdentifier id = new HNIdentifier(endToken = next);
                            expressions.add(id);
                        } else {
                            log().error("X000", null, "expected identifier", next);
                        }
                    }
                    break;
                }
                default: {
                    log().error("X000", null, "not expected : " + n, n);
                    end = true;
                }
            }
        }
        return new HNStringInterop(
                tokens.toArray(new JToken[0]),
                expressions.toArray(new HNode[0]),
                startToken,
                endToken
        );
    }

    public HNode parseStatement(JMessageList err) {
        JToken p = peek();
        switch (p.id()) {
            //what to choose??
            case HTokenId.KEYWORD_PACKAGE: {
                return parsePackageNode();
            }
            case HTokenId.KEYWORD_STATIC: {
                if (peekIds(HTokenId.KEYWORD_STATIC, HTokenId.DOT) != null) {
                    return parseExpressionAsStatement();
                }
                return parseDeclarationAsStatement(DECL_ANY_1, err);
            }
            case HTokenId.KEYWORD_DEF:
            case HTokenId.KEYWORD_PUBLIC:
            case HTokenId.KEYWORD_PRIVATE:
            case HTokenId.KEYWORD_PROTECTED:
            case HTokenId.KEYWORD_FINAL:
            case HTokenId.KEYWORD_VAR: {
                return parseDeclarationAsStatement(DECL_ANY_1, err);
            }
            case HTokenId.KEYWORD_CLASS: {
                HNode dec = null;
                if (metaParsingMode) {
                    err.error("X113", "class declaration", "class definitions are not allowed in package declaration", p);
                    //ignore it;
                }
                DefaultJMessageList err2 = errorList();
                dec = parseDeclaration(DECL_ANY_2, err2);
                if (dec instanceof HNDeclareIdentifier) {
                    ((HNDeclareIdentifier) dec).setSyntacticType(HNDeclareIdentifier.SyntacticType.FIELD);
                }
                if (!metaParsingMode) {
                    err.addAll(err2);
                    return dec;
                }
                break;
            }
            case HTokenId.KEYWORD_IMPORT: {
                return parseImportNode();
            }
            case HTokenId.KEYWORD_BREAK: {
                return parseBreak();
            }
            case HTokenId.KEYWORD_CONTINUE: {
                return parseContinue();
            }
            case HTokenId.KEYWORD_RETURN: {
                return parseReturn();
            }
            case HTokenId.KEYWORD_IF: {
                return parseIf(false);
            }
            case HTokenId.KEYWORD_SWITCH: {
                return parseSwitch(false);
            }
            case HTokenId.KEYWORD_WHILE: {
                return parseWhile(false);
            }
            case HTokenId.KEYWORD_FOR: {
                return parseFor(false);
            }
            case HTokenId.LEFT_CURLY_BRACKET: {
                return parseBraces(HNBlock.BlocType.LOCAL_BLOC);
            }
        }

        //boolean acceptModifiers, boolean acceptVar, boolean acceptFunction, boolean acceptClass,
        //                                    boolean requiredSemiColumnForVar, boolean acceptEqValue, boolean acceptInValue
        DefaultJMessageList err2 = errorList();
        HNode n = parseDeclarationAsStatement(DECL_ANY_3, err2);
        if (n != null) {
            err.addAll(err2);
            return n;
        }
        return parseExpressionAsStatement();
    }

    public HNode parseDeclarationAsStatement(HLDeclarationOptions options, JMessageList err) {
        HNode n = parseDeclaration(options, err);
        if (n != null) {
            if (n instanceof HNDeclareIdentifier) {
                JToken p = peek();
                if (p.id() == HTokenId.SEMICOLON) {
                    skip();
                } else {
                    err.error("X000", "declaration", "expected ';'", p);
                }
            }
            return n;
        }
        return n;
    }


    public boolean requireSemiColumn(String logName, boolean acceptMultiple) {
        if (!acceptMultiple) {
            JToken t = peek();
            if (t.id() == HTokenId.SEMICOLON) {
                next();
                return true;
            } else {
                log().error("X131", logName, "expected ';'", t);
            }
            return false;
        }
        boolean ok = false;
        JToken found = null;
        while (true) {
            JToken t = peek();
            if (t.id() == HTokenId.SEMICOLON) {
                next();
                ok = true;
            } else {
                found = t;
                break;
            }
        }
        if (!ok) {
            log().error("X131", logName, "expected ';'", found);
        }
        return ok;
    }

    public HNode parseExpressionAsStatement() {
        HNode expr = parseExpression();
        if (expr != null) {
            if (isRequireSemiColumn(expr)) {
                requireSemiColumn(null, false);
            }
        }
        return expr;
    }

    private boolean isRequireSemiColumn(HNode t) {
        switch (t.id()) {
            case H_IF:
            case H_WHILE:
            case H_FOR:
            case H_DECLARE_INVOKABLE:
            case H_DECLARE_TYPE:
            case H_DECLARE_META_PACKAGE:
            case H_SWITCH:
            case H_BLOCK: {
                return false;
            }
        }
        return true;
    }

    protected HNode parseTypeConstructorCall() {
        try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
            JToken startToken = peek().copy();
            JToken endToken = startToken;
            JTypeNameAndInit tt = parseTypeNameAndInit(true, false);
            if (tt == null /*|| (!tt.type.isVarArg() && !tt.type.isArray())*/) {
                snapshot.rollback();
                return null;
            }
            endToken = tt.typeToken.endToken();
            JToken t2 = peek();
            HNode setter = null;
            if (t2.isImage("(")) {
                endToken = next();
                JToken t3 = peek();
                if (t3.isImage(")")) {
                    endToken = next();
                    if (tt.inits.length == 0) {
                        // How can we create an array without specifying a valid constructor?
                        log().error("X193", "constructor", "unresolved empty constructor", t2);
                    }
                    return new HNArrayNew(tt.typeToken, tt.inits, null, startToken, endToken);
                } else {
                    setter = parseExpression();
                }
                if (setter != null) {
                    endToken = setter.endToken();
                }
                t2 = peek();
                if (t2.isImage(")")) {
                    endToken = next();
                } else {
                    log().error("X114", "constructor", "expected ')'", t2);

                }
                if (tt.type.isArray() || tt.type.isVarArg()) {
//                    if (setter != null && tt.inits.length == 0) {
//                        log().error("S052", null, "initialized array is missing initializer", startToken);
//                    }
                    return new HNArrayNew(tt.typeToken, tt.inits, setter, startToken, endToken);
                } else {
                    if (tt.inits.length != 0) {
                        throw new JFixMeLaterException();
                    } else {
//                        if (setter != null && tt.inits.length == 0) {
//                            log().error("S052", null, "initialized array is missing initializer", startToken);
//                        }
                        return new HNObjectNew(tt.typeToken, new HNode[]{setter}, startToken, endToken);
                    }
                }
            } else if(tt.inits.length==0 && peekIds(HTokenId.DOT,HTokenId.KEYWORD_CLASS)!=null){
                JToken dotToken = next();
                JToken classToken = next();
                return new HNDotClass(tt.typeToken, dotToken,tt.typeToken.startToken(), classToken);
            } else if(tt.inits.length==0 && peekIds(HTokenId.DOT,HTokenId.KEYWORD_THIS)!=null){
                JToken dotToken = next();
                JToken classToken = next();
                return new HNDotThis(tt.typeToken, dotToken,tt.typeToken.startToken(), classToken);
            } else {
                snapshot.rollback();
                return null;
            }
        }
    }

    private HNTypeToken parseTypeName() {
        JTypeNameAndInit t = parseTypeNameAndInit(false, false);
        return t == null ? null : t.typeToken;
    }

    private JListWithSeparators<HNTypeToken> parseJTypeNameOrVariables() {
        List<HNTypeToken> args = new ArrayList<>();
        JToken n = peek();
        List<JToken> separators = new ArrayList<>();
        if (n.id() == HTokenId.LT) {
            JToken startToken = n;
            JToken endToken = n;
            separators.add(n);
            skip();
            while (true) {
                HNTypeToken a = null;
                JToken varName = null;
                {
                    n = peek();
                    if (n.isImage("?")) {
                        next();
                        varName = n;
                    } else {
                        a = parseTypeName();
                        if (a == null) {
                            return null;
                        }
                        if (a.getTypenameOrVar() instanceof JTypeName && !((JTypeName) a.getTypenameOrVar()).isArray()
                                && ((JTypeName) a.getTypenameOrVar()).varsCount() == 0
                                && ((peek().id() == HTokenId.KEYWORD_SUPER || peek().id() == HTokenId.KEYWORD_EXTENDS))) {
                            varName = a.getNameToken();
                        }
                    }
                }
                if (varName != null) {
                    //expect super or extends
                    n = peek();
                    if (n.id() == HTokenId.KEYWORD_EXTENDS) {
                        separators.add(n);
                        List<HNTypeToken> lowers = new ArrayList<>();
                        List<JTypeName> _lowers = new ArrayList<>();
                        endToken = varName;
                        while (true) {
                            HNTypeToken e = parseTypeName();
                            if (e == null) {
                                return null;
                            }
                            endToken = e.endToken();
                            lowers.add(e);
                            _lowers.add((JTypeName) e.getTypenameOrVar());
                            n = peek();
                            if (n.isImage("&")) {
                                separators.add(next());
                            } else {
                                break;
                            }
                        }
                        a = new HNTypeToken(
                                varName,
                                new JTypeNameBounded(varName.image, _lowers.toArray(new JTypeName[0]), new JTypeName[0]),
                                new HNTypeToken[0],
                                (HNTypeToken[]) lowers.toArray(new HNTypeToken[0]),
                                new HNTypeToken[0],
                                varName, endToken
                        );
                    } else if (n.id() == HTokenId.KEYWORD_SUPER) {
                        separators.add(n);
                        List<HNTypeToken> uppers = new ArrayList<>();
                        List<JTypeName> _uppers = new ArrayList<>();
                        endToken = varName;
                        while (true) {
                            HNTypeToken e = parseTypeName();
                            if (e == null) {
                                return null;
                            }
                            endToken = e.endToken();
                            uppers.add(e);
                            _uppers.add((JTypeName) e.getTypenameOrVar());
                            n = peek();
                            if (n.isImage("&")) {
                                separators.add(next());
                            } else {
                                break;
                            }
                        }
                        a = new HNTypeToken(
                                varName,
                                new JTypeNameBounded(varName.image, new JTypeName[0], _uppers.toArray(new JTypeName[0])),
                                new HNTypeToken[0],
                                new HNTypeToken[0],
                                (HNTypeToken[]) uppers.toArray(new HNTypeToken[0]),
                                varName, endToken
                        );
                    } else {
                        a = new HNTypeToken(
                                varName,
                                new JTypeNameBounded(varName.image, new JTypeName[0], new JTypeName[0]),
                                new HNTypeToken[0],
                                new HNTypeToken[0],
                                new HNTypeToken[0],
                                varName, n
                        );
                    }
                }
                if (a == null) {
                    return null;
                }
                args.add(a);
                n = peek();
                if (n.id() == HTokenId.COMMA) {
                    separators.add(next());
                } else {
                    break;
                }
            }
            n = peek();
            if (n.id() == HTokenId.GT) {
                endToken = next();
                separators.add(endToken);
            } else if (n.id() == HTokenId.GT2) {
                //explode operator
                n = next();
                endToken = n.copy();
                endToken.def = DEF_GT;
                endToken.image = ">";
                endToken.endColumnNumber--;
                endToken.endCharacterNumber--;
                separators.add(endToken);

                n.def = DEF_GT;
                n.image = ">";
                n.sval = ">";
                n.startColumnNumber++;
                n.startCharacterNumber++;
                pushBack(n);
            } else if (n.isOperator(">>>")) {
                //explode operator
                n = next();
                endToken = n.copy();
                endToken.def = DEF_GT;
                endToken.image = ">";
                endToken.endColumnNumber -= 2;
                endToken.endCharacterNumber -= 2;
                separators.add(endToken);

                n.def = DEF_GT2;
                n.image = ">>";
                n.sval = ">>";
                n.startColumnNumber++;
                n.startCharacterNumber++;
                pushBack(n);
            } else if (n.isOperator(">>>>")) {
                //explode operator
                n = next();
                endToken = n.copy();
                endToken.def = DEF_GT;
                endToken.image = ">";
                endToken.endColumnNumber -= 3;
                endToken.endCharacterNumber -= 3;
                separators.add(endToken);

                n.def = DEF_GT3;
                n.image = ">>";
                n.sval = ">>";
                n.startColumnNumber++;
                n.startCharacterNumber++;
                pushBack(n);
            } else {
                return null;
            }
            return new DefaultJListWithSeparators<>(
                    args,
                    startToken,
                    separators,
                    endToken
            );
        } else {
            return null;
        }
    }

    //    private HLTypeWithInitializer parseTypeWithInitializer(boolean acceptInit, boolean visitedVarVal) {
//        JToken v = next();
//        if (v == null || v.isEOF()) {
//            return null;
//        }
//        HLTypeWithInitializer type = new HLTypeWithInitializer();
//        type.visitedVarVal = visitedVarVal;
//        switch (v.ttype) {
//            case JToken.TT_KEYWORD: {
//                switch (v.sval) {
//                    case "boolean":
//                    case "char":
//                    case "byte":
//                    case "int":
//                    case "long":
//                    case "float":
//                    case "double": {
//                        if (visitedVarVal) {
//                            log().error("X055", "type construct: after var/val keywords, you need not to add type declaration", peek());
//                        }
//                        type.type = context().types().parseName(v.sval);
//                        break;
//                    }
//                    case "var":
//                    case "val": {
//                        if (visitedVarVal) {
//                            log().error("X056", "type construct: multiple var/val keywords", peek());
//                        }
//                        type.visitedVarVal = true;
//                        break;
//                    }
//                    case "void": {
//                        if (visitedVarVal) {
//                            log().error("X056", "type construct: var/val and void combination mismatch", peek());
//                        }
//                        type.visitedVoid = true;
//                        type.type = context().types().parseName("void");
//                        break;
//                    }
//                    default: {
//                        pushBack(v);
//                        return null;
//                    }
//                }
//                break;
//            }
//            case JToken.TT_WORD: {
//                List<JToken> saved = new ArrayList<>();
//                StringBuilder c = new StringBuilder(v.sval);
//                saved.add(v);
//                while (true) {
//                    JToken u = next();
//                    if (u.isOperator(".")) {
//                        saved.add(u);
//                        c.append(".");
//                        u = next();
//                        if (u.isWord()) {
//                            saved.add(u);
//                            c.append(u.sval);
//                        } else /*if (u.ttype == '(')*/ {
//                            //this is a function
//                            //revert all and return;
//                            saved.add(u);
//                            pushBackAll(saved);
//                            return null;
//                        }
//                    } else if (
//                            u.isOperator("<")
//                                    || u.isType('[')
//                                    || u.isType(':')
//                                    || u.isWord()
//                    ) {
//                        //this is the end of the type definition!
//                        pushBack(u);
//                        break;
//                    } else {
//                        saved.add(u);
//                        pushBackAll(saved);
//                        return null;
//                    }
//                }
//                type.type = context().types().parseName(c.toString());
//                break;
//            }
//            default: {
//                pushBack(v);
//                return null;
//            }
//        }
//        List<HNode> arrImplicitInitialization = new ArrayList<>();
//        if (acceptInit) {
//            if (!type.visitedVarVal && !type.visitedVoid) {
//                JToken c = next();
//                if (c.ttype == JToken.TT_OPERATOR && c.sval.equals("<")) {
//                    type.visitedParams = true;
//                    List<JTypeNameOrVariable> z = new ArrayList<>();
//                    JTypeNameOrVariable t2 = parseTypeName();
//                    if (t2 == null) {
//                        log().error("X057", "type construct: generic type argument missing", peek());
//                        t2 = new DefaultTypeName("unknown");
//                    }
//                    z.add(t2);
//                    while (true) {
//                        JToken t = peek();
//                        if ((t.ttype == JToken.TT_OPERATOR && t.image.equals(","))
//                                || (t.ttype == ',')
//                        ) {
//                            //read next
//                        } else if ((t.ttype == JToken.TT_OPERATOR && t.image.equals(">"))) {
//                            break;
//                        } else {
//                            log().error("X058", "type construct: expected ',' or '>'", peek());
//                            skip();
//                        }
//                    }
//                    type.type = type.type.addArguments(z.toArray(new JTypeNameOrVariable[0]));
//                } else {
//                    pushBack(c);
//                }
//                boolean lastOk = true;
//                while (true) {
//                    c = next();
//                    if (c.ttype == '[') {
//                        type.visitedBrackets = true;
//                        c = peek();
//                        if (c.ttype == ']') {
//                            type.type = type.type.toArray();
//                            next();
//                            lastOk = false;
//                        } else {
//                            if (!lastOk) {
//                                log().error("X058", "type construct: expected '[]'", peek());
//                            }
//                            //String[12*6] a;
//                            HNode jNode = parseExpression();
//                            if (jNode == null) {
//                                jNode = parseExpression();
//                                log().error("X058", "type construct: expected initializer expression", peek());
//                            } else {
//                                arrImplicitInitialization.add(jNode);
//                            }
//                            c = peek();
//                            if (c.ttype == ']') {
//                                next();
//                                type.type = type.type.toArray();
//                            } else {
//                                pushBack(c);
//                            }
//                        }
//                    } else {
//                        pushBack(c);
//                        break;
//                    }
//                }
//                if (!type.visitedBrackets) {
//                    JToken[] peek = peek(2);
//                    if (peek.length >= 2 && peek[0].isOperator("...")) {
//                        next();
//                        type.varArgs = true;
//                    }
//                }
//                if (type.varArgs) {
//                    type.type = type.type.toArray();
//                }
//            }
//        }
//        if (type.type == null && !type.visitedBrackets && !type.visitedParams) {
//            //this is for sure a type
//            throw new IllegalArgumentException("What to do....");
//
//        }
//        type.init = arrImplicitInitialization.toArray(new HNode[0]);
//        return type;
//    }
//    private String parseNameWithNamespace() {
//        JToken[] r = parseNameWithNamespaceTokens();
//        if(r==null){
//            return null;
//        }
//        return Arrays.stream(r).map(x->x.image).collect(Collectors.joining());
//    }
    private JTypeNameAndInit parseTypeNameAndInit(boolean acceptInit, boolean acceptVarArg) {
        try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
            JToken n = peek();
            JToken endToken = n.copy();
            JToken wToken = null;
            boolean primitive = false;
            if (n.def.ttype == JTokenType.TT_KEYWORD) {
                switch (n.image) {
                    case "boolean":
                    case "char":
                    case "byte":
                    case "int":
                    case "long":
                    case "float":
                    case "double":
                    case "void": {
                        endToken = next();
                        primitive = true;
                        wToken = n;
                        break;
                    }
                }
            } else if (n.def.ttype == JTokenType.TT_IDENTIFIER) {
                wToken = parseNameWithPackageSingleToken();
                endToken = wToken;
            }
            if (wToken == null) {
                snapshot.rollback();
                return null;
            }
            JListWithSeparators<HNTypeToken> jTypeNameOrVariables = null;
            if (!primitive) {
                n = peek();
                if (n.id() == HTokenId.LT) {
                    jTypeNameOrVariables = parseJTypeNameOrVariables();
                    if (jTypeNameOrVariables == null) {
                        snapshot.rollback();
                        return null;
                    }
                    endToken = jTypeNameOrVariables.getEndToken();
                }
            }
            int dim = 0;
            List<HNode> inits = new ArrayList<>();
            while (true) {
                n = peek();
                if (n.id() == HTokenId.LEFT_SQUARE_BRACKET) {
                    skip();
                    n = peek();
                    if (n.isImage("]")) {
                        endToken = next().copy();
                        dim++;
                        //
                    } else {
                        if (acceptInit) {
                            if (inits.size() == dim) {
                                HNode jNode = parseExpression();
                                if (jNode == null) {
                                    snapshot.rollback();
                                    return null;
                                }
                                endToken = jNode.endToken();
                                inits.add(jNode);
                                n = next();
                                if (n.isImage("]")) {
                                    endToken = n.copy();
                                    dim++;
                                } else {
                                    snapshot.rollback();
                                    return null;
                                }
                            } else {
                                snapshot.rollback();
                                return null;
                            }
                        } else {
                            snapshot.rollback();
                            return null;
                        }
                    }
                } else {
                    break;
                }
            }
            boolean varArg = false;
            n = peek();
            if (n.isImage("...")) {
                endToken = next().copy();
                if (acceptVarArg) {
                    varArg = true;
                } else {
                    log().error("X115", null, "vararg not supported in this context", n);
                }
            }
            JTypeNameOrVariable[] jTypeNameOrVariables0 = new JTypeNameOrVariable[jTypeNameOrVariables == null ? 0 : jTypeNameOrVariables.getItems().size()];
            for (int i = 0; i < jTypeNameOrVariables0.length; i++) {
                jTypeNameOrVariables0[i] = jTypeNameOrVariables.getItems().get(i).getTypenameOrVar();
            }
            JTypeName tname = new DefaultTypeName(wToken.image, jTypeNameOrVariables0,
                    dim, varArg
            );
            HNTypeToken tnamet = new HNTypeToken(
                    wToken,
                    tname,
                    jTypeNameOrVariables == null ? new HNTypeToken[0] : jTypeNameOrVariables.getItems().toArray(new HNTypeToken[0]),
                    new HNTypeToken[0],
                    new HNTypeToken[0],
                    wToken,
                    endToken
            );
            return new JTypeNameAndInit(tnamet, inits.toArray(new HNode[0]));
        }
    }

    private HNDeclareTokenTupleItem parseDeclareTokenTupleItem(JMessageList reportedErrors) {
        JToken x = peek();
        if (x.isIdentifier()) {
            return new HNDeclareTokenIdentifier(next());
        } else if (x.isImage("(")) {
            JToken startToken = next();
            JToken endToken = startToken;
            List<HNDeclareTokenTupleItem> items = new ArrayList<>();
            List<JToken> separators = new ArrayList<>();
            HNDeclareTokenTupleItem q = parseDeclareTokenTupleItem(reportedErrors);
            if (q != null) {
                items.add(q);
                while (true) {
                    x = peek();
                    if (x.isEOF()) {
                        reportedErrors.error("X000", null, "expected ','", x);
                        break;
                    } else if (x.id() == HTokenId.COMMA) {
                        endToken = x = next();
                        separators.add(x);
                        HNDeclareTokenTupleItem a = parseDeclareTokenTupleItem(reportedErrors);
                        if (a != null) {
                            endToken = a.endToken();
                            items.add(a);
                        } else {
                            break;
                        }
                    } else if (x.isImage(")")) {
                        endToken = x = next();
                        break;
                    } else {
                        reportedErrors.error("X000", null, "expected ','", x);
                        HNDeclareTokenTupleItem a = parseDeclareTokenTupleItem(reportedErrors);
                        if (a != null) {
                            endToken = a.endToken();
                            items.add(a);
                        } else {
                            break;
                        }
                    }
                }
                return new HNDeclareTokenTuple(
                        items.toArray(new HNDeclareTokenTupleItem[0]), separators.toArray(new JToken[0]), startToken, endToken
                );
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private JToken parseNameWithPackageSingleToken() {
        JToken[] r = parseNameWithPackageTokens();
        if (r == null) {
            return null;
        }
        if (r.length == 1) {
            return r[0];
        }
        String n = Arrays.stream(r).map(x -> x.image).collect(Collectors.joining());
        JToken r2 = r[0].copy();
        r2.endCharacterNumber = r[r.length - 1].endCharacterNumber;
        r2.endLineNumber = r[r.length - 1].endLineNumber;
        r2.endColumnNumber = r[r.length - 1].endColumnNumber;
        r2.image = n;
        r2.sval = n;
        return r2;
    }

    private JToken[] parseNameWithPackageTokens() {
        List<JToken> all = new ArrayList<>();
        JToken v = next();
        if (!v.isIdentifier()) {
            pushBack(v);
            return null;
        }
        all.add(v.copy());
        while (true) {
            JToken u = next();
            if (u.id() == HTokenId.DOT) {
                all.add(u);
                u = next();
                if (u.isIdentifier()) {
                    all.add(u);
                } else if (u.id() == HTokenId.LEFT_PARENTHESIS) {
                    pushBack(u);
                    break;
                }
            } else {
                pushBack(u);
                break;
            }
        }
        return all.toArray(new JToken[0]);
    }

    private int parseModifiers() {
        int modifiers = 0;
        while (true) {
            boolean accepted = true;
            JToken p = next();
            switch (p.id()) {
                case HTokenId.KEYWORD_PUBLIC: {
                    modifiers |= Modifier.PUBLIC;
                    break;
                }
                case HTokenId.KEYWORD_PACKAGE: {
                    modifiers |= HUtils.PACKAGE;
                    break;
                }
                case HTokenId.KEYWORD_PRIVATE: {
                    modifiers |= Modifier.PRIVATE;
                    break;
                }
                case HTokenId.KEYWORD_PROTECTED: {
                    modifiers |= Modifier.PROTECTED;
                    break;
                }
                case HTokenId.KEYWORD_STATIC: {
                    modifiers |= Modifier.STATIC;
                    break;
                }
                case HTokenId.KEYWORD_FINAL: {
                    modifiers |= Modifier.FINAL;
                    break;
                }
                case HTokenId.KEYWORD_ABSTRACT: {
                    modifiers |= Modifier.ABSTRACT;
                    break;
                }
                case HTokenId.KEYWORD_CONST: {
                    modifiers |= HUtils.CONST;
                    break;
                }
                case HTokenId.KEYWORD_TRANSIENT: {
                    modifiers |= Modifier.TRANSIENT;
                    break;
                }
                case HTokenId.KEYWORD_VOLATILE: {
                    modifiers |= Modifier.VOLATILE;
                    break;
                }
                case HTokenId.KEYWORD_STRICTFP: {
                    modifiers |= Modifier.STRICT;
                    break;
                }
                case HTokenId.KEYWORD_READONLY: {
                    modifiers |= HUtils.READONLY;
                    break;
                }
                default: {
                    accepted = false;
                }
            }
            if (!accepted) {
                pushBack(p);
                break;
            }
        }
        return modifiers;
    }

    //    public JToken parseVarNameOrError() {
//        JToken name = next();
//        if (name.isIdentifier()) {
//            return name;
//        } else {
//            JToken name2 = name.copy();
//            pushBack(name);
//            name2.ttype = JToken.TT_IDENTIFIER;
//            name2.sval = "<anonymous>";
//            name2.sttype = "<error>";
//            name2.errorId = 1;
//            name2.errorName = "invalid";
//
//        }
//        return name;
//    }

    /**
     * var a=2
     * val a=2
     * a=Z
     * int a=2;
     * String a="";
     * The same applies with ':' instead of '='
     *
     * @return
     */
    private HNode parseDeclareAssign(Boolean eqOp, boolean acceptAnyExpression, JMessageList err) {
//        JToken a = peek();
//        boolean acceptColon = eqOp == null || !eqOp;
//        boolean acceptEq = eqOp == null || eqOp;
        HLDeclarationOptions options = eqOp == null ? DECLARE_ASSIGN_EQ_OR_COLON :
                eqOp ? DECLARE_ASSIGN_EQ : DECLARE_ASSIGN_COLON;

//        switch (a.id()) {
//            case HTokenId.KEYWORD_VAL:
//            case HTokenId.KEYWORD_VAR:
//            case HTokenId.KEYWORD_BOOLEAN:
//            case HTokenId.KEYWORD_BYTE:
//            case HTokenId.KEYWORD_SHORT:
//            case HTokenId.KEYWORD_CHAR:
//            case HTokenId.KEYWORD_INT:
//            case HTokenId.KEYWORD_LONG:
//            case HTokenId.KEYWORD_FLOAT:
//            case HTokenId.KEYWORD_DOUBLE: {
//                break;
//            }
//            case HTokenId.IDENTIFIER: {
//                JToken[] n = peek(2);
//                if (n.length > 1 &&
//                        (
//                                (eqOp == null && (n[1].id() == HTokenId.EQ || n[1].id() == HTokenId.COLON))
//                                        ||
//                                        (eqOp != null && n[1].id() == (eqOp ? HTokenId.EQ : HTokenId.COLON)
//                                        )
//                        )
//                ) {
//                    JToken idVar = next();
//                    JToken startToken = idVar;
//                    JToken opToken = next();
//                    JToken endToken = opToken;
//                    HNDeclareTokenIdentifier t = new HNDeclareTokenIdentifier(idVar);
//                    HNode e = parseExpression();
//                    if (e == null) {
//                        log().error("X127", "identifier declaration", "expected initialization", peek());
//                    } else {
//                        endToken = e.endToken();
//                    }
//                    return new HNDeclareIdentifier(
//                            t, e, null, opToken, startToken, endToken
//                    );
//                }
//            }
//        }
        JMessageList err2 = errorList();
        HNDeclareIdentifier ii = (HNDeclareIdentifier) parseDeclaration(options, err2);
        if (ii != null) {
            err.addAll(err2);
            return ii;
        }
        if (acceptAnyExpression) {
            HNode n = parseExpression();
//            HNode n = parseExpressionAsStatement();
            if (n != null) {
                return n;
            }
            err.addAll(err2);
        }
        err.error("X000", null, "expected expression", peek());
        return null;
    }

    private DefaultJMessageList errorList() {
        return new DefaultJMessageList();
    }

    private HNode parseDeclaration(HLDeclarationOptions options, JMessageList err) {
        try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
            int modifiers = 0;
            JToken startToken = peek();
            JToken endToken = startToken;
            if (options.acceptModifiers) {
                modifiers = parseModifiers();
                if (Modifier.isStatic(modifiers)) {
                    if (peek().id() == HTokenId.LEFT_CURLY_BRACKET) {
                        if (modifiers != Modifier.STATIC) {
                            err.error("X116", null, "static initializer should not have modifiers", peek());
                            modifiers = Modifier.STATIC;
                        }
                        HNode jNode = parseBraces(HNBlock.BlocType.METHOD_BODY);
                        if (jNode != null) {
                            endToken = jNode.endToken();
                            HNDeclareInvokable fct = new HNDeclareInvokable(
                                    null,
                                    jNode.startToken(), endToken);
                            fct.setModifiers(modifiers);
                            fct.setBody(jNode);
                            fct.setReturnTypeName(null);
                            fct.setSignature(
                                    JNameSignature.of(
                                            fct.getName(),
                                            fct.getArguments().stream()
                                                    .map(HNDeclareIdentifier::getIdentifierTypeName).toArray(JTypeName[]::new)
                                    )
                            );
                            return fct;
                        }
                        //
                    }
                }
            }
            JToken t = peek();
            if (t.id() == HTokenId.KEYWORD_CLASS) {
                if (!options.acceptClass) {
                    err.error("X117", "class definition", "not allowed class definition in the current context", t);
                }
                skip();
                return parseDeclareClass(modifiers, startToken);
            } else if (t.id() == HTokenId.KEYWORD_DEF || t.id() == HTokenId.KEYWORD_CONSTRUCTOR || t.id() == HTokenId.KEYWORD_VOID) {
                if (!options.acceptFunction) {
                    log().error("X118", "function/method definition", "not allowed class method/function/constructor definition in the current context", t);
                }
                if (t.id() == HTokenId.KEYWORD_VOID) {
                    JToken voidToken = next();
                    pushBack(JTokenUtils.createKeywordToken("def"));
                    pushBack(voidToken);
                }
                return parseDeclareFunction(modifiers, false, null, true, startToken);
            }

            ParseDeclareIdentifierContext pdic = new ParseDeclareIdentifierContext();
            pdic.modifiers = modifiers;
            pdic.options = options;
            pdic.startToken = startToken;
            pdic.endToken = endToken;
            HNode n = parseDeclareIdentifier(pdic, err);
            if (n == null) {
                snapshot.rollback();
                return null;
            }
            return n;
        }
    }

    private HNode parseDeclareIdentifier(ParseDeclareIdentifierContext pdic, JMessageList err) {
        String groupName = null;
        JToken t = peek();
        if (t.id() == HTokenId.KEYWORD_VAR) {
            if (pdic.options.acceptVar) {
                next();
                pdic.varVal = t;
            } else {
                return null;
            }
        } else if (t.id() == HTokenId.KEYWORD_VAL) {
            if (pdic.options.acceptVar) {
                next();
                pdic.varVal = t;
            } else {
                return null;
            }
        } else {
            if (pdic.options.getNoTypeNameOption() == HLDeclarationOptions.NoTypeNameOption.NAME) {
                DefaultJMessageList err2 = errorList();
                if (_parseDeclareIdentifierVarIds(pdic, errorList())) {
                    if (pdic.varIds != null) {
                        err.addAll(err2);
                        err2.clear();
                        HNode hNode = _parseDeclareIdentifierInitialization(pdic, err2);
                        err.addAll(err2);
                        if (hNode == null) {
                            return null;
                        }
                        return hNode;
                    }
                }
            }
            pdic.tt = parseTypeNameAndInit(true, pdic.options.acceptVarArg);
            if (pdic.tt == null) {
                if (pdic.modifiers != 0) {
                    err.error("X119", groupName, "identifier definition : unexpected modifier", peek());
                    return null;
                } else {
                    //this is not a declaration!
                    return null;
                }
            }
        }
        DefaultJMessageList err2 = errorList();
        if (!_parseDeclareIdentifierVarIds(pdic, err2)) {
            err.addAll(err2);
            //there is no var, use 'value' var name (or 'exception')
            if (pdic.options.getNoTypeNameOption() == HLDeclarationOptions.NoTypeNameOption.TYPE) {
                pdic.varIds = new HNDeclareTokenIdentifier(HTokenUtils.createToken(pdic.options.getDefaultVarName()));
            } else {
                return null;
            }
        } else {
            err.addAll(err2);
            if (pdic.varIds == null) {
                if (pdic.options.getNoTypeNameOption() == HLDeclarationOptions.NoTypeNameOption.TYPE) {
                    pdic.varIds = new HNDeclareTokenIdentifier(HTokenUtils.createToken(pdic.options.getDefaultVarName()));
                } else {
                    err.error("X000", groupName, "missing name", peek());
                    return null;
                }
            }
        }
        err2.clear();
        HNode hNode = _parseDeclareIdentifierInitialization(pdic, err2);
        err.addAll(err2);
        if (hNode == null) {
            return null;
        }
        return hNode;
    }

    /**
     * parse declare vars in the following forms :
     * a
     *
     * @param pdic
     * @return
     */
    private boolean _parseDeclareIdentifierVarIds(ParseDeclareIdentifierContext pdic, JMessageList err) {
        HNDeclareToken varIds = null;
        JToken endToken = pdic.endToken;
        try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
            String logGroup = "identifier definition";
            if ( /*(varVal != null) && */ peek().id() == HTokenId.LEFT_PARENTHESIS) {
                JMessageList reportedErrors = errorList();
                HNDeclareTokenTupleItem u = parseDeclareTokenTupleItem(reportedErrors);
                if (u != null) {
                    varIds = u;
                    JToken pp = peek();
                    if (pp.id() != HTokenId.EQ && pp.id() != HTokenId.COLON) {
                        //not a tuple assignment!
                        err.error("X120", logGroup, "expected ':' or '='", peek());
                        snapshot.rollback();
                        return false;
                    }
                    for (JSourceMessage reportedError : reportedErrors) {
                        log().add(reportedError);
                    }
                } else {
                    err.addAll(reportedErrors);
                    snapshot.rollback();
                    return false;
                }
                //tuple definition
            } else {
                JToken name = null;
                JToken p = peek();
                if (p.isIdentifier()) {
                    if (pdic.options.isAcceptDotName()) {
                        name = parseNameWithPackageSingleToken();
                    } else {
                        name = next();
                    }
                }
                if (name == null) {
                    //this is not a name. It should be an expression
//                    err.error("X000", logGroup, "expected name", peek());
//                    snapshot.rollback();
//                    return false;
                    //has null name
                    snapshot.rollback();
                    return true;
                } else {
                    endToken = name;
                }
                List<HNDeclareTokenTupleItem> allVarNames = new ArrayList<>();
                allVarNames.add(new HNDeclareTokenIdentifier(name));
                if (pdic.options.isAcceptMultiVars()) {
                    while (peek().id() == HTokenId.COMMA) {
                        next();
                        JToken name2 = parseNameWithPackageSingleToken();
                        if (name2 == null) {
                            err.error("X120", logGroup, "expected name", peek());
                            break;
                        } else {
                            endToken = name2.copy();
                            allVarNames.add(new HNDeclareTokenIdentifier(endToken));
                        }
                    }
                }
                for (HNDeclareTokenTupleItem vname : allVarNames) {
                    checkVarName(vname, err);
                }
                if (allVarNames.size() == 1) {
                    varIds = allVarNames.get(0);
                } else {
                    List<HNDeclareTokenIdentifier> ids = new ArrayList<>();
                    for (HNDeclareTokenTupleItem v : allVarNames) {
                        if (v instanceof HNDeclareTokenIdentifier) {
                            ids.add((HNDeclareTokenIdentifier) v);
                        } else {
                            err.error("X000", logGroup, "expected identifier", v.startToken());
                        }
                    }
                    varIds = new HNDeclareTokenList(ids.toArray(new HNDeclareTokenIdentifier[0]),
                            allVarNames.get(0).startToken(),
                            allVarNames.get(allVarNames.size() - 1).endToken()
                    );
                }
            }
            JToken p = peek();
            switch (p.id()) {
                case HTokenId.EOF:
                case HTokenId.EQ:
                case HTokenId.COLON:
                case HTokenId.SEMICOLON:
                case HTokenId.COMMA:
                case HTokenId.RIGHT_PARENTHESIS: {
                    pdic.varIds = varIds;
                    pdic.endToken = endToken;
                    return true;
                }
                case HTokenId.LEFT_PARENTHESIS: {
                    if (pdic.tt != null) {
                        pdic.varIds = varIds;
                        pdic.endToken = endToken;
                        return true;
                    }
                    break;
                }
            }
            err.error("X000", logGroup, "expected identifier", p);
            snapshot.rollback();
            return false;
        }
    }

    private HNode _parseDeclareIdentifierInitialization(ParseDeclareIdentifierContext pdic, JMessageList err) {
        HNode val = null;
        JToken opToken = null;
        if (pdic.tt == null || pdic.tt.inits.length == 0) {
            JToken n = next();
            if (n.isOperator("=") && (pdic.varVal != null || pdic.options.acceptEqValue)) {
                opToken = n.copy();
                pdic.endToken = opToken;
                val = parseExpression();
                if (val == null) {
                    err.error("X121", "identifier definition", "expected value assignment", peek());
                } else {
                    pdic.endToken = val.endToken();
                }
            } else if (n.id() == HTokenId.COLON && (pdic.varVal != null || pdic.options.acceptInValue)) {
                opToken = n.copy();
                pdic.endToken = opToken;
                val = parseExpression();
                if (val == null) {
                    log().error("X121", "identifier definition", "expected value assignment", peek());
                } else {
                    pdic.endToken = val.endToken();
                }
            } else if (n.isImage("(")) {
                //this is a constructor call!
                pushBack(n);
                JListWithSeparators<HNode> argument_declaration = parseParsList("constructor", "constructor argument", ()
                        -> parseExpression()
                );
                val = new HNObjectNew(pdic.tt.typeToken, argument_declaration == null ? new HNode[0]
                        : argument_declaration.getItems().toArray(new HNode[0]), argument_declaration.getStartToken(), argument_declaration.getEndToken()
                );
                pdic.endToken = val.endToken();
            } else {
                if (pdic.varVal != null) {
                    err.error("X121", "identifier definition", "expected value assignment", n);
                }
                pushBack(n);
            }
        } else {
            //this is an array initialization
            JToken t2 = peek();
            HNode setter = null;
            //array special constructor!
            if (t2.isImage("(")) {
                next();
                JToken t3 = peek();
//                    if (t3.isImage("(")) {
//                        setter = parseFunctionDeclaration(0, true, tt.typeToken, false, startToken);
//                    } else {
                setter = parseExpression();
//                    }
                if (setter != null) {
                    pdic.endToken = setter.endToken();
                }
                t2 = peek();
                if (t2.isImage(")")) {
                    pdic.endToken = next();
                } else {
                    err.error("X122", "identifier definition", "expected ')'", t2);
                }
            }
            if (setter != null && pdic.tt.inits.length == 0) {
                err.error("S052", null, "initialized array is missing initializer", pdic.startToken);
            }
            val = new HNArrayNew(pdic.tt.typeToken, pdic.tt.inits, setter, pdic.startToken, pdic.endToken);
            //create init!!
        }
        if (opToken == null) {
            opToken = HTokenUtils.createToken("=");
        }
        HNDeclareIdentifier h = new HNDeclareIdentifier(
                pdic.varIds,
                val,
                (pdic.tt == null) ? null : pdic.tt.typeToken,
                opToken, pdic.startToken, pdic.endToken
        );
        h.setStartToken(pdic.startToken);
        h.setModifiers(pdic.modifiers);
        h.setInitValue(val);
        if (pdic.tt == null || pdic.tt.typeToken == null) {
            h.setIdentifierTypeName(null);
        } else {
            h.setIdentifierTypeName(pdic.tt.typeToken);
        }
        return h;
    }

    private void checkVarName(HNDeclareTokenTupleItem vname, JMessageList err) {
        if (vname instanceof HNDeclareTokenIdentifier) {
            JToken ttt = ((HNDeclareTokenIdentifier) vname).getToken();
            if (ttt.image.indexOf('.') >= 0) {
                err.error("X120", "identifier definition", "invalid identifier name ", ttt);
            }
        } else {
            throw new JFixMeLaterException();
        }
    }

    /**
     * String a;
     * a;
     *
     * @param options
     * @return
     */
    private HNDeclareIdentifier parseDeclareArgument(HLDeclarationOptions options, JMessageList err) {
        JToken[] peeked = peek(2);
        if (peeked.length >= 2 && peeked[0].isIdentifier() && (peeked[1].id() == HTokenId.COMMA || peeked[1].isImage(")"))) {
            //this is an untyped declaration
            JToken w = next();
            JToken opToken = HTokenUtils.createToken("=");
            HNDeclareIdentifier did = new HNDeclareIdentifier(
                    new HNDeclareTokenIdentifier(w),
                    null, null, opToken, peeked[0], w);
            did.setStartToken(peeked[0]);
            return did;
        }
        //boolean acceptModifiers, boolean acceptVar, boolean acceptFunction, boolean acceptClass,
        //                                    boolean requiredSemiColumnForVar, boolean acceptEqValue, boolean acceptInValue

        return (HNDeclareIdentifier) parseDeclaration(options, err);
    }

    private HNExtends parseExtends() {
        JToken startNode = peek().copy();
        JToken endToken = startNode;

        int modifiers = parseModifiers();
        JToken[] name = parseNameWithPackageTokens();
        if (name == null) {
            if (modifiers == 0) {
                return null;
            }
            log().error("X123", "class declaration", "expected class name", peek());
            return null;
        }
        endToken = name[name.length - 1];
        String[] nameAndNamespace = HUtils.splitNameAndPackage(name);
        JListWithSeparators<HNode> li = parseParsList("extends", "super argument declaration", () -> parseExpression());
        HNExtends dec = new HNExtends(nameAndNamespace[1], startNode, li == null ? endToken : li.getEndToken());
        dec.setPackageName(nameAndNamespace[0]);
        dec.setModifiers(modifiers);
        dec.setArguments(li == null ? new ArrayList<>() : li.getItems());
        return dec;
    }

    public LambdaBody parseFunctionArrowBody() {
        if (peek().id() == HTokenId.MINUS_GT) {
            JToken n = next();
            if (peek().id() == HTokenId.LEFT_CURLY_BRACKET) {
                HNode b = parseBraces(HNBlock.BlocType.LOCAL_BLOC);
                if (b == null) {
                    log().error("X000", "lambda", "expected lambda body", peek());
                }
                return new LambdaBody(n, b, false);
            } else {
                HNode b = parseExpression();
                if (b == null) {
                    log().error("X000", "lambda", "expected expression", peek());
                }
                return new LambdaBody(n, b, true);
            }
        }
        return null;
    }

    private HNode parseDeclareFunction(
            int modifiers, boolean anonymous, HNTypeToken type, boolean requiredSemiColumnForVar,
            JToken startToken
    ) {
        startToken = startToken.copy();
        JToken name = null;
        JListWithSeparators<HNTypeToken> jTypeNameOrVariables = null;
        boolean constr = false;
        if (anonymous) {
            jTypeNameOrVariables = parseJTypeNameOrVariables();
        } else {
            if (peek().isImage("def")) {
                next();
                if (peek().isImage("constructor")) {
                    name = next();
                    constr = true;

                    jTypeNameOrVariables = parseJTypeNameOrVariables();
                } else {
                    JToken typeToken = peek().copy();
                    type = parseTypeName();
                    if (peek().isImage("(")) {
                        //we did'nt read the type, actually this is the name.
                        //type is supposed "void" or none (for constructors)
                        if (type == null) {
                            log().error("X124", "function/method declaration", "expected type", peek());
                            //type = createSpecialTypeToken("void");
                        }
                        if (type != null && (type.getTypename().isArray() || type.getTypenameOrVar().name().indexOf('.') >= 0)) {
                            log().error("X125", "function/method declaration", "expected name", peek());
                        } else {
                            if (type != null) {
                                JTokenUtils.fillToken(
                                        new JTokenDef(
                                                JTokenId.IDENTIFIER,
                                                "IDENTIFIER",
                                                JTokenType.TT_IDENTIFIER,
                                                "TT_IDENTIFIER",
                                                type.getTypenameOrVar().name(),
                                                0,
                                                "PARSER"
                                        ), typeToken);
                                typeToken.endCharacterNumber = typeToken.startCharacterNumber + type.getTypenameOrVar().name().length();
                                typeToken.endColumnNumber = typeToken.startColumnNumber + type.getTypenameOrVar().name().length();
                                typeToken.image = type.getTypenameOrVar().name();
                                typeToken.sval = typeToken.image;
                                if (type.vars().size() > 0) {
                                    jTypeNameOrVariables = new DefaultJListWithSeparators<>(type.vars(), null, new ArrayList<>(), null);
                                    name = typeToken;
                                } else {
                                    name = typeToken;
                                    jTypeNameOrVariables = parseJTypeNameOrVariables();
                                }
                            }
                            type = null;//createSpecialTypeToken("void");
                        }
                    } else {
                        if (peek().isIdentifier()) {
                            name = next();
                        }
                        if (name == null) {
                            log().error("X125", "function/method declaration", "expected name", peek());
                        }
                        jTypeNameOrVariables = parseJTypeNameOrVariables();
                    }
                }
            } else if (peek().isImage("constructor")) {
                name = next();
                constr = true;
                jTypeNameOrVariables = parseJTypeNameOrVariables();
            } else {
                return null;
            }
        }
        if (jTypeNameOrVariables == null) {
            //log().error("X052", "function/method declaration: invalid generic arguments", peek());
            jTypeNameOrVariables = new DefaultJListWithSeparators<>(new ArrayList<>(), null, new ArrayList<>(), null);
        }
//        String[] nameAndNamespace = HUtils.splitNameAndNamespace(name);
        modifiers = HUtils.publifyModifiers(modifiers);


        HLDeclarationOptions argOptions = constr ? DECLARE_CONSTR_ARG : DECLARE_FUNCTION_ARG;
        JListWithSeparators<HNDeclareIdentifier> li = parseParsList("function/method declaration", "argument declaration",
                () -> parseDeclareArgument(argOptions, log())
        );
        if (li == null) {
            log().error("X126", "function/method declaration", "expected parameters", peek());
        }
        HNDeclareInvokable f = new HNDeclareInvokable(name, startToken, tokenizer().lastToken());
        f.setNameToken(name);
        f.setConstr(constr);
        f.setGenericVariables(jTypeNameOrVariables.getItems().toArray(new HNTypeToken[0]));
        f.setArguments(li == null ? new ArrayList<>() : li.getItems());
        if (f.getArguments() == null) {
            //parseParsList failed
            f.setArguments(new ArrayList<>());
        }
//        System.out.println(f.toString());
        f.setReturnTypeName(type);
        f.setModifiers(modifiers);
        boolean hasSig = true;
        for (HNDeclareIdentifier argument : f.getArguments()) {
            if (argument.getIdentifierType() == null) {
                hasSig = false;
            }
        }
        if (hasSig) {
            f.setSignature(
                    JNameSignature.of(
                            f.getName(),
                            f.getArguments().stream()
                                    .map(HNDeclareIdentifier::getIdentifierTypeName)
                                    .map(HNTypeToken::getTypename)
                                    .toArray(JTypeName[]::new)
                    )
            );
        }
        JToken n = peek();
        if (n.id() == HTokenId.SEMICOLON) {
            f.setEndToken(next());
            modifiers |= Modifier.ABSTRACT;
            f.setModifiers(modifiers);
        } else if (n.id() == HTokenId.MINUS_GT) {
            LambdaBody lambdaBody = parseFunctionArrowBody();
            f.setEndToken(lambdaBody.op);
            f.setImmediateBody(true);
            f.setEndToken(lambdaBody.getEndToken());
            f.setBody(lambdaBody.body);
            if (requiredSemiColumnForVar) {
                requireSemiColumn("function/method declaration", true);
            }
        } else if (n.id() == HTokenId.LEFT_CURLY_BRACKET) {
            f.setBody(parseBraces(HNBlock.BlocType.METHOD_BODY));
            f.setEndToken(f.getBody().endToken());
        } else {
            log().error("X128", "function/method declaration", "expected '{' or '->'", peek());
            HNode e = parseExpression();
            if (e != null) {
                f.setBody(e);
                f.setEndToken(f.getBody().endToken());
            }
            if (requiredSemiColumnForVar) {
                requireSemiColumn("function/method declaration", true);
            }
        }
//        f.setEndToken(tokenizer().lastToken());
        return f;
    }

    private HNTypeToken createSpecialTypeToken(String name) {
        return new HNTypeToken(
                null,
                context().types().forName(name).typeName(),
                new HNTypeToken[0],
                new HNTypeToken[0],
                new HNTypeToken[0],
                null,
                null
        );
    }

    private HNode parseDeclareClass(int modifiers, JToken startToken) {
        try {
            HNDeclareType classDef = new HNDeclareType(startToken);
            JToken endToken = startToken;
            pushDeclarationContext(classDef);
            JToken[] name = parseNameWithPackageTokens();
            if (name == null) {
                log().error("X129", "class declaration", "expected class name", peek());
                name = new JToken[]{JTokenUtils.createTokenIdPointer(startToken, "<anonymous>")};
            }
            String[] nameAndNamespace = HUtils.splitNameAndPackage(name);
//            HNDeclareType f = new HNDeclareType(nameAndNamespace[1]);
//            f.setNamespace(nameAndNamespace[0]);
            classDef.setNameToken(name[name.length - 1]);
            classDef.setModifiers(modifiers);
            classDef.setPackageName(nameAndNamespace[0]);
            JToken n = peek();
            if (n.isImage("(")) {
                JListWithSeparators<HNDeclareIdentifier> sli = parseParsList("class declaration", "argument", () -> {
                    return parseDeclareArgument(
                            DECLARE_DEFAULT_CONSTR_ARG, log()
                    );
                });
                classDef.setMainConstructorArgs(sli.getItems());
                if (sli.getEndToken() != null) {
                    classDef.setEndToken(sli.getEndToken());
                    endToken = sli.getEndToken();
                }
                n = peek();
            }
            if (n.id() == HTokenId.KEYWORD_EXTENDS || n.id() == HTokenId.COLON) {
                JToken sep = next();
                endToken = sep;
                JListWithSeparators<HNExtends> extends_declaration = parseGroupedList(
                        "extends declarations", "extends declaration", () -> parseExtends(), null, ",", null, null);
                classDef.setExtends(extends_declaration.getItems());
                classDef.setExtendsSepToken(sep);
                if (extends_declaration.getEndToken() != null) {
                    endToken = extends_declaration.getEndToken();
                }
                n = peek();
            }
            if (n.id() == HTokenId.SEMICOLON) {
                endToken = next();
            } else if (n.id() == HTokenId.LEFT_CURLY_BRACKET) {
                classDef.setBody(parseBraces(HNBlock.BlocType.CLASS_BODY));
                if (classDef.getBody() != null) {
                    for (HNode statement : ((HNBlock) classDef.getBody()).getStatements()) {
                        bindDeclaringType(classDef, statement);
                    }
                    endToken = (classDef.getBody().endToken());
                }
            } else {
                log().error("X130", "class declaration", "expected '{' or '->'", peek());
            }
            classDef.setEndToken(endToken);
            return classDef;
        } finally {
            popDeclarationContext();
        }
    }

    private void bindDeclaringType(HNDeclareType classDef, HNode statement) {
        if (statement instanceof HNDeclareIdentifier) {
            HNDeclareIdentifier s = (HNDeclareIdentifier) statement;
            s.setDeclaringType(classDef);
        } else if (statement instanceof HNDeclareInvokable) {
            HNDeclareInvokable s = (HNDeclareInvokable) statement;
            s.setDeclaringType(classDef);
        } else if (statement instanceof HNDeclareType) {
            HNDeclareType s = (HNDeclareType) statement;
            s.setDeclaringType(classDef);

        } else if (statement instanceof HNBlock) {
            HNBlock b = (HNBlock) statement;
            //this is implicit import bloc
            if (b.getBlocType() == HNBlock.BlocType.IMPORT_BLOC) {
                for (HNode statement2 : b.getStatements()) {
                    bindDeclaringType(classDef, statement2);
                }
            }
        }
    }

    public HNBlock.CompilationUnitBlock parseDocument() {
        JToken startToken = peek();
        JToken endToken = startToken;
        JListWithSeparators<HNode> statements = parseStatements();
        if (statements.getEndToken() != null) {
            endToken = statements.getEndToken();
        }
        JToken p = peek();
        if (!p.isEOF()) {
            log().error("X133", null, "unable to parse till the end of the document.", p);
        }
        HNBlock.CompilationUnitBlock block = new HNBlock.CompilationUnitBlock(
                statements.getItems().toArray(new HNode[0]),
                startToken,
                endToken
        );
        return block;
    }

    protected HNode[][] parseMatrixOrArrayOfExpr() {
        JToken n = next();
        if (n.id() != HTokenId.LEFT_SQUARE_BRACKET) {
            return null;
        }
        JToken startToken = n.copy();
        List<List<HNode>> rows = new ArrayList<>();
        List<HNode> lastRow = null;
        int maxColumns = 0;
        if (peek().id() == HTokenId.RIGHT_SQUARE_BRACKET) {
            next();
        } else {
            while (true) {
                HNode t = parseExpression();
                if (t == null) {
                    log().error("X134", "array", "invalid expression", peek());
                    skipUntil(x -> x.isImage("]"));
                    break;
                }
                if (lastRow == null) {
                    lastRow = new ArrayList<>();
                    rows.add(lastRow);
                }
                lastRow.add(t);
                if (lastRow.size() > maxColumns) {
                    maxColumns = lastRow.size();
                }
                JToken n2 = next();
                if (n2.id() == HTokenId.COMMA) {
                    //next cell
                } else if (n2.id() == HTokenId.SEMICOLON) {
                    //next line
                    lastRow = null;
                } else if (n2.isImage("]")) {
                    break;
                } else {
                    pushBack(n2);
                }
            }
        }
        HNode[][] mat = new HNode[rows.size()][maxColumns];
        for (int i = 0; i < mat.length; i++) {
            List<HNode> row = rows.get(i);
            if (row.size() != maxColumns) {
                log().error("X135", "matrix", "columns mismatch : "
                                + "expected " + maxColumns + " but found " + row.size() + " at row " + (i + 1),
                        startToken);
            }
            mat[i] = row.toArray(new HNode[0]);
        }
        return mat;
    }

    protected HNode parseIf(boolean asExpr) {
        JToken startToken = peek().copy();
        JToken endToken = startToken;
        if (peek().id() == HTokenId.KEYWORD_IF) {
            next();//skip if
            JToken p = peek();//skip if
            HNode condExpr = null;
            HNIf i = new HNIf(startToken);
            endToken = (startToken);
            condExpr = parseExpression(noBracesExpressionOptions);
            if (condExpr == null) {
                log().error("X136", "if statement", "expected condition", peek());
                i.setEndToken(endToken);
                return i;
            }
            HNode doExpr = parseExpressionOrStatement(asExpr,false);
            if (doExpr == null) {
                log().error("X137", "if statement", "expected then statement/expression", peek());
                if (peek().id() == HTokenId.KEYWORD_ELSE) {
                    //doExpr=HUtils.createUnknownBlocNode();
                } else {
                    i.setEndToken(endToken);
                    return i;
                }
            } else {
                endToken = (doExpr.endToken());
            }
            i.add(condExpr, doExpr);

            while (true) {
                JToken[] p2 = peek(2);
                if (p2.length >= 2 && (p2[0].id() == HTokenId.KEYWORD_ELSE && p2[1].id() == HTokenId.KEYWORD_IF)) {
                    //skip else if!
                    next();
                    endToken = (next());
                    condExpr = parseParenthesis("else if condition");
                    if (condExpr == null) {
                        log().error("X138", "if statement", "expected else condition", peek());
                        i.setEndToken(endToken);
                        return i;
                    }
                    endToken = (condExpr.endToken());
                    doExpr = parseExpressionOrStatement(asExpr,false);
                    if (doExpr == null) {
                        log().error("X139", "if statement", "expected then statement/expression", peek());
                        //doExpr=HUtils.createUnknownBlocNode();
                    } else {
                        endToken = (doExpr.endToken());
                    }
                    i.add(condExpr, doExpr);
                } else if (p2.length >= 1 && p2[0].id() == HTokenId.KEYWORD_ELSE) {
                    //skip else!
                    endToken = (next());
                    doExpr = parseExpressionOrStatement(asExpr,false);
                    if (doExpr == null) {
                        log().error("X140", "if statement", "expected else statement/expression", peek());
                    } else {
                        endToken = (doExpr.endToken());
                    }
                    i.setElse(doExpr);
                    break;
                } else {
                    break;
                }
            }
            i.setEndToken(endToken);
            return i;
        }
        return null;
    }

    protected HNode parseWhile(boolean asExpr) {
        JToken startToken = peek().copy();
        JToken endToken = startToken;
        if (startToken.id() == HTokenId.KEYWORD_WHILE) {
            skip();
            JToken p = next();
            HNode cond = null;
            if (p.isImage("(")) {
                pushBack(p);
                cond = parseParenthesis("while condition");
                if (cond == null) {
                    log().error("X141", "while statement", "expected while condition", peek());
                    while (true) {
                        JToken peek = next();
                        if (!peek.isImage(")") || peek.isEOF()) {
                            break;
                        } else if (peek.id() == HTokenId.LEFT_CURLY_BRACKET) {
                            pushBack(peek);
                            break;
                        }
                    }
                } else {
                    endToken = cond.endToken();
                }
            } else {
                log().error("X142", "while statement", "expected '(' for while condition", peek());
                cond = parseExpression();
                if (cond == null) {
                    log().error("X143", "while statement", "expected while condition", peek());
                } else {
                    endToken = cond.endToken();
                }
            }
            HNode block = null;
            JToken t = peek().copy();
            if (t.id() == HTokenId.SEMICOLON) {
                //this is an empty while;
                //block = new HNBlock();
            } else {
                block = parseExpressionOrStatement(asExpr,false);
                if (block == null) {
//                    block = new HNBlock();
                    log().error("X144", "while statement", "expected while block", peek());
                } else {
                    endToken = block.endToken();
                }
            }
            HNWhile i = new HNWhile(startToken);
            i.setExpr(cond);
            i.setBlock(block);
            i.setEndToken(endToken);
            return i;
        }
        return null;
    }

    //    private void logError(String message) {
//        log().add(JSourceMessage.error(null,message,peek()));
//    }
    protected HNode prepareForInitNode(JMessageList err) {
        return parseDeclareAssign(null, true, err);
//        HNode c = parseDeclareAssign(null, true);
//
//        HNode leftNode = null;
//        HNode rightNode = null;
//        JToken assignOperator = null;
//        if (c instanceof HNDeclareIdentifier) {
//            return c;
//        }
//        if (c instanceof HNAssign) {
//            HNAssign a = (HNAssign) c;
//            leftNode = a.getLeft();
//            rightNode = a.getRight();
//            assignOperator = a.getOp();
//        } else if (c instanceof HNOpBinaryCall
//                && (((HNOpBinaryCall) c).getName().equals(":") || ((HNOpBinaryCall) c).getName().equals("="))) {
//
//            HNOpBinaryCall bc = (HNOpBinaryCall) c;
//            assignOperator = bc.getNameToken();
//            leftNode = bc.getLeft();
//            rightNode = bc.getRight();
//        } else {
//            log().error("X145", "for statement", "expected ':' or '=' assignment var initializer", c.startToken());
//        }
//        if (assignOperator == null) {
//            assignOperator = HNodeUtils.createToken("=");
//        }
//        if (leftNode != null && rightNode != null) {
//            if (leftNode instanceof HNIdentifier) {
//                return new HNDeclareIdentifier(
//                        HNodeUtils.toDeclareTokenIdentifier((HNIdentifier) leftNode),
//                        rightNode, (HNTypeToken) null, assignOperator, c.startToken(), rightNode.endToken());
//            } else if (leftNode instanceof HNTuple) {
//                return new HNDeclareIdentifier(
//                        HNodeUtils.toDeclareTupleItem((HNTuple) leftNode, log()),
//                        rightNode, (HNTypeToken) null, assignOperator, c.startToken(), rightNode.endToken());
////            } else if (leftNode instanceof HNPars) { //DEPRECATED
////                //check or tuples
////                HNode[] items = ((HNPars) leftNode).getItems();
////                boolean ok2 = items.length > 0;
////                if (!ok2) {
////                    log().error("X146", "for statement: expected '" + assignOperator.image + "' assignment var initializer but found empty tuple", c.startToken());
////                }
////                List<HNDeclareIdentifier> identifiers = new ArrayList<>();
////                for (int i = 0; i < items.length; i++) {
////                    HNode item = items[i];
////                    if (item instanceof HNIdentifier) {
////                        HNIdentifier hid = (HNIdentifier) item;
////                        HNDeclareIdentifier did = new HNDeclareIdentifier(new JToken[]{hid.startToken().copy()}, null, (HNTypeToken) null, assignOperator, item.startToken(), item.endToken());
////                        identifiers.add(did);
////                    } else {
////                        log().error("X147", "for statement: expected '" + assignOperator.image + "' assignment var initializer for expression tuple", c.startToken());
////                        ok2 = false;
////                    }
////                }
////                if (ok2) {
////                    HNDeclareTuple tt = new HNDeclareTuple(
////                            identifiers.toArray(new HNDeclareIdentifier[0]),
////                            rightNode,
////                            assignOperator,
////                            c.startToken(),
////                            rightNode.endToken());
////                    tt.setAssignOperator(assignOperator);
////                    return tt;
////                }
//            } else {
//                log().error("X148", "for statement", "expected '" + assignOperator.image + "' assignment var initializer", c.startToken());
//            }
//        }
//        return null;
    }

    protected HNode parseFor(boolean asExpr) {
        JToken startNode = peek().copy();
        JToken endToken = startNode;
        if (startNode.id() == HTokenId.KEYWORD_FOR) {
            endToken = next();
            JToken p = next();
            if (p.isImage("(")) {
                endToken = p;
                //okkay
            } else {
                pushBack(p);
                log().error("X149", "for statement", "expected '(' after 'for'", peek());
            }
            HNFor hf = new HNFor(startNode);
            int state = 1;
            boolean wasSep = true;
            while (state > 0) {
                switch (state) {
                    case 1: {
                        switch (peek().image) {
                            case ",": {
                                endToken = next();
                                if (wasSep) {
                                    log().error("X145", "for statement", "unexpected ','", peek());
                                }
                                wasSep = true;
                                break;
                            }
                            case ";": {
                                endToken = next();
                                wasSep = true;
                                state = 2;
                                break;
                            }
                            case ")": {
                                endToken = next();
                                state = -1;
                                break;
                            }
                            case ""/*JToken.TT_EOF*/: {
                                log().error("X146", "for statement", "expected ')'", peek());
                                state = -1;
                                break;
                            }
                            default: {
                                if (!wasSep) {
                                    log().error("X147", "for statement", "expected ','", peek());
                                }
                                wasSep = false;
                                DefaultJMessageList err2 = errorList();
                                HNode e = prepareForInitNode(err2);
                                log().addAll(err2);
                                if (e == null) {
                                    wasSep = true;
                                    log().error("X148", "for statement", "expected expression", peek());
                                    skip();
                                } else {
                                    endToken = e.endToken();
                                    hf.addInit(e);
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case 2: {
                        switch (peek().image) {
                            case ";": {
                                wasSep = true;
                                state = 3;
                                endToken = next();
                                break;
                            }
                            case ")": {
                                endToken = next();
                                state = -1;
                                break;
                            }
                            case ""/*JToken.TT_EOF*/: {
                                log().error("X146", "for statement", "expected ')'", peek());
                                state = -1;
                                break;
                            }
                            default: {
                                if (!wasSep) {
                                    log().error("X147", "for statement", "expected ','", peek());
                                }
                                wasSep = false;
                                HNode e = parseExpression();
                                if (e == null) {
                                    wasSep = true;
                                    log().error("X149", "for statement", "expected expression", peek());
                                    skip();
                                } else {
                                    endToken = e.endToken();
                                    hf.setFilter(e);
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case 3: {
                        switch (peek().image) {
                            case ",": {
                                endToken = next();
                                if (wasSep) {
                                    log().error("X145", "for statement", "unexpected ','", peek());
                                }
                                wasSep = true;
                                break;
                            }
                            case ")": {
                                endToken = next();
                                state = -1;
                                break;
                            }
                            case ";":
                            case ""/*JToken.TT_EOF*/: {
                                log().error("X146", "for statement", "expected ')'", peek());
                                state = -1;
                                break;
                            }
                            default: {
                                if (!wasSep) {
                                    log().error("X147", "for statement", "expected ','", peek());
                                }
                                wasSep = false;
                                HNode e = parseExpression();
                                if (e == null) {
                                    wasSep = true;
                                    log().error("X150", "for statement", "expected expression", peek());
                                    skip();
                                } else {
                                    endToken = e.endToken();
                                    hf.addInc(e);
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            if (peek().id() == HTokenId.MINUS_GT) {
                LambdaBody lambdaBody = parseFunctionArrowBody();
                endToken = lambdaBody.getEndToken();
                hf.setBody(lambdaBody.body);
                hf.setExpressionMode(true);
            } else {
                HNode ok = parseStatement(log());
                if (ok == null) {
                    log().error("X152", "for statement", "expected for statement body", peek());
                } else {
                    endToken = (ok.endToken());
                }
                hf.setBody(ok);
                hf.setExpressionMode(false);
            }
            int countIterable = 0;
            int countNonIterable = 0;
            for (HNode c : hf.getInitExprs()) {
                JToken o = null;
                if (c instanceof HNDeclareIdentifier) {
                    o = ((HNDeclareIdentifier) c).getAssignOperator();
                }
                if (o.image.equals("=")) {
                    if (countIterable > 0) {
                        log().error("X153", "for statement", "cannot mix simple and iterable for statement constructs", c.startToken());
                    }
                    countNonIterable++;
                } else {
                    if (countNonIterable > 0) {
                        log().error("X153", "for statement", "cannot mix simple and iterable for statement constructs", c.startToken());
                    }
                    countIterable++;
                }
            }
            hf.setIteratorType(countIterable > 0);
            hf.setEndToken(endToken);
            return hf;
        }
        return null;
    }

    protected HNode parsePackageNode() {
        JToken n = next().copy();
        if (n.id() == HTokenId.KEYWORD_PACKAGE) {
            JToken endToken = n;
            HNMetaPackageId id = parseMetaPackageId();
            if (id != null) {
                endToken = id.endToken();
            }
            HNDeclareMetaPackage pp = new HNDeclareMetaPackage(n);
            pp.setModuleId(id);
            n = peek();
            if (n.id() == HTokenId.LEFT_CURLY_BRACKET) {
                endToken = n;
                metaParsingMode = true;
                try {
                    pp.setBody((HNBlock) parseBraces(HNBlock.BlocType.PACKAGE_BODY));
                    if (pp.getBody() != null) {
                        endToken = (pp.getBody().endToken());
                    }
                } finally {
                    metaParsingMode = false;
                }
            } else if (n.id() == HTokenId.SEMICOLON) {
                endToken = (next());
            } else {
                log().error("X154", "package definition", "expected ';' or '{'", n);
            }
            pp.setEndToken(endToken);
            return pp;
        }
        pushBack(n);
        return null;
    }

    private JToken parseBreakOrContinueLabel(String stmtType) {
        JToken p = peek();
        if (p.id() == JTokenId.NUMBER_INT) {
            next();
            int ival = 0;
            try {
                ival = (int) HNumberEvaluator.H_NUMBER.eval(p.id(), p.image, p.sval, "int");
                if (ival == 0) {
                    log().error("X155", stmtType + " statement", stmtType + " label should be positive number (excluding zero which you can apply without any label)", p);
                    return p;
                } else if (ival < 0) {
                    log().error("X156", stmtType + " statement", stmtType + " label should be positive number", p);
                    return p;
                }
                return p;
            } catch (Exception ex) {
                log().error("X157", stmtType + " statement", "invalid " + stmtType + " label : " + p.image, p);
                return p;
            }
        } else {
            return null;
        }
    }

    protected HNode parseBreak() {
        JToken n = next().copy();
        if (n.id() == HTokenId.KEYWORD_BREAK) {
            JToken ival = parseBreakOrContinueLabel("break");
            return new HNBreak(ival, n, ival == null ? n : ival);
        }
        return null;
    }

    protected HNode parseContinue() {
        JToken n = next().copy();
        if (n.id() == HTokenId.KEYWORD_CONTINUE) {
            JToken ival = parseBreakOrContinueLabel("continue");
            return new HNContinue(ival, n, ival == null ? n : ival);
        }
        return null;
    }

    protected HNode parseReturn() {
        JToken n = next();
        JToken startToken = n.copy();
        if (n.id() == HTokenId.KEYWORD_RETURN) {
            JToken p = peek();
            if (p.id() == HTokenId.SEMICOLON) {
                return new HNReturn(null, startToken, startToken);
            } else {
                HNode e = parseExpression();
                if (e == null) {
                    log().error("X158", "return statement", "expected expression", peek());
                }
                return new HNReturn(e, startToken, e == null ? startToken : e.endToken());
            }
        }
        return null;
    }

    protected HNode parseImportNode() {
        JToken n = next();
        JToken startToken = n.copy();
        JToken endToken = n.copy();
        if (n.id() == HTokenId.KEYWORD_IMPORT) {
            endToken = n = peek();
            if (n.id() == HTokenId.KEYWORD_PACKAGE) {
                if (!metaParsingMode) {
                    log().error("X159", "import package statement", "import package is not allowed outside package declaration", n);
                }
                endToken = next();
                HNode id = parseExpression();
                if (id != null) {
                    endToken = id.endToken();
                }
                HNMetaImportPackage node = new HNMetaImportPackage(startToken);
                node.setImportedPackageNode(id);
                JToken p = next();
                if (p.id() == HTokenId.SEMICOLON) {
                    pushBack(p);
                    node.setEndToken(endToken);
                    return node;
                }
                if (p.id() == HTokenId.KEYWORD_FOR) {
                    endToken = p;
                    p = next();
                    if (p.isImage("optional")) {
                        endToken = p;
                        node.setOptional(true);
                        p = next();
                    }
                    switch (p.image) {
                        case "compile":
                        case "build":
                        case "test":
                        case "api":
                        case "runtime": {
                            endToken = p;
                            node.setScope(p.image);
                            break;
                        }
                        default: {
                            log().error("X160", "import package statement", "expected compile,build,test,api,runtime", p);
                            pushBack(p);
                            node.setEndToken(endToken);
                            return node;
                        }
                    }
                }
                p = peek();
                if (p.id() == HTokenId.KEYWORD_BREAK) {
                    endToken = next();
                    while (true) {
                        HNode i = parseExpression();
                        if (i != null) {
                            endToken = i.endToken();
                            node.addExclusion(i);
                        } else {
                            node.setEndToken(endToken);
                            return node;
                        }
                        p = peek();
                        if (p.id() == HTokenId.COMMA) {
                            endToken = next();
                            //okkay, consume it
                        }
                        if (p.id() == HTokenId.SEMICOLON) {
                            break;
                            //okkay, consume it
                        }
                    }
                }
                node.setEndToken(endToken);
                return node;
            } else {
                JToken importStartToken = peek().copy();
                StringBuilder sb = new StringBuilder();
                endToken = importStartToken;
                while (true) {
                    n = next();
                    if (n.isImage(".")) {
                        if (sb.length() == 0 || sb.charAt(sb.length() - 1) == '.' || sb.charAt(sb.length() - 1) == '*') {
                            log().error("X161", "import package statement", "invalid package to import " + sb, importStartToken);
                        } else {
                            sb.append(".");
                        }
                        endToken = n;
                    } else if (n.isIdentifier()) {
                        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '.') {
                            log().error("X161", "import package statement", "invalid package to import " + sb, importStartToken);
                        } else {
                            sb.append(n.sval);
                        }
                        endToken = n;
                    } else if (n.isImage("*")) {
                        String sbs = sb.toString();
                        if (sbs.isEmpty() || sbs.endsWith(".")) {
                            sb.append(n.sval);
                            endToken = n;
                            break;
                        } else {
                            endToken = n;
                            log().error("X161", "import package statement", "invalid package to import " + sb, importStartToken);
                        }
                    } else if (n.isImage("**")) {
                        String sbs = sb.toString();
                        if (sbs.isEmpty() || sbs.endsWith(".")) {
                            sb.append(n.sval);
                            endToken = n.copy();
                            break;
                        } else {
                            endToken = n;
                            log().error("X161", "import package statement", "invalid package to import " + sb, importStartToken);
                        }
                    } else if (n.isKeyword()) {
                        log().error("X161", "import package statement", "invalid package to import " + sb, importStartToken);
                        sb.append(n.sval);
                        endToken = n.copy();
                    } else {
                        pushBack(n);
                        break;
                    }
                }
                HNImport hnImport = new HNImport(sb.toString(), startToken, endToken);
                JListWithSeparators<HNode> nextStatements = parseStatements();
                if (nextStatements.size() > 0) {
                    hnImport.setEndToken(endToken);
                    if (nextStatements.getEndToken() != null) {
                        endToken = nextStatements.getEndToken();
                    }
                    List<HNode> stmts = new ArrayList<>();
                    stmts.add(hnImport);
                    stmts.addAll(nextStatements.getItems());

                    HNBlock b = new HNBlock(HNBlock.BlocType.IMPORT_BLOC,
                            stmts.toArray(new HNode[0]),
                            startToken,
                            endToken);
                    b.getImports().add(hnImport.getJImportInfo());
                    return b;
                }
                hnImport.setEndToken(endToken);
                return hnImport;
            }
        } else {
            pushBack(n);
            return null;
        }
    }

    protected HNMetaPackageId parseMetaPackageId() {
        final int STATE_GROUP = 0;
        final int STATE_ARTIFACT = 1;
        final int STATE_VERSION = 2;
        int state = STATE_GROUP;
        HNMetaPackageGroup group = null;
        HNMetaPackageArtifact artifact = null;
        HNMetaPackageVersion version = null;
        JToken lastDot = null;
        JToken colon = null;
        JToken sharp = null;
        JToken startToken = null;
        JToken endToken = null;
        while (true) {
            JToken t = next();
            if (state == STATE_GROUP) {
                if (t.isKeyword() || t.isIdentifier() || t.isString() || t.isImage("-") || t.isImage("_")) {
                    if (group == null) {
                        group = new HNMetaPackageGroup();
                    }
                    group.addToken(t);
                    lastDot = null;
                    if (startToken == null) {
                        startToken = t;
                    }
                    endToken = t;
                } else if (t.isImage(".")) {
                    if (lastDot != null) {
                        log().error("X162", "package statement", "unexpected '.'", t);
                    } else {
                        if (group != null) {
                            group.addToken(t);
                        }
                    }
                    lastDot = t;
                    if (startToken == null) {
                        startToken = t;
                    }
                    endToken = t;
                } else if (t.id() == HTokenId.COLON) {
                    state = STATE_ARTIFACT;
                    colon = t;
                    if (startToken == null) {
                        startToken = t;
                    }
                    endToken = t;
                } else if (t.isImage("#")) {
                    log().error("X162", "package statement", "unexpected Token", t);
                    sharp = t;
                    state = STATE_VERSION;
                    if (startToken == null) {
                        startToken = t;
                    }
                    endToken = t;
                    break;
                } else if (t.id() == HTokenId.LEFT_CURLY_BRACKET || t.id() == HTokenId.SEMICOLON) {
                    pushBack(t);
                    break;
                } else if (t.isEOF()) {
                    break;
                } else {
                    pushBack(t);
                    break;
                }
            } else if (state == STATE_ARTIFACT) {
                if (t.isKeyword() || t.isIdentifier() || t.isString() || t.id() == HTokenId.MINUS || t.id() == HTokenId.KEYWORD__) {
                    if (artifact == null) {
                        artifact = new HNMetaPackageArtifact();
                    }
                    artifact.addToken(t);
                    if (startToken == null) {
                        startToken = t;
                    }
                    endToken = t;
                } else if (t.isImage("#")) {
                    sharp = t;
                    state = STATE_VERSION;
                    if (startToken == null) {
                        startToken = t;
                    }
                    endToken = t;
                } else if (t.id() == HTokenId.LEFT_CURLY_BRACKET || t.id() == HTokenId.SEMICOLON) {
                    pushBack(t);
                    break;
                } else if (t.isEOF()) {
                    break;
                } else {
                    pushBack(t);
                    break;
                }
            } else if (state == STATE_VERSION) {
                if (t.isKeyword() || t.isIdentifier()
                        || t.isString()
                        || t.isNumber()
                        || t.id() == HTokenId.MINUS
                        || t.id() == HTokenId.KEYWORD__
                        || t.id() == HTokenId.DOT) {
                    t.image = t.sval;
                    if (version == null) {
                        version = new HNMetaPackageVersion();
                    }
                    version.addToken(t);
                    if (startToken == null) {
                        startToken = t;
                    }
                    endToken = t;
                } else if (t.id() == HTokenId.LEFT_CURLY_BRACKET || t.id() == HTokenId.SEMICOLON) {
                    pushBack(t);
                    break;
                } else if (t.isEOF()) {
                    break;
                } else {
                    pushBack(t);
                    break;
                }
            }
        }
        if (startToken == null) {
            return null;
        }
        HNMetaPackageId hnMetaPackageId = new HNMetaPackageId();
        if (group != null) {
            group.setElement(new HNElementMetaPackageGroup(group.toString()));
        }
        if (artifact != null) {
            artifact.setElement(new HNElementMetaPackageArtifact(artifact.toString()));
        }
        if (version != null) {
            version.setElement(new HNElementMetaPackageVersion(version.toString()));
        }
        hnMetaPackageId.setGroup(group);
        hnMetaPackageId.setArtifact(artifact);
        hnMetaPackageId.setVersion(version);
        hnMetaPackageId.setColonToken(colon);
        hnMetaPackageId.setSharpToken(sharp);
        hnMetaPackageId.setStartToken(startToken);
        hnMetaPackageId.setEndToken(endToken);
        return hnMetaPackageId;

    }

    protected HNode parseExpressionSimple() {
        return parseExpression(simpleExpressionOptions);
    }

    private List<HNode> splitByBinaryOperator(HNode n, String op) {
        if (n == null) {
            return new ArrayList<>();
        }
        if (n instanceof HNOpBinaryCall && ((HNOpBinaryCall) n).getName().equals(op)) {
            List<HNode> a = splitByBinaryOperator(((HNOpBinaryCall) n).getLeft(), op);
            List<HNode> b = splitByBinaryOperator(((HNOpBinaryCall) n).getRight(), op);
            a.addAll(b);
            return a;
        }
        return new ArrayList<>(Arrays.asList(n));
    }

    protected HNode _parseSwitch_parseCaseBody(ParseSwitchContext psc, String condName) {
        JToken p = peek();
//        String condName = psc.defaultVisited ? "default" : psc.caseVisited ? "case" : psc.ifVisited ? "if" : psc.isVisited ? "is" : "case";
        HNode stmt = null;
        if (p.id() == HTokenId.COLON) {
            psc.endToken = next();
//            if (psc.functionalSwitch == null) {
//                psc.functionalSwitch = false;
//            } else if (psc.functionalSwitch) {
//                log().error("X172", "switch statement", "expected '->' after " + condName, p);
//            }
            if (psc.op == null) {
                psc.op = psc.endToken;
            }
//        } else if (p.id() == HTokenId.MINUS_GT) {
//            if (psc.op == null) {
//                psc.op = p;
//            }
//            LambdaBody lambdaBody = parseFunctionArrowBody();
//            if (lambdaBody.isExpr()) {
//                //require semicolon
//                requireSemiColumn("case", true);
//            }
//            psc.endToken = lambdaBody.getEndToken();
//            stmt = lambdaBody.body;
//            if (psc.functionalSwitch == null) {
//                psc.functionalSwitch = true;
//            } else if (!psc.functionalSwitch) {
//                log().error("X172", "switch statement", "expected ':' after " + condName, p);
//            }
        } else {
            log().error("X172", "switch statement", "expected ':' after " + condName, p);
//            if (psc.functionalSwitch == null) {
//                log().error("X172", "switch statement", "expected ':' after " + condName, p);
//            } else if (psc.functionalSwitch) {
//                log().error("X172", "switch statement", "expected ':' after " + condName, p);
//            } else {
//                log().error("X172", "switch statement", "expected '->' after " + condName, p);
//            }
//            if (psc.functionalSwitch != null && psc.functionalSwitch) {
//                stmt = parseExpression();
//                if (stmt == null) {
//                    log().error("X173", "switch statement", "expected expression", peek());
//                } else {
//                    JToken comma = peek();
//                    if (comma.id() == HTokenId.SEMICOLON) {
//                        psc.endToken = next();
//                    } else {
//                        log().error("X173", "switch statement", "expected ';'", peek());
//                    }
//                }
//            } else {
//                stmt = parseStatement(log());
//            }
        }
        stmt = parseExpressionOrStatement(psc.functionalSwitch,true);
        if (stmt == null) {
            log().error("X173", "switch statement", "expected switch " + condName + " " + (psc.functionalSwitch ? "expression" : "statement"), peek());
        } else {
            psc.endToken = stmt.endToken();
        }
        return stmt;
    }

    public HNode parseExpressionOrStatement(boolean asExpr,boolean alwaysTerminateWithSemicolon) {
        if(asExpr){
            HNode e = parseExpression();
            if(e!=null){
                if(alwaysTerminateWithSemicolon) {
                    if (peek().id() == HTokenId.SEMICOLON) {
                        next();
                    }else {
                        log().error("X000", null, "expected ';'", peek());
                    }
                }
                return e;
            }
        }
        return parseStatement(log());
    }

    protected HNode parseTryCatch(boolean asExpr) {
        ParseTryCatchContext psc = new ParseTryCatchContext();
        psc.startToken = peek().copy();
        psc.endToken = psc.startToken;
        if (psc.startToken.id() == HTokenId.KEYWORD_TRY) {
            skip();
            JToken p = peek();
            if (p.isImage("(")) {
                psc.separators.add(psc.endToken = next());
                HNDeclareIdentifier a = (HNDeclareIdentifier) parseDeclareAssign(true, false, log());
                psc.declaredResource = a;
                psc.endToken = JeepUtils.coalesce(psc.endToken, a.endToken());
                p = peek();
                if (p.isImage(")")) {
                    psc.separators.add(psc.endToken = next());
                }
                //read resource
            }
            if (p.id() == HTokenId.MINUS_GT) {
                //try -> body
                LambdaBody lambdaBody = parseFunctionArrowBody();
                psc.endToken = lambdaBody.getEndToken();
                psc.fct = true;
                psc.fctOp = lambdaBody.op;
                psc.block = lambdaBody.body;
                //read resource
            } else if (p.isImage("{")) {
                psc.endToken = next();
                psc.block = parseBraces(HNBlock.BlocType.LOCAL_BLOC);
            } else {
                log().error("X000", "try statement", "expected '{' or '->'", peek());
            }
            while (true) {
                p = peek();
                if (p.id() == HTokenId.KEYWORD_CATCH) {
                    JToken catchStart = next();
                    JToken catchEnd = catchStart;
                    List<JToken> catchSeparators = new ArrayList<>();
                    HNDeclareTokenIdentifier ti = null;
                    JListWithSeparators<HNTypeToken> catchTypes = null;
                    p = peek();
                    if (p.id() == HTokenId.LEFT_PARENTHESIS) {
                        catchSeparators.add(catchEnd = next());
                        catchTypes = parseGroupedList("catch", "exception type",
                                () -> parseTypeName(),
                                null, "|", null, null
                        );
                        catchSeparators.addAll(catchTypes.getSeparatorTokens());
                        catchEnd = catchTypes.getEndToken() != null ? catchTypes.getEndToken() : catchStart;
                        p = peek();
                        if (p.id() == HTokenId.IDENTIFIER) {
                            ti = new HNDeclareTokenIdentifier(next());
                        }
                        if (p.id() == HTokenId.RIGHT_PARENTHESIS) {
                            catchSeparators.add(next());
                        } else {
                            log().error("X000", "catch", "expected ')'", peek());
                        }
                    }
                    p = peek();
                    HNode block = null;
                    boolean catchExpr = false;
                    if (p.id() == HTokenId.LEFT_CURLY_BRACKET) {
                        if (psc.fct) {
                            log().error("X000", "catch", "try is an expression, expected '->'", peek());
                        }
                        block = parseBraces(HNBlock.BlocType.LOCAL_BLOC);
                        if (block == null) {
                            log().error("X000", "catch", "expected catch body", peek());
                        }
                    } else if (p.id() == HTokenId.MINUS_GT) {
                        if (!psc.fct) {
                            log().error("X000", "catch", "try is not an expression, expected '{'", peek());
                        }
                        catchExpr = true;
                        catchSeparators.add(next());
                        block = parseExpression();
                        if (block == null) {
                            log().error("X000", "catch", "expected catch expression", peek());
                        }
                    } else {
                        break;
                    }
                    HNTryCatch.CatchBranch c = new HNTryCatch.CatchBranch(
                            catchTypes == null ? new HNTypeToken[0] : catchTypes.getItems().toArray(new HNTypeToken[0]),
                            ti,
                            block,
                            catchExpr,
                            catchStart,
                            catchEnd,
                            catchSeparators.toArray(new JToken[0])
                    );
                    psc.catches.add(c);
                } else {
                    break;
                }
            }
            if (p.id() == HTokenId.KEYWORD_FINALLY) {
                psc.endToken = next();
                p = peek();
                if (p.id() == HTokenId.LEFT_CURLY_BRACKET) {
                    if (psc.fct) {
                        log().error("X000", "catch", "try is an expression, expected '->'", peek());
                    }
                    psc.endToken = next();
                    psc.finallyBlock = parseBraces(HNBlock.BlocType.LOCAL_BLOC);
                    psc.endToken = psc.finallyBlock.endToken();
                } else if (p.id() == HTokenId.MINUS_GT) {
                    if (!psc.fct) {
                        log().error("X000", "catch", "try is not an expression, expected '{'", peek());
                    }
                    psc.endToken = next();
                    psc.finallyBlock = parseBraces(HNBlock.BlocType.LOCAL_BLOC);
                    psc.endToken = psc.finallyBlock.endToken();
                } else {
                    log().error("X000", "try statement", "expected '{'", peek());
                }
            }
            HNTryCatch hnTryCatch = new HNTryCatch(psc.startToken);
            hnTryCatch.setEndToken(psc.endToken);
            hnTryCatch.setFinallyBranch(psc.finallyBlock);
            for (HNTryCatch.CatchBranch o : psc.catches) {
                hnTryCatch.addCatch(o);
            }
            return hnTryCatch;
        }
        return null;
    }

    protected HNode parseSwitch(boolean asExpr) {
        ParseSwitchContext psc = new ParseSwitchContext();
        JToken startToken = peek().copy();
        psc.endToken = startToken;
        if (startToken.id() == HTokenId.KEYWORD_SWITCH) {
            skip();
            JToken p = peek();
//            if (!p.isImage("(")) {
//                log().error("X163", "switch statement", "expected '('", peek());
//            }
            HNSwitch jNodeHSwitch = new HNSwitch(startToken);
            HNode expr = parseExpression(noBracesExpressionOptions);
            if (expr == null) {
                log().error("X164", "switch statement", "expected switch discriminator", peek());
                JToken f = p.copy();
                f.setError(1, "expected switch discriminator");
                f.sval = "false";
                f.def = tokenizer().getFirstTokenDefinition(x -> x.idName.equals("false"));
                expr = new HNPars(new HNode[]{new HNLiteral(false, f)}, peek(), new JToken[0], peek());
                expr.setStartToken(peek());
            } else {
                expr=HNodeUtils.assignToDeclare(expr,true);
                psc.endToken = expr.endToken();
            }
            jNodeHSwitch.setExpr(expr);
            p = peek();
            if (!p.isImage("{")) {
                log().error("X167", "switch statement", "expected { after switch keyword", peek());
            } else {
                psc.endToken = next();
            }
            psc.functionalSwitch = asExpr;

            while (true) {
                p = peek();
                if (p.isEOF()) {
                    log().error("X168", "switch statement", "expected closing '}'", peek());
                    break;
                } else if (p.id() == HTokenId.KEYWORD_CASE) {
                    if (psc.defaultVisited) {
                        log().error("X169", "switch statement", "unexpected switch case after default", peek());
                    }
                    if (psc.isVisited || psc.ifVisited) {
                        log().error("X170", "switch statement", "cannot  merge case with if or is constructs", peek());
                    }
                    psc.caseVisited = true;
                    psc.endToken = next();
                    List<HNode> matches = splitByBinaryOperator(parseExpressionSimple(), "|");
                    if (matches.size() == 0) {
                        log().error("X171", "switch statement", "expected case expression", peek());
                    } else {
                        psc.endToken = matches.get(matches.size() - 1).endToken();
                    }
                    HNode stmt = _parseSwitch_parseCaseBody(psc, "case");
                    jNodeHSwitch.add(new HNSwitch.SwitchCase(matches, psc.op, stmt, p, psc.endToken));
                } else if (p.id() == HTokenId.KEYWORD_IF) {
                    if (psc.defaultVisited) {
                        log().error("X174", "switch statement", "unexpected switch case after default", peek());
                    }
                    if (psc.isVisited || psc.caseVisited) {
                        log().error("X175", "switch statement", "cannot  merge case with if or is constructs", peek());
                    }
                    psc.ifVisited = true;
                    JToken ss = p.copy();
                    psc.endToken = next();
                    HNode m = parseExpression();
                    if (m == null) {
                        log().error("X176", "switch statement", "expected case expression", peek());
                    }
                    HNode stmt = _parseSwitch_parseCaseBody(psc, "case");
                    jNodeHSwitch.add(new HNSwitch.SwitchIf(m, psc.op, stmt, ss, psc.endToken));
                } else if (p.isImage("is")) {
                    if (psc.defaultVisited) {
                        log().error("X179", "switch statement", "unexpected switch case after default", peek());
                    }
                    if (psc.caseVisited || psc.ifVisited) {
                        log().error("X180", "switch statement", "cannot  merge case with if or is constructs", peek());
                    }
                    psc.isVisited = true;
                    JToken ss = p.copy();
                    psc.endToken = next();
                    List<HNTypeToken> typeNames = new ArrayList<>();

                    while (true) {
                        JToken vn = peek();
                        if (vn.id() == HTokenId.KEYWORD_NULL) {
                            psc.endToken = next();
                            typeNames.add(createNullTypeToken(psc.endToken));
                        } else {
                            HNTypeToken t = parseTypeName();
                            if (t == null) {
                                log().error("X181", "switch statement", "expected type name", peek());
                                break;
                            } else {
                                psc.endToken = t.endToken();
                                if (t.getTypename().varsCount() > 0) {
                                    log().error("X182", "switch statement", "cannot switch on generic types", peek());
//                                    t = t.rawType();
                                }
                                typeNames.add(t);
                            }
                        }
                        vn = peek();
                        if (vn.isEOF()) {
                            log().error("X183", "switch statement", "expected type name", peek());
                            break;
                        } else if (vn.isOperator("|")) {
                            psc.endToken = next();
                        } else {
                            break;
                        }
                    }
                    if (typeNames.isEmpty()) {
                        log().error("X184", "switch statement", "expected type name", peek());
                    }
                    JToken vn = peek();
                    JToken varName = null;
                    if (vn.isIdentifier()) {
                        psc.endToken = varName = next();
                    }
                    HNode stmt = _parseSwitch_parseCaseBody(psc, "is");
                    jNodeHSwitch.add(new HNSwitch.SwitchIs(typeNames,
                            varName == null ? null : new HNDeclareTokenIdentifier(varName),
                            psc.op, stmt, ss, psc.endToken));
                } else if (p.id() == HTokenId.KEYWORD_DEFAULT) {
                    next();
                    if (psc.defaultVisited) {
                        log().error("X187", "switch statement", "default already matched", peek());
                    }
                    psc.defaultVisited = true;
                    HNode stmt = _parseSwitch_parseCaseBody(psc, "default");
                    jNodeHSwitch.setElse(stmt);
                    p = peek();
                    if (p.id() == HTokenId.RIGHT_CURLY_BRACKET || p.id() == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
                        psc.endToken = next();
                        break;
                    } else {
                        log().error("X189", "switch statement", "expect switch '}'", peek());
                        break;
                    }
                } else if (p.id() == HTokenId.RIGHT_CURLY_BRACKET || p.id() == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
                    psc.endToken = next();
                    if (jNodeHSwitch.getCases().isEmpty()) {
                        log().error("X191", "switch statement", "expect case expression", peek());
                    }
                    break;
                } else {
                    log().error("X192", "switch statement", "expect case or default keywords", peek());
                    skip();
                }
            }
            jNodeHSwitch.setEndToken(psc.endToken);
            jNodeHSwitch.setExpressionMode(asExpr);
            return jNodeHSwitch;
        }
        return null;
    }

    protected HNDeclareType currentClassDeclarationNode() {
        int len = getDeclarationContexts().size();
        for (int i = len - 1; i >= 0; i--) {
            HNode v = getDeclarationContexts().elementAt(i);
            if (v instanceof HNDeclareType) {
                return (HNDeclareType) v;
            }
        }
        return null;
    }

    private static class ParseDeclareIdentifierContext {
        HNDeclareToken varIds = null;
        int modifiers;
        JTypeNameAndInit tt;
        JToken varVal;
        HLDeclarationOptions options;
        JToken startToken;
        JToken endToken;
    }

    public static class LambdaBody {
        public JToken op;
        public HNode body;
        public boolean expr;

        public LambdaBody(JToken op, HNode body, boolean expr) {
            this.op = op;
            this.body = body;
            this.expr = expr;
        }

        public boolean isExpr() {
            return expr;
        }

        public JToken getEndToken() {
            if (body != null) {
                return body.endToken();
            }
            if (op != null) {
                return op;
            }
            return null;
        }
    }

    private static class ParseSwitchContext {

        boolean defaultVisited = false;
        boolean caseVisited = false;
        boolean ifVisited = false;
        boolean isVisited = false;
        boolean functionalSwitch = false;
        JToken endToken = null;
        JToken op = null;
    }

    public static class JTypeNameAndInit {

        HNTypeToken typeToken;
        JTypeName type;
        HNode[] inits;

        public JTypeNameAndInit(HNTypeToken typeToken, HNode[] inits) {
            this.typeToken = typeToken;
            this.type = (typeToken.getTypenameOrVar() instanceof JTypeName) ? (JTypeName) typeToken.getTypenameOrVar() : null;
            this.inits = inits;
        }
    }

    private class ParseTryCatchContext {
        public HNode finallyBlock;
        public List<HNTryCatch.CatchBranch> catches = new ArrayList<>();
        public List<JToken> separators = new ArrayList<>();
        public HNDeclareIdentifier declaredResource;
        JToken startToken;
        JToken endToken;
        JToken fctOp;
        HNode block;
        boolean fct;
    }
}
