package net.vpc.hadralang.compiler.parser;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.JParserNodeFactory;
import net.vpc.common.jeep.util.JTokenUtils;
import net.vpc.hadralang.compiler.parser.ast.*;
import net.vpc.hadralang.compiler.utils.HNodeUtils;
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
    public HNLiteral createLiteralNode(Object literal, JToken token) {
        if(literal==null){
            return new HNLiteral(null, token);
        }
        Object literal2 = literal;
        Object literal3;
        for (JResolver r : context().resolvers().getResolvers()) {
            literal3 = r.implicitConvertLiteral(literal2, context());
            if (literal3 != null) {
                literal2 = literal3;
            }
        }
        HNLiteral lnode = new HNLiteral(literal2, token);
        lnode.setStartToken(token);
        return lnode;
    }


    @Override
    public HNode createParsNode(List<HNode> o, JToken startToken, List<JToken> separators, JToken endToken) {
        if(o.size()==1){
            //how can i create tuple of one??
            return new HNPars(o.toArray(new HNode[0]), startToken,separators,endToken);
        }else{
            return new HNTuple(o.toArray(new HNode[0]),startToken,separators.toArray(new JToken[0]),endToken);
        }
    }

    @Override
    public HNode createBracesNode(List<HNode> o, JToken startToken, JToken endToken) {
        return new HNBraces(o.toArray(new HNode[0]), startToken,endToken);
    }

    @Override
    public HNode createPrefixUnaryOperatorNode(JToken op, HNode arg2, JToken startToken, JToken endToken) {
        return new HNOpUnaryCall(startToken, arg2, true, startToken,endToken);
    }

    @Override
    public HNode createPostfixBracketsNode(HNode o, HNode indices, JToken startToken, JToken endToken) {
        HNBrackets p=(HNBrackets) indices;
        return new HNParsPostfix(o, Arrays.asList(p.getItems()),
                startToken,
                p.startToken(),
                p.getSeparators(),
                endToken);

//        if (!(indices instanceof HNBrackets)) {
//            indices = new HNBrackets(new HNode[]{indices}, o.startToken(),indices.endToken());
//        }
//        return new HNBracketsPostfix(o, (HNBrackets) indices, o.startToken(),endToken);
    }

    @Override
    public HNode createPrefixBracketsNode(HNode indices, HNode o, JToken startToken, JToken endToken) {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public HNode createPostfixParenthesisNode(HNode o, HNode indices, JToken startToken, JToken endToken) {
        HNPars p=(HNPars) indices;
        return new HNParsPostfix(o, Arrays.asList(p.getItems()),
                startToken,
                p.startToken(),
                p.getSeparators(),
                endToken);
    }

    /**
     * this is a cast
     *
     * @param indices
     * @param o
     * @param endParsToken
     * @param startToken
     * @param endToken
     * @return
     */
    @Override
    public HNode createPrefixParenthesisNode(HNode indices, HNode o, JToken endParsToken, JToken startToken, JToken endToken) {
        return new HNCast(indices, o, endParsToken,startToken,endToken);
    }

    @Override
    public HNode createPostfixBracesNode(HNode o, HNode indices, JToken startToken, JToken endToken) {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public HNode createPrefixBracesNode(HNode indices, HNode o, JToken startToken, JToken endToken) {
        throw new IllegalArgumentException("Unsupported");
    }

    @Override
    public HNode createBracketsNode(List<HNode> o, JToken startToken, List<JToken> separators, JToken endToken) {
        return new HNBrackets(o.toArray(new HNode[0]), startToken,separators,endToken);
    }

    @Override
    public HNode createPostfixUnaryOperatorNode(JToken name, HNode arg1, HNode postNode, JToken startToken, JToken endToken) {
        return new HNOpUnaryCall(name, arg1, false, startToken,endToken);
    }

    @Override
    public HNode createListOperatorNode(JToken token, List<HNode> args, JToken startToken, JToken endToken) {
        throw new JShouldNeverHappenException();
//        if (args.length == 2) {
//            HNode o1 = args[0];
//            HNode o2 = args[1];
//            if (o2 instanceof HNInvokerCall && (((HNInvokerCall) o2).getName()).equals(token.image)) {
//                HNInvokerCall o21 = (HNInvokerCall) o2;
//                List<HNode> aa = new ArrayList<>();
//                aa.add(o1);
//                aa.addAll(Arrays.asList(o21.getArgs()));
//                return new HNInvokerCall(token, aa.toArray(new HNode[0]), startToken,endToken);
//            } else {
//                return new HNInvokerCall(token, args, startToken,endToken);
//            }
//        } else {
//            return new HNInvokerCall(token, args, startToken,endToken);
//        }
    }


//    @Override
//    public HNode createFunctionCallNode(String name, HNode[] args, JToken startToken) {
//        String[] o = name.split("[.]");
//        if (o.length == 0) {
//            log().error("S036", "No named methods are not allowed", startToken);
//            return new HNInvokerCall("", args, startToken);
//        }
//        if (o.length == 1) {
//            return new HNInvokerCall(name, args, startToken);
//        }
//        String[] rest = Arrays.copyOfRange(o, 0, o.length - 1);
//        HNode n = new HNIdentifier(rest[0], startToken);
//        for (int i = 1; i < rest.length; i++) {
//            n = new HNOpDot(rest[1], n, startToken,n);
//        }
//        return new HNMethodCall(name, args, n, startToken);
//    }

    @Override
    public HNode createVarNameNode(JToken token) {
        return new HNIdentifier(token);
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

    public HNode createBinaryOperatorNode(JToken op, HNode o1, HNode o2, JToken startToken, JToken endToken) {
        switch (op.image) {
            case "=": {
                return new HNAssign(o1, op, o2, startToken,endToken);
            }
            case "->": {
                return createLambdaExpression(o1, op, o2, startToken, endToken);
            }
            case "??": {
                return new HNOpCoalesce(o1, op, o2, startToken, endToken);
            }
            case "?": {
                return createNonNullExpr(o1, op, o2, startToken, endToken);
            }
            case ".": {
                return createDotCheckedMember(o1, op, o2, startToken, endToken);
            }
            case ".?": {
                return createApplyUncheckedMember(o1, op, o2, startToken, endToken);
            }
        }
        return new HNOpBinaryCall(op, o1, o2, startToken, endToken);
    }

    public HNode createApplyUncheckedMember(HNode o1, JToken op, HNode o2, JToken startToken, JToken endToken) {
        return new HNOpDot(o1, op, o2,
                startToken,
                endToken
        ).setUncheckedMember(true);
//        return new HNFieldUnchecked(o1, op,
//                o2,
//                startToken,o2.endToken());
//        if (o2 instanceof HNIdentifier) {
//            return new HNFieldUnchecked(o1, op,
//                    o2,
//                    startToken,o2.endToken());
//        } else if (o2 instanceof HNInvokerCall) {
//            throw new JFixMeLaterException();
////                    return new HNMethodUncheckedCall(
////                            ((HNInvokerCall) node.getArg2()).getName(),
////                            ((HNInvokerCall) node.getArg2()).getArgs(),
////                            new JNodeHApplyUncheckedOperator(node.getArg1(), node.token()),
////                            node.getArg2().token()
////                    ).setUnchecked(true);
//        } else {
//            log().error("S046", "cannot call '.?' operator (UncheckedMember) on " + o2.getClass().getSimpleName(),
//                    o2.startToken());
//            return new HNOpBinaryCall(op, o1, o2, startToken, endToken);
//        }
    }

    public HNode createDotCheckedMember(HNode o1, JToken op, HNode o2, JToken startToken, JToken endToken) {
        return new HNOpDot(o1, op, o2,
                startToken,
                endToken
        );
//        if (o2 instanceof HNIdentifier) {
//            return new HNOpDot(o1, op, o2,
//                    startToken,
//                    o2.endToken()
//            );
////        } else if (o2 instanceof HNParsPostfix && ((HNParsPostfix) o2).getBase() instanceof HNIdentifier) {
////            HNIdentifier idn =(HNIdentifier) ((HNParsPostfix) o2).getBase();
////            return new HNMethodCall(op,
////                    idn.getNameToken(),
////                    ((HNParsPostfix) o2).getItems(),
////                    o1,
////                    o1.startToken(),
////                    ((HNParsPostfix) o2).endToken()
////            );
//        } else if (o2 instanceof HNInvokerCall) {
//            return new HNMethodCall(op,
//                    ((HNInvokerCall) o2).getNameToken(),
//                    ((HNInvokerCall) o2).getArgs(),
//                    o1,
//                    o1.startToken(),
//                    ((HNInvokerCall) o2).endToken()
//            );
//        } else {
//            log().error("S046", "cannot call '.' operator on " + o2.getClass().getSimpleName(),
//                    o2.startToken());
//            return new HNOpBinaryCall(op, o1, o2, startToken, endToken);
//        }
    }

    public HNode createNonNullExpr(HNode o1, JToken op, HNode o2, JToken startToken, JToken endToken) {
        return new HNOpDot(
                o1,op,o2,startToken,endToken
        ).setNullableInstance(true);
    }

    @Override
    public HNode createImplicitOperatorNode(HNode o1, HNode o2, JToken startToken, JToken endToken) {
        log().error("X300", null, "Implicit operator not allowed", o1.startToken());
        return new HNTuple(new HNode[]{o1, o2}, startToken,new JToken[0], endToken);
    }

    public HNode createLambdaExpression(HNode decl, JToken op, HNode body, JToken startToken, JToken endToken) {
        //lambda expression
        HNLambdaExpression lex = new HNLambdaExpression(op,startToken,endToken);
        if (decl instanceof HNPars) {
            for (HNode item : ((HNPars) decl).getItems()) {
                if (item instanceof HNIdentifier) {
                    lex.addArgument(
                            new HNDeclareIdentifier(
                                    HNodeUtils.toDeclareTokenIdentifier((HNIdentifier) item),
                                    null,
                                    (HNTypeToken) null,
                                    HNodeUtils.createToken("="), item.startToken(),
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
                            HNodeUtils.createToken("="), decl.startToken(),
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
