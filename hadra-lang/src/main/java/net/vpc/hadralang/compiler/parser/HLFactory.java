package net.vpc.hadralang.compiler.parser;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JParserNodeFactory;
import net.vpc.common.jeep.core.nodes.JNodeTokens;
import net.vpc.hadralang.compiler.parser.ast.*;
import net.vpc.hadralang.compiler.utils.HNodeUtils;
import net.vpc.hadralang.compiler.utils.HTokenUtils;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.util.Arrays;
import java.util.List;

public class HLFactory implements JParserNodeFactory<HNode> {
    private JContext context;
    private JCompilationUnit compilationUnit;

    public HLFactory(JCompilationUnit compilationUnit, JContext context) {
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
                p.startToken(),
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
                p.startToken(),
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
    public HNode createIdentifierNode(String name, JNodeTokens nodeTokens) {
        return new HNIdentifier(nodeTokens.getStart());
//        String[] y = varName.split("[.]");
//        HNode n = null;
//        for (String s : y) {
//            if (s.isEmpty()) {
//                log().error("S036", "Invalid identifier " + varName, startToken);
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
        switch (op.image) {
            case "=": {
                return new HNAssign(o1, op, o2, nodeTokens.getStart(),nodeTokens.getEnd());
            }
            case "->": {
                return createLambdaExpression(o1, op, o2, nodeTokens);
            }
            case "??": {
                return new HNOpCoalesce(o1, op, o2, nodeTokens.getStart(),nodeTokens.getEnd());
            }
            case "?": {
                return createNonNullExpr(o1, op, o2, nodeTokens);
            }
            case ".": {
                return createDotCheckedMember(o1, op, o2, nodeTokens);
            }
            case ".?": {
                return createApplyUncheckedMember(o1, op, o2, nodeTokens);
            }
        }
        return new HNOpBinaryCall(op, o1, o2, nodeTokens.getStart(),nodeTokens.getEnd());
    }

    public HNode createApplyUncheckedMember(HNode o1, JToken op, HNode o2, JNodeTokens nodeTokens) {
        return new HNOpDot(o1, op, o2,
                nodeTokens.getStart(),nodeTokens.getEnd()
        ).setUncheckedMember(true);
//        return new HNFieldUnchecked(o1, op,
//                o2,
//                startToken,o2.endToken());
//        if (o2 instanceof HNIdentifier) {
//            return new HNFieldUnchecked(o1, op,
//                    o2,
//                    startToken,o2.endToken());
//        } else if (o2 instanceof HXInvokableCall) {
//            throw new JFixMeLaterException();
////                    return new HNMethodUncheckedCall(
////                            ((HXInvokableCall) node.getArg2()).getName(),
////                            ((HXInvokableCall) node.getArg2()).getArgs(),
////                            new JNodeHApplyUncheckedOperator(node.getArg1(), node.token()),
////                            node.getArg2().token()
////                    ).setUnchecked(true);
//        } else {
//            log().error("S046", "cannot call '.?' operator (UncheckedMember) on " + o2.getClass().getSimpleName(),
//                    o2.startToken());
//            return new HNOpBinaryCall(op, o1, o2, startToken, endToken);
//        }
    }

    public HNode createDotCheckedMember(HNode o1, JToken op, HNode o2, JNodeTokens nodeTokens) {
        return new HNOpDot(o1, op, o2,
                nodeTokens.getStart(),nodeTokens.getEnd()
        );
    }

    public HNode createNonNullExpr(HNode o1, JToken op, HNode o2,JNodeTokens nodeTokens) {
        return new HNOpDot(
                o1,op,o2,nodeTokens.getStart(),nodeTokens.getEnd()
        ).setNullableInstance(true);
    }

    @Override
    public HNode createImplicitOperatorNode(HNode o1, HNode o2, JNodeTokens nodeTokens) {
        log().error("X300", null, "Implicit operator not allowed", o1.startToken());
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
                                    HTokenUtils.createToken("="), item.startToken(),
                                    item.endToken()
                            )
                    );
                } else if (item instanceof HNDeclareIdentifier) {
                    HNDeclareIdentifier a1 = (HNDeclareIdentifier) item;
                    if (a1.getInitValue() != null) {
                        log().error("X301", "lambda expression", "unexpected default value for lambda expression parameter", a1.startToken());
                        a1.setInitValue(null);
                    }
                    if (a1.getModifiers() != 0) {
                        log().error("X302", "lambda expression", "lambda expression: unexpected modifiers " + HUtils.modifiersToString0(a1.getModifiers()), a1.startToken());
                        a1.setModifiers(0);
                    }
                    lex.addArgument(a1);
                } else {
                    log().error("X303", "lambda expression", "unexpected expression of type " +
                                    item.getClass().getSimpleName() + ". Was expecting identifier or declared identifier"
                            , decl.startToken());
                }
            }
        } else if (decl instanceof HNIdentifier) {
            lex.addArgument(
                    new HNDeclareIdentifier(
                            HNodeUtils.toDeclareTokenIdentifier((HNIdentifier) decl),
                            null,
                            (HNTypeToken) null,
                            HTokenUtils.createToken("="), decl.startToken(),
                            decl.endToken()
                    )
            );
        } else if (decl instanceof HNDeclareIdentifier) {
            HNDeclareIdentifier a1 = (HNDeclareIdentifier) decl;
            if (a1.getInitValue() != null) {
                log().error("X304", "lambda expression", "unexpected default value for lambda expression parameter", a1.startToken());
                a1.setInitValue(null);
            }
            if (a1.getModifiers() != 0) {
                log().error("X305", "lambda expression", "unexpected modifiers " + HUtils.modifiersToString0(a1.getModifiers()), a1.startToken());
                a1.setModifiers(0);
            }
            lex.addArgument(a1);
        } else {
            log().error("X306", "lambda expression", "unexpected expression of type " +
                            decl.getClass().getSimpleName() + ". Was expecting identifier or declared identifier"
                    , decl.startToken());
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
