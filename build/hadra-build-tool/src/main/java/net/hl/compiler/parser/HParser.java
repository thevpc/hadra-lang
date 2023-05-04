package net.hl.compiler.parser;

import net.hl.compiler.ast.*;
import net.hl.compiler.core.HTokenId;
import net.hl.compiler.core.HTokenIdOffsets;
import net.hl.compiler.core.elements.HNElementMetaPackageArtifact;
import net.hl.compiler.core.elements.HNElementMetaPackageGroup;
import net.hl.compiler.core.elements.HNElementMetaPackageVersion;
import net.hl.compiler.stages.runtime.HNumberEvaluator;
import net.hl.compiler.utils.HNodeUtils;
import net.hl.compiler.utils.HTokenUtils;
import net.hl.compiler.utils.HSharedUtils;
import net.thevpc.jeep.*;
import net.thevpc.jeep.core.DefaultJListWithSeparators;
import net.thevpc.jeep.core.DefaultJParser;
import net.thevpc.jeep.core.JExpressionUnaryOptions;
import net.thevpc.jeep.core.nodes.JNodeTokens;
import net.thevpc.jeep.core.tokens.JTokenDef;
import net.thevpc.jeep.core.types.DefaultTypeName;
import net.thevpc.jeep.core.types.JTypeNameBounded;
import net.thevpc.jeep.impl.functions.JNameSignature;
import net.thevpc.jeep.impl.tokens.JTokenId;
import net.thevpc.jeep.log.JMessageList;
import net.thevpc.jeep.log.JSourceMessage;
import net.thevpc.jeep.log.impl.DefaultJMessageList;
import net.thevpc.jeep.util.JTokenUtils;
import net.thevpc.jeep.util.JeepUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HParser extends DefaultJParser<HNode, HExpressionOptions> {

    //    private static final HashSet<String> unacceptableWordSuffixedForNumbers = new HashSet<>();
//
//    static {
//        unacceptableWordSuffixedForNumbers.add("(");
//        unacceptableWordSuffixedForNumbers.add("[");
//        unacceptableWordSuffixedForNumbers.add("{");
//    }
    private boolean metaParsingMode = false;

    public HParser(JTokenizer tokenizer, JCompilationUnit compilationUnit, JContext context) {
        super(tokenizer, compilationUnit, context, new HFactory(compilationUnit, context));

        setDefaultExpressionOptions(HParserOptions.hDefaultExpr);
    }

    protected static HNTypeToken createNullTypeToken(JToken nullToken) {
        return new HNTypeToken(nullToken, new DefaultTypeName("null"), null, null, null, nullToken, nullToken);
    }

    @Override
    public HFactory getNodeFactory() {
        return (HFactory) super.getNodeFactory();
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
            JMessageList err = errorList();
            JListWithSeparators<HNDeclareIdentifier> li = parseGroupedList("lambda expression", "lambda argument declaration",
                    () -> parseDeclareArgument(HParserOptions.DECLARE_LAMBDA_ARG, err), "(", ",", ")", silencedMessages);
            if (li != null && li.getEndToken() != null) {
                JToken endToken = li.getEndToken();
                JToken op = peek();
                if (op.id() == HTokenId.SEQ_MINUS_GT) {
                    //this is for sure a lambda expression so if any error was tracked, reported it now...
                    log().addAll(err);
                    for (JSourceMessage silencedMessage : silencedMessages) {
                        log().add(silencedMessage);
                    }
                    endToken = op = next();
                    //this is a lambda expression
                    HNode e = parseExpression();
                    if (e == null) {
                        log().jerror("X127", "lambda expression", peek(), "expected body");
                    } else {
                        endToken = e.getEndToken();
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
    protected HNAnnotationList parseAnnotations(int opPrecedence, HExpressionOptions options) {
        List<HNAnnotationCall> annotations = new ArrayList<>();
        JTokenBoundsBuilder bounds = new JTokenBoundsBuilder();
        bounds.visit(peek());

        while (true) {
            JToken token = peek();
            HNAnnotationCall[] anns = parseAnnotationGroup(bounds, options);
            if (anns.length == 0 && peek().isImage("@")) {
                log().jerror("X000", "annotation", token, "expected annotation");
            }
            if (anns.length == 0) {
                break;
            }
            annotations.addAll(Arrays.asList(anns));
        }
        if (annotations.isEmpty()) {
            return null;
        }
        return new HNAnnotationList(annotations.toArray(new HNAnnotationCall[0]), new JToken[0], bounds.getStartToken(), bounds.getEndToken());
    }

    @Override
    protected HNode parseExpressionUnarySuffix(int opPrecedence, HNode middle, HExpressionOptions options, ParseExpressionUnaryContext ucontext) {
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
                        log().jerror("X101", null, next, "superscript: invalid superscript characters " + next.image);
                    }
                }
            }
            int intv = 1;
            try {
                intv = Integer.parseInt(sb.toString());
            } catch (Exception any) {
                log().jerror("X101", null, next, "superscript: invalid superscript characters " + next.image);
            }
            return new HNOpBinaryCall(HTokenUtils.createToken("^"), middle, new HNLiteral(intv, next), middle.getStartToken(), middle.getEndToken());
        }
        if (middle instanceof HNLiteral) {
            //support for suffixed numbers
            //like 1.2 GHz
            //and replace them with a multiplication!

            HNLiteral lit = (HNLiteral) middle;
            if (lit.getValue() instanceof Number) {
                //check if pure number that is does not terminate with suffix
                char lastChar = lit.getStartToken().image.charAt(lit.getStartToken().image.length() - 1);
                if (lastChar >= '0' && lastChar <= '9') {
                    //in that case we will check for word suffixed of the number
                    JToken p = peek();
                    if (p.isIdentifier()) {
                        HNode suffix = parseExpressionUnaryTerminal(options);
                        return new HNOpBinaryCall(HTokenUtils.createToken("*"), middle, suffix, middle.getStartToken(), middle.getEndToken());
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

    public HNode parsePrefixParsNode(HExpressionOptions options) {
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
                                        .setEnd(toCast.getEndToken())
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
                left.getStartToken(), li.getStartToken(), li.getSeparatorTokens(), li.getEndToken()
        );
    }

    @Override
    public HNode parsePostfixBracketsNode(HNode left, JToken startToken) {
        JListWithSeparators<HNode> li = parseBracketsList("postfix brackets", "item", this::parseExpression);
        return new HNBracketsPostfix(
                left,
                li.getItems(),
                left.getStartToken(), li.getStartToken(), li.getSeparatorTokens(), li.getEndToken()
        );
    }

    @Override
    public HNode parsePostfixBracesNode(HNode middle, JToken copy) {
        HNode p = parseBraces(false, HNBlock.BlocType.LOCAL_BLOC);
        return getNodeFactory().createPostfixBracesNode(middle, p,
                new JNodeTokens().setStart(middle.getStartToken()).setEnd(p.getEndToken())
        );
    }

    @Override
    protected HNode parseBrackets() {
        return super.parseBrackets();
    }

    public HNode parseBraces() {
        return parseBraces(true, HNBlock.BlocType.LOCAL_BLOC);
    }

    protected HNode parseAndBuildExpressionBinary(JToken op, HNode o1, int opPrecedence, HExpressionOptions options) {
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
                                log().jerror("X105", null, n.getStartToken(), "invalid '." + next.sval + "' operator on " + o1);
                                return o1;
                            }
                        }
                        typeToken = new HNTypeToken(
                                JTokenUtils.createTokenIdPointer(o1.getStartToken(), sb.toString()),
                                new DefaultTypeName(sb.toString()),
                                new HNTypeToken[0],
                                new HNTypeToken[0],
                                new HNTypeToken[0],
                                o1.getStartToken(),
                                o1.getEndToken()
                        );
                    }
                    if (next.sval.equals("class")) {
                        return new HNDotClass(typeToken, op.copy(), o1.getStartToken(), next);
                    } else {
                        return new HNDotThis(typeToken, op.copy(), o1.getStartToken(), next);
                    }
                }

            }
            log().jerror("X106", null, next, "unexpected '." + next.sval + "'");
            return o1;
        } else if (op.id() == HTokenId.KEYWORD_IS) {
            JToken n = peek();
            if (n.id() == HTokenId.KEYWORD_NULL) {
                JToken nullToken = next();
                return new HNIs(
                        createNullTypeToken(nullToken), o1,
                        null,
                        o1.getStartToken(),
                        n
                );
            }
            HNTypeToken tn = parseTypeName();
            if (tn == null) {
                log().jerror("X107", null, next, "expected type name");
                return new HNIs(
                        null, o1,
                        null,
                        o1.getStartToken(), n
                );
            }
            n = peek();
            if (n.isIdentifier()) {
                next();
                return new HNIs(
                        tn, o1,
                        new HNDeclareTokenIdentifier(n),
                        o1.getStartToken(),
                        n
                );
            } else {
                return new HNIs(
                        tn, o1,
                        null,
                        o1.getStartToken(),
                        n
                );
            }
        } else if (op.id() == HTokenId.NOT_IS) {
            JToken n = peek();
            if (n.id() == HTokenId.KEYWORD_NULL) {
                JToken nullToken = next();
                new HNIs(
                        createNullTypeToken(nullToken), o1,
                        null,
                        o1.getStartToken(),
                        n
                );
            }
            HNTypeToken tn = parseTypeName();
            if (tn == null) {
                log().jerror("X107", null, next, "expected type name");
                return new HNIs(
                        null, o1,
                        null,
                        o1.getStartToken(), n
                );
            }
            n = peek();
            if (n.isIdentifier()) {
                next();
                return new HNIs(
                        tn, o1,
                        new HNDeclareTokenIdentifier(n),
                        o1.getStartToken(),
                        n
                );
            } else {
                return new HNIs(
                        tn, o1,
                        null,
                        o1.getStartToken(),
                        n
                );
            }
        } else {
            return super.parseAndBuildExpressionBinary(op, o1, opPrecedence, options);
        }
    }

    protected HNode parseAndBuildListOpNodeElement(HNode o1, int opPrecedence, JToken token, HExpressionOptions options) {
        log().jerror("X108", null, peek(), "list operator not supported in this context");
        token = token.copy();
        HNode o2 = parseExpression(opPrecedence, options);
        JToken s = o1 != null && o1.getStartToken() != null ? o1.getStartToken() : token;
        return new HNTuple(new HNode[]{o1, o2}, s, new JToken[0], o2.getEndToken());
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

    @Override
    public HNAnnotationList parseAnnotations(HExpressionOptions options) {
        return (HNAnnotationList) super.parseAnnotations(options);
    }

    protected HNode parseCallArgument() {
        JToken[] jTokens = nextIds(HTokenId.IDENTIFIER, HTokenId.COLON);
        if (jTokens != null) {
            HNode e = parseExpression();
            if (e == null) {
                pushBackAll(Arrays.asList(jTokens));
            } else {
                return new HNamedNode(
                        jTokens[0],
                        jTokens[1],
                        e
                );
            }
            return e;
        }
        return parseExpression();
    }

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
    public HNode parseExpressionUnaryTerminal(HExpressionOptions options) {
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
                                    log().warn("X000", null, p, "invalid static postfix : " + p.sval);
                                }
                            }
                        }
                        default: {
                            log().warn("X000", null, p, "invalid static postfix : " + p.sval);
                        }
                    }
                }
                break;
            }
            case HTokenId.IDENTIFIER: {
                //no pars Lambda expression
                //example : x->x*2
                JToken[] s = nextIds(HTokenId.IDENTIFIER, HTokenId.SEQ_MINUS_GT);
                if (s != null) {
                    JNodeTokens jNodeTokens = new JNodeTokens();
                    HNIdentifier id = new HNIdentifier(s[0]);
                    JToken op = s[1];
                    jNodeTokens.addSeparator(op);
                    HNode e = parseExpression();
                    return getNodeFactory().createLambdaExpression(id,
                            op, e,
                            jNodeTokens
                                    .setStart(id.getStartToken())
                                    .setEnd(e == null ? op : e.getEndToken())
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
            case HTokenId.TEMPORAL: {
                JToken token = next();
                if (token.isError()) {
                    log().warn("X000", null, token, "token never terminated");
                }
                Object parsed = null;
                try {
                    parsed = HSharedUtils.parseTemporal(token.sval);
                } catch (Exception ex) {
                    log().warn("X013", null, token, ex.getMessage());
                    parsed = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0);
                }
                return getNodeFactory().createLiteralNode(parsed, new JNodeTokens().setStart(token).setEnd(token));
            }
            case HTokenId.REGEX: {
                JToken token = next();
                if (token.isError()) {
                    log().warn("X000", null, token, "token never terminated");
                }
                return getNodeFactory().createLiteralNode(Pattern.compile(token.sval), new JNodeTokens().setStart(token).setEnd(token));
            }
            case HTokenId.STRING_INTERP_START: {
                return parseStringInterp();
            }
            case HTokenId.DOUBLE_QUOTES: {
                JToken token = next();
                if (token.isError()) {
                    log().warn("X000", null, token, "token never terminated");
                }
                return getNodeFactory().createLiteralNode(token.sval, new JNodeTokens().setStart(token).setEnd(token));
            }
            case HTokenId.SIMPLE_QUOTES: {
                JToken token = next();
                if (token.isError()) {
                    log().warn("X000", null, token, "token never terminated");
                }
                if (token.sval.length() == 1) {
                    return getNodeFactory().createLiteralNode(token.sval.charAt(0), new JNodeTokens().setStart(token).setEnd(token));
                } else {
//                            log().jerror("X110", "Invalid character token", token);
                    return getNodeFactory().createLiteralNode(token.sval, new JNodeTokens().setStart(token).setEnd(token));
                }
            }
            case JTokenId.ANTI_QUOTES: {//TODO FIX ME
                JToken token = next();
                if (token.isError()) {
                    log().warn("X000", null, token, "token never terminated");
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

    //
//    public HNode parseExpression() {
//        return super.parseExpression();
//    }
    protected HNAnnotationCall[] parseAnnotationGroup(JTokenBoundsBuilder bounds, HExpressionOptions options) {
        JExpressionUnaryOptions unary = options == null ? null : options.getUnary();
        JToken token = peek();
        List<HNAnnotationCall> a = new ArrayList<>();
        if (isSupportedAnnotation(unary)) {
            if (peek().id() == HTokenId.COMMERCIAL_AT) {
                if (peekIds(HTokenId.COMMERCIAL_AT, HTokenId.LEFT_PARENTHESIS) != null) {
                    bounds.visit(token = next());
                    while (true) {
                        JTokenBoundsBuilder bounds0 = new JTokenBoundsBuilder();
                        if (peek().id() == HTokenId.RIGHT_PARENTHESIS) {
                            bounds.visit(next());
                            break;
                        }
                        HNAnnotationCall aa = parseAnnotation(bounds0, options);
                        if (aa == null) {
                            log().jerror("X000", "annotation", peek(), "expected annotation");
                            break;
                        }
                        a.add(aa);
                    }
                    return a.toArray(new HNAnnotationCall[0]);
                } else {
                    JTokenBoundsBuilder bounds0 = new JTokenBoundsBuilder();
                    HNAnnotationCall aa = parseAnnotation(bounds0, options);
                    if (aa == null) {
                        log().jerror("X000", "annotation", peek(), "expected annotation");
                    }
                    return new HNAnnotationCall[]{aa};
                }
            } else {
                if (options.isAcceptModifiersAsAnnotation()) {
                    JToken p = peek();
                    switch (p.id()) {
                        case HTokenId.KEYWORD_PUBLIC:
                        case HTokenId.KEYWORD_PACKAGE:
                        case HTokenId.KEYWORD_PRIVATE:
                        case HTokenId.KEYWORD_PROTECTED:
                        case HTokenId.KEYWORD_STATIC:
                        case HTokenId.KEYWORD_FINAL:
                        case HTokenId.KEYWORD_ABSTRACT:
                        case HTokenId.KEYWORD_CONST:
                        case HTokenId.KEYWORD_TRANSIENT:
                        case HTokenId.KEYWORD_VOLATILE:
                        case HTokenId.KEYWORD_READONLY:
                        case HTokenId.KEYWORD_SYNCHRONIZED: {
                            JTokenBoundsBuilder bb = new JTokenBoundsBuilder();
                            bb.visit(p = next());
                            return new HNAnnotationCall[]{
                                    new HNAnnotationCall(new HNTypeTokenSpecialAnnotation(p), new HNode[0], bb)
                            };
                        }
                    }
                }
            }
        }
        return new HNAnnotationCall[0];
    }

    protected HNAnnotationCall parseAnnotation(JTokenBoundsBuilder bounds, HExpressionOptions options) {
        JExpressionUnaryOptions unary = options == null ? null : options.getUnary();
        JToken token = peek();
        bounds.visit(token);
        if (isVisitSupportedAnnotation(token, unary)) {
            skip();//@
            JToken y = peek();
            HNode name = null;
            switch (y.image) {
                //special annotations
                case "private":
                case "protected":
                case "public":
                case "virtual":
                case "override":
                case "delegate":
                case "value":

                case "abstract":
                case "const": //all fields are final and const
                case "final": //cannot be overridden
                case "sealed": //can be overridden by a white list

                case "serializable":
                case "synchronized":
                case "strictfp":

                case "volatile":
                case "transient":

                case "enum":
                case "exception":
                case "interface":
                case "data":
                case "record":

                case "parallel":
                case "serial": {
                    bounds.visit(next());
                    name = new HNTypeTokenSpecialAnnotation(y);
                    break;
                }
                default: {
                    name = parseTypeName();
                    if (name == null) {
                        log().jerror("X000", "annotation", peek(), "expected annotation");
                    }
                }
            }
            List<HNode> args = new ArrayList<>();
            if (peek().id() == HTokenId.LEFT_PARENTHESIS) {
                bounds.visitSeparator(next());
                if (peek().id() == HTokenId.RIGHT_PARENTHESIS) {
                    bounds.visitSeparator(next());
                } else {
                    HNode e = bounds.visit(parseAnnotationArgument());
                    if (e != null) {
                        args.add(e);
                        while (true) {
                            if (peek().id() == HTokenId.COMMA) {
                                bounds.visitSeparator(next());
                                e = bounds.visit(parseAnnotationArgument());
                                if (e != null) {
                                    args.add(e);
                                }
                            } else if (peek().id() == HTokenId.RIGHT_PARENTHESIS) {
                                bounds.visitSeparator(next());
                                break;
                            } else {
                                log().jerror("X000", "annotation", peek(), "expected ','");
                                e = bounds.visit(parseAnnotationArgument());
                                if (e != null) {
                                    args.add(e);
                                } else {
                                    skip();
                                }
                            }
                        }
                    }
                }
            }
            return new HNAnnotationCall(name, args.toArray(new HNode[0]), bounds);
        } else {
            return null;
        }
    }

    protected HNode parseAnnotationArgument() {
        if (peekIds(HTokenId.IDENTIFIER, HTokenId.COLON) != null) {
            //TODO : argument in the form argname:argvalue
        }
        HNode e = parseExpression(HParserOptions.annotationsOptions);
        if (e == null) {
            log().jerror("X000", "annotation", peek(), "expected annotation argument");
        }
        return e;
    }

    public HNode parseBraces(boolean asExpr, HNBlock.BlocType type) {
        JToken n = peek();
        JToken endToken = n;
        if (n.id() != HTokenId.LEFT_CURLY_BRACKET) {
            pushBack(n);
            return null;
        }
        JNodeResult<HNMap> mo = parseMapObject(asExpr);
        if (mo.getNode() != null) {
            log().addAll(mo.getMessages());
            return mo.getNode();
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
            log().jerror("X102", null, peek(), "block statement: expected '}'");
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
                        endToken = t.getEndToken();
                        if (startToken == null) {
                            startToken = endToken;
                        }
                        break;
                    }
                    JToken toSkip = next();
                    log().jerror("X103", null, toSkip, "block statement: invalid statement");
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
//                            log().jerror("X104", null, "block statement: expected ';' at the end of a statement", peek);
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
                    log().jerror("X000", null, n, "expected '\"'");
                    break;
                }
                case HTokenId.STRING_INTERP_TEXT: {
                    JToken next = next();
                    tokens.add(endToken = next);
                    if (n.isError()) {
                        log().jerror("X000", null, n, "expected '\"'");
                    }
                    break;
                }
                case HTokenId.STRING_INTERP_END: {
                    end = true;
                    JToken next = next();
                    tokens.add(endToken = next);
                    if (n.isError()) {
                        log().jerror("X000", null, n, "expected '\"'");
                    }
                    break;
                }
                case HTokenId.STRING_INTERP_DOLLAR_END: {
                    JToken next = next();
                    tokens.add(endToken = next);
                    if (n.isError()) {
                        log().jerror("X000", null, n, "expected '\"'");
                    }
                    break;
                }
                case HTokenId.STRING_INTERP_DOLLAR_START: {
                    JToken next = next();
                    tokens.add(endToken = next);
                    if (next.isImage("${")) {
                        HNode e = parseExpression();
                        if (e != null) {
                            endToken = e.getEndToken();
                            expressions.add(e);
                            if (next.id() == HTokenId.LEFT_CURLY_BRACKET) {
                                next = peek();
                                if (next.id() == HTokenId.RIGHT_CURLY_BRACKET || next.id() == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
                                    tokens.add(endToken = next());
                                } else {
                                    log().jerror("X000", null, next, "expected '}'");
                                }
                            }
                        } else {
                            end = true;
                            log().jerror("X000", null, next, "expected valid expression");
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
                            log().jerror("X000", null, next, "expected identifier");
                        }
                    }
                    break;
                }
                default: {
                    log().jerror("X000", null, n, "not expected : " + n);
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
                return parseDeclarationAsStatement(HParserOptions.DECL_ANY_1, err);
            }
            case HTokenId.KEYWORD_FUN:
            case HTokenId.KEYWORD_PUBLIC:
            case HTokenId.KEYWORD_PRIVATE:
            case HTokenId.KEYWORD_PROTECTED:
            case HTokenId.KEYWORD_FINAL:
            case HTokenId.KEYWORD_VAR: {
                return parseDeclarationAsStatement(HParserOptions.DECL_ANY_1, err);
            }
            case HTokenId.KEYWORD_CLASS: {
                HNode dec = null;
                if (metaParsingMode) {
                    err.jerror("X113", "class declaration", p, "class definitions are not allowed in package declaration");
                    //ignore it;
                }
                JMessageList err2 = errorList();
                dec = parseDeclaration(HParserOptions.DECL_ANY_2, err2);
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
                return parseBraces(false, HNBlock.BlocType.LOCAL_BLOC);
            }
        }

        //boolean acceptModifiers, boolean acceptVar, boolean acceptFunction, boolean acceptClass,
        //                                    boolean requiredSemiColumnForVar, boolean acceptEqValue, boolean acceptInValue
        JMessageList err2 = errorList();
        HNode n = parseDeclarationAsStatement(HParserOptions.DECL_ANY_3, err2);
        if (n != null) {
            err.addAll(err2);
            return n;
        }
        return parseExpressionAsStatement();
    }

    public HNode parseDeclarationAsStatement(HDeclarationOptions options, JMessageList err) {
        HNode n = parseDeclaration(options, err);
        if (n != null) {
            if (n instanceof HNDeclareIdentifier) {
                JToken p = peek();
                if (p.id() == HTokenId.SEMICOLON) {
                    skip();
                } else {
                    err.jerror("X000", "declaration", p, "expected ';'");
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
                log().jerror("X131", logName, t, "expected ';'");
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
            log().jerror("X131", logName, found, "expected ';'");
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
            case H_TRY_CATCH: {
                return false;
//                HNTryCatch c=(HNTryCatch) t;
//                if(c.getFinallyBranch()!=null){
//                    return isRequireSemiColumn(c.getFinallyBranch());
//                }
//                if(c.getCatches().length>0){
//                    return isRequireSemiColumn(c.getCatches()[c.getCatches().length-1].getDoNode());
//                }
//                return true;
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
            endToken = tt.typeToken.getEndToken();
            JToken t2 = peek();
            HNode setter = null;
            if (t2.isImage("(")) {
                endToken = next();
                JToken t3 = peek();
                if (t3.isImage(")")) {
                    endToken = next();
                    if (tt.inits.length == 0) {
                        // How can we create an array without specifying a valid constructor?
                        log().jerror("X193", "constructor", t2, "unresolved empty constructor");
                    }
                    return new HNArrayNew(tt.typeToken, tt.inits, null, startToken, endToken);
                } else {
                    setter = parseExpression();
                }
                if (setter != null) {
                    endToken = setter.getEndToken();
                }
                t2 = peek();
                if (t2.isImage(")")) {
                    endToken = next();
                } else {
                    log().jerror("X114", "constructor", t2, "expected ')'");

                }
                if (tt.type.isArray() || tt.type.isVarArg()) {
//                    if (setter != null && tt.inits.length == 0) {
//                        log().jerror("S052", null, "initialized array is missing initializer", startToken);
//                    }
                    return new HNArrayNew(tt.typeToken, tt.inits, setter, startToken, endToken);
                } else {
                    if (tt.inits.length != 0) {
                        throw new JFixMeLaterException();
                    } else {
//                        if (setter != null && tt.inits.length == 0) {
//                            log().jerror("S052", null, "initialized array is missing initializer", startToken);
//                        }
                        return new HNObjectNew(tt.typeToken, new HNode[]{setter}, startToken, endToken);
                    }
                }
            } else if (tt.inits.length == 0 && peekIds(HTokenId.DOT, HTokenId.KEYWORD_CLASS) != null) {
                JToken dotToken = next();
                JToken classToken = next();
                return new HNDotClass(tt.typeToken, dotToken, tt.typeToken.getStartToken(), classToken);
            } else if (tt.inits.length == 0 && peekIds(HTokenId.DOT, HTokenId.KEYWORD_THIS) != null) {
                JToken dotToken = next();
                JToken classToken = next();
                return new HNDotThis(tt.typeToken, dotToken, tt.typeToken.getStartToken(), classToken);
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
                    if (n.id() == HTokenId.QUESTION) {
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
                            endToken = e.getEndToken();
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
                            endToken = e.getEndToken();
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
                endToken.def = HParserOptions.DEF_GT;
                endToken.image = ">";
                endToken.endColumnNumber--;
                endToken.endCharacterNumber--;
                separators.add(endToken);

                n.def = HParserOptions.DEF_GT;
                n.image = ">";
                n.sval = ">";
                n.startColumnNumber++;
                n.startCharacterNumber++;
                pushBack(n);
            } else if (n.isOperator(">>>")) {
                //explode operator
                n = next();
                endToken = n.copy();
                endToken.def = HParserOptions.DEF_GT;
                endToken.image = ">";
                endToken.endColumnNumber -= 2;
                endToken.endCharacterNumber -= 2;
                separators.add(endToken);

                n.def = HParserOptions.DEF_GT2;
                n.image = ">>";
                n.sval = ">>";
                n.startColumnNumber++;
                n.startCharacterNumber++;
                pushBack(n);
            } else if (n.isOperator(">>>>")) {
                //explode operator
                n = next();
                endToken = n.copy();
                endToken.def = HParserOptions.DEF_GT;
                endToken.image = ">";
                endToken.endColumnNumber -= 3;
                endToken.endCharacterNumber -= 3;
                separators.add(endToken);

                n.def = HParserOptions.DEF_GT3;
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
//                            log().jerror("X055", "type construct: after var/val keywords, you need not to add type declaration", peek());
//                        }
//                        type.type = context().types().parseName(v.sval);
//                        break;
//                    }
//                    case "var":
//                    case "val": {
//                        if (visitedVarVal) {
//                            log().jerror("X056", "type construct: multiple var/val keywords", peek());
//                        }
//                        type.visitedVarVal = true;
//                        break;
//                    }
//                    case "void": {
//                        if (visitedVarVal) {
//                            log().jerror("X056", "type construct: var/val and void combination mismatch", peek());
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
//                        log().jerror("X057", "type construct: generic type argument missing", peek());
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
//                            log().jerror("X058", "type construct: expected ',' or '>'", peek());
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
//                                log().jerror("X058", "type construct: expected '[]'", peek());
//                            }
//                            //String[12*6] a;
//                            HNode jNode = parseExpression();
//                            if (jNode == null) {
//                                jNode = parseExpression();
//                                log().jerror("X058", "type construct: expected initializer expression", peek());
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
                                endToken = jNode.getEndToken();
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
                    log().jerror("X115", null, n, "vararg not supported in this context");
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
                        reportedErrors.jerror("X000", null, x, "expected ','");
                        break;
                    } else if (x.id() == HTokenId.COMMA) {
                        endToken = x = next();
                        separators.add(x);
                        HNDeclareTokenTupleItem a = parseDeclareTokenTupleItem(reportedErrors);
                        if (a != null) {
                            endToken = a.getEndToken();
                            items.add(a);
                        } else {
                            break;
                        }
                    } else if (x.isImage(")")) {
                        endToken = x = next();
                        break;
                    } else {
                        reportedErrors.jerror("X000", null, x, "expected ','");
                        HNDeclareTokenTupleItem a = parseDeclareTokenTupleItem(reportedErrors);
                        if (a != null) {
                            endToken = a.getEndToken();
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
//    private int parseModifiers() {
//        int modifiers = 0;
//        while (true) {
//            boolean accepted = true;
//            JToken p = next();
//            switch (p.id()) {
//                case HTokenId.KEYWORD_PUBLIC: {
//                    modifiers |= Modifier.PUBLIC;
//                    break;
//                }
//                case HTokenId.KEYWORD_PACKAGE: {
//                    modifiers |= HSharedUtils.PACKAGE;
//                    break;
//                }
//                case HTokenId.KEYWORD_PRIVATE: {
//                    modifiers |= Modifier.PRIVATE;
//                    break;
//                }
//                case HTokenId.KEYWORD_PROTECTED: {
//                    modifiers |= Modifier.PROTECTED;
//                    break;
//                }
//                case HTokenId.KEYWORD_STATIC: {
//                    modifiers |= Modifier.STATIC;
//                    break;
//                }
//                case HTokenId.KEYWORD_FINAL: {
//                    modifiers |= Modifier.FINAL;
//                    break;
//                }
//                case HTokenId.KEYWORD_ABSTRACT: {
//                    modifiers |= Modifier.ABSTRACT;
//                    break;
//                }
//                case HTokenId.KEYWORD_CONST: {
//                    modifiers |= HSharedUtils.CONST;
//                    break;
//                }
//                case HTokenId.KEYWORD_TRANSIENT: {
//                    modifiers |= Modifier.TRANSIENT;
//                    break;
//                }
//                case HTokenId.KEYWORD_VOLATILE: {
//                    modifiers |= Modifier.VOLATILE;
//                    break;
//                }
//                case HTokenId.KEYWORD_STRICTFP: {
//                    modifiers |= Modifier.STRICT;
//                    break;
//                }
//                case HTokenId.KEYWORD_READONLY: {
//                    modifiers |= HSharedUtils.READONLY;
//                    break;
//                }
//                case HTokenId.KEYWORD_SYNCHRONIZED: {
//                    modifiers |= Modifier.SYNCHRONIZED;
//                    break;
//                }
//                default: {
//                    accepted = false;
//                }
//            }
//            if (!accepted) {
//                pushBack(p);
//                break;
//            }
//        }
//        return modifiers;
//    }

    /**
     * var a=2 val a=2 a=Z int a=2; String a=""; The same applies with ':'
     * instead of '='
     *
     * @return
     */
    private HNode parseDeclareAssign(Boolean eqOp, boolean acceptAnyExpression, JMessageList err) {
//        JToken a = peek();
//        boolean acceptColon = eqOp == null || !eqOp;
//        boolean acceptEq = eqOp == null || eqOp;
        HDeclarationOptions options = eqOp == null ? HParserOptions.DECLARE_ASSIGN_EQ_OR_COLON
                : eqOp ? HParserOptions.DECLARE_ASSIGN_EQ : HParserOptions.DECLARE_ASSIGN_COLON;

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
//                        log().jerror("X127", "identifier declaration", "expected initialization", peek());
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
        err.jerror("X000", null, peek(), "expected expression");
        return null;
    }

    private JMessageList errorList() {
        return new DefaultJMessageList();
    }

    private HNode parseDeclaration(HDeclarationOptions options, JMessageList err) {
        try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
            JTokenBoundsBuilder jTokenBoundsBuilder = new JTokenBoundsBuilder();
            jTokenBoundsBuilder.visit(peek());
            HNAnnotationList annotations = parseAnnotations(getDefaultExpressionOptions());
            if (options.acceptModifiers) {
                if (HNAnnotationList.isModifier("static", annotations)) {
                    if (peek().id() == HTokenId.LEFT_CURLY_BRACKET) {
                        if (annotations.getChildren().length != 1) {
                            err.jerror("X116", null, peek(), "static initializer should not have modifiers");
                        }
                        HNode jNode = parseBraces(false, HNBlock.BlocType.METHOD_BODY);
                        if (jNode != null) {
                            jTokenBoundsBuilder.visit(jNode);
                            HNDeclareInvokable fct = new HNDeclareInvokable(
                                    null,
                                    jNode.getStartToken(), jTokenBoundsBuilder.getEndToken());
                            fct.addAnnotations(annotations.getChildren());
                            fct.setBody(jNode);
                            fct.setReturnTypeName(null);
                            fct.setSignature(
                                    JNameSignature.of(
                                            fct.getName(),
                                            fct.getArguments().stream()
                                                    .map(HNDeclareIdentifier::getIdentifierTypeNode).toArray(JTypeName[]::new)
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
                    err.jerror("X117", "class definition", t, "not allowed class definition in the current context");
                }
                skip();
                return parseDeclareClass(annotations, jTokenBoundsBuilder.getStartToken());
            } else if (t.id() == HTokenId.KEYWORD_FUN || t.id() == HTokenId.KEYWORD_INIT || t.id() == HTokenId.KEYWORD_VOID) {
                if (!options.acceptFunction) {
                    log().jerror("X118", "function/method definition", t, "not allowed class method/function/constructor definition in the current context");
                }
                if (t.id() == HTokenId.KEYWORD_VOID) {
                    JToken voidToken = next();
                    pushBack(HTokenUtils.createToken("fun"));
                    pushBack(voidToken);
                }
                return parseDeclareFunction(annotations, false, null, true, jTokenBoundsBuilder.getStartToken());
            }

            ParseDeclareIdentifierContext pdic = new ParseDeclareIdentifierContext();
            pdic.annotations = annotations;
            pdic.options = options;
            pdic.bounds = jTokenBoundsBuilder;
            HNode n = parseDeclareIdentifier(pdic, err);
            if (n == null) {
                snapshot.rollback();
                return null;
            }
            return n;
        }
    }

    private HNDeclareIdentifier parseDeclareIdentifier(ParseDeclareIdentifierContext pdic, JMessageList err) {
        String groupName = null;
        JToken t = peek();
        if (t.id() == HTokenId.KEYWORD_VAR) {
            if (pdic.options.acceptVar) {
                pdic.bounds.visit(next());
                pdic.varVal = t;
            } else {
                return null;
            }
        } else if (t.id() == HTokenId.KEYWORD_VAL) {
            if (pdic.options.acceptVar) {
                pdic.bounds.visit(next());
                pdic.varVal = t;
            } else {
                return null;
            }
        } else {
            if (pdic.options.getNoTypeNameOption() == HDeclarationOptions.NoTypeNameOption.NAME) {
                JMessageList err2 = errorList();
                if (_parseDeclareIdentifierVarIds(pdic, errorList())) {
                    if (pdic.varIds != null) {
                        err.addAll(err2);
                        err2.clear();
                        HNDeclareIdentifier hNode = _parseDeclareIdentifierInitialization(pdic, err2);
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
                if (HNAnnotationList.size(pdic.annotations) != 0) {
                    err.jerror("X119", groupName, peek(), "identifier definition : unexpected modifier");
                    return null;
                } else {
                    //this is not a declaration!
                    return null;
                }
            }
        }
        JMessageList err2 = errorList();
        if (!_parseDeclareIdentifierVarIds(pdic, err2)) {
            err.addAll(err2);
            //there is no var, use 'value' var name (or 'exception')
            if (pdic.options.getNoTypeNameOption() == HDeclarationOptions.NoTypeNameOption.TYPE) {
                pdic.varIds = new HNDeclareTokenIdentifier(HTokenUtils.createToken(pdic.options.getDefaultVarName()));
            } else {
                return null;
            }
        } else {
            err.addAll(err2);
            if (pdic.varIds == null) {
                if (pdic.options.getNoTypeNameOption() == HDeclarationOptions.NoTypeNameOption.TYPE) {
                    pdic.varIds = new HNDeclareTokenIdentifier(HTokenUtils.createToken(pdic.options.getDefaultVarName()));
                } else {
                    err.jerror("X000", groupName, peek(), "missing name");
                    return null;
                }
            }
        }
        err2.clear();
        HNDeclareIdentifier hNode = _parseDeclareIdentifierInitialization(pdic, err2);
        err.addAll(err2);
        if (hNode == null) {
            return null;
        }
        return hNode;
    }

    /**
     * parse declare vars in the following forms : a
     *
     * @param pdic
     * @return
     */
    private boolean _parseDeclareIdentifierVarIds(ParseDeclareIdentifierContext pdic, JMessageList err) {
        HNDeclareToken varIds = null;
        JTokenBoundsBuilder se = new JTokenBoundsBuilder(pdic.bounds);
        try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
            String logGroup = "identifier definition";
            if ( /*(varVal != null) && */peek().id() == HTokenId.LEFT_PARENTHESIS) {
                se.visit(peek());
                JMessageList reportedErrors = errorList();
                HNDeclareTokenTupleItem u = parseDeclareTokenTupleItem(reportedErrors);
                if (u != null) {
                    varIds = u;
                    JToken pp = peek();
                    if (pp.id() != HTokenId.EQ && pp.id() != HTokenId.COLON) {
                        //not a tuple assignment!
                        err.jerror("X120", logGroup, peek(), "expected ':' or '='");
                        snapshot.rollback();
                        return false;
                    }
                    se.visit(u);

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
                se.visit(p);
                if (p.isIdentifier()) {
                    if (pdic.options.isAcceptDotName()) {
                        name = parseNameWithPackageSingleToken();
                    } else {
                        name = next();
                    }
                }
                if (name == null) {
                    //this is not a name. It should be an expression
//                    err.jerror("X000", logGroup, "expected name", peek());
//                    snapshot.rollback();
//                    return false;
                    //has null name
                    snapshot.rollback();
                    return true;
                } else {
                    se.visit(name);
                }
                List<HNDeclareTokenTupleItem> allVarNames = new ArrayList<>();
                allVarNames.add(new HNDeclareTokenIdentifier(name));
                if (pdic.options.isAcceptMultiVars()) {
                    while (peek().id() == pdic.options.getMultiVarSeparator()) {
                        next();
                        JToken name2 = parseNameWithPackageSingleToken();
                        if (name2 == null) {
                            err.jerror("X120", logGroup, peek(), "expected name");
                            break;
                        } else {
                            se.visit(name2);
                            allVarNames.add(new HNDeclareTokenIdentifier(name2));
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
                            err.jerror("X000", logGroup, v.getStartToken(), "expected identifier");
                        }
                    }
                    varIds = new HNDeclareTokenList(ids.toArray(new HNDeclareTokenIdentifier[0]),
                            allVarNames.get(0).getStartToken(),
                            allVarNames.get(allVarNames.size() - 1).getEndToken()
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
                    pdic.bounds = se;
                    return true;
                }
                case HTokenId.LEFT_PARENTHESIS: {
                    if (pdic.tt != null) {
                        pdic.varIds = varIds;
                        pdic.bounds = se;
                        return true;
                    }
                    break;
                }
            }
            err.jerror("X000", logGroup, p, "expected identifier");
            snapshot.rollback();
            return false;
        }
    }

    private HNDeclareIdentifier _parseDeclareIdentifierInitialization(ParseDeclareIdentifierContext pdic, JMessageList err) {
        HNode val = null;
        JToken opToken = null;
        if (pdic.tt == null || pdic.tt.inits.length == 0) {
            JToken n = next();
            if (n.isOperator("=") && (pdic.varVal != null || pdic.options.acceptEqValue)) {
                opToken = n;
                pdic.bounds.visit(opToken);
                val = parseExpression();
                pdic.bounds.visit(val);
                if (val == null) {
                    err.jerror("X121", "identifier definition", peek(), "expected value assignment");
                }
            } else if (n.id() == HTokenId.COLON && (pdic.varVal != null || pdic.options.acceptInValue)) {
                opToken = n;
                pdic.bounds.visit(opToken);
                val = parseExpression();
                pdic.bounds.visit(val);
                if (val == null) {
                    log().jerror("X121", "identifier definition", peek(), "expected value assignment");
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
                pdic.bounds.visit(val);
            } else {
                if (pdic.varVal != null) {
                    err.jerror("X121", "identifier definition", n, "expected value assignment");
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
                pdic.bounds.visit(setter);
                t2 = peek();
                if (t2.isImage(")")) {
                    pdic.bounds.visit(next());
                } else {
                    err.jerror("X122", "identifier definition", t2, "expected ')'");
                }
            }
            if (setter != null && pdic.tt.inits.length == 0) {
                err.jerror("S052", null, pdic.bounds.getStartToken(), "initialized array is missing initializer");
            }
            val = new HNArrayNew(pdic.tt.typeToken, pdic.tt.inits, setter, pdic.bounds.getStartToken(), pdic.bounds.getEndToken());
            //create init!!
        }
        if (opToken == null) {
            opToken = HTokenUtils.createToken("=");
        }
        HNDeclareIdentifier h = new HNDeclareIdentifier(
                pdic.varIds,
                val,
                (pdic.tt == null) ? null : pdic.tt.typeToken,
                opToken, pdic.bounds.getStartToken(), pdic.bounds.getEndToken()
        );
        h.setAnnotations(HNAnnotationList.toArray(pdic.annotations));
        h.setInitValue(val);
        if (pdic.tt == null || pdic.tt.typeToken == null) {
            h.setIdentifierTypeNode(null);
        } else {
            h.setIdentifierTypeNode(pdic.tt.typeToken);
        }
        return h;
    }

    private void checkVarName(HNDeclareTokenTupleItem vname, JMessageList err) {
        if (vname instanceof HNDeclareTokenIdentifier) {
            JToken ttt = ((HNDeclareTokenIdentifier) vname).getToken();
            if (ttt.image.indexOf('.') >= 0) {
                err.jerror("X120", "identifier definition", ttt, "invalid identifier name ");
            }
        } else {
            throw new JFixMeLaterException();
        }
    }

    /**
     * String a; a;
     *
     * @param options
     * @return
     */
    private HNDeclareIdentifier parseDeclareArgument(HDeclarationOptions options, JMessageList err) {
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

        HNAnnotationList annotations = parseAnnotations(getDefaultExpressionOptions());
        JToken[] name = parseNameWithPackageTokens();
        if (name == null) {
            if (HNAnnotationList.size(annotations) == 0) {
                return null;
            }
            log().jerror("X123", "class declaration", peek(), "expected class name");
            return null;
        }
        endToken = name[name.length - 1];
        String[] nameAndNamespace = HSharedUtils.splitNameAndPackage(name);
        JListWithSeparators<HNode> li = parseParsList("extends", "super argument declaration", () -> parseExpression());
        HNExtends dec = new HNExtends(nameAndNamespace[1], startNode, li == null ? endToken : li.getEndToken());
        dec.setPackageName(nameAndNamespace[0]);
        dec.setAnnotations(HNAnnotationList.toArray(annotations));
        dec.setArguments(li == null ? new ArrayList<>() : li.getItems());
        return dec;
    }

    public LambdaBody parseFunctionArrowBody(boolean asExpr) {
        if (peek().id() == HTokenId.SEQ_MINUS_GT) {
            JToken n = next();
            if (peek().id() == HTokenId.LEFT_CURLY_BRACKET) {
                HNode b = parseBraces(asExpr, HNBlock.BlocType.LOCAL_BLOC);
                if (b == null) {
                    log().jerror("X000", "lambda", peek(), "expected lambda body");
                }
                return new LambdaBody(n, b, false);
            } else {
                HNode b = parseExpression();
                if (b == null) {
                    log().jerror("X000", "lambda", peek(), "expected expression");
                }
                return new LambdaBody(n, b, true);
            }
        }
        return null;
    }

    private HNode parseDeclareFunction(
            HNAnnotationList annotations, boolean anonymous, HNTypeToken type, boolean requiredSemiColumnForVar,
            JToken startToken
    ) {
        startToken = startToken.copy();
        JToken name = null;
        JListWithSeparators<HNTypeToken> jTypeNameOrVariables = null;
        boolean constr = false;
        if (anonymous) {
            jTypeNameOrVariables = parseJTypeNameOrVariables();
        } else {
            if (peek().id() == HTokenId.KEYWORD_FUN) {
                next();

                if (annotations == null || annotations.size() == 0) {
                    //check if there are modifiers after the def keyword
                    annotations = parseAnnotations(getDefaultExpressionOptions());
                }
                if (peek().id() == HTokenId.KEYWORD_INIT) {
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
                            log().jerror("X124", "function/method declaration", peek(), "expected type");
                            //type = createSpecialTypeToken("void");
                        }
                        if (type != null && (type.getTypename().isArray() || type.getTypenameOrVar().name().indexOf('.') >= 0)) {
                            log().jerror("X125", "function/method declaration", peek(), "expected name");
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
                            log().jerror("X125", "function/method declaration", peek(), "expected name");
                        }
                        jTypeNameOrVariables = parseJTypeNameOrVariables();
                    }
                }
            } else if (peek().id() == HTokenId.KEYWORD_INIT) {
                name = next();
                constr = true;
                jTypeNameOrVariables = parseJTypeNameOrVariables();
            } else {
                return null;
            }
        }
        if (jTypeNameOrVariables == null) {
            //log().jerror("X052", "function/method declaration: invalid generic arguments", peek());
            jTypeNameOrVariables = new DefaultJListWithSeparators<>(new ArrayList<>(), null, new ArrayList<>(), null);
        }
//        String[] nameAndNamespace = HSharedUtils.splitNameAndNamespace(name);
        annotations = HNAnnotationList.publify(annotations);

        HDeclarationOptions argOptions = constr ? HParserOptions.DECLARE_CONSTR_ARG : HParserOptions.DECLARE_FUNCTION_ARG;
        JListWithSeparators<HNDeclareIdentifier> li = parseParsList("function/method declaration", "argument declaration",
                () -> parseDeclareArgument(argOptions, log())
        );
        if (li == null) {
            log().jerror("X126", "function/method declaration", peek(), "expected parameters");
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
        f.setAnnotations(HNAnnotationList.toArray(annotations));
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
                                    .map(HNDeclareIdentifier::getIdentifierTypeNode)
                                    .map(HNTypeToken::getTypename)
                                    .toArray(JTypeName[]::new)
                    )
            );
        }
        JToken n = peek();
        if (n.id() == HTokenId.SEMICOLON) {
            f.setEndToken(next());
            annotations = HNAnnotationList.nonNull(annotations).addModifier("abstract");
            f.setAnnotations(HNAnnotationList.toArray(annotations));
        } else if (n.id() == HTokenId.SEQ_MINUS_GT) {
            boolean asExpr = true;
            if (type == null || type.toString().equals("void")) {
                asExpr = false;
            }
            LambdaBody lambdaBody = parseFunctionArrowBody(asExpr);
            f.setEndToken(lambdaBody.op);
            f.setImmediateBody(true);
            f.setEndToken(lambdaBody.getEndToken());
            f.setBody(lambdaBody.body);
            if (requiredSemiColumnForVar) {
                requireSemiColumn("function/method declaration", true);
            }
        } else if (n.id() == HTokenId.LEFT_CURLY_BRACKET) {
            f.setBody(parseBraces(false, HNBlock.BlocType.METHOD_BODY));
            f.setEndToken(f.getBody().getEndToken());
        } else {
            log().jerror("X128", "function/method declaration", peek(), "expected '{' or '->'");
            HNode e = parseExpression();
            if (e != null) {
                f.setBody(e);
                f.setEndToken(f.getBody().getEndToken());
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

    private HNode parseDeclareClass(HNAnnotationList annotations, JToken startToken) {
        try {
            HNDeclareType classDef = new HNDeclareType(startToken);
            JToken endToken = startToken;
            pushDeclarationContext(classDef);
            JToken[] name = parseNameWithPackageTokens();
            if (name == null) {
                log().jerror("X129", "class declaration", peek(), "expected class name");
                name = new JToken[]{JTokenUtils.createTokenIdPointer(startToken, "<anonymous>")};
            }
            String[] nameAndNamespace = HSharedUtils.splitNameAndPackage(name);
//            HNDeclareType f = new HNDeclareType(nameAndNamespace[1]);
//            f.setNamespace(nameAndNamespace[0]);
            classDef.setNameToken(name[name.length - 1]);
            classDef.setAnnotations(HNAnnotationList.toArray(annotations));
            classDef.setPackageName(nameAndNamespace[0]);
            JToken n = peek();
            if (n.id() == HTokenId.LEFT_PARENTHESIS) {
                JListWithSeparators<HNDeclareIdentifier> sli = parseParsList("class declaration", "argument", () -> {
                    return parseDeclareArgument(
                            HParserOptions.DECLARE_DEFAULT_CONSTR_ARG, log()
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
                classDef.setBody(parseBraces(false, HNBlock.BlocType.CLASS_BODY));
                if (classDef.getBody() != null) {
                    for (HNode statement : ((HNBlock) classDef.getBody()).getStatements()) {
                        bindDeclaringType(classDef, statement);
                    }
                    endToken = (classDef.getBody().getEndToken());
                }
            } else {
                log().jerror("X130", "class declaration", peek(), "expected '{' or '->'");
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
            log().jerror("X133", null, p, "unable to parse till the end of the document.");
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
                    log().jerror("X134", "array", peek(), "invalid expression");
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
                } else if (n2.id() == HTokenId.RIGHT_SQUARE_BRACKET) {
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
                log().jerror("X135", "matrix",
                        startToken, "columns mismatch : "
                                + "expected " + maxColumns + " but found " + row.size() + " at row " + (i + 1));
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
            condExpr = parseExpression(HParserOptions.noBracesExpressionOptions);
            if (condExpr == null) {
                log().jerror("X136", "if statement", peek(), "expected condition");
                i.setEndToken(endToken);
                return i;
            }
            HNode doExpr = parseExpressionOrStatement(asExpr, false);
            if (doExpr == null) {
                log().jerror("X137", "if statement", peek(), "expected then statement/expression");
                if (peek().id() == HTokenId.KEYWORD_ELSE) {
                    //doExpr=HSharedUtils.createUnknownBlocNode();
                } else {
                    i.setEndToken(endToken);
                    return i;
                }
            } else {
                endToken = (doExpr.getEndToken());
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
                        log().jerror("X138", "if statement", peek(), "expected else condition");
                        i.setEndToken(endToken);
                        return i;
                    }
                    endToken = (condExpr.getEndToken());
                    doExpr = parseExpressionOrStatement(asExpr, false);
                    if (doExpr == null) {
                        log().jerror("X139", "if statement", peek(), "expected then statement/expression");
                        //doExpr=HSharedUtils.createUnknownBlocNode();
                    } else {
                        endToken = (doExpr.getEndToken());
                    }
                    i.add(condExpr, doExpr);
                } else if (p2.length >= 1 && p2[0].id() == HTokenId.KEYWORD_ELSE) {
                    //skip else!
                    endToken = (next());
                    doExpr = parseExpressionOrStatement(asExpr, false);
                    if (doExpr == null) {
                        log().jerror("X140", "if statement", peek(), "expected else statement/expression");
                    } else {
                        endToken = (doExpr.getEndToken());
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
            if (p.id() == HTokenId.LEFT_PARENTHESIS) {
                pushBack(p);
                cond = parseParenthesis("while condition");
                if (cond == null) {
                    log().jerror("X141", "while statement", peek(), "expected while condition");
                    while (true) {
                        JToken peek = next();
                        if (peek.id() != HTokenId.RIGHT_PARENTHESIS || peek.isEOF()) {
                            break;
                        } else if (peek.id() == HTokenId.LEFT_CURLY_BRACKET) {
                            pushBack(peek);
                            break;
                        }
                    }
                } else {
                    endToken = cond.getEndToken();
                }
            } else {
                log().jerror("X142", "while statement", peek(), "expected '(' for while condition");
                cond = parseExpression();
                if (cond == null) {
                    log().jerror("X143", "while statement", peek(), "expected while condition");
                } else {
                    endToken = cond.getEndToken();
                }
            }
            HNode block = null;
            JToken t = peek().copy();
            if (t.id() == HTokenId.SEMICOLON) {
                //this is an empty while;
                //block = new HNBlock();
            } else {
                block = parseExpressionOrStatement(asExpr, false);
                if (block == null) {
//                    block = new HNBlock();
                    log().jerror("X144", "while statement", peek(), "expected while block");
                } else {
                    endToken = block.getEndToken();
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
//            log().jerror("X145", "for statement", "expected ':' or '=' assignment var initializer", c.startToken());
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
////                    log().jerror("X146", "for statement: expected '" + assignOperator.image + "' assignment var initializer but found empty tuple", c.startToken());
////                }
////                List<HNDeclareIdentifier> identifiers = new ArrayList<>();
////                for (int i = 0; i < items.length; i++) {
////                    HNode item = items[i];
////                    if (item instanceof HNIdentifier) {
////                        HNIdentifier hid = (HNIdentifier) item;
////                        HNDeclareIdentifier did = new HNDeclareIdentifier(new JToken[]{hid.startToken().copy()}, null, (HNTypeToken) null, assignOperator, item.startToken(), item.endToken());
////                        identifiers.add(did);
////                    } else {
////                        log().jerror("X147", "for statement: expected '" + assignOperator.image + "' assignment var initializer for expression tuple", c.startToken());
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
//                log().jerror("X148", "for statement", "expected '" + assignOperator.image + "' assignment var initializer", c.startToken());
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
            if (p.id() == HTokenId.LEFT_PARENTHESIS) {
                endToken = p;
                //okkay
            } else {
                pushBack(p);
                log().jerror("X149", "for statement", peek(), "expected '(' after 'for'");
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
                                    log().jerror("X145", "for statement", peek(), "unexpected ','");
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
                                log().jerror("X146", "for statement", peek(), "expected ')'");
                                state = -1;
                                break;
                            }
                            default: {
                                if (!wasSep) {
                                    log().jerror("X147", "for statement", peek(), "expected ','");
                                }
                                wasSep = false;
                                JMessageList err2 = errorList();
                                HNode e = prepareForInitNode(err2);
                                log().addAll(err2);
                                if (e == null) {
                                    wasSep = true;
                                    log().jerror("X148", "for statement", peek(), "expected expression");
                                    skip();
                                } else {
                                    endToken = e.getEndToken();
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
                                log().jerror("X146", "for statement", peek(), "expected ')'");
                                state = -1;
                                break;
                            }
                            default: {
                                if (!wasSep) {
                                    log().jerror("X147", "for statement", peek(), "expected ','");
                                }
                                wasSep = false;
                                HNode e = parseExpression();
                                if (e == null) {
                                    wasSep = true;
                                    log().jerror("X149", "for statement", peek(), "expected expression");
                                    skip();
                                } else {
                                    endToken = e.getEndToken();
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
                                    log().jerror("X145", "for statement", peek(), "unexpected ','");
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
                                log().jerror("X146", "for statement", peek(), "expected ')'");
                                state = -1;
                                break;
                            }
                            default: {
                                if (!wasSep) {
                                    log().jerror("X147", "for statement", peek(), "expected ','");
                                }
                                wasSep = false;
                                HNode e = parseExpression();
                                if (e == null) {
                                    wasSep = true;
                                    log().jerror("X150", "for statement", peek(), "expected expression");
                                    skip();
                                } else {
                                    endToken = e.getEndToken();
                                    hf.addInc(e);
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            if (peek().id() == HTokenId.SEQ_MINUS_GT) {
                LambdaBody lambdaBody = parseFunctionArrowBody(true);
                endToken = lambdaBody.getEndToken();
                hf.setBody(lambdaBody.body);
                hf.setExpressionMode(true);
            } else {
                HNode ok = parseStatement(log());
                if (ok == null) {
                    log().jerror("X152", "for statement", peek(), "expected for statement body");
                } else {
                    endToken = (ok.getEndToken());
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
                        log().jerror("X153", "for statement", c.getStartToken(), "cannot mix simple and iterable for statement constructs");
                    }
                    countNonIterable++;
                } else {
                    if (countNonIterable > 0) {
                        log().jerror("X153", "for statement", c.getStartToken(), "cannot mix simple and iterable for statement constructs");
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
                endToken = id.getEndToken();
            }
            HNDeclareMetaPackage pp = new HNDeclareMetaPackage(n);
            pp.setModuleId(id);
            n = peek();
            if (n.id() == HTokenId.LEFT_CURLY_BRACKET) {
                endToken = n;
                pp.setBody((HNBlock) parseBraces(false, HNBlock.BlocType.PACKAGE_BODY));
                if (pp.getBody() != null) {
                    endToken = (pp.getBody().getEndToken());
                }
            } else if (n.id() == HTokenId.SEMICOLON) {
                endToken = (next());
            } else {
                log().jerror("X154", "package definition", n, "expected ';' or '{'");
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
                    log().jerror("X155", stmtType + " statement", p, stmtType + " label should be positive number (excluding zero which you can apply without any label)");
                    return p;
                } else if (ival < 0) {
                    log().jerror("X156", stmtType + " statement", p, stmtType + " label should be positive number");
                    return p;
                }
                return p;
            } catch (Exception ex) {
                log().jerror("X157", stmtType + " statement", p, "invalid " + stmtType + " label : " + p.image);
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
                    log().jerror("X158", "return statement", peek(), "expected expression");
                }
                return new HNReturn(e, startToken, e == null ? startToken : e.getEndToken());
            }
        }
        return null;
    }

    HNIdentifier parsePackageId() {
        JToken idToken = new JToken();
        idToken.def = new JTokenDef(HTokenId.IDENTIFIER, "IDENTIFIER", HTokenId.IDENTIFIER, "IDENTIFIER", "");
        List<JToken> idTokens = new ArrayList<>();
        StringBuilder idTokensSB = new StringBuilder();
        while (true) {
            JToken t = peek();
            if (t == null) {
                if (idTokensSB.length() == 0) {
                    return null;
                }
                break;
            }
            boolean validNext = false;
            switch (t.id()) {
                case HTokenId.IDENTIFIER:
                case HTokenId.COLON:
                case HTokenId.NUMBER_INT:
                case HTokenId.NUMBER_FLOAT:
                case HTokenId.DOT: {
                    validNext = true;
                    break;
                }
                default: {
                    if ("#".equals(t.image)) {
                        validNext = true;
                    } else {
                        validNext = false;
                        break;
                    }
                }
            }
            if (validNext) {
                next();
                idTokens.add(t);
                if (idTokensSB.length() == 0) {
                    idToken.startColumnNumber = t.startColumnNumber;
                    idToken.startLineNumber = t.startLineNumber;
                    idToken.startCharacterNumber = t.startCharacterNumber;
                }
                idToken.endColumnNumber = t.endColumnNumber;
                idToken.endLineNumber = t.endLineNumber;
                idToken.endCharacterNumber = t.endCharacterNumber;
                idTokensSB.append(t.image);
                idToken.image = idTokensSB.toString();
                idToken.sval = idTokensSB.toString();
            } else {
                break;
            }
        }
        return new HNIdentifier(idToken);
    }

    protected HNode parseImportNode() {
        JToken n = next();
        JToken startToken = n.copy();
        JToken endToken = n.copy();
        if (n.id() == HTokenId.KEYWORD_IMPORT) {
            endToken = n = peek();
            if (n.id() == HTokenId.KEYWORD_PACKAGE) {
//                if (!metaParsingMode) {
//                    log().jerror("X159", "import package statement", n, "import package is not allowed outside package declaration");
//                }
                boolean oldMetaParsingMode = metaParsingMode;
                HNMetaImportPackage node;
                try {
                    metaParsingMode = true;

                    endToken = next();
                    HNode id = parsePackageId();
                    if (id != null) {
                        endToken = id.getEndToken();
                    }
                    node = new HNMetaImportPackage(startToken);
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
                                log().jerror("X160", "import package statement", p, "expected compile,build,test,api,runtime");
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
                                endToken = i.getEndToken();
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
                } finally {
                    metaParsingMode = oldMetaParsingMode;
                }
                return node;
            } else {
                JToken importStartToken = peek().copy();
                StringBuilder sb = new StringBuilder();
                endToken = importStartToken;
                boolean exitWhile = true;
                while (exitWhile) {
                    n = next();
                    switch (n.id()) {
                        case HTokenId.DOT: {
                            if (sb.length() == 0 || sb.charAt(sb.length() - 1) == '.' || sb.charAt(sb.length() - 1) == '*') {
                                log().jerror("X161", "import package statement", importStartToken, "invalid package to import " + sb);
                            } else {
                                sb.append(".");
                            }
                            endToken = n;
                            break;
                        }
                        case HTokenId.SEQ_DOT_ASTERISK:
                        case HTokenId.DOT_ASTERISK2: {
                            if (sb.length() == 0 || sb.charAt(sb.length() - 1) == '.' || sb.charAt(sb.length() - 1) == '*') {
                                log().jerror("X161", "import package statement", importStartToken, "invalid package to import " + sb);
                            } else {
                                sb.append(n.image);
                            }
                            endToken = n;
                            break;
                        }
                        case HTokenId.IDENTIFIER: {
                            if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '.') {
                                log().jerror("X161", "import package statement", importStartToken, "invalid package to import " + sb);
                            } else {
                                sb.append(n.sval);
                            }
                            endToken = n;
                            break;
                        }
                        case HTokenId.ASTERISK: {
                            String sbs = sb.toString();
                            if (sbs.isEmpty() || sbs.endsWith(".")) {
                                sb.append(n.sval);
                                endToken = n;
                                break;
                            } else {
                                endToken = n;
                                log().jerror("X161", "import package statement", importStartToken, "invalid package to import " + sb);
                            }
                            break;
                        }
                        case HTokenId.ASTERISK2: {
                            String sbs = sb.toString();
                            if (sbs.isEmpty() || sbs.endsWith(".")) {
                                sb.append(n.sval);
                                endToken = n.copy();
                                break;
                            } else {
                                endToken = n;
                                log().jerror("X161", "import package statement", importStartToken, "invalid package to import " + sb);
                            }
                            break;
                        }
                        default: {
                            if (n.isKeyword()) {
                                log().jerror("X161", "import package statement", importStartToken, "invalid package to import " + sb);
                                sb.append(n.sval);
                                endToken = n.copy();
                            } else {
                                pushBack(n);
                                exitWhile = false;
                            }
                        }
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
                        log().jerror("X162", "package statement", t, "unexpected '.'");
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
                    log().jerror("X162", "package statement", t, "unexpected Token");
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
        return parseExpression(HParserOptions.simpleExpressionOptions);
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
//                log().jerror("X172", "switch statement", "expected '->' after " + condName, p);
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
//                log().jerror("X172", "switch statement", "expected ':' after " + condName, p);
//            }
        } else {
            log().jerror("X172", "switch statement", p, "expected ':' after " + condName);
//            if (psc.functionalSwitch == null) {
//                log().jerror("X172", "switch statement", "expected ':' after " + condName, p);
//            } else if (psc.functionalSwitch) {
//                log().jerror("X172", "switch statement", "expected ':' after " + condName, p);
//            } else {
//                log().jerror("X172", "switch statement", "expected '->' after " + condName, p);
//            }
//            if (psc.functionalSwitch != null && psc.functionalSwitch) {
//                stmt = parseExpression();
//                if (stmt == null) {
//                    log().jerror("X173", "switch statement", "expected expression", peek());
//                } else {
//                    JToken comma = peek();
//                    if (comma.id() == HTokenId.SEMICOLON) {
//                        psc.endToken = next();
//                    } else {
//                        log().jerror("X173", "switch statement", "expected ';'", peek());
//                    }
//                }
//            } else {
//                stmt = parseStatement(log());
//            }
        }
        stmt = parseExpressionOrStatement(psc.functionalSwitch, true);
        if (stmt == null) {
            log().jerror("X173", "switch statement", peek(), "expected switch " + condName + " " + (psc.functionalSwitch ? "expression" : "statement"));
        } else {
            psc.endToken = stmt.getEndToken();
        }
        return stmt;
    }

    public HNode parseExpressionOrStatement(boolean asExpr, boolean alwaysTerminateWithSemicolon) {
        if (asExpr) {
            HNode e = parseExpression();
            if (e != null) {
                if (alwaysTerminateWithSemicolon) {
                    if (peek().id() == HTokenId.SEMICOLON) {
                        next();
                    } else {
                        log().jerror("X000", null, peek(), "expected ';'");
                    }
                }
                return e;
            }
        }
        return parseStatement(log());
    }

    private boolean isPipeSeparatedException(HNode n) {
        if (n instanceof HNIdentifier) {
            return true;
        }
        if (n instanceof HNTypeToken) {
            return true;
        }
        if (n instanceof HNOpDot) {
            return isPipeSeparatedException(((HNOpDot) n).getLeft()) && isPipeSeparatedException(((HNOpDot) n).getRight());
        }
        return false;
    }

    private HNode[] _toPipeSeparatedIdsOrNull(HNode n) {
        if (isPipeSeparatedException(n)) {
            return new HNode[]{n};
        }
        if (n instanceof HNPars) {
            return null;
        }
        if (n instanceof HNOpBinaryCall) {
            HNode[] left = _toPipeSeparatedIdsOrNull(((HNOpBinaryCall) n).getLeft());
            if (left == null) {
                return null;
            }
            HNode[] right = _toPipeSeparatedIdsOrNull(((HNOpBinaryCall) n).getRight());
            if (right == null) {
                return null;
            }
            return JeepUtils.arrayConcat(HNode.class, left, right);
        }
        return null;
    }

    protected HNode toExpr(HNDeclareToken t) {
        if (t instanceof HNDeclareTokenIdentifier) {
            return new HNIdentifier(((HNDeclareTokenIdentifier) t).getToken());
        }
        if (t instanceof HNDeclareTokenTuple) {
            List<HNode> items = new ArrayList<>();
            for (HNDeclareTokenTupleItem item : ((HNDeclareTokenTuple) t).getItems()) {
                items.add(toExpr(item));
            }
            return new HNTuple(items.toArray(new HNode[0]), t.getStartToken(), t.getSeparators(), t.getEndToken());
        }
        throw new IllegalArgumentException();
    }

    protected HNode parseExpressionOrDeclareIdentifier(JMessageList err) {
        JToken p = peek();
        if (p.id() == HTokenId.KEYWORD_VAR || p.id() == HTokenId.KEYWORD_VAL) {
            ParseDeclareIdentifierContext c = new ParseDeclareIdentifierContext();
            c.options = HParserOptions.DECLARE_ASSIGN_EQ;
            return parseDeclareIdentifier(c, err);
        }
        try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
            ParseDeclareIdentifierContext c = new ParseDeclareIdentifierContext();
            c.options = HParserOptions.DECLARE_ASSIGN_EQ;
            JMessageList err1 = errorList();
            HNDeclareIdentifier id = parseDeclareIdentifier(c, err1);
            if (id != null) {
                err.addAll(err1);
                if (id.getInitValue() == null) {
                    if (id.getIdentifierTypeNode() == null) {
                        return toExpr(id.getIdentifierToken());
                    }
                }
                return id;
            }
            snapshot.rollback();
        }
        return parseExpression();
    }

    protected HNPars parseExpressionOrDeclareIdentifierInPars(JMessageList err, Predicate<HNode> endValidator) {
        if (peek().id() == HTokenId.LEFT_PARENTHESIS) {
            try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
                JTokenBoundsBuilder tbb = new JTokenBoundsBuilder();
                List<JToken> seps = new ArrayList<>();
                seps.add(tbb.visit(next()));
                JMessageList err2 = errorList();
                HNode id = parseExpressionOrDeclareIdentifier(err2);
                tbb.visit(id);
                if (id != null) {
                    if (peek().id() == HTokenId.RIGHT_PARENTHESIS) {
                        seps.add(tbb.visit(next()));
                        HNPars v = new HNPars(
                                new HNode[]{
                                        id
                                }, tbb.getStartToken(), seps.toArray(new JToken[0]), tbb.getEndToken()
                        );
                        if (endValidator != null && endValidator.test(v)) {
                            err.addAll(err2);
                            return v;
                        }
                    }
                }
                snapshot.rollback();
            }
            //return parseExpression(HLParserOptions.noBracesExpressionOptions);
        }
        return null;
    }

    protected HNTryCatch.CatchBranch _parseCatchDecl(boolean asExpr, JMessageList err) {
        if (peek().id() == HTokenId.LEFT_PARENTHESIS) {
            try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
                boolean hasErr = false;
                boolean quiteSureThisIsCatchDecl = false;
                List<JToken> separators = new ArrayList<>();
                separators.add(next());
                List<HNTypeToken> exceptionTypes = new ArrayList<>();
                HNTypeToken tn = parseTypeName();
                exceptionTypes.add(tn);
                while (true) {
                    if (peek().id() == HTokenId.PIPE) {
                        quiteSureThisIsCatchDecl = true;
                        separators.add(next());
                        tn = parseTypeName();
                        if (tn != null) {
                            exceptionTypes.add(tn);
                        } else {
                            hasErr = true;
                            err.jerror("X000", "try statement", peek(), "expected catch exception type");
                            break;
                        }
                    } else {
                        break;
                    }
                }
                HNDeclareTokenIdentifier id = null;
                if (peek().isIdentifier()) {
                    id = new HNDeclareTokenIdentifier(next());
                }
                if (peek().id() == HTokenId.RIGHT_PARENTHESIS) {
                    separators.add(next());
                } else {
                    hasErr = true;
                    err.jerror("X000", "try statement", peek(), "expected ')'");
                }
                if (hasErr && !quiteSureThisIsCatchDecl) {
                    snapshot.rollback();
                    return null;
                }
                return new HNTryCatch.CatchBranch(
                        exceptionTypes.toArray(new HNTypeToken[0]),
                        id, null, asExpr,
                        null, null, separators.toArray(new JToken[0])
                );
            }
        }
        return null;
    }

    protected JNodeResult<HNMap> parseMapObject(boolean acceptEmpty) {
        if (peek().id() == HTokenId.LEFT_CURLY_BRACKET) {
            JMessageList err = errorList();
            JTokenBoundsBuilder tbb = new JTokenBoundsBuilder();
            List<JToken> seps = new ArrayList<>();
            if (false && acceptEmpty) {
                JToken[] emptyMapSuite = nextIds(HTokenId.LEFT_CURLY_BRACKET, HTokenId.RIGHT_CURLY_BRACKET);
                if (emptyMapSuite != null) {
                    for (JToken jToken : emptyMapSuite) {
                        seps.add(tbb.visit(jToken));
                    }
                    return new JNodeResult<>(
                            (HNMap) new HNMap(new HNMap.HNMapEntry[0], tbb.getStartToken(), tbb.getEndToken()).setSeparators(seps.toArray(new JToken[0])),
                            err
                    );
                }
            }
            JToken[] emptyMapSuite = nextIds(HTokenId.LEFT_CURLY_BRACKET, HTokenId.COLON, HTokenId.RIGHT_CURLY_BRACKET);
            if (emptyMapSuite != null) {
                for (JToken jToken : emptyMapSuite) {
                    seps.add(tbb.visit(jToken));
                }
                return new JNodeResult<>(
                        (HNMap) new HNMap(new HNMap.HNMapEntry[0], tbb.getStartToken(), tbb.getEndToken()).setSeparators(seps.toArray(new JToken[0])),
                        err
                );
            }
            try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
                seps.add(tbb.visit(next()));
                List<HNMap.HNMapEntry> entries = new ArrayList<>();
                HNode k = tbb.visit(parseExpression());
                JToken op = null;
                if (k != null) {
                    if (peek().id() == HTokenId.COLON) {
                        //yes this is a map!
                        op = tbb.visit(next());
                    }
                }
                if (op == null) {
                    snapshot.rollback();
                    //return null with no error
                    return new JNodeResult<>(null, errorList());
                }
                HNode v = tbb.visit(parseExpression());
                if (v == null) {
                    err.jerror("X000", "map", peek(), "expected value");
                }
                entries.add(new HNMap.HNMapEntry(k, op, v, k.getStartToken(), tbb.getEndToken()));
                boolean hasNextEntry = true;
                while (hasNextEntry) {
                    JToken p = peek();
                    switch (p.id()) {
                        case HTokenId.RIGHT_CURLY_BRACKET:
                        case HTokenId.EOF: {
                            break;
                        }
                        case HTokenId.COMMA: {
                            seps.add(tbb.visit(next()));
                            k = tbb.visit(parseExpression());
                            if (k == null) {
                                if (peek().id() == HTokenId.RIGHT_CURLY_BRACKET || peek().id() == HTokenId.EOF) {
                                    //this is a trailing comma, ignore it!
                                } else {
                                    err.jerror("X000", "map", peek(), "expected entry key");
                                }
                                hasNextEntry = false;
                            } else {
                                if (peek().id() == HTokenId.COLON) {
                                    //yes this is a map!
                                    op = tbb.visit(next());
                                } else {
                                    err.jerror("X000", "map", peek(), "expected ':'");
                                }
                                v = tbb.visit(parseExpression());
                                if (v == null) {
                                    err.jerror("X000", "map", peek(), "expected value");
                                }
                                entries.add(new HNMap.HNMapEntry(k, op, v, k.getStartToken(), tbb.getEndToken()));
                            }
                        }
                    }
                }
                return new JNodeResult<>(
                        new HNMap(
                                entries.toArray(new HNMap.HNMapEntry[0]),
                                tbb.getStartToken(), tbb.getEndToken()
                        ), err
                );
            }
        }
        //return null with no error
        return new JNodeResult<>(null, errorList());
    }

    protected HNode parseTryCatch(boolean asExpr) {
        ParseTryCatchContext psc = new ParseTryCatchContext();
        if (peek().id() == HTokenId.KEYWORD_TRY) {
            psc.separators.add(psc.bounds.visit(next()));
            psc.resource = parseExpressionOrDeclareIdentifierInPars(errorList(), n -> peek().id() != HTokenId.KEYWORD_CATCH && peek().id() != HTokenId.KEYWORD_FINALLY);
            psc.bounds.visit(psc.resource);
            psc.block = parseExpressionOrStatement(asExpr, false);
            psc.bounds.visit(psc.block);
            if (psc.block == null) {
                log().jerror("X000", "try statement", peek(), "expected try block");
            }
            while (true) {
                JToken p = peek();
                if (p.id() == HTokenId.KEYWORD_CATCH) {
                    JTokenBoundsBuilder cbounds = new JTokenBoundsBuilder();
                    cbounds.visit(p);
                    psc.separators.add(psc.bounds.visit(next()));
                    p = peek();
                    JMessageList err = errorList();
                    HNTryCatch.CatchBranch catchDecl = _parseCatchDecl(asExpr, err);
                    psc.bounds.visit(catchDecl);
                    if (catchDecl != null) {
                        log().addAll(err);
                    }
                    HNode body = parseExpressionOrStatement(asExpr, false);
                    cbounds.visit(body);
                    if (body == null) {
                        log().jerror("X000", "try statement", peek(), "expected catch block");
                    }
                    if (catchDecl == null) {
                        catchDecl = new HNTryCatch.CatchBranch(new HNTypeToken[0], null, body, asExpr, cbounds.getStartToken(), cbounds.getEndToken(), new JToken[0]);
                    } else {
                        catchDecl.setDoNode(body);
                        catchDecl.setStartToken(cbounds.getStartToken());
                        catchDecl.setEndToken(cbounds.getEndToken());
                    }
                    psc.catches.add(catchDecl);

                } else if (p.id() == HTokenId.KEYWORD_FINALLY) {
                    psc.separators.add(psc.bounds.visit(next()));
                    psc.finallyBlock = parseExpressionOrStatement(asExpr, false);
                    psc.bounds.visit(psc.finallyBlock);
                    if (psc.finallyBlock == null) {
                        log().jerror("X000", "try statement", peek(), "expected finally block");
                    }
                    break;
                } else {
                    break;
                }
            }
            HNTryCatch hnTryCatch = new HNTryCatch(psc.bounds.getStartToken());
            hnTryCatch.setResource(psc.resource);
            hnTryCatch.setBody(psc.block);
            hnTryCatch.setEndToken(psc.bounds.getEndToken());
            hnTryCatch.setFinallyBranch(psc.finallyBlock);
            for (HNTryCatch.CatchBranch o : psc.catches) {
                hnTryCatch.addCatch(o);
            }
            hnTryCatch.setSeparators(psc.separators.toArray(new JToken[0]));
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
//                log().jerror("X163", "switch statement", "expected '('", peek());
//            }
            HNSwitch jNodeHSwitch = new HNSwitch(startToken);
            HNode expr = parseExpression(HParserOptions.noBracesExpressionOptions);
            if (expr == null) {
                log().jerror("X164", "switch statement", peek(), "expected switch discriminator");
                JToken f = p.copy();
                f.setError(1, "expected switch discriminator");
                f.sval = "false";
                f.def = tokenizer().getFirstTokenDefinition(x -> x.idName.equals("false"));
                expr = new HNPars(new HNode[]{new HNLiteral(false, f)}, peek(), new JToken[0], peek());
                expr.setStartToken(peek());
            } else {
                expr = HNodeUtils.assignToDeclare(expr, true);
                psc.endToken = expr.getEndToken();
            }
            jNodeHSwitch.setExpr(expr);
            p = peek();
            if (!p.isImage("{")) {
                log().jerror("X167", "switch statement", peek(), "expected { after switch keyword");
            } else {
                psc.endToken = next();
            }
            psc.functionalSwitch = asExpr;

            while (true) {
                p = peek();
                if (p.isEOF()) {
                    log().jerror("X168", "switch statement", peek(), "expected closing '}'");
                    break;
                } else if (p.id() == HTokenId.KEYWORD_CASE) {
                    if (psc.defaultVisited) {
                        log().jerror("X169", "switch statement", peek(), "unexpected switch case after default");
                    }
                    if (psc.isVisited || psc.ifVisited) {
                        log().jerror("X170", "switch statement", peek(), "cannot  merge case with if or is constructs");
                    }
                    psc.caseVisited = true;
                    psc.endToken = next();
                    List<HNode> matches = splitByBinaryOperator(parseExpressionSimple(), "|");
                    if (matches.size() == 0) {
                        log().jerror("X171", "switch statement", peek(), "expected case expression");
                    } else {
                        psc.endToken = matches.get(matches.size() - 1).getEndToken();
                    }
                    HNode stmt = _parseSwitch_parseCaseBody(psc, "case");
                    jNodeHSwitch.add(new HNSwitch.SwitchCase(matches, psc.op, stmt, p, psc.endToken));
                } else if (p.id() == HTokenId.KEYWORD_IF) {
                    if (psc.defaultVisited) {
                        log().jerror("X174", "switch statement", peek(), "unexpected switch case after default");
                    }
                    if (psc.isVisited || psc.caseVisited) {
                        log().jerror("X175", "switch statement", peek(), "cannot  merge case with if or is constructs");
                    }
                    psc.ifVisited = true;
                    JToken ss = p.copy();
                    psc.endToken = next();
                    HNode m = parseExpression();
                    if (m == null) {
                        log().jerror("X176", "switch statement", peek(), "expected case expression");
                    }
                    HNode stmt = _parseSwitch_parseCaseBody(psc, "case");
                    jNodeHSwitch.add(new HNSwitch.SwitchIf(m, psc.op, stmt, ss, psc.endToken));
                } else if (p.isImage("is")) {
                    if (psc.defaultVisited) {
                        log().jerror("X179", "switch statement", peek(), "unexpected switch case after default");
                    }
                    if (psc.caseVisited || psc.ifVisited) {
                        log().jerror("X180", "switch statement", peek(), "cannot  merge case with if or is constructs");
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
                                log().jerror("X181", "switch statement", peek(), "expected type name");
                                break;
                            } else {
                                psc.endToken = t.getEndToken();
                                if (t.getTypename().varsCount() > 0) {
                                    log().jerror("X182", "switch statement", peek(), "cannot switch on generic types");
//                                    t = t.rawType();
                                }
                                typeNames.add(t);
                            }
                        }
                        vn = peek();
                        if (vn.isEOF()) {
                            log().jerror("X183", "switch statement", peek(), "expected type name");
                            break;
                        } else if (vn.isOperator("|")) {
                            psc.endToken = next();
                        } else {
                            break;
                        }
                    }
                    if (typeNames.isEmpty()) {
                        log().jerror("X184", "switch statement", peek(), "expected type name");
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
                        log().jerror("X187", "switch statement", peek(), "default already matched");
                    }
                    psc.defaultVisited = true;
                    HNode stmt = _parseSwitch_parseCaseBody(psc, "default");
                    jNodeHSwitch.setElse(stmt);
                    p = peek();
                    if (p.id() == HTokenId.RIGHT_CURLY_BRACKET || p.id() == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
                        psc.endToken = next();
                        break;
                    } else {
                        log().jerror("X189", "switch statement", peek(), "expect switch '}'");
                        break;
                    }
                } else if (p.id() == HTokenId.RIGHT_CURLY_BRACKET || p.id() == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
                    psc.endToken = next();
                    if (jNodeHSwitch.getCases().isEmpty()) {
                        log().jerror("X191", "switch statement", peek(), "expect case expression");
                    }
                    break;
                } else {
                    log().jerror("X192", "switch statement", peek(), "expect case or default keywords");
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
        HNAnnotationList annotations;
        JTypeNameAndInit tt;
        JToken varVal;
        HDeclarationOptions options;
        JTokenBoundsBuilder bounds = new JTokenBoundsBuilder();
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
                return body.getEndToken();
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

    class ParseTryCatchContext {

        public HNode finallyBlock;
        public List<HNTryCatch.CatchBranch> catches = new ArrayList<>();
        public List<JToken> separators = new ArrayList<>();
        HNPars resource;
        HNode block;
        JTokenBoundsBuilder bounds = new JTokenBoundsBuilder();
        JToken fctOp;
        boolean fct;
    }
}
