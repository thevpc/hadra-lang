package net.hl.compiler.parser;

import net.hl.compiler.core.HTokenId;
import net.thevpc.jeep.*;
import net.thevpc.jeep.JParserNodeFactory;
import net.thevpc.jeep.core.nodes.JNodeTokens;
import net.hl.compiler.ast.*;
import net.hl.compiler.utils.HNodeUtils;
import net.hl.compiler.utils.HTokenUtils;

import java.util.Arrays;
import java.util.List;

public class HFactory implements JParserNodeFactory<HNode> {
    private JContext context;
    private JCompilationUnit compilationUnit;

    public HFactory(JCompilationUnit compilationUnit, JContext context) {
        this.context = context;
        this.compilationUnit = compilationUnit;
    }

    public JCompilationUnit compilationUnit() {
        return compilationUnit;
    }

    public JContext context() {
        return context;
    }

    public JCompilerLog log() {
        return context.log();
    }

    @Override
    public HNLiteral createLiteralNode(Object literal, JNodeTokens nodeTokens) {
        if(literal==null){
            return new HNLiteral(null, nodeTokens.getStart());
        }
        Object literal2 = literal;
        Object literal3;
        for (JResolver r : context().resolvers().getResolvers()) {
            literal3 = r.implicitConvertLiteral(literal2, context());
            if (literal3 != null) {
                literal2 = literal3;
            }
        }
        HNLiteral lnode = new HNLiteral(literal2, nodeTokens.getStart());
        lnode.setStartToken(nodeTokens.getStart());
        return lnode;
    }


    @Override
    public HNode createParsNode(List<HNode> o, JNodeTokens nodeTokens) {
        if(o.size()==1){
            //how can i create tuple of one??
            return new HNPars(o.toArray(new HNode[0]), nodeTokens.getStart(),nodeTokens.getSeparatorsArray(),nodeTokens.getEnd());
        }else{
            return new HNTuple(o.toArray(new HNode[0]),nodeTokens.getStart(),nodeTokens.getSeparatorsArray(),nodeTokens.getEnd());
        }
    }

    @Override
    public HNode createBracesNode(List<HNode> o, JNodeTokens nodeTokens) {
        return new HNBraces(o.toArray(new HNode[0]), nodeTokens.getStart(),nodeTokens.getEnd());
    }

    @Override
    public HNode createPrefixUnaryOperatorNode(JToken op, HNode arg2, JNodeTokens nodeTokens) {
        return new HNOpUnaryCall(nodeTokens.getStart(), arg2, true, nodeTokens.getStart(),nodeTokens.getEnd());
    }

    @Override
    public HNode createPostfixBracketsNode(HNode o, HNode indices, JNodeTokens nodeTokens) {
        HNBrackets p=(HNBrackets) indices;
        return new HNParsPostfix(o, Arrays.asList(p.getItems()),
                nodeTokens.getStart(),
                p.getStartToken(),
                Arrays.asList(p.getSeparators()),
                nodeTokens.getEnd());

//        if (!(indices instanceof HNBrackets)) {
//            indices = new HNBrackets(new HNode[]{indices}, o.startToken(),indices.endToken());
//        }
//        return new HNBracketsPostfix(o, (HNBrackets) indices, o.startToken(),endToken);
    }

    @Override
    public HNode createPrefixBracketsNode(HNode indices, HNode o, JNodeTokens nodeTokens) {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public HNode createPostfixParenthesisNode(HNode o, HNode indices, JNodeTokens nodeTokens) {
        HNPars p=(HNPars) indices;
        return new HNParsPostfix(o, Arrays.asList(p.getItems()),
                nodeTokens.getStart(),
                p.getStartToken(),
                Arrays.asList(p.getSeparators()),
                nodeTokens.getEnd());
    }

    /**
     * this is a cast
     *
     * @param indices
     * @param o
     * @return
     */
    @Override
    public HNode createPrefixParenthesisNode(HNode indices, HNode o, JNodeTokens nodeTokens) {
        return new HNCast(indices, o, nodeTokens.getSeparatorsArray(),nodeTokens.getStart(),nodeTokens.getEnd());
    }

    @Override
    public HNode createPostfixBracesNode(HNode o, HNode indices, JNodeTokens nodeTokens) {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public HNode createPrefixBracesNode(HNode indices, HNode o, JNodeTokens nodeTokens) {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public HNode createBracketsNode(List<HNode> o, JNodeTokens nodeTokens) {
        return new HNBrackets(o.toArray(new HNode[0]), nodeTokens.getStart(), nodeTokens.getSeparatorsArray(),nodeTokens.getEnd());
    }

    @Override
    public HNode createPostfixUnaryOperatorNode(JToken name, HNode argumentChild, JNodeTokens nodeTokens) {
        return new HNOpUnaryCall(name, argumentChild, false, nodeTokens.getStart(),nodeTokens.getEnd());
    }

    @Override
    public HNode createListOperatorNode(JToken token, List<HNode> args, JNodeTokens nodeTokens) {
        throw new JShouldNeverHappenException();
//        if (args.length == 2) {
//            HNode o1 = args[0];
//            HNode o2 = args[1];
//            if (o2 instanceof HXInvokableCall && (((HXInvokableCall) o2).getName()).equals(token.image)) {
//                HXInvokableCall o21 = (HXInvokableCall) o2;
//                List<HNode> aa = new ArrayList<>();
//                aa.add(o1);
//                aa.addAll(Arrays.asList(o21.getArgs()));
//                return new HXInvokableCall(token, aa.toArray(new HNode[0]), startToken,endToken);
//            } else {
//                return new HXInvokableCall(token, args, startToken,endToken);
//            }
//        } else {
//            return new HXInvokableCall(token, args, startToken,endToken);
//        }
    }

    @Override
    public HNode createAnnotatedNode(HNode node, HNode annotations, JNodeTokens nodeTokens) {
        HNAnnotationList l=(HNAnnotationList) annotations;
        node.setAnnotations(l.getChildren());
        return node;
    }

    @Override
    public HNode createIdentifierNode(String name, JNodeTokens nodeTokens) {
        return new HNIdentifier(nodeTokens.getStart());
//        String[] y = varName.split("[.]");
//        HNode n = null;
//        for (String s : y) {
//            if (s.isEmpty()) {
//                log().jerror("S036", "Invalid identifier " + varName, startToken);
//            } else {
//                if (n == null) {
//                    n = new HNIdentifier(s, startToken);
//                } else {
//                    n = new HNOpDot(s, n, startToken,n.endToken());
//                }
//            }
//        }
//        return n;
    }

    public HNode createBinaryOperatorNode(JToken op, HNode o1, HNode o2, JNodeTokens nodeTokens) {
        switch (op.id()) {
            case HTokenId.EQ: {
                return new HNAssign(o1, op, o2, nodeTokens.getStart(),nodeTokens.getEnd());
            }
            case HTokenId.SEQ_MINUS_EQ: {
                return createLambdaExpression(o1, op, o2, nodeTokens);
            }
            case HTokenId.QUESTION2: {
                return new HNOpCoalesce(o1, op, o2, nodeTokens.getStart(),nodeTokens.getEnd());
            }
            case HTokenId.QUESTION: {
                return createNonNullExpr(o1, op, o2, nodeTokens);
            }
            case HTokenId.DOT: {
                return createDotMember(o1, op, o2, false,false,nodeTokens);
            }
            case HTokenId.SEQ_QUESTION_DOT: {
                return createDotMember(o1, op, o2, false,true,nodeTokens);
            }
            case HTokenId.SEQ_DOT_QUESTION: {
                return createDotMember(o1, op, o2, true,false,nodeTokens);
            }
            case HTokenId.SEQ_QUESTION_DOT_QUESTION: {
                return createDotMember(o1, op, o2, true,true,nodeTokens);
            }
        }
        return new HNOpBinaryCall(op, o1, o2, nodeTokens.getStart(),nodeTokens.getEnd());
    }

//    public HNode createApplyUncheckedMember(HNode o1, JToken op, HNode o2, JNodeTokens nodeTokens) {
//        return new HNOpDot(o1, op, o2,
//                nodeTokens.getStart(),nodeTokens.getEnd()
//        ).setUncheckedMember(true);
//    }
//
//    public HNode createDotCheckedMember(HNode o1, JToken op, HNode o2, JNodeTokens nodeTokens) {
//        return new HNOpDot(o1, op, o2,
//                nodeTokens.getStart(),nodeTokens.getEnd()
//        );
//    }

    public HNode createDotMember(HNode o1, JToken op, HNode o2, boolean unchecked,boolean nullable,JNodeTokens nodeTokens) {
        return new HNOpDot(o1, op, o2,
                nodeTokens.getStart(),nodeTokens.getEnd()
        ).setNullableInstance(nullable).setUncheckedMember(unchecked);
    }

    public HNode createNonNullExpr(HNode o1, JToken op, HNode o2,JNodeTokens nodeTokens) {
        return new HNOpDot(
                o1,op,o2,nodeTokens.getStart(),nodeTokens.getEnd()
        ).setNullableInstance(true);
    }

    @Override
    public HNode createImplicitOperatorNode(HNode o1, HNode o2, JNodeTokens nodeTokens) {
        log().jerror("X300", null, o1.getStartToken(), "Implicit operator not allowed");
        return new HNTuple(new HNode[]{o1, o2}, nodeTokens.getStart(),new JToken[0], nodeTokens.getEnd());
    }

    public HNode createLambdaExpression(HNode decl, JToken op, HNode body, JNodeTokens nodeTokens) {
        //lambda expression
        HNLambdaExpression lex = new HNLambdaExpression(op,nodeTokens.getStart(),nodeTokens.getEnd());
        if (decl instanceof HNPars) {
            for (HNode item : ((HNPars) decl).getItems()) {
                if (item instanceof HNIdentifier) {
                    lex.addArgument(
                            new HNDeclareIdentifier(
                                    HNodeUtils.toDeclareTokenIdentifier((HNIdentifier) item),
                                    null,
                                    (HNTypeToken) null,
                                    HTokenUtils.createToken("="), item.getStartToken(),
                                    item.getEndToken()
                            )
                    );
                } else if (item instanceof HNDeclareIdentifier) {
                    HNDeclareIdentifier a1 = (HNDeclareIdentifier) item;
                    if (a1.getInitValue() != null) {
                        log().jerror("X301", "lambda expression", a1.getStartToken(), "unexpected default value for lambda expression parameter");
                        a1.setInitValue(null);
                    }
                    List<String> unacceptableCalls = HNodeUtils.filterModifierAnnotations(a1.getAnnotations(),"private", "protected", "public", "static", "final", "const");
                    if (!unacceptableCalls.isEmpty()) {
                        log().jerror("X302", "lambda expression", a1.getStartToken(), "lambda expression: unexpected modifiers " + String.join(",",unacceptableCalls));
                    }
                    lex.addArgument(a1);
                } else {
                    log().jerror("X303", "lambda expression", decl.getStartToken(), "unexpected expression of type " +
                            item.getClass().getSimpleName() + ". Was expecting identifier or declared identifier");
                }
            }
        } else if (decl instanceof HNIdentifier) {
            lex.addArgument(
                    new HNDeclareIdentifier(
                            HNodeUtils.toDeclareTokenIdentifier((HNIdentifier) decl),
                            null,
                            (HNTypeToken) null,
                            HTokenUtils.createToken("="), decl.getStartToken(),
                            decl.getEndToken()
                    )
            );
        } else if (decl instanceof HNDeclareIdentifier) {
            HNDeclareIdentifier a1 = (HNDeclareIdentifier) decl;
            if (a1.getInitValue() != null) {
                log().jerror("X304", "lambda expression", a1.getStartToken(), "unexpected default value for lambda expression parameter");
                a1.setInitValue(null);
            }
            List<String> unacceptableCalls = HNodeUtils.filterModifierAnnotations(a1.getAnnotations(),"private", "protected", "public", "static", "final", "const");
            if (!unacceptableCalls.isEmpty()) {
                log().jerror("X305", "lambda expression", a1.getStartToken(), "unexpected modifiers " + String.join(",",unacceptableCalls));
            }
            lex.addArgument(a1);
        } else {
            log().jerror("X306", "lambda expression", decl.getStartToken(), "unexpected expression of type " +
                    decl.getClass().getSimpleName() + ". Was expecting identifier or declared identifier");
        }
        lex.setBody(body);
        if (body instanceof HNBlock) {
            lex.setImmediateBody(false);
        } else {
            lex.setImmediateBody(true);
        }
        return lex;

    }

}
