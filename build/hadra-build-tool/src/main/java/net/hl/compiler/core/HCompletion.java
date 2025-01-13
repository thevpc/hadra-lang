package net.hl.compiler.core;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.nodes.AbstractJNode;
import net.thevpc.jeep.core.tokens.JTokenDef;
import net.thevpc.jeep.util.JStringUtils;
import net.hl.compiler.HL;
import net.hl.compiler.core.elements.HNElement;
import net.hl.compiler.core.invokables.HLJCompilerContext;
import net.hl.compiler.ast.*;
import net.hl.compiler.utils.HNodeUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HCompletion implements JCompletion {
    public static final Logger LOG = Logger.getLogger(HCompletion.class.getName());
    private JCompilationUnit compilationUnit;
    private HProjectContext projectContext;

    public HCompletion(HProjectContext projectContext) {
        this.projectContext = projectContext;
//        for (JToken token : hadraLanguage.tokens().tokenTemplates()) {
//            System.out.println(token.id + "::" + token);
//        }
//        System.out.println("=====================");
    }

    @Override
    public void setCompilationUnit(JCompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    @Override
    public void setCompilationUnit(String compilationUnitText, String sourceName) {
        HProject c = new HL(projectContext)
                .addSourceText(
                        compilationUnitText == null ? "" : compilationUnitText,
                        sourceName
                )
                .setProjectRoot(projectContext.rootId())
                .setIncremental(true)
                .compile();
//        for (JToken token : hadraLanguage.tokens().of(compilationUnitText)) {
//            System.out.println(token+"  "+token.length()+" :: "+token.image.length());
//        }
        if(c.getCompilationUnits().length>0) {
            JCompilationUnit compilationUnit = c.getCompilationUnit(0);
            setCompilationUnit(compilationUnit);
        }
    }

    @Override
    public JLocationContext findLocation(int caretOffset) {
        try {
            if (compilationUnit == null) {
                return null;
            }
            debug((HNode) this.compilationUnit.getAst());
            HProject program = new HProject(projectContext.languageContext().newContext(), projectContext.indexer());
            JLocationContext searchResult = searchNode(program.newCompilerContext(compilationUnit), caretOffset, 0, null);
            return searchResult;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Unexpected Error", ex);
        }
        return null;
    }

    @Override
    public List<JCompletionProposal> findProposals(int caretOffset, int completeLevel) {
        ProposalsList completionProposals = new ProposalsList();
        try {
            if (compilationUnit == null) {
                return completionProposals.list();
            }
//            JNode ast = this.compilationUnit.getAst();
            debug((HNode) compilationUnit.getAst());
            HProject program = new HProject(projectContext.languageContext().newContext(), projectContext.indexer());
            JLocationContext searchResult = searchNode(program.newCompilerContext(compilationUnit), caretOffset, completeLevel, completionProposals);
            System.out.println(searchResult);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Unexpected Error", ex);
        }
        List<JCompletionProposal> list = completionProposals.list();
        list.sort(null);
        return list;
    }

    private void debug(HNode root) {

        List<HNode> jNodes = (List) root.getChildrenNodes();
        //DEBUG... some check
        {
            HNode jNodeLast = null;
            for (HNode jNode : jNodes) {
                if (jNode != null) {
                    if (jNode.getStartToken() == null) {
                        throw new JShouldNeverHappenException();
                    }
                    if (jNode.getEndToken() == null) {
                        throw new JShouldNeverHappenException("end token is null " + jNode.getClass().getSimpleName());
                    }
                    if (jNode.getStartToken().startCharacterNumber < root.getStartToken().startCharacterNumber) {
                        throw new JShouldNeverHappenException("invalid startChar for " + jNode.getClass().getSimpleName());
                    }
                    if (jNode.getEndToken().endCharacterNumber > root.getEndToken().endCharacterNumber) {
                        throw new JShouldNeverHappenException("invalid endChar for " + jNode.getClass().getSimpleName());
                    }
                    if (jNodeLast != null) {
                        if (jNode.getStartToken().startCharacterNumber < jNodeLast.getEndToken().endCharacterNumber) {
                            if (jNode instanceof HNArrayNew || root instanceof HNArrayNew) {
                                //ignore for now
                                //this is the case where array is initialized like this
                                //int[10] x(2);
                            } else {
                                throw new JShouldNeverHappenException("invalid startChar for " + jNode.getClass().getSimpleName());
                            }
                        }
                    }
                    jNodeLast = jNode;
                    debug(jNode);
                }
            }
        }

    }

    private JLocationContext searchNode(HLJCompilerContext compilerContext, int caretOffset, int completeLevel, ProposalsList completionProposals) {
        JLocationContext loctx;
        HNode root = compilerContext.getNode();
        if (root.containsCaret(caretOffset)) {
            List<JNode> jNodes = compilerContext.getNode().getChildrenNodes();
            for (JNode jNode : jNodes) {
                if (jNode != null) {
                    JLocationContext t = searchNode((HLJCompilerContext) compilerContext.nextNode(jNode), caretOffset, completeLevel, completionProposals);
                    if (t != null) {
                        return t;
                    }
                }
            }
            loctx = locate(compilerContext, caretOffset, completeLevel, completionProposals);
            if (loctx != null) {
                return loctx;
            }
        }
        return null;
    }

    private JLocationContext locate(JCompilerContext compilerContext, int caretOffset, int completeLevel, ProposalsList completionProposals) {
        JNode node = compilerContext.getPath().last();
//        System.out.println("[locate] Look into " + compilerContext.path().getPathString());
        switch (node.getClass().getSimpleName()) {
            case "HNDeclareType": {
                HNDeclareType n = (HNDeclareType) node;
                JToken extendsSepToken = n.getExtendsSepToken();
                boolean completed = false;
                if (extendsSepToken != null) {
                    JPositionStyle y = extendsSepToken.getPosition(caretOffset);
                    if (y == JPositionStyle.END) {
                        completeTypes(caretOffset, completeLevel, s -> true, compilerContext, completionProposals);
                        completed = true;
                    }
                }
                break;
            }
            case "HNBlock":
            case "CompilationUnitBlock": {
                HNBlock n = HNBlock.get(node);
                JPositionStyle y = null;
                if (n.getStartToken().def.id == HTokenId.LEFT_CURLY_BRACKET) {
                    y = n.getStartToken().getPosition(caretOffset);
                    if (y != JPositionStyle.AFTER) {
                        return new JLocationContext(compilerContext.getPath(), n.getStartToken(), y, null);
                    }
                }
                if (n.getEndToken().isImage("}")) {
                    y = n.getEndToken().getPosition(caretOffset);
                    if (y != JPositionStyle.BEFORE) {
                        return new JLocationContext(compilerContext.getPath(), n.getEndToken(), y, null);
                    }
                }
                if (completionProposals != null) {
                    completeStatement(caretOffset, completeLevel, x -> true, compilerContext, completionProposals);
                }
                break;
            }
            case "HNDeclareIdentifier": {
                HNDeclareIdentifier n = (HNDeclareIdentifier) node;
                HNTypeToken identifierTypeName = n.getIdentifierTypeNode();
                JPositionStyle y = null;
                y = AbstractJNode.getPosition(identifierTypeName, caretOffset);
                if (y == JPositionStyle.START) {
                    //start of the type
                    completeTypes(identifierTypeName.getNameToken().startCharacterNumber, completeLevel, x -> true, compilerContext, completionProposals);
                    return new JLocationContext(compilerContext.getPath(), identifierTypeName.getStartToken(), y, null);
                } else if (y == JPositionStyle.MIDDLE) {
                    JPositionStyle y2 = identifierTypeName.getNameToken().getPosition(caretOffset);
                    if (y2.isInside()) {
                        JToken token = identifierTypeName.getNameToken();
                        String image = token.image;
                        String sub = image.substring(0,
                                caretOffset - identifierTypeName.getNameToken().startCharacterNumber
                        );
                        completeTypes(identifierTypeName.getNameToken().startCharacterNumber - sub.length(), completeLevel,
                                new SubStringPredicate(sub)
                                , compilerContext, completionProposals);
                        return new JLocationContext(compilerContext.getPath(), token, y2, null);
                    }
                } else if (y == JPositionStyle.END) {
                    //end of the type
                    return new JLocationContext(
                            compilerContext.getPath(),
                            null, compilerContext.getNode().getPosition(caretOffset), null
                    );
                } else if (y == JPositionStyle.AFTER) {
                    y = n.getAssignOperator().getPosition(caretOffset);
                    if (completionProposals != null) {
                        if (y == null || y == JPositionStyle.BEFORE || y == JPositionStyle.START) {
                            HNTypeToken tname = n.getIdentifierTypeNode();
                            if (tname != null) {
                                String sname = tname.getNameToken().sval;
                                switch (sname) {
                                    case "boolean": {
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "b"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "aBool"));
                                        break;
                                    }
                                    case "byte": {
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "b"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "aByte"));
                                        break;
                                    }
                                    case "short": {
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "s"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "aShort"));
                                        break;
                                    }
                                    case "char": {
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "c"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "aChar"));
                                        break;
                                    }
                                    case "int": {
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "i"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "j"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "k"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "anInt"));
                                        break;
                                    }
                                    case "long": {
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "i"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "j"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "k"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "aLong"));
                                        break;
                                    }
                                    case "float": {
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "f"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "aFloat"));
                                        break;
                                    }
                                    case "double": {
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "d"));
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "aDouble"));
                                        break;
                                    }
                                    default: {
                                        completionProposals.add(HCompletionProposals.proposeVar(caretOffset, "a" + JStringUtils.capitalize(sname)));
                                    }
                                }
                            }
                        }
                    }
                }
                for (HNDeclareTokenIdentifier identifierToken : HNodeUtils.flatten(n.getIdentifierToken())) {
                    y = identifierToken.getToken().getPositionStrict(caretOffset);
                    if (y != null) {
                        return new JLocationContext(compilerContext.getPath(), identifierToken.getToken(), y, null);
                    }
                }
                JToken assignOperator = n.getAssignOperator();
                y = assignOperator.getPosition(caretOffset);
                if (y == JPositionStyle.AFTER) {
                    //after '='
                }
                if (y != JPositionStyle.BEFORE && y != JPositionStyle.AFTER) {
                    return new JLocationContext(compilerContext.getPath(), assignOperator, y, null);
                }
                break;
            }
//            case "HNDeclareInvokable":
//                return JNodeHDeclareInvokable_ToString((HNDeclareInvokable) node, cuctx, path);
            case "HNLiteral": {
                HNLiteral xnode = (HNLiteral) node;
                JPositionStyle y = xnode.getStartToken().getPosition(caretOffset);
                if (y == JPositionStyle.START) {
                    completeExpr(caretOffset, completeLevel, x -> true, compilerContext, completionProposals);
                }
                break;
            }
            case "HNLiteralDefault": {
                HNLiteralDefault xnode = (HNLiteralDefault) node;
                JPositionStyle y = xnode.getStartToken().getPosition(caretOffset);
                if (y == JPositionStyle.START) {
                    completeExpr(caretOffset, completeLevel, x -> true, compilerContext, completionProposals);
                } else if (y == JPositionStyle.AFTER) {
                    completeTypes(caretOffset, completeLevel, x -> true, compilerContext, completionProposals);
                }
                break;
            }
            case "HNAssign": {
                HNAssign n = (HNAssign) node;
                JToken t = n.getOp();
                JPositionStyle y = t.getPositionStrict(caretOffset);
                if (y != null) {
                    return new JLocationContext(
                            compilerContext.getPath(),
                            t,
                            y,
                            null
                    );
                }
                break;
            }
            case "HNThis": {
                HNThis n = (HNThis) node;
                JToken t = n.getStartToken();
                JPositionStyle y = t.getPositionStrict(caretOffset);
                if (y != null) {
                    return new JLocationContext(
                            compilerContext.getPath(),
                            t,
                            y,
                            null
                    );
                }
                break;
            }
            case "HNSuper": {
                HNSuper n = (HNSuper) node;
                JToken t = n.getStartToken();
                JPositionStyle y = t.getPositionStrict(caretOffset);
                if (y != null) {
                    return new JLocationContext(
                            compilerContext.getPath(),
                            t,
                            y,
                            null
                    );
                }
                break;
            }
            case "HNOpBinaryCall": {
                HNOpBinaryCall n = (HNOpBinaryCall) node;
                JPositionStyle y = n.getNameToken().getPositionStrict(caretOffset);
                if (y == JPositionStyle.END) {
                    //after binary operator
                    completeExpr(caretOffset, completeLevel, s -> true, compilerContext, completionProposals);
                }
                break;
            }
//            case "HNParsPostfix": {
//                HNParsPostfix n=(HNParsPostfix)node;
//                HNPars right = n.getRight();
//                boolean completed=false;
//                JPositionStyle y = n.getRight().startToken().getPosition(caretOffset);
//                if(y==JPositionStyle.END){
//                    //after binary operator
//                    completeExpr(caretOffset, completeLevel, s -> true,compilerContext,completionProposals);
//                    completed=true;
//                }
//                if(!completed){
//                    y = n.getRight().endToken().getPosition(caretOffset);
//                    if(y==JPositionStyle.START){
//                        //after binary operator
//                        completeExpr(caretOffset, completeLevel, s -> true,compilerContext,completionProposals);
//                        completed=true;
//                    }
//                }
//                break;
//            }
            case "HNPars": {
                HNPars n = (HNPars) node;
                boolean completed = false;
                JPositionStyle y = n.getStartToken().getPosition(caretOffset);
                if (y == JPositionStyle.END) {
                    //after binary operator
                    completeExpr(caretOffset, completeLevel, s -> true, compilerContext, completionProposals);
                    completed = true;
                }
                if (!completed) {
                    y = n.getEndToken().getPosition(caretOffset);
                    if (y == JPositionStyle.START) {
                        //after binary operator
                        completeExpr(caretOffset, completeLevel, s -> true, compilerContext, completionProposals);
                        completed = true;
                    }
                }
                break;
            }
            case "HNExtends": {
                HNExtends n = (HNExtends) node;
                boolean completed = false;
                JPositionStyle y = n.getStartToken().getPosition(caretOffset);
                if (y == JPositionStyle.START) {
                    completeTypes(caretOffset, completeLevel, s -> true, compilerContext, completionProposals);
                    completed = true;
                }
                break;
            }
//            case "HNOpUnaryCall":
//                return JNodeHOpUnaryCall_ToString((HNOpUnaryCall) node, cuctx, path);
//            case "HNObjectNew":
//                return JNodeHObjectNew_ToString((HNObjectNew) node, cuctx, path);
//            case "HNOpCoalesce":
//                return JNodeHApplyWhenExistsOperator_ToString((HNOpCoalesce) node, cuctx, path);
//            case "HNArrayNew":
//                return JNodeHArrayNew_ToString((HNArrayNew) node, cuctx, path);
//            case "HNArrayCall":
//                return JNodeHArrayGet_ToString((HNArrayCall) node, cuctx, path);
//            case "HNLambdaExpression":
//                return JNodeHLambdaExpression_ToString((HNLambdaExpression) node, cuctx, path);
//            case "HNTuple":
//                return JNodeHTuple_ToString((HNTuple) node, cuctx, path);
//            case "HNIf":
//                return JNodeHIf_ToString((HNIf) node, cuctx, path);
//            case "HNWhile":
//                return JNodeHWhileNode_ToString((HNWhile) node, cuctx, path);
//            case "HNFor":
//                return JNodeHForNode_ToString((HNFor) node, cuctx, path);
//            case "HNBreak":
//                return JNodeHBreak_ToString((HNBreak) node, cuctx, path);
            case "HNBreak":
            case "HNContinue": {
                HNBreakOrContinue n = (HNBreakOrContinue) node;
                JPositionStyle posForReturn = n.getStartToken().getPosition(caretOffset);
                JToken expr = n.getLeaps();
                if (posForReturn == JPositionStyle.START) {
                    completeStatement(caretOffset, completeLevel, x -> true, compilerContext, completionProposals);
                }
                if (posForReturn == JPositionStyle.AFTER && expr != null) {
                    if (expr.containsCaret(caretOffset)) {
                        return new JLocationContext(
                                compilerContext.getPath(),
                                null, compilerContext.getNode().getPosition(caretOffset), null
                        );
                    }
                }
                return new JLocationContext(
                        compilerContext.getPath(),
                        n.getStartToken(),
                        posForReturn,
                        null
                );
            }
            case "HNOpDot": {
                HNOpDot n = (HNOpDot) node;
                if (n.getOp().getPosition(caretOffset) == JPositionStyle.END) {
                    if (completionProposals != null) {
                        JNode left = n.getLeft();
                        HNElement kind = ((HNode) left).getElement();
                        if (kind != null) {
                            switch (kind.getKind()) {
                                case PACKAGE: {
                                    break;
                                }
                            }
                        }
                        System.out.println(left);
                    }
                }
                break;
            }
            case "HNReturn": {
                HNReturn n = (HNReturn) node;
                JPositionStyle posForReturn = n.getStartToken().getPosition(caretOffset);
                JNode expr = n.getExpr();
                if (posForReturn == JPositionStyle.START) {
                    completeStatement(caretOffset, completeLevel, x -> true, compilerContext, completionProposals);
                }
                if (posForReturn == JPositionStyle.AFTER && expr != null) {
                    if (expr.containsCaret(caretOffset)) {
                        //will be processed later;
                        return new JLocationContext(
                                compilerContext.getPath(),
                                null, compilerContext.getNode().getPosition(caretOffset), null
                        );
                    }
                }
                return new JLocationContext(
                        compilerContext.getPath(),
                        n.getStartToken(),
                        posForReturn,
                        null
                );
            }
//            case "JNodeHOpClass":
//                return JNodeHOpClass_ToString((HNDotClass) node, cuctx, path);
            case "HNDotThis": {
                HNDotThis n = (HNDotThis) node;
                JPositionStyle y = n.getDotToken().getPosition(caretOffset);
                if (y == JPositionStyle.END) {
                    if (completionProposals != null) {
                        completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, "class"));
                        // should also add static methods and fields...
                    }
                }
                break;
            }
            case "HNDotClass": {
                HNDotClass n = (HNDotClass) node;
                JPositionStyle y = n.getDotToken().getPosition(caretOffset);
                if (y == JPositionStyle.END) {
                    if (completionProposals != null) {
                        completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, "this"));
                        // should also add static methods and fields...
                    }
                }
                break;
            }
//            case "HNSwitch":
//                return JNodeHSwitch_ToString((HNSwitch) node, cuctx, path);
            case "HNIdentifier": {
                HNIdentifier n = (HNIdentifier) node;
                if (isExprNode(n.getParentNode())) {
                    JToken token = n.getNameToken();
                    String image = token.image;
                    String sub = image.substring(0, caretOffset - token.startCharacterNumber);
                    completeExpr(token.startCharacterNumber, completeLevel, new SubStringPredicate(sub)
                            , compilerContext, completionProposals);
                } else if (isStatementNode(n.getParentNode())) {
                    JToken token = n.getNameToken();
                    String image = token.image;
                    String sub = image.substring(0, caretOffset - token.startCharacterNumber);
                    completeStatement(token.startCharacterNumber, completeLevel, new SubStringPredicate(sub)
                            , compilerContext, completionProposals);
                } else {
                    JPositionStyle y = JToken.getPosition(n.getNameToken(), caretOffset);
                    if (y == JPositionStyle.END) {
                        if (completionProposals != null) {
                            for (JTokenDef token : projectContext.languageContext().tokens().tokenDefinitions()) {
                                if (token.ttype == JTokenType.TT_KEYWORD) {
                                    if (token.idName.startsWith(n.getName())) {
                                        completionProposals.add(HCompletionProposals.proposeKeyword(n.getNameToken().startCharacterNumber, token.idName));
                                    }
                                }
                            }
                        }
                    }
                    return new JLocationContext(
                            compilerContext.getPath(),
                            n.getNameToken(),
                            y,
                            null
                    );
                }
                break;
            }
            case "HNTypeToken": {
                HNTypeToken n = (HNTypeToken) node;
                JPositionStyle y = n.getNameToken().getPositionStrict(caretOffset);
                if (y == JPositionStyle.START) {
                    completeTypes(caretOffset, completeLevel, x -> true, compilerContext, completionProposals);
                } else if (y == JPositionStyle.MIDDLE) {
                    String image = n.getNameToken().sval;
                    String sub = image.substring(0,
                            caretOffset - n.getNameToken().startCharacterNumber
                    );
                    completeTypes(caretOffset - sub.length(), completeLevel, new SubStringPredicate(sub), compilerContext, completionProposals);
                }
                break;
            }
//            case "HNCast":
//                return JNodeHCast_ToString((HNCast) node, cuctx, path);
//            case "HNIs":
//                return JNodeHIs_ToString((HNIs) node, cuctx, path);
            case "HNField":
            case "HXInvokableCall":
            case "HNXInvokableCall":
            case "JNodeRaw":
            case "HNVar": {
                throw new JShouldNeverHappenException();
            }
        }
        System.err.println("Not yet supported " + node.getClass().getSimpleName());
        return new JLocationContext(
                compilerContext.getPath(),
                null, compilerContext.getNode().getPosition(caretOffset), null
        );
    }

    private void completeStatement(int caretOffset, int completeLevel, Predicate<String> prefix, JCompilerContext compilerContext, ProposalsList completionProposals) {
        if (completionProposals != null) {
            System.err.println("completeStatement");

            completeBlocVarsAndMethods(caretOffset, completeLevel, prefix, compilerContext.getNode(), compilerContext, completionProposals);
            completeTypes(caretOffset, completeLevel, prefix, compilerContext, completionProposals);
            JNode firstDeclaringParent = compilerContext.getNode();
            while (firstDeclaringParent != null &&
                    !(
                            (firstDeclaringParent instanceof HNBlock && ((HNBlock) firstDeclaringParent).getBlocType() != HNBlock.BlocType.IMPORT_BLOC)
                                    || (firstDeclaringParent instanceof HNDeclareInvokable)
                                    || (firstDeclaringParent instanceof HNDeclareType)
                                    || (firstDeclaringParent instanceof HNDeclareMetaPackage)
                    )) {
                firstDeclaringParent = firstDeclaringParent.getParentNode();
            }
            for (String kw : new String[]{"fun", "var", "val", "class"}) {
                completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, kw));
            }
            boolean _localBloc = (firstDeclaringParent instanceof HNBlock)
                    && ((HNBlock) firstDeclaringParent).getBlocType() != HNBlock.BlocType.GLOBAL_BODY
                    && ((HNBlock) firstDeclaringParent).getBlocType() != HNBlock.BlocType.CLASS_BODY;
            boolean _cls = (
                    (firstDeclaringParent instanceof HNBlock)
                            && ((HNBlock) firstDeclaringParent).getBlocType() != HNBlock.BlocType.CLASS_BODY
            ) || firstDeclaringParent instanceof HNDeclareType;
            boolean _global = (
                    (firstDeclaringParent instanceof HNBlock)
                            && ((HNBlock) firstDeclaringParent).getBlocType() != HNBlock.BlocType.GLOBAL_BODY
            );
            if (_cls || _global) {
                for (String kw : new String[]{"public", "static", "private", "protected"}) {
                    if (prefix.test(kw)) {
                        completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, kw));
                    }
                }
            }
            if (_cls) {
                for (String kw : new String[]{"init", "operator"}) {
                    if (prefix.test(kw)) {
                        completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, kw));
                    }
                }
            }
            completeTypes(caretOffset, completeLevel, prefix, compilerContext, completionProposals);
            completeBlocVarsAndMethods(caretOffset, completeLevel, prefix, compilerContext.getNode(), compilerContext, completionProposals);
            completeTypes(caretOffset, completeLevel, prefix, compilerContext, completionProposals);

            for (String kw : new String[]{"fun", "var", "val", "class"}) {
                if (prefix.test(kw)) {
                    completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, kw));
                }
            }
//        if(n.getBlocType()== HNBlock.BlocType.CLASS_BODY || n.getBlocType()== HNBlock.BlocType.GLOBAL_BODY){
//            for (String kw : new String[]{"public", "static", "private", "protected"}) {
//                completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, kw));
//            }
//        }
//        if(n.getBlocType()== HNBlock.BlocType.CLASS_BODY){
//            completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, "init"));
//            completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, "operator"));
//        }
            completeTypes(caretOffset, completeLevel, prefix, compilerContext, completionProposals);
        }
    }

    private void completeExpr(int caretOffset, int completeLevel, Predicate<String> prefix, JCompilerContext compilerContext, ProposalsList completionProposals) {
        if (completionProposals != null) {
            System.err.println("completeExpr");
            for (String kw : new String[]{"null", "true", "false"}) {
                if (prefix.test(kw)) {
                    completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, kw));
                }
            }
            completeBlocVarsAndMethods(caretOffset, completeLevel, prefix, compilerContext.getNode(), compilerContext, completionProposals);
        }
    }

    private void completeBlocVarsAndMethods(int caretOffset, int completeLevel, Predicate<String> prefix, JNode n, JCompilerContext compilerContext, ProposalsList completionProposals) {
        if (completionProposals != null) {
            System.err.println("completeBlocVarsAndMethods");
            JNode n0 = n;
            boolean _breakOrContinue = false;
            boolean _return = false;
            while (n0 != null) {
                if (!_breakOrContinue) {
                    if (n0 instanceof HNIf) {
                        JNode n1 = n0.getParentNode();
                        boolean inWhileOrFor = false;
                        while (n1 != null) {
                            if (n1 instanceof HNWhile || n1 instanceof HNFor) {
                                inWhileOrFor = true;
                                break;
                            }
                        }
                        if (inWhileOrFor) {
                            _breakOrContinue = true;
                            if (prefix.test("break")) {
                                completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, "break"));
                            }
                            if (prefix.test("continue")) {
                                completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, "continue"));
                            }
                        }
                    }
                }
                if (!_return) {
                    if (n0 instanceof HNDeclareInvokable && !((HNDeclareInvokable) n0).isConstructor()) {
                        _return = true;
                        if (prefix.test("return")) {
                            completionProposals.add(HCompletionProposals.proposeKeyword(caretOffset, "return"));
                        }
                    }
                }
                if (n0 instanceof HNDeclareInvokable) {
                    HNDeclareInvokable inv = (HNDeclareInvokable) n0;
                    for (HNDeclareIdentifier argument : inv.getArguments()) {
                        if (prefix.test(argument.getIdentifierName())) {
                            completionProposals.add(HCompletionProposals.proposeVar(caretOffset, argument.getIdentifierName()));
                        }
                    }
                }
                if (n0 instanceof HNBlock) {
                    HNBlock body = (HNBlock) n0;
                    for (JNode statement : body.getStatements()) {
                        if (statement instanceof HNDeclareType) {
                            String fullName = ((HNDeclareType) statement).getFullName();
                            if (prefix.test(fullName)) {
                                completionProposals.add(HCompletionProposals.proposeType(caretOffset, fullName));
                            }
                        }
                        if (statement instanceof HNDeclareInvokable) {
                            HNDeclareInvokable mm = (HNDeclareInvokable) statement;
                            String[] argTypes = mm.getArguments().stream().map(x -> x.getIdentifierTypeNode().getTypename().fullName())
                                    .toArray(String[]::new);
                            String[] argNames = mm.getArguments().stream().map(x -> HNodeUtils.flatten(x.getIdentifierToken()))
                                    .map(x->Arrays.stream(x).map(HNDeclareTokenIdentifier::getName).toArray(String[]::new))
                                    .reduce(
                                    (x1, x2) -> {
                                        List<String> t = new ArrayList<>();
                                        t.addAll(Arrays.asList(x1));
                                        t.addAll(Arrays.asList(x2));
                                        return t.toArray(new String[0]);
                                    }
                            ).orElse(new String[0]);
                            String name = mm.getName();
                            if (mm.isConstructor()) {
                                name = mm.getDeclaringType().getName();
                                if (prefix.test(name)) {
                                    completionProposals.add(HCompletionProposals.proposeConstructor(caretOffset, name, argNames, argTypes));
                                }
                            } else {
                                if (prefix.test(name)) {
                                    completionProposals.add(HCompletionProposals.proposeMethod(caretOffset, name, argNames, argTypes));
                                }
                            }
                        }
                        if (statement instanceof HNDeclareIdentifier) {
                            HNDeclareIdentifier mm = (HNDeclareIdentifier) statement;
                            for (HNDeclareTokenIdentifier identifierToken : HNodeUtils.flatten(mm.getIdentifierToken())) {
                                if (prefix.test(identifierToken.getName())) {
                                    completionProposals.add(HCompletionProposals.proposeVar(caretOffset, identifierToken.getName()));
                                }
                            }
                        }
                    }
                }
                n0 = n0.getParentNode();
            }
        }
    }

    private void completeTypes(int offset, int completeLevel, Predicate<String> prefix, JCompilerContext compilerContext, ProposalsList completionProposals) {
        System.err.println("completeTypes");
        if (completionProposals == null) {
            return;
        }
        HLJCompilerContext hCompilerContext = (HLJCompilerContext) compilerContext;
        for (JType jtype : hCompilerContext.lookupTypes(completeLevel <= 0, (String fullName) -> {
            int d = fullName.lastIndexOf('.');
            String name = d < 0 ? fullName : fullName.substring(d + 1);
            return prefix.test(name);
        })) {
            completionProposals.add(HCompletionProposals.proposeType(offset, jtype));
        }
    }

    private boolean isExprNode(JNode n) {
        if (n instanceof HNDeclareTokenBase) {
            return true;
        }
        if (n instanceof HNDeclare) {
            return false;
        }
        return true;
    }

    private boolean isStatementNode(JNode n) {
        return n.getParentNode() instanceof HNBlock;
    }

    private static class ProposalsList {
        Map<String, JCompletionProposal> map = new HashMap<>();

        public void add(JCompletionProposal p) {
            if (!map.containsKey(p.sortText())) {
                map.put(p.sortText(), p);
            }
        }

        public List<JCompletionProposal> list() {
            return new ArrayList<>(map.values());
        }
    }

    private static class SubStringPredicate implements Predicate<String> {
        private final String sub;

        public SubStringPredicate(String sub) {
            this.sub = sub;
        }

        @Override
        public boolean test(String s) {
            return s.startsWith(sub);
        }
    }
}
