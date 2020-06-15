package net.vpc.hadralang.compiler.parser;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.*;
import net.vpc.common.jeep.core.tokens.JTokenDef;
import net.vpc.common.jeep.core.types.DefaultTypeName;
import net.vpc.common.jeep.core.types.JTypeNameBounded;
import net.vpc.common.jeep.impl.functions.JNameSignature;
import net.vpc.common.jeep.impl.tokens.JTokenId;
import net.vpc.common.jeep.util.JTokenUtils;
import net.vpc.hadralang.compiler.core.HTokenId;
import net.vpc.hadralang.compiler.core.HTokenIdOffsets;
import net.vpc.hadralang.compiler.core.elements.HNElementMetaPackageArtifact;
import net.vpc.hadralang.compiler.core.elements.HNElementMetaPackageGroup;
import net.vpc.hadralang.compiler.core.elements.HNElementMetaPackageVersion;
import net.vpc.hadralang.compiler.parser.ast.*;
import net.vpc.hadralang.compiler.utils.HNodeUtils;
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

    //    private static final HashSet<String> unacceptableWordSuffixedForNumbers = new HashSet<>();
//
//    static {
//        unacceptableWordSuffixedForNumbers.add("(");
//        unacceptableWordSuffixedForNumbers.add("[");
//        unacceptableWordSuffixedForNumbers.add("{");
//    }
    private boolean metaParsingMode = false;
    private JExpressionOptions simpleExpressionOptions;

    public HLParser(JTokenizer tokenizer, JCompilationUnit compilationUnit, JContext context) {
        super(tokenizer, compilationUnit, context);
        setNodeFactory(new HLFactory(compilationUnit, context));

        JExpressionOptions def = new JExpressionOptions();
        def.binary = new JExpressionBinaryOptions();
        def.binary.excludedListOperator = true;
        def.binary.excludedImplicitOperator = true;
        def.binary.excludedBinaryOperators = null;

        def.unary = new JExpressionUnaryOptions();
        def.unary.excludedPrefixParenthesis = false;
        def.unary.excludedPrefixBrackets = true;
        def.unary.excludedPrefixBraces = true;
        def.unary.excludedPostfixParenthesis = false;
        def.unary.excludedPostfixBrackets = false;
        def.unary.excludedPostfixBraces = true;
        def.unary.excludedPrefixUnaryOperators = null;
        def.unary.excludedPostfixUnaryOperators = null;
        setDefaultExpressionOptions(def);

        JExpressionOptions simple = new JExpressionOptions();
        simple.binary = new JExpressionBinaryOptions();
        simple.binary.excludedListOperator = true;
        simple.binary.excludedImplicitOperator = true;
        simple.binary.excludedBinaryOperators = new HashSet<>(Arrays.asList(":", "->"));
        simple.unary = new JExpressionUnaryOptions();
        simple.unary.excludedPrefixParenthesis = true;
        simple.unary.excludedPrefixBrackets = true;
        simple.unary.excludedPrefixBraces = true;
        simple.unary.excludedPostfixParenthesis = true;
        simple.unary.excludedPostfixBrackets = true;
        simple.unary.excludedPostfixBraces = true;
        simple.unary.excludedPrefixUnaryOperators = new HashSet<>(Arrays.asList("++", "--"));
        simple.unary.excludedPostfixUnaryOperators = new HashSet<>(Arrays.asList("++", "--"));
        simpleExpressionOptions = simple;
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
        HLDeclarationOptions argOptions = new HLDeclarationOptions()
                .setAcceptModifiers(false)
                .setAcceptFunction(false).setAcceptClass(false).setAcceptInValue(false)
                .setAcceptVarArg(true);
        try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
            argOptions.setNoMessages(true);
            ArrayList<JCompilerMessage> silencedMessages = new ArrayList<>();
            JListWithSeparators<HNDeclareIdentifier> li = parseGroupedList("lambda expression", "argument declaration",
                    () -> parseArgumentDeclaration(argOptions), "(", ",", ")", silencedMessages);
            if (li.getEndToken() != null) {
                JToken endToken = li.getEndToken();
                JToken op = peek();
                if (op.isImage("->")) {
                    //this is for sure a lambda expression so if any error was tracked, reported it now...
                    for (JCompilerMessage silencedMessage : silencedMessages) {
                        log().add(silencedMessage);
                    }
                    endToken = op = next();
                    //this is a lambda expression
                    HNode e = parseExpression();
                    if (e == null) {
                        log().error("X127", null, "lambda expression: expected body", peek());
                    } else {
                        endToken = e.endToken();
                    }
                    HNPars decl = new HNPars(
                            li.getItems().toArray(new HNode[0]),
                            li.getStartToken(),
                            li.getSeparatorTokens(),
                            li.getEndToken()
                    );
                    return getNodeFactory().createLambdaExpression(decl,
                            op, e, li.getStartToken(), endToken
                    );
                }
            }
            snapshot.rollback();
        }
        return parseParenthesis("parenthesis");
    }

    @Override
    protected HNode parseExpressionUnary(int opPrecedence, JExpressionOptions options) {
        JToken n = peek();
        switch (n.image) {
            case "for": {
                return parseFor();
            }
            case "if": {
                return parseIf();
            }
            case "while": {
                return parseWhile();
            }
            case "switch": {
                return parseSwitch();
            }
            case "boolean":
            case "byte":
            case "short":
            case "char":
            case "int":
            case "long":
            case "float":
            case "double": {
                return parseTypeConstructorCall();
            }
        }
        HNode jNode = (HNode) super.parseExpressionUnary(opPrecedence, options);
//        if (jNode == null) {
//            JToken peek = peek();
//            if (peek.isWord()) {
//                return parseArrayInitialization();
//            }
//        }
        return jNode;
    }

    protected HNode parseExpressionUnarySuffix(int opPrecedence, HNode middle, JExpressionOptions options, ParseExpressionUnaryContext ucontext) {
        JToken o = peek();
        if (o.def.id == HTokenId.SUPERSCRIPT) {
            // chech ofr superscript exponents
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
            return new HNOpBinaryCall(HNodeUtils.createToken("^"), middle, new HNLiteral(intv, next), middle.startToken(), middle.endToken());
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
                        HNode suffix = parseExpressionTerminal(options);
                        return new HNOpBinaryCall(HNodeUtils.createToken("*"), middle, suffix, middle.startToken(), middle.endToken());
                    }
                }
            }
        }
        return (HNode) super.parseExpressionUnarySuffix(opPrecedence, middle, options, ucontext);
    }

    public HNode parsePrefixParsNode(JExpressionOptions options) {
        JToken t = peek().copy();
        if (!t.isImage("(")) {
            return null;
        }
        try (JTokenizerSnapshot snapshot = tokenizer().snapshot()) {
            JToken startToken = next();
            HNode expr = parseTypeName();
            if (expr != null) {
                JToken endToken = next();
                if (endToken.isImage(")")) {
                    JToken parsEnd = peek();
                    HNode toCast = parseExpression();
                    if (toCast != null) {
                        return getNodeFactory().createPrefixParenthesisNode(expr, toCast, parsEnd, startToken, toCast.endToken());
                    }
                }
            }
            snapshot.rollback();
        }
        return null;
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

    public HNode parseBraces() {
        return parseBraces(HNBlock.BlocType.LOCAL_BLOC);
    }

    protected HNode parseAndBuildExpressionBinary(JToken op, HNode o1, int opPrecedence, JExpressionOptions options) {
        JToken next = peek();
        if (op.isImage(".") && next.isKeyword()) {
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
        } else if (op.isImage("is")) {
            JToken n = peek();
            if (n.isKeyword("null")) {
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

    protected static HNTypeToken createNullTypeToken(JToken nullToken) {
        return new HNTypeToken(nullToken, new DefaultTypeName("null"), null, null, null, nullToken, nullToken);
    }

    @Override
    protected HNode parseExpressionBinary(int opPrecedence, JExpressionOptions options) {
        return super.parseExpressionBinary(opPrecedence, options);
    }

    protected HNode parseAndBuildListOpNodeElement(HNode o1, int opPrecedence, JToken token, JExpressionOptions options) {
        log().error("X108", null, "list operator not supported in this context", peek());
        token = token.copy();
        HNode o2 = parseExpressionBinary(opPrecedence, options);
        JToken s = o1 != null && o1.startToken() != null ? o1.startToken() : token;
        return new HNTuple(new HNode[]{o1, o2}, s, new JToken[0], o2.endToken());
//        if (o2 instanceof HNInvokerCall && (((HNInvokerCall) o2).getName()).equals(token.image)) {
//            HNInvokerCall o21 = (HNInvokerCall) o2;
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
    protected HNode parseParenthesis(String name) {
        JToken startToken = peek();
        if (name == null) {
            name = "parenthesis";
        }
        JListWithSeparators<HNode> expression = parseGroupedList(name, "expression", () -> parseExpression(), "(", ",", ")", null);
        if (expression == null) {
            return null;
        }
        return getNodeFactory().createParsNode(expression.getItems(), expression.getStartToken(), expression.getSeparatorTokens(), expression.getEndToken());
    }

    @Override
    public HNode parseExpressionTerminal(JExpressionOptions options) {
        JToken token = peek();
        String timage = token.image;
        switch (timage) {
            case "this": {
                JToken next = next();
                return new HNThis(null, next);
            }
            case "super": {
                JToken next = next();
                return new HNSuper(null, next);
            }
            case "boolean":
            case "byte":
            case "char":
            case "int":
            case "long":
            case "float":
            case "double": {
                JToken[] n2 = peek(2);
                if (n2.length == 2
                        && (n2[1].isImage("(")
                        || n2[1].isImage("::") //method handle
                )) {
                    token = next(); //skip one!
                    return new HNTypeToken(
                            token,
                            DefaultTypeName.of(timage),
                            new HNTypeToken[0],
                            new HNTypeToken[0],
                            new HNTypeToken[0],
                            token,
                            token);
                }
                break;
            }
        }
        switch (token.def.ttype) {
            case JTokenType.TT_TEMPORAL: {
                token = next();
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
                return getNodeFactory().createLiteralNode(parsed, token);
            }
            case JTokenType.TT_REGEX: {
                token = next();
                if (token.isError()) {
                    log().warn("X000", null, "token never terminated", token);
                }
                return getNodeFactory().createLiteralNode(Pattern.compile(token.sval), token);
            }
            case JTokenType.TT_STRING_INTERP: {
                return parseStringInterp();
            }
            case JTokenType.TT_STRING: {
                switch (token.def.id) {
                    case JTokenId.DOUBLE_QUOTES: {
                        token = next();
                        if (token.isError()) {
                            log().warn("X000", null, "token never terminated", token);
                        }
                        return getNodeFactory().createLiteralNode(token.sval, token);
                    }
                    case JTokenId.SIMPLE_QUOTES: {
                        token = next();
                        if (token.isError()) {
                            log().warn("X000", null, "token never terminated", token);
                        }
                        if (token.sval.length() == 1) {
                            return getNodeFactory().createLiteralNode(token.sval.charAt(0), token);
                        } else {
//                            log().error("X110", "Invalid character token", token);
                            return getNodeFactory().createLiteralNode(token.sval, token);
                        }
                    }
                    case JTokenId.ANTI_QUOTES: {
                        token = next();
                        if (token.isError()) {
                            log().warn("X000", null, "token never terminated", token);
                        }
                        return getNodeFactory().createVarNameNode(token);
                    }
                    default: {
                        token = next();
                        log().error("X111", null, "unsupported literal string token", token);
                        return getNodeFactory().createLiteralNode(token.sval, token);
                    }
                }
            }
            case JTokenType.TT_NUMBER: {
                switch (token.def.id) {
                    case JTokenId.NUMBER_INT: {
                        token = next();
                        return getNodeFactory().createLiteralNode(HNumberEvaluator.H_NUMBER.eval(token.def.id, token.image, token.sval, "int"), token);
                    }
                    case JTokenId.NUMBER_FLOAT: {
                        token = next();
                        return getNodeFactory().createLiteralNode(HNumberEvaluator.H_NUMBER.eval(token.def.id, token.image, token.sval, "float"), token);
                    }
                    case JTokenId.NUMBER_INFINITY: {
                        token = next();
                        return getNodeFactory().createLiteralNode(HNumberEvaluator.H_NUMBER.eval(token.def.id, token.image, token.sval, "float"), token);
                    }
                    default: {
                        throw new JShouldNeverHappenException();
                    }
                }
            }
        }
        return super.parseExpressionTerminal(options);
    }

    public HNode parseBraces(HNBlock.BlocType type) {
        JToken n = peek();
        JToken endToken = n;
        if (n.def.id != HTokenId.LEFT_CURLY_BRACKET) {
            pushBack(n);
            return null;
        }
        endToken = next();
        JListWithSeparators<HNode> elements = parseStatements();
        if (elements.getEndToken() != null) {
            endToken = elements.getEndToken();
        }
        JToken p = peek();
        if (p.def.id == HTokenId.RIGHT_CURLY_BRACKET || p.def.id == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
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
            } else if (n1.def.id == HTokenId.RIGHT_CURLY_BRACKET || n1.def.id == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
                pushBack(n1);
                break;
            } else {
                pushBack(n1);
                HNode t = null;
                while (true) {
                    t = parseStatement();
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
        if (n.def.id == HTokenId.STRING_INTERP_START) {
            startToken = next();
            tokens.add(startToken);
            endToken = startToken;
        } else {
            return null;
        }
        boolean end = false;
        while (!end) {
            n = peek();
            switch (n.def.id) {
                case JTokenType.TT_EOF: {
                    end = true;
                    log().error("X000", null, "missing '\"'", n);
                    break;
                }
                case HTokenId.STRING_INTERP_TEXT: {
                    JToken next = next();
                    tokens.add(endToken = next);
                    if (n.isError()) {
                        log().error("X000", null, "missing '\"'", n);
                    }
                    break;
                }
                case HTokenId.STRING_INTERP_END: {
                    end = true;
                    JToken next = next();
                    tokens.add(endToken = next);
                    if (n.isError()) {
                        log().error("X000", null, "missing '\"'", n);
                    }
                    break;
                }
                case HTokenId.STRING_INTERP_DOLLAR_END: {
                    JToken next = next();
                    tokens.add(endToken = next);
                    if (n.isError()) {
                        log().error("X000", null, "missing '\"'", n);
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
                            if (next.def.id == HTokenId.LEFT_CURLY_BRACKET) {
                                next = peek();
                                if (next.def.id == HTokenId.RIGHT_CURLY_BRACKET || next.def.id == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
                                    tokens.add(endToken = next());
                                } else {
                                    log().error("X000", null, "missing '}'", next);
                                }
                            }
                        } else {
                            end = true;
                            log().error("X000", null, "missing valid expression", next);
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
                        if (next.def.id == JTokenId.IDENTIFIER) {
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

    public HNode parseStatement() {
        JToken p = peek();
        switch (p.def.ttype) {
            case JTokenType.TT_KEYWORD: {
                switch (p.sval) {
                    //what to choose??
                    case "package": {
                        return parsePackageNode();
                    }
                    case "def":
                    case "public":
                    case "private":
                    case "protected":
                    case "static":
                    case "final":
                    case "var": {
                        return parseDeclaration(new HLDeclarationOptions().setAcceptInValue(false).setAcceptMultiVars(true).setAcceptVarArg(false));
                    }
                    case "class": {
                        HNode dec = null;
                        if (metaParsingMode) {
                            log().error("X113", "class declaration", "class definitions are not allowed in package declaration", p);
                            //ignore it;
                        }
                        dec = parseDeclaration(new HLDeclarationOptions().setAcceptInValue(false).setAcceptVarArg(false));
                        if (dec instanceof HNDeclareIdentifier) {
                            ((HNDeclareIdentifier) dec).setSyntacticType(HNDeclareIdentifier.SyntacticType.FIELD);
                        }
                        if (!metaParsingMode) {
                            return dec;
                        }
                        break;
                    }
                    case "import": {
                        return parseImportNode();
                    }
                    case "break": {
                        return parseBreak();
                    }
                    case "continue": {
                        return parseContinue();
                    }
                    case "return": {
                        return parseReturn();
                    }
                    case "if": {
                        return parseIf();
                    }
                    case "switch": {
                        return parseSwitch();
                    }
                    case "while": {
                        return parseWhile();
                    }
                    case "for": {
                        return parseFor();
                    }
                }
                break;
            }
            default: {
                switch (p.def.id) {
                    case HTokenId.LEFT_CURLY_BRACKET: {
                        return parseBraces(HNBlock.BlocType.LOCAL_BLOC);
                    }
                }
            }
        }
        //boolean acceptModifiers, boolean acceptVar, boolean acceptFunction, boolean acceptClass,
        //                                    boolean requiredSemiColumnForVar, boolean acceptEqValue, boolean acceptInValue
        HNode n = parseDeclaration(new HLDeclarationOptions().setAcceptInValue(false).setAcceptMultiVars(true).setAcceptVarArg(false));
        if (n != null) {
            return n;
        }
        HNode expr = parseExpression();
        if(expr!=null){
            if(isRequireSemiColumn(expr)){
                JToken sc = peek();
                if(sc.def.id==HTokenId.SEMICOLON){
                    next();
                }else{
                    log().error("X000",null,"missing ';'",sc);
                }
            }
        }
        return expr;
    }
    private boolean isRequireSemiColumn(HNode t){
        switch (t.id()){
            case H_IF:
            case H_WHILE:
            case H_FOR:
            case H_DECLARE_INVOKABLE:
            case H_DECLARE_TYPE:
            case H_DECLARE_META_PACKAGE:
            case H_SWITCH:{
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
                JToken startToken2 = t3;
                if (t3.isImage("(")) {
                    setter = parseFunctionDeclaration(0, true, tt.typeToken,
                            false, startToken2
                    );
                } else if (t3.isImage(")")) {
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
        if (n.isImage("<")) {
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
                                && ((peek().isImage("super") || peek().isImage("extends")))) {
                            varName = a.getNameToken();
                        }
                    }
                }
                if (varName != null) {
                    //expect super or extends
                    n = peek();
                    if (n.isImage("extends")) {
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
                    } else if (n.isImage("super")) {
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
                if (n.isImage(",")) {
                    separators.add(next());
                } else {
                    break;
                }
            }
            n = peek();
            if (n.isOperator(">")) {
                endToken = next();
                separators.add(endToken);
            } else if (n.isOperator(">>")) {
                //explode operator
                n = next();
                endToken = n.copy();
                endToken.image = ">";
                endToken.endColumnNumber--;
                endToken.endCharacterNumber--;
                separators.add(endToken);

                n.image = ">";
                n.sval = ">";
                n.startColumnNumber++;
                n.startCharacterNumber++;
                pushBack(n);
            } else if (n.isOperator(">>>")) {
                //explode operator
                n = next();
                endToken = n.copy();
                endToken.image = ">";
                endToken.endColumnNumber -= 2;
                endToken.endCharacterNumber -= 2;
                separators.add(endToken);

                n.image = ">";
                n.sval = ">";
                n.startColumnNumber++;
                n.startCharacterNumber++;
                pushBack(n);
                n.startColumnNumber++;
                n.startCharacterNumber++;
                pushBack(n);
            } else if (n.isOperator(">>>>")) {
                //explode operator
                n = next();
                endToken = n.copy();
                endToken.image = ">";
                endToken.endColumnNumber -= 3;
                endToken.endCharacterNumber -= 3;
                separators.add(endToken);

                n.image = ">";
                n.sval = ">";
                n.startColumnNumber++;
                n.startCharacterNumber++;
                pushBack(n);
                n.startColumnNumber++;
                n.startCharacterNumber++;
                pushBack(n);
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
                if (n.isImage("<")) {
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
                if (n.isImage("[")) {
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

    private HNDeclareTokenTupleItem parseDeclareTokenTupleItem(List<JCompilerMessage> reportedErrors) {
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
                        reportedErrors.add(JCompilerMessage.error("X000", null, "expected ','", x));
                        break;
                    } else if (x.isImage(",")) {
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
                        reportedErrors.add(JCompilerMessage.error("X000", null, "expected ','", x));
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
            if (u.isImage(".")) {
                all.add(u);
                u = next();
                if (u.isIdentifier()) {
                    all.add(u);
                } else if (u.isImage("(")) {
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
            if (p.isKeyword()) {
                switch (p.sval) {
                    case "public": {
                        modifiers |= Modifier.PUBLIC;
                        break;
                    }
                    case "package": {
                        modifiers |= HUtils.PACKAGE;
                        break;
                    }
                    case "private": {
                        modifiers |= Modifier.PRIVATE;
                        break;
                    }
                    case "protected": {
                        modifiers |= Modifier.PROTECTED;
                        break;
                    }
                    case "static": {
                        modifiers |= Modifier.STATIC;
                        break;
                    }
                    case "final": {
                        modifiers |= Modifier.FINAL;
                        break;
                    }
                    case "abstract": {
                        modifiers |= Modifier.ABSTRACT;
                        break;
                    }
                    case "transient": {
                        modifiers |= Modifier.TRANSIENT;
                        break;
                    }
                    case "volatile": {
                        modifiers |= Modifier.VOLATILE;
                        break;
                    }
                    case "strictfp": {
                        modifiers |= Modifier.STRICT;
                        break;
                    }
                    case "const": {
                        modifiers |= HUtils.CONST;
                        break;
                    }
                    case "readonly": {
                        modifiers |= HUtils.READONLY;
                        break;
                    }
                    default: {
                        accepted = false;
                    }
                }

            } else {
                accepted = false;
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
    private HNode parseDeclaration(
            HLDeclarationOptions options
    ) {
        JTokenizerSnapshot snapshot = tokenizer().snapshot();
        try {
            int modifiers = 0;
            int modifiers0 = 0;
            JToken startToken = peek().copy();
            JToken endToken = startToken;
            if (options.acceptModifiers) {
                modifiers = parseModifiers();
                modifiers0 = modifiers;
                if (Modifier.isStatic(modifiers)) {
                    if (peek().def.id == HTokenId.LEFT_CURLY_BRACKET) {
                        if (modifiers != Modifier.STATIC) {
                            log().error("X116", null, "static initializer should not have modifiers", peek());
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
            if (t.isKeyword("class")) {
                if (!options.acceptClass) {
                    log().error("X117", "class definition", "not allowed class definition in the current context", t);
                }
                skip();
                return parseClassDeclaration(modifiers, startToken);
            } else if (t.isKeyword("def") || t.isKeyword("constructor") || t.isKeyword("void")) {
                if (!options.acceptFunction) {
                    log().error("X118", "function/method definition", "not allowed class method/function/constructor definition in the current context", t);
                }
                if (t.isKeyword("void")) {
                    JToken voidToken = next();
                    pushBack(JTokenUtils.createKeywordToken("def"));
                    pushBack(voidToken);
                }
                return parseFunctionDeclaration(modifiers, false, null, true, startToken);
            }
            JTypeNameAndInit tt = null;
            JToken varVal = null;
            if (options.acceptVar && (t.isKeyword("var"))) {
                next();
                varVal = t.copy();
            } else if (options.acceptVar && t.isKeyword("val")) {
                next();
                varVal = t.copy();
            } else {
                tt = parseTypeNameAndInit(true, options.acceptVarArg);
                if (tt == null) {
                    if (modifiers0 != 0) {
                        log().error("X119", null, "identifier definition : unexpected modifier", peek());
                        return null;
                    } else {
                        //this is not a declaration!
                        return null;
                    }
                }
            }
            HNDeclareToken varIds = null;
            if ( /*(varVal != null) && */ peek().def.id==HTokenId.LEFT_PARENTHESIS) {
                ArrayList<JCompilerMessage> reportedErrors = new ArrayList<>();
                HNDeclareTokenTupleItem u = parseDeclareTokenTupleItem(reportedErrors);
                if (u != null) {
                    varIds = u;
                    JToken pp = peek();
                    if(!pp.isImage("=") && !pp.isImage(":")){
                        //not a tuple assignment!
                        snapshot.rollback();
                        return null;
                    }
                    for (JCompilerMessage reportedError : reportedErrors) {
                        log().add(reportedError);
                    }
                } else {
                    snapshot.rollback();
                    return null;
                }
                //tuple definition
            } else {
                JToken name = parseNameWithPackageSingleToken();
                if (name == null) {
                    //this is not a name. It should be an expression
                    snapshot.rollback();
                    return null;
                } else {
                    endToken = name;
                }
                List<HNDeclareTokenTupleItem> allVarNames = new ArrayList<>();
                allVarNames.add(new HNDeclareTokenIdentifier(name));
                if (options.isAcceptMultiVars()) {
                    while (peek().isImage(",")) {
                        next();
                        JToken name2 = parseNameWithPackageSingleToken();
                        if (name2 == null) {
                            log().error("X120", "identifier definition", "expected name", peek());
                            break;
                        } else {
                            endToken = name2.copy();
                            allVarNames.add(new HNDeclareTokenIdentifier(endToken));
                        }
                    }
                }
                for (HNDeclareTokenTupleItem vname : allVarNames) {
                    checkVarName(vname);
                }
                if (allVarNames.size() == 1) {
                    varIds = allVarNames.get(0);
                } else {
                    List<HNDeclareTokenIdentifier> ids = new ArrayList<>();
                    for (HNDeclareTokenTupleItem v : allVarNames) {
                        if (v instanceof HNDeclareTokenIdentifier) {
                            ids.add((HNDeclareTokenIdentifier) v);
                        } else {
                            log().error("X000", "identifier definition", "expected identifier", v.startToken());
                        }
                    }
                    varIds = new HNDeclareTokenList(ids.toArray(new HNDeclareTokenIdentifier[0]),
                            allVarNames.get(0).startToken(),
                            allVarNames.get(allVarNames.size() - 1).endToken()
                    );
                }
            }
            HNode val = null;
            JToken opToken = null;
            if (tt == null || tt.inits.length == 0) {
                JToken n = next();
                if (n.isOperator("=") && (varVal != null || options.acceptEqValue)) {
                    opToken = n.copy();
                    endToken = opToken;
                    val = parseExpression();
                    if (val == null) {
                        log().error("X121", "identifier definition", "expected value assignment", peek());
                    } else {
                        endToken = val.endToken();
                    }
                } else if (n.isOperator(":") && (varVal != null || options.acceptInValue)) {
                    opToken = n.copy();
                    endToken = opToken;
                    val = parseExpression();
                    if (val == null) {
                        log().error("X121", "identifier definition", "expected value assignment", peek());
                    } else {
                        endToken = val.endToken();
                    }
                } else if (n.isImage("(")) {
                    //this is a constructor call!
                    pushBack(n);
                    JListWithSeparators<HNode> argument_declaration = parseParsList("constructor", "constructor argument", ()
                                    -> parseExpression()
                            //                                                    parseArgumentDeclaration(new HLDeclarationOptions().setAcceptFunction(false).setAcceptClass(false).setRequiredSemiColumnForVar(false).setAcceptVarArg(true))
                    );
                    val = new HNObjectNew(tt.typeToken, argument_declaration == null ? new HNode[0]
                            : argument_declaration.getItems().toArray(new HNode[0]), argument_declaration.getStartToken(), argument_declaration.getEndToken()
                    );
                    endToken = val.endToken();
                } else {
                    if (varVal != null) {
                        log().error("X121", "identifier definition", "expected value assignment", t);
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
                        endToken = setter.endToken();
                    }
                    t2 = peek();
                    if (t2.isImage(")")) {
                        endToken = next();
                    } else {
                        log().error("X122", "identifier definition", "expected ')'", t);
                    }
                }
                if (setter != null && tt.inits.length == 0) {
                    log().error("S052", null, "initialized array is missing initializer", startToken);
                }
                val = new HNArrayNew(tt.typeToken, tt.inits, setter, startToken, endToken);
                //create init!!
            }
            if (opToken == null) {
                opToken = HNodeUtils.createToken("=");
            }
            HNDeclareIdentifier h = new HNDeclareIdentifier(
                    varIds,
                    val,
                    (tt == null) ? null : tt.typeToken,
                    opToken, startToken, endToken
            );
            h.setStartToken(startToken);
            h.setModifiers(modifiers);
            h.setInitValue(val);
            if (tt == null || tt.typeToken == null) {
                h.setIdentifierTypeName(null);
            } else {
                h.setIdentifierTypeName(tt.typeToken);
            }
            return h;
        } finally {
            snapshot.dispose();
        }
    }

    private void checkVarName(HNDeclareTokenTupleItem vname) {
        if (vname instanceof HNDeclareTokenIdentifier) {
            JToken ttt = ((HNDeclareTokenIdentifier) vname).getToken();
            if (ttt.image.indexOf('.') >= 0) {
                log().error("X120", "identifier definition", "invalid identifier name ", ttt);
            }
        } else {
            throw new JFixMeLaterException();
        }
    }

    private HNDeclareIdentifier parseArgumentDeclaration(HLDeclarationOptions options) {
        JToken[] peeked = peek(2);
        if (peeked.length >= 2 && peeked[0].isIdentifier() && (peeked[1].isImage(",") || peeked[1].isImage(")"))) {
            //this is an untyped declaration
            JToken w = next();
            JToken opToken = HNodeUtils.createToken("=");
            HNDeclareIdentifier did = new HNDeclareIdentifier(
                    new HNDeclareTokenIdentifier(w),
                    null, (HNTypeToken) null, opToken, peeked[0], w);
            did.setStartToken(peeked[0]);
            return did;
        }
        //boolean acceptModifiers, boolean acceptVar, boolean acceptFunction, boolean acceptClass,
        //                                    boolean requiredSemiColumnForVar, boolean acceptEqValue, boolean acceptInValue

        return (HNDeclareIdentifier) parseDeclaration(options);
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
        JListWithSeparators<HNode> li = parseParsList("extends", "argument declaration", () -> parseExpression());
        HNExtends dec = new HNExtends(nameAndNamespace[1], startNode, li == null ? endToken : li.getEndToken());
        dec.setPackageName(nameAndNamespace[0]);
        dec.setModifiers(modifiers);
        dec.setArguments(li == null ? new ArrayList<>() : li.getItems());
        return dec;
    }

    private HNode parseFunctionDeclaration(
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

        HLDeclarationOptions argOptions = new HLDeclarationOptions()
                .setAcceptModifiers(constr)
                .setAcceptFunction(false).setAcceptClass(false).setAcceptInValue(false)
                .setAcceptVarArg(true);
        JListWithSeparators<HNDeclareIdentifier> li = parseParsList("function/method declaration", "argument declaration", () -> parseArgumentDeclaration(argOptions));
        if (li == null) {
            log().error("X126", "function/method declaration", "missing parameters", peek());
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
        if (n.isImage(";")) {
            f.setEndToken(next());
            modifiers |= Modifier.ABSTRACT;
            f.setModifiers(modifiers);
        } else if (n.isImage("->")) {
            f.setEndToken(next());
            f.setImmediateBody(true);
            HNode e = parseExpression();
            if (e == null) {
                log().error("X127", "function/method declaration", "expected expression", peek());
            } else {
                f.setEndToken(e.endToken());
            }
            f.setBody(e);
            if (requiredSemiColumnForVar) {
                requireSemiColumn("function/method declaration");
            }
        } else if (n.def.id == HTokenId.LEFT_CURLY_BRACKET) {
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
                requireSemiColumn("function/method declaration");
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

    private HNode parseClassDeclaration(int modifiers, JToken startToken) {
        try {
            HNDeclareType classDef = new HNDeclareType(startToken);
            JToken endToken = startToken;
            pushDeclarationContext(classDef);
            JToken[] name = parseNameWithPackageTokens();
            if (name == null) {
                log().error("X129", "class declaration", "expected name", peek());
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
                JListWithSeparators<HNDeclareIdentifier> sli = parseParsList("class declaration", "argument", () -> parseArgumentDeclaration(
                        new HLDeclarationOptions()
                                .setAcceptModifiers(true)
                                .setAcceptFunction(false).setAcceptClass(false).setAcceptInValue(false)
                                .setAcceptVarArg(true)
                ));
                classDef.setMainConstructorArgs(sli.getItems());
                if (sli.getEndToken() != null) {
                    classDef.setEndToken(sli.getEndToken());
                    endToken = sli.getEndToken();
                }
                n = peek();
            }
            if (n.isKeyword("extends") || n.isImage(":")) {
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
            if (n.isImage(";")) {
                endToken = next();
            } else if (n.def.id == HTokenId.LEFT_CURLY_BRACKET) {
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

    public boolean requireSemiColumn(String logName) {
        boolean ok = false;
        JToken found = null;
        while (true) {
            JToken t = peek();
            if (t.isImage(";")) {
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
        if (!n.isImage("[")) {
            return null;
        }
        JToken startToken = n.copy();
        List<List<HNode>> rows = new ArrayList<>();
        List<HNode> lastRow = null;
        int maxColumns = 0;
        if (peek().isImage("]")) {
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
                if (n2.isImage(",")) {
                    //next cell
                } else if (n2.isImage(";")) {
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

    protected HNode parseIf() {
        JToken startToken = peek().copy();
        JToken endToken = startToken;
        if (peek().isKeyword("if")) {
            next();//skip if
            JToken p = peek();//skip if
            HNode condExpr = null;
            HNIf i = new HNIf(startToken);
            endToken = (startToken);
            if (p.isImage("(")) {
                endToken = (p);
                condExpr = parseParenthesis("if condition");
                if (condExpr == null) {
                    log().error("X136", "if statement", "missing condition", peek());
                    i.setEndToken(endToken);
                    return i;
                } else if (((HNPars) condExpr).getItems().length == 0) {
                    endToken = (condExpr.endToken());
                    log().error("X136", "if statement", "missing condition", peek());
                } else {
                    endToken = (condExpr.endToken());
                }
            } else {
                log().error("X137", "if statement", "missing '(' or condition", peek());
                i.setEndToken(endToken);
                return i;
            }
            HNode doExpr = parseStatement();
            if (doExpr == null) {
                doExpr = parseStatement();
                log().error("X137", "if statement", "missing then statement/expression", peek());
                if (peek().isKeyword("else")) {
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
                if (p2.length >= 2 && (p2[0].isKeyword("else") && p2[1].isKeyword("if"))) {
                    //skip else if!
                    next();
                    endToken = (next());
                    condExpr = parseParenthesis("else if condition");
                    if (condExpr == null) {
                        log().error("X138", "if statement", "missing else condition", peek());
                        i.setEndToken(endToken);
                        return i;
                    }
                    endToken = (condExpr.endToken());
                    doExpr = parseStatement();
                    if (doExpr == null) {
                        log().error("X139", "if statement", "missing then statement/expression", peek());
                        //doExpr=HUtils.createUnknownBlocNode();
                    } else {
                        endToken = (doExpr.endToken());
                    }
                    i.add(condExpr, doExpr);
                } else if (p2.length >= 1 && p2[0].isKeyword("else")) {
                    //skip else!
                    endToken = (next());
                    doExpr = parseStatement();
                    if (doExpr == null) {
                        log().error("X140", "if statement", "missing else statement/expression", peek());
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

    protected HNode parseWhile() {
        JToken startToken = peek().copy();
        JToken endToken = startToken;
        if (startToken.isKeyword("while")) {
            skip();
            JToken p = next();
            HNode cond = null;
            if (p.isImage("(")) {
                pushBack(p);
                cond = parseParenthesis("while condition");
                if (cond == null) {
                    log().error("X141", "while statement", "missing while condition", peek());
                    while (true) {
                        JToken peek = next();
                        if (!peek.isImage(")") || peek.isEOF()) {
                            break;
                        } else if (peek.def.id == HTokenId.LEFT_CURLY_BRACKET) {
                            pushBack(peek);
                            break;
                        }
                    }
                } else {
                    endToken = cond.endToken();
                }
            } else {
                log().error("X142", "while statement", "missing '(' for while condition", peek());
                cond = parseExpression();
                if (cond == null) {
                    log().error("X143", "while statement", "missing while condition", peek());
                } else {
                    endToken = cond.endToken();
                }
            }
            HNode block = null;
            JToken t = peek().copy();
            if (t.isImage(";")) {
                //this is an empty while;
                //block = new HNBlock();
            } else {
                block = parseStatement();
                if (block == null) {
//                    block = new HNBlock();
                    log().error("X144", "while statement", "missing while block", peek());
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
//        log().add(JCompilerMessage.error(null,message,peek()));
//    }
    protected HNode prepareForInitNode(HNode c) {
        HNode leftNode = null;
        HNode rightNode = null;
        JToken assignOperator = null;
        if (c instanceof HNAssign) {
            HNAssign a = (HNAssign) c;
            leftNode = a.getLeft();
            rightNode = a.getRight();
            assignOperator = a.getOp();
        } else if (c instanceof HNOpBinaryCall
                && (((HNOpBinaryCall) c).getName().equals(":") || ((HNOpBinaryCall) c).getName().equals("="))) {

            HNOpBinaryCall bc = (HNOpBinaryCall) c;
            assignOperator = bc.getNameToken();
            leftNode = bc.getLeft();
            rightNode = bc.getRight();
        } else {
            log().error("X145", "for statement", "expected ':' or '=' assignment var initializer", c.startToken());
        }
//        //Tuples are processed earlier
//        if (rightNode != null) {
//            if (rightNode instanceof HNPars) {
//                HNPars p = (HNPars) rightNode;
//                if (p.getItems().length == 1 && p.getItems()[0] instanceof HNPars && ((HNPars) p.getItems()[0]).getItems().length == 1) {
//                    // case of single item tuple of type ((x))
//                    HNPars item = (HNPars) p.getItems()[0];
//                    rightNode = new HNTuple(item.getItems(), item.startToken(), item.endToken());
//                } else if (p.getItems().length != 1) {
//                    rightNode = new HNTuple(p.getItems(), p.startToken(), p.endToken());
//                }
//            }
//        }
        if (assignOperator == null) {
            assignOperator = HNodeUtils.createToken("=");
        }
        if (leftNode != null && rightNode != null) {
            if (leftNode instanceof HNIdentifier) {
                return new HNDeclareIdentifier(
                        HNodeUtils.toDeclareTokenIdentifier((HNIdentifier) leftNode),
                        rightNode, (HNTypeToken) null, assignOperator, c.startToken(), rightNode.endToken());
            } else if (leftNode instanceof HNTuple) {
                return new HNDeclareIdentifier(
                        HNodeUtils.toDeclareTupleItem((HNTuple) leftNode, log()),
                        rightNode, (HNTypeToken) null, assignOperator, c.startToken(), rightNode.endToken());
//            } else if (leftNode instanceof HNPars) { //DEPRECATED
//                //check or tuples
//                HNode[] items = ((HNPars) leftNode).getItems();
//                boolean ok2 = items.length > 0;
//                if (!ok2) {
//                    log().error("X146", "for statement: expected '" + assignOperator.image + "' assignment var initializer but found empty tuple", c.startToken());
//                }
//                List<HNDeclareIdentifier> identifiers = new ArrayList<>();
//                for (int i = 0; i < items.length; i++) {
//                    HNode item = items[i];
//                    if (item instanceof HNIdentifier) {
//                        HNIdentifier hid = (HNIdentifier) item;
//                        HNDeclareIdentifier did = new HNDeclareIdentifier(new JToken[]{hid.startToken().copy()}, null, (HNTypeToken) null, assignOperator, item.startToken(), item.endToken());
//                        identifiers.add(did);
//                    } else {
//                        log().error("X147", "for statement: expected '" + assignOperator.image + "' assignment var initializer for expression tuple", c.startToken());
//                        ok2 = false;
//                    }
//                }
//                if (ok2) {
//                    HNDeclareTuple tt = new HNDeclareTuple(
//                            identifiers.toArray(new HNDeclareIdentifier[0]),
//                            rightNode,
//                            assignOperator,
//                            c.startToken(),
//                            rightNode.endToken());
//                    tt.setAssignOperator(assignOperator);
//                    return tt;
//                }
            } else {
                log().error("X148", "for statement", "expected '" + assignOperator.image + "' assignment var initializer", c.startToken());
            }
        }
        return null;
    }

    protected HNode parseFor() {
        JToken startNode = peek().copy();
        JToken endToken = startNode;
        if (startNode.isKeyword("for")) {
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
                                HNode e = parseExpression();
                                if (e == null) {
                                    wasSep = true;
                                    log().error("X148", "for statement", "expected expression", peek());
                                    skip();
                                } else {
                                    endToken = e.endToken();
                                    HNode a = prepareForInitNode(e);
                                    if (a != null) {
                                        hf.addInit(a);
                                    }
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
            if (peek().isImage("->")) {
                endToken = next();
                HNode ok = parseExpression();
                if (ok == null) {
                    log().error("X151", "for statement", "missing for expression body", peek());
                } else {
                    endToken = (ok.endToken());
                }
                hf.setBody(ok);
                hf.setExpressionMode(true);
            } else {
                HNode ok = parseStatement();
                if (ok == null) {
                    log().error("X152", "for statement", "missing for statement body", peek());
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
        if (n.isKeyword("package")) {
            JToken endToken = n;
            HNMetaPackageId id = parseMetaPackageId();
            if (id != null) {
                endToken = id.endToken();
            }
            HNDeclareMetaPackage pp = new HNDeclareMetaPackage(n);
            pp.setModuleId(id);
            n = peek();
            if (n.def.id == HTokenId.LEFT_CURLY_BRACKET) {
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
            } else if (n.isImage(";")) {
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
        if (p.def.id == JTokenId.NUMBER_INT) {
            next();
            int ival = 0;
            try {
                ival = (int) HNumberEvaluator.H_NUMBER.eval(p.def.id, p.image, p.sval, "int");
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
        if (n.isKeyword("break")) {
            JToken ival = parseBreakOrContinueLabel("break");
            return new HNBreak(ival, n, ival == null ? n : ival);
        }
        return null;
    }

    protected HNode parseContinue() {
        JToken n = next().copy();
        if (n.isKeyword("continue")) {
            JToken ival = parseBreakOrContinueLabel("continue");
            return new HNContinue(ival, n, ival == null ? n : ival);
        }
        return null;
    }

    protected HNode parseReturn() {
        JToken n = next();
        JToken startToken = n.copy();
        if (n.isKeyword("return")) {
            JToken p = peek();
            if (p.isImage(";")) {
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
        if (n.isKeyword("import")) {
            endToken = n = peek();
            if (n.isImage("package")) {
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
                if (p.isImage(";")) {
                    pushBack(p);
                    node.setEndToken(endToken);
                    return node;
                }
                if (p.isImage("for")) {
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
                if (p.isImage("break")) {
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
                        if (p.isImage(",")) {
                            endToken = next();
                            //okkay, consume it
                        }
                        if (p.isImage(";")) {
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
                } else if (t.isImage(":")) {
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
                } else if (t.def.id == HTokenId.LEFT_CURLY_BRACKET || t.isImage(";")) {
                    pushBack(t);
                    break;
                } else if (t.isEOF()) {
                    break;
                } else {
                    pushBack(t);
                    break;
                }
            } else if (state == STATE_ARTIFACT) {
                if (t.isKeyword() || t.isIdentifier() || t.isString() || t.isImage("-") || t.isImage("_")) {
                    artifact = new HNMetaPackageArtifact();
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
                } else if (t.def.id == HTokenId.LEFT_CURLY_BRACKET || t.isImage(";")) {
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
                        || t.isImage("-")
                        || t.isImage("_")
                        || t.isImage(".")) {
                    t.image = t.sval;
                    if (version == null) {
                        version = new HNMetaPackageVersion();
                    }
                    version.addToken(t);
                    if (startToken == null) {
                        startToken = t;
                    }
                    endToken = t;
                } else if (t.def.id == HTokenId.LEFT_CURLY_BRACKET || t.isImage(";")) {
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
        return parseExpressionBinary(-1, simpleExpressionOptions);
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

    protected HNode _parseSwitch_parseLHS(ParseSwitchContext psc, String condName) {
        JToken p = peek();
//        String condName = psc.defaultVisited ? "default" : psc.caseVisited ? "case" : psc.ifVisited ? "if" : psc.isVisited ? "is" : "case";
        if (p.isOperator(":")) {
            psc.endToken = next();
            if (psc.functionalSwitch == null) {
                psc.functionalSwitch = false;
            } else if (psc.functionalSwitch) {
                log().error("X172", "switch statement", "expected '->' after " + condName, p);
            }
            if (psc.op == null) {
                psc.op = psc.endToken;
            }
        } else if (p.isOperator("->")) {
            psc.endToken = next();
            if (psc.functionalSwitch == null) {
                psc.functionalSwitch = true;
            } else if (!psc.functionalSwitch) {
                log().error("X172", "switch statement", "expected ':' after " + condName, p);
            }
            if (psc.op == null) {
                psc.op = psc.endToken;
            }
        } else {
            if (psc.functionalSwitch == null) {
                log().error("X172", "switch statement", "missing ':' after " + condName, p);
            } else if (psc.functionalSwitch) {
                log().error("X172", "switch statement", "missing ':' after " + condName, p);
            } else {
                log().error("X172", "switch statement", "missing '->' after " + condName, p);
            }
        }
        HNode stmt = null;
        if (psc.functionalSwitch != null && psc.functionalSwitch) {
            stmt = parseExpression();
            if (stmt == null) {
                log().error("X173", "switch statement", "missing expression", peek());
            } else {
                JToken comma = peek();
                if (comma.isImage(";")) {
                    psc.endToken = next();
                } else {
                    log().error("X173", "switch statement", "missing ';'", peek());
                }
            }
        } else {
            stmt = parseStatement();
        }
        if (stmt == null) {
            log().error("X173", "switch statement", "missing case expression", peek());
        } else {
            psc.endToken = stmt.endToken();
        }
        return stmt;
    }

    protected HNode parseSwitch() {
        ParseSwitchContext psc = new ParseSwitchContext();
        JToken startToken = peek().copy();
        psc.endToken = startToken;
        if (startToken.isKeyword("switch")) {
            skip();
            JToken p = peek();
            if (!p.isImage("(")) {
                log().error("X163", "switch statement", "expected '('", peek());
            }
            HNSwitch jNodeHSwitch = new HNSwitch(startToken);
            HNPars expr = (HNPars) parseParenthesis("switch discriminator");
            if (expr == null || expr.getItems().length == 0) {
                log().error("X164", "switch statement", "missing switch condition", peek());
                JToken f = p.copy();
                f.setError(1, "missing switch condition");
                f.sval = "false";
                JTokenDef FALSE = null;
                //TODO fix me later
                for (JTokenDef tokenTemplate : tokenizer().getTokenDefinitions()) {
                    if (tokenTemplate.idName.equals("false")) {
                        FALSE = tokenTemplate;
                        break;
                    }
                }
                if (FALSE == null) {
                    throw new JFixMeLaterException();
                }
                JTokenUtils.fillToken(FALSE, f);

                if (expr != null) {
                    psc.endToken = expr.endToken();
                }
                expr = new HNPars(new HNode[]{new HNLiteral(false, f)}, peek(), new ArrayList<>(), peek());
                expr.setStartToken(peek());
            } else {
                psc.endToken = expr.endToken();
            }
            if (expr.getItems().length == 0) {
                log().error("X165", "switch statement", "missing switch expression", peek());
                jNodeHSwitch.setExpr(new HNLiteral(null, expr.startToken()));
            } else if (expr.getItems().length >= 1) {
                if (expr.getItems().length > 1) {
                    log().error("X166", "switch statement", "expression list are not allowed in switch", peek());
                }
                HNode item = expr.getItems()[0];
                if (item instanceof HNAssign && ((HNAssign) item).getLeft() instanceof HNIdentifier) {
                    HNIdentifier i = (HNIdentifier) ((HNAssign) item).getLeft();
                    item = new HNDeclareIdentifier(
                            HNodeUtils.toDeclareTokenIdentifier(i),
                            ((HNAssign) item).getRight(),
                            (HNTypeToken) null,//item.getType()
                            HNodeUtils.createToken("="), item.startToken(), item.endToken());
                }
                jNodeHSwitch.setExpr(item);
            }

            p = peek();
            if (!p.isImage("{")) {
                log().error("X167", "switch statement", "expected { after switch keyword", peek());
            } else {
                psc.endToken = next();
            }

            while (true) {
                p = peek();
                if (p.isEOF()) {
                    log().error("X168", "switch statement", "missing closing '}'", peek());
                    break;
                } else if (p.isKeyword("case")) {
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
                        log().error("X171", "switch statement", "missing case expression", peek());
                    } else {
                        psc.endToken = matches.get(matches.size() - 1).endToken();
                    }
                    HNode stmt = _parseSwitch_parseLHS(psc, "case");
                    jNodeHSwitch.add(new HNSwitch.SwitchCase(matches, psc.op, stmt, p, psc.endToken));
                } else if (p.isKeyword("if")) {
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
                        log().error("X176", "switch statement", "missing case expression", peek());
                    }
                    HNode stmt = _parseSwitch_parseLHS(psc, "case");
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
                        if (vn.isKeyword("null")) {
                            psc.endToken = next();
                            typeNames.add(createNullTypeToken(psc.endToken));
                        } else {
                            HNTypeToken t = parseTypeName();
                            if (t == null) {
                                log().error("X181", "switch statement", "missing type name", peek());
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
                            log().error("X183", "switch statement", "missing type name", peek());
                            break;
                        } else if (vn.isOperator("|")) {
                            psc.endToken = next();
                        } else {
                            break;
                        }
                    }
                    if (typeNames.isEmpty()) {
                        log().error("X184", "switch statement", "missing type name", peek());
                    }
                    JToken vn = peek();
                    JToken varName = null;
                    if (vn.isIdentifier()) {
                        psc.endToken = varName = next();
                    }
                    HNode stmt = _parseSwitch_parseLHS(psc, "is");
                    jNodeHSwitch.add(new HNSwitch.SwitchIs(typeNames,
                            varName == null ? null : new HNDeclareTokenIdentifier(varName),
                            psc.op, stmt, ss, psc.endToken));
                } else if (p.isKeyword("default")) {
                    next();
                    if (psc.defaultVisited) {
                        log().error("X187", "switch statement", "default already matched", peek());
                    }
                    psc.defaultVisited = true;
                    HNode stmt = _parseSwitch_parseLHS(psc, "default");
                    jNodeHSwitch.setElse(stmt);
                    p = peek();
                    if (p.def.id == HTokenId.RIGHT_CURLY_BRACKET || p.def.id == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
                        psc.endToken = next();
                        break;
                    } else {
                        log().error("X189", "switch statement", "expect switch '}'", peek());
                        break;
                    }
                } else if (p.def.id == HTokenId.RIGHT_CURLY_BRACKET || p.def.id == HTokenIdOffsets.OFFSET_CLOSE_BRACE2) {
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

    private static class ParseSwitchContext {

        boolean defaultVisited = false;
        boolean caseVisited = false;
        boolean ifVisited = false;
        boolean isVisited = false;
        Boolean functionalSwitch = null;
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

    @Override
    public HNode parsePrefixParsNodePars() {
        return (HNode) super.parsePrefixParsNodePars();
    }

    @Override
    public HNode parsePostfixParsNodePars() {
        return (HNode) super.parsePostfixParsNodePars();
    }

    @Override
    protected HNode parseBrackets() {
        return (HNode) super.parseBrackets();
    }

    public HNode parseExpression() {
        return (HNode) super.parseExpression();
    }
}
