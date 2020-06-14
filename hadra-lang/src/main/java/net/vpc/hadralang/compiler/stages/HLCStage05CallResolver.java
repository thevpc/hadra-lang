package net.vpc.hadralang.compiler.stages;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.types.DefaultJField;
import net.vpc.common.jeep.impl.types.DefaultJRawMethod;
import net.vpc.common.jeep.util.JTypeUtils;
import net.vpc.common.jeep.util.JeepUtils;
import net.vpc.hadralang.compiler.core.HFunctionType;
import net.vpc.hadralang.compiler.core.HLCOptions;
import net.vpc.hadralang.compiler.core.HLProject;
import net.vpc.hadralang.compiler.core.HMissingLinkageException;
import net.vpc.hadralang.compiler.core.elements.*;
import net.vpc.hadralang.compiler.core.invokables.FindMatchFailInfo;
import net.vpc.hadralang.compiler.core.invokables.HLJCompilerContext;
import net.vpc.hadralang.compiler.index.HLIndexedField;
import net.vpc.hadralang.compiler.index.HLIndexedMethod;
import net.vpc.hadralang.compiler.parser.ast.*;
import net.vpc.hadralang.compiler.utils.HLExtensionNames;
import net.vpc.hadralang.compiler.utils.HNodeUtils;
import net.vpc.hadralang.compiler.utils.HTypeUtils;
import net.vpc.hadralang.compiler.utils.HUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HLCStage05CallResolver extends HLCStageType2 {

    public static final Logger LOG = Logger.getLogger(HLCStage05CallResolver.class.getName());
    private List<HLJCompilerContext> replays = new ArrayList<>();
    private boolean showFinalErrors = false;
    private boolean inPreprocessor = false;
    private boolean implicitConvertAssignment = true;

    public HLCStage05CallResolver(boolean inPreprocessor) {
        this.inPreprocessor = inPreprocessor;
    }

    public boolean processCompilerStageCurrent(HNode node, HLJCompilerContext compilerContext) {
        switch (node.id()) {
            case H_IDENTIFIER: {
                return onIdentifier((HNIdentifier) node, compilerContext);
            }
            case H_OP_UNARY: {
                return onOpUnaryCall((HNOpUnaryCall) node, compilerContext);
            }
            case H_OP_BINARY: {
                return onOpBinaryCall((HNOpBinaryCall) node, compilerContext);
            }
            case H_DECLARE_IDENTIFIER: {
                return onDeclareIdentifier((HNDeclareIdentifier) node, compilerContext);
            }
            case H_DECLARE_INVOKABLE: {
                return onDeclareInvokable((HNDeclareInvokable) node, compilerContext);
            }
            case H_ARRAY_NEW: {
                return onArrayNew((HNArrayNew) node, compilerContext);
            }
            case H_PARS_POSTFIX: {
                return onParsPostfix((HNParsPostfix) node, compilerContext);
            }
            case H_ASSIGN: {
                return onAssign((HNAssign) node, compilerContext);
            }
            case H_BRACKETS: {
                return onBrackets((HNBrackets) node, compilerContext);
            }
            case H_BRACKETS_POSTFIX: {
                return onBracketsPostfix((HNBracketsPostfix) node, compilerContext);
            }
            case H_OBJECT_NEW: {
                return onObjectNew((HNObjectNew) node, compilerContext);
            }
            case H_TUPLE: {
                return onTuple((HNTuple) node, compilerContext);
            }
            case H_OP_DOT: {
                return onOpDot((HNOpDot) node, compilerContext);
            }
            case H_OP_COALESCE: {
                return onOpCoalesce((HNOpCoalesce) node, compilerContext);
            }
            case H_DECLARE_TYPE: {
                return onDeclareType((HNDeclareType) node, compilerContext);
            }
            case H_LAMBDA_EXPR: {
                return onLambdaExpression((HNLambdaExpression) node, compilerContext);
            }
            case H_PARS: {
                return onPars((HNPars) node, compilerContext);
            }
            case H_IS: {
                return onIs((HNIs) node, compilerContext);
            }
            case H_IF_WHEN_DO: {
                return onWhenDoBranchNode((HNIf.WhenDoBranchNode) node, compilerContext);
            }
            case H_IF: {
                return onIf((HNIf) node, compilerContext);
            }
            case H_WHILE: {
                return onWhile((HNWhile) node, compilerContext);
            }
            case H_FOR: {
                return onFor((HNFor) node, compilerContext);
            }
            case H_CONTINUE: {
                return onContinue((HNContinue) node, compilerContext);
            }
            case H_BREAK: {
                return onBreak((HNBreak) node, compilerContext);
            }
            case H_DECLARE_TOKEN_IDENTIFIER: {
                return onDeclareTokenIdentifier((HNDeclareTokenIdentifier) node, compilerContext);
            }
            case H_DECLARE_TOKEN_LIST: {
                return onDeclareTokenList((HNDeclareTokenList) node, compilerContext);
            }
            case H_DECLARE_TOKEN_TUPLE: {
                return onDeclareTokenTuple((HNDeclareTokenTuple) node, compilerContext);
            }
            case H_SWITCH: {
                return onSwitch((HNSwitch) node, compilerContext);
            }
            case H_SWITCH_CASE: {
                return onSwitchCase((HNSwitch.SwitchCase) node, compilerContext);
            }
            case H_SWITCH_IS: {
                return onSwitchIs((HNSwitch.SwitchIs) node, compilerContext);
            }
            case H_SWITCH_IF: {
                return onSwitchIf((HNSwitch.SwitchIf) node, compilerContext);
            }
            case H_RETURN: {
                return onReturn((HNReturn) node, compilerContext);
            }
            case H_DOT_CLASS: {
                return onDotClass((HNDotClass) node, compilerContext);
            }
            case H_EXTENDS: {
                return onExtends((HNExtends) node, compilerContext);
            }
            case H_THIS: {
                return onThis((HNThis) node, compilerContext);
            }
            case H_SUPER: {
                return onSuper((HNSuper) node, compilerContext);
            }
            case H_LITERAL_DEFAULT:
            case H_LITERAL:
            case H_BLOCK:
            case H_TYPE_TOKEN:
            case H_STRING_INTEROP:
            case H_IMPORT: {
                //do nothing
                return true;
            }
            case H_META_PACKAGE_GROUP:
            case H_META_PACKAGE_ARTIFACT:
            case H_META_PACKAGE_VERSION:
            case H_META_PACKAGE_ID:
            case H_DECLARE_META_PACKAGE: {
                //wont happen
                throw new JShouldNeverHappenException();
            }

            /////////////////////////////////////////
            case H_META_IMPORT_PACKAGE: {
                return onMetaImportPackage((HNMetaImportPackage) node, compilerContext);

            }

            case H_INVOKER_CALL:
            case H_INVOKE_METHOD: {
                throw new JShouldNeverHappenException();
            }
        }
        //in stage 1 wont change node instance
        throw new JShouldNeverHappenException("Unsupported node class in " + getClass().getSimpleName() + ": " + node.getClass().getSimpleName());
//        return node;
    }

    private boolean onExtends(HNExtends node, HLJCompilerContext compilerContext) {
        //node.setElement(new HNElementNonExpr());
        return false;
    }

    private boolean onSuper(HNSuper node, HLJCompilerContext compilerContext) {
        //node.setElement(new HNElementNonExpr());
        return true;
    }
    private boolean onThis(HNThis node, HLJCompilerContext compilerContext) {
        //node.setElement(new HNElementNonExpr());
        return true;
    }

    private boolean onDotClass(HNDotClass node, HLJCompilerContext compilerContext) {
        HNElementExpr e = (HNElementExpr) node.getElement();
        if (e.getType() == null) {
            JType tv = node.getTypeRefName().getTypeVal();
            if (tv != null) {
                e.setType(HTypeUtils.classOf(tv));
            }
        }
        return true;
    }

    private boolean onReturn(HNReturn node, HLJCompilerContext compilerContext) {
        return true;
    }

    private boolean onSwitchIf(HNSwitch.SwitchIf node, HLJCompilerContext compilerContext) {
        return true;
    }

    private boolean onSwitchIs(HNSwitch.SwitchIs node, HLJCompilerContext compilerContext) {
        if (node.getIdentifierToken() != null) {
            //when IdentifierToken is present, only one item is present in 'WhenTypes'
            HNElementLocalVar element = (HNElementLocalVar) node.getIdentifierToken().getElement();
            JType typeVal = node.getWhenTypes().get(0).getTypeVal();
            element.setEffectiveType(typeVal);
        }
        return true;
    }

    private boolean onSwitchCase(HNSwitch.SwitchCase node, HLJCompilerContext compilerContext) {
        ((HNElementExpr) node.getElement()).setType(JTypeUtils.forVoid(compilerContext.types()));
        return true;
    }

    private boolean onSwitch(HNSwitch node, HLJCompilerContext compilerContext) {
        JTypeOrLambda t = null;
        for (HNSwitch.SwitchBranch aCase : node.getCases()) {
            if (aCase.getOp().isImage("->")) {
                HNode e = (HNode) aCase.getDoNode();
                JTypeOrLambda t2 = e.getElement().getTypeOrLambda();
                if (t2 == null) {
                    return false;
                }
                t = JTypeUtils.firstCommonSuperTypeOrLambda(t, t2, compilerContext.types());
            } else {
                ((HNElementExpr) node.getElement()).setType(JTypeUtils.forVoid(compilerContext.types()));
                return true;
            }
        }
        if (t == null) {
            return false;
        }
        ((HNElementExpr) node.getElement()).setType(t);
        return true;
    }

    private boolean onDeclareTokenTuple(HNDeclareTokenTuple node, HLJCompilerContext compilerContext) {
        return true;
    }

    private boolean onDeclareTokenList(HNDeclareTokenList node, HLJCompilerContext compilerContext) {
        return true;
    }

    private HNode lookupDeclarationStatement(HNDeclareTokenIdentifier node, HLJCompilerContext compilerContext) {
        HNode n = node.parentNode();
        while (true) {
            if (n instanceof HNIs) {
                return n;
            } else if (n instanceof HNDeclareIdentifier) {
                return n;
            } else if (n instanceof HNSwitch.SwitchIs) {
                return n;
            } else if (n instanceof HNDeclareTokenList) {
                n = n.parentNode();
            } else if (n instanceof HNDeclareTokenTuple) {
                n = n.parentNode();
            } else {
                throw new JShouldNeverHappenException();
            }
        }
    }

    private boolean onDeclareTokenIdentifier(HNDeclareTokenIdentifier node, HLJCompilerContext compilerContext) {
//        HNode p = lookupDeclarationStatement(node, compilerContext);
//        if (p instanceof HNIs) {
//            HNIs n=(HNIs)p;
//            if (n.getIdentifierToken() != null) {
//                HNElementLocalVar element = (HNElementLocalVar) node.getElement();
//                element.setEffectiveType(node.getIdentifierType());
//            }
//        }else if (p instanceof HNSwitch.SwitchIs) {
//            HNSwitch.SwitchIs n=(HNSwitch.SwitchIs)p;
//            if (n.getIdentifierToken() != null) {
//                HNElementLocalVar element = (HNElementLocalVar) node.getElement();
//                element.setEffectiveType(node.getIdentifierType());
//            }
//        }else if (p instanceof HNDeclareIdentifier) {
//            //will be processed later...
////            HNDeclareIdentifier n=(HNDeclareIdentifier)p;
////            if (n.getIdentifierToken() != null) {
////                HNElementLocalVar element = (HNElementLocalVar) node.getElement();
////                element.setEffectiveType(node.getIdentifierType());
////            }
//        }

        return true;
    }

    private boolean onWhile(HNWhile node, HLJCompilerContext compilerContext) {
        HNode expr = node.getExpr();
        JTypeOrLambda exprTypeOrLambda = compilerContext.jTypeOrLambda(showFinalErrors, expr);
        HNode block = node.getBlock();
        JTypeOrLambda blockTypeOrLambda = compilerContext.jTypeOrLambda(showFinalErrors, block);
        if (exprTypeOrLambda == null || blockTypeOrLambda == null) {
            return false;
        }
        if (exprTypeOrLambda.isType() && JTypeUtils.isBooleanResolvableType(exprTypeOrLambda)) {
            ((HNElementExpr) node.getElement()).setType(JTypeUtils.forVoid(compilerContext.types()));
        } else {
            JType exprType = compilerContext.types().forName("java.util.function.Supplier<" + exprTypeOrLambda.getType().name() + ">");
            JType blockType = compilerContext.types().forName("java.util.function.Supplier<" + blockTypeOrLambda.getType().name() + ">");
            FindMatchFailInfo failInfo = new FindMatchFailInfo("<while> function");
            JInvokable fct = compilerContext.lookupFunctionMatch(
                    JOnError.TRACE, "While",
                    HFunctionType.NORMAL, new JTypeOrLambda[]{
                        JTypeOrLambda.of(exprType),
                        JTypeOrLambda.of(blockType)
                    },
                    node.startToken(), failInfo
            );
            if (fct != null) {
                setElement(node, new HNElementWhenDo("while", fct, exprTypeOrLambda.getType(), blockTypeOrLambda.getType(),
                        new HNIf.WhenDoBranchNode[]{
                            new HNIf.WhenDoBranchNode(expr, block, null)
                        }, null));
            }
        }
        return true;
    }

    private boolean onFor(HNFor node, HLJCompilerContext compilerContext) {
        JTypeOrLambda bodyToL = node.getBody() == null ? null : compilerContext.jTypeOrLambda(showFinalErrors, node.getBody());
        JTypeOrLambda filterToL = node.getFilter() == null ? null : compilerContext.jTypeOrLambda(showFinalErrors, node.getFilter());
        JTypeOrLambda[] incsToL = compilerContext.jTypeOrLambdas(showFinalErrors, node.getIncs());
        JTypeOrLambda[] initToL = compilerContext.jTypeOrLambdas(showFinalErrors, node.getInitExprs());

        if (node.getBody() != null && bodyToL == null) {
            return false;
        }
        if (node.getFilter() != null && filterToL == null) {
            return false;
        }
        if (node.getIncs() != null && incsToL == null) {
            return false;
        }
        if (node.getInitExprs() != null && initToL == null) {
            return false;
        }
        if (bodyToL == null) {
            ((HNElementExpr) node.getElement()).setType(JTypeUtils.forVoid(compilerContext.types()));
        } else {
            ((HNElementExpr) node.getElement()).setType(bodyToL);
        }
        return true;
    }

    private boolean onContinue(HNContinue node, HLJCompilerContext compilerContext) {
        return true;
    }

    private boolean onBreak(HNBreak node, HLJCompilerContext compilerContext) {
        return true;
    }

    private boolean onIs(HNIs node, HLJCompilerContext compilerContext) {
        if (node.getIdentifierToken() != null) {
            HNElementLocalVar element = (HNElementLocalVar) node.getIdentifierToken().getElement();
            element.setEffectiveType(node.getIdentifierType());
        }
        return true;
    }

    private boolean onWhenDoBranchNode(HNIf.WhenDoBranchNode node, HLJCompilerContext compilerContext) {
        HNode whenNode = (HNode) node.getWhenNode();
        HNode doNode = (HNode) node.getDoNode();
        JTypeOrLambda whenToL = compilerContext.jTypeOrLambda(showFinalErrors, whenNode);
        JTypeOrLambda doToL = compilerContext.jTypeOrLambda(showFinalErrors, doNode);
        if (whenToL == null || doToL == null) {
            return false;
        }
        HNElementExpr ee = (HNElementExpr) node.getElement();
        ee.setType(doToL);
        return true;
    }

    private boolean onIf(HNIf node, HLJCompilerContext compilerContext) {
        boolean stateStd = true;
        List<HNIf.WhenDoBranchNode> std = new ArrayList<>();
        List<HNIf.WhenDoBranchNode> nonStd = new ArrayList<>();
        List<HNode> whenNodeWithLambdas = new ArrayList<>();
        JTypeOrLambda condType = null;
        JTypeOrLambda resultType = null;
        for (HNIf.WhenDoBranchNode branch : node.getBranches()) {
            HNode whenNode = (HNode) branch.getWhenNode();
            HNode doNode = (HNode) branch.getDoNode();
            JTypeOrLambda whenToL = compilerContext.jTypeOrLambda(showFinalErrors, whenNode);
            JTypeOrLambda doToL = compilerContext.jTypeOrLambda(showFinalErrors, doNode);
            if (whenToL == null || doToL == null) {
                return false;
            }
            if (whenToL.isLambda()) {
                whenNodeWithLambdas.add(whenNode);
            }
            if (stateStd) {
                condType = JTypeUtils.firstCommonSuperTypeOrLambda(condType, whenToL, compilerContext.types());
                if (!JTypeUtils.isBooleanResolvableType(whenToL)) {
                    //this is a standard condition
                    stateStd = false;
                    node.setUserObject("WHEN_DO_TYPE", "METHOD");
                    nonStd.add(branch);
                } else {
                    node.setUserObject("WHEN_DO_TYPE", "BOOLEAN");
                    std.add(branch);
                }
            } else {
                condType = JTypeUtils.firstCommonSuperTypeOrLambda(condType, whenToL, compilerContext.types());
                node.setUserObject("WHEN_DO_TYPE", "METHOD");
                nonStd.add(branch);
            }
            resultType = JTypeUtils.firstCommonSuperTypeOrLambda(resultType, doToL, compilerContext.types());
        }
        if (node.getElseNode() != null) {
            JTypeOrLambda elseToL = compilerContext.jTypeOrLambda(showFinalErrors, node.getElseNode());
            if (elseToL == null) {
                return false;
            }
            resultType = JTypeUtils.firstCommonSuperTypeOrLambda(resultType, elseToL, compilerContext.types());
        }
        if (condType == null || condType.isLambda()) {
            compilerContext.log().error("X000", null, "invalid condition type : " + condType, node.startToken());
            for (HNode whenNodeWithLambda : whenNodeWithLambdas) {
                compilerContext.log().error("X000", null, "invalid condition type", whenNodeWithLambda.startToken());
            }
        }
        if (resultType == null || resultType.isLambda()) {
            resultType = JTypeOrLambda.of(JTypeUtils.forObject(compilerContext.types()));
        }
        if (condType == null) {
            condType = JTypeOrLambda.of(JTypeUtils.forBoolean(compilerContext.types()));
        }
        if (!JTypeUtils.isBooleanResolvableType(condType)) {
            JType branchesArrayType = compilerContext.types().forName("net.vpc.hadralang.stdlib.Branch<" + condType.getType().name()
                    + ","
                    + resultType.getType().name()
                    + ">");
            JType elseType = compilerContext.types().forName("java.util.function.Supplier<" + resultType.getType().name() + ">");
            FindMatchFailInfo failInfo = new FindMatchFailInfo("<if> function");
            JInvokable fct = compilerContext.lookupFunctionMatch(
                    JOnError.TRACE, "if",
                    HFunctionType.SPECIAL, new JTypeOrLambda[]{
                        JTypeOrLambda.of(branchesArrayType.toArray()),
                        JTypeOrLambda.of(elseType)
                    },
                    node.startToken(), failInfo
            );
            if (fct != null) {
                setElement(nonStd.get(0), new HNElementWhenDo("if", fct, condType.getType(),
                        resultType.getType(), nonStd.toArray(new HNIf.WhenDoBranchNode[0]), node.getElseNode()));
            }
        }
        if (!std.isEmpty() || nonStd.isEmpty()) {
            HNElementExpr element = (HNElementExpr) node.getElement();
            element.setType(resultType);
        } else {
            setElement(node, nonStd.get(0).getElement());
        }
        return true;
    }

    private boolean onPars(HNPars node, HLJCompilerContext compilerContext) {
        if (node.getItems().length == 1) {
            HNode n = (HNode) node.getItems()[0];
            if (n.getElement().getTypeOrLambda() != null) {
                ((HNElementExpr) node.getElement()).setType(n.getElement().getTypeOrLambda());
                return true;
            }
        }
        return false;
    }

    private boolean onDeclareType(HNDeclareType node, HLJCompilerContext compilerContext) {
        return true;
    }

    private boolean onMetaImportPackage(HNMetaImportPackage node, HLJCompilerContext compilerContext) {
        return true;
    }

    private boolean onOpCoalesce(HNOpCoalesce node, HLJCompilerContext compilerContext) {
        HNode left = (HNode) node.getLeft();
        HNode right = (HNode) node.getRight();
        if (left.getElement().getTypeOrLambda() == null) {
            return false;
        }
        if (right.getElement().getTypeOrLambda() == null) {
            return false;
        }
        ((HNElementExpr) node.getElement()).setType(
                left.getElement().getType().firstCommonSuperType(
                        right.getElement().getType()
                ));
        return true;
    }

    private boolean onTuple(HNTuple node, HLJCompilerContext compilerContext) {
        JTypeOrLambda[] etypes = compilerContext.jTypeOrLambdas(showFinalErrors, node.getItems());
        if (etypes == null) {
            return false;
        }
        boolean err = false;
        for (int i = 0; i < etypes.length; i++) {
            JTypeOrLambda etype = etypes[i];
            if (etype.isLambda()) {
                compilerContext.log().error("S000", null, "lambda expressions are not supported in Tuples", node.getItems()[i].startToken());
                err = true;
            }
        }
        if (err) {
            return true;
        }
        HNElementExpr e = (HNElementExpr) node.getElement();
        e.setType(HTypeUtils.tupleType(compilerContext.types(), Arrays.stream(etypes).map(JTypeOrLambda::getType).toArray(JType[]::new)));
        return true;
    }

    private boolean onBrackets(HNBrackets node, HLJCompilerContext compilerContext) {
        if (node.fullChildInfo().equals("HNBracketsPostfix:right")) {
            //will be processed later
        } else {
            //this is an array initialization
            JTypeOrLambda[] allToL = compilerContext.jTypeOrLambdas(showFinalErrors, node.getItems());
            if (allToL == null) {
                return false;
            }
            JType arrType = null;
            if (allToL.length == 0) {
                compilerContext.log().error("S000", null, "could not resolve empty array type", node.startToken());
            } else {
                for (int i = 0; i < allToL.length; i++) {
                    if (allToL[i].isLambda()) {
                        compilerContext.log().error("S000", null, "unsupported lambda expressions in array initialization", node.getItems()[i].startToken());
                    } else {
                        if (arrType == null) {
                            arrType = allToL[i].getType();
                        } else {
                            arrType = arrType.firstCommonSuperType(allToL[i].getType());
                        }
                    }
                }
                if (arrType != null) {
                    HNElementExpr e = (HNElementExpr) node.getElement();
                    e.setType(arrType.toArray());
                }
            }
        }
        return true;
    }

    private boolean onBracketsPostfix(HNBracketsPostfix node, HLJCompilerContext compilerContext) {
        HNode base = node.getLeft();
        HNode[] inodes = node.getRight().toArray(new HNode[0]);
        JTypeOrLambda baseToL = compilerContext.jTypeOrLambda(showFinalErrors, base);
        JTypeOrLambda[] inodesToL = compilerContext.jTypeOrLambdas(showFinalErrors, inodes);
        if (inodesToL == null) {
            return false;
        }
        if (baseToL == null) {
            return false;
        }
        if (onAssign_isLeft(node)) {
            //ignore, will be processed later (in assign node)
        } else {
            boolean acceptMethodImpl = true;
            if (baseToL.getType().isArray()) {
                if (Arrays.stream(inodes)
                        .map(x -> compilerContext.jTypeOrLambda(showFinalErrors, x))
                        .allMatch(x -> x.isType() && x.getType().boxed().name().equals("java.lang.Integer"))) {
                    // this is a regular array
                    JTypeArray arrType = (JTypeArray) baseToL.getType();
                    if (arrType.arrayDimension() >= inodes.length) {
                        acceptMethodImpl = false;
                        HNElementExpr element = (HNElementExpr) node.getElement();
                        JType tt = arrType;
                        for (int i = 0; i < inodes.length; i++) {
                            if (tt instanceof JTypeArray) {
                                tt = ((JTypeArray) tt).componentType();
                            } else {
                                compilerContext.log().error("X000", null, "not an array " + tt, node.startToken());
                            }
                        }
                        element.setType(tt);
                        //ok
                    } else {
                        acceptMethodImpl = false;
                        compilerContext.log().error("S000", null, "array type expected",
                                inodes[inodes.length - 1].startToken());
                    }
                }
            }
            if (acceptMethodImpl) {
                HNode[] nargs = JeepUtils.arrayAppend(HNode.class, base, inodes);
                JTypeOrLambda[] ntypes = JeepUtils.arrayAppend(JTypeOrLambda.class, baseToL, inodesToL);
                JInvokable m = compilerContext.lookupFunctionMatch(JOnError.TRACE, HLExtensionNames.BRACKET_GET_SHORT, HFunctionType.SPECIAL, ntypes, node.startToken());
                if (m != null) {
                    HNElementMethod impl = new HNElementMethod(HLExtensionNames.BRACKET_GET_SHORT, m);
                    impl.setArgNodes(nargs);
                    setElement(node, impl);
                    inferType(base, m.signature().argType(0), compilerContext);
                    for (int i = 0; i < inodes.length; i++) {
                        inferType(inodes[i], m.signature().argType(i + 1), compilerContext);
                    }
                }
            }
        }
        return true;
    }

    private boolean onAssign_isLeft(HNode node) {
        HNode hnode = (HNode) node;
        if (hnode.fullChildInfo().equals("HNAssign:left")) {
            return true;
        }
        if (node.parentNode() instanceof HNTuple) {
            return onAssign_isLeft(node.parentNode());
        }
        return false;
    }

    private boolean onObjectNew(HNObjectNew node, HLJCompilerContext compilerContext) {
        JTypeOrLambda[] argTypes = compilerContext.jTypeOrLambdas(showFinalErrors, node.getInits());
        if (argTypes == null) {
            return false;
        }
        JType declaringType = node.getObjectTypeName().getTypeVal();
        if(declaringType==null){
            return false;
        }
        JInvokable m = compilerContext.findConstructorMatch(JOnError.TRACE, declaringType, argTypes, node.startToken(), null);
        if (m != null) {
            HNElement e = node.getElement();
            if (e instanceof HNElementConstructor) {
                HNElementConstructor cc = (HNElementConstructor) e;
                cc.setInvokable(m);
                cc.setArgNodes(node.getInits());
            } else {
                setElement(node, new HNElementConstructor(declaringType, m, node.getInits()));
            }
        }
        return true;
    }

    protected boolean implicitConvert(JTypeOrLambda leftType, HNode rightNode, HLJCompilerContext compilerContext) {
        JTypeOrLambda rightToL = rightNode.getElement().getTypeOrLambda();
        JToken location = rightNode.startToken();

        if (JTypeUtils.isVoid(leftType) && JTypeUtils.isVoid(rightToL)) {
            compilerContext.log().error("S052", null, "void is not an expression", location);
            return true;
        }

        if (JTypeUtils.isVoid(leftType) || JTypeUtils.isVoid(rightToL)) {
            compilerContext.log().error("S052", null, "void is not an expression", location);
            return true;
        }

        if (!leftType.isType() || !rightToL.isType()) {
            return false;
        }
        if (leftType.getType().isAssignableFrom(rightToL.getType())) {
            return true;
        }
        if (implicitConvertAssignment) {
            if (leftType.getType().boxed().isAssignableFrom(rightToL.getType().boxed())) {
                //TODO, check if assign nullable to non nullable
                return true;
            } else {
                JInvokable e = compilerContext.createConverter(JOnError.TRACE, rightToL.getType(), leftType.getType(),
                        rightNode, null);
                if (e != null) {
                    rightNode.getElement().setConverterInvokable(e);
                    return true;
                }
                return true;
            }
        }
        compilerContext.log().error("S052", null, "type mismatch. expected " + leftType + " but found " + rightToL, location);
        return true;
    }

    private boolean onAssign_deconstruct(HNode left, HNode right, HLJCompilerContext compilerContext) {
        JTypeOrLambda rightToL = compilerContext.jTypeOrLambda(showFinalErrors, right);
        if (rightToL == null) {
            return false;
        }
        switch (left.getElement().getKind()) {
            case LOCAL_VAR:
            case FIELD: {
                JTypeOrLambda leftToL = compilerContext.jTypeOrLambda(showFinalErrors, left);
                if (leftToL == null) {
                    return false;
                }
                if(left.getElement().getKind()==HNElementKind.FIELD){
                    JField field = ((HNElementField) left.getElement()).getField();
                    if(field.isStatic()) {
                        left.setUserObject("StaticLHS");
                    }
                }
                return implicitConvert(leftToL, right, compilerContext);
            }
            case EXPR: {
                if (left instanceof HNBracketsPostfix) {
                    HNBracketsPostfix aleft = (HNBracketsPostfix) left;
                    HNode base = aleft.getLeft();
                    List<HNode> indicesNodes = aleft.getRight();
                    JTypeOrLambda baseToL = compilerContext.jTypeOrLambda(showFinalErrors, base);
                    JTypeOrLambda[] indicesToL = compilerContext.jTypeOrLambdas(showFinalErrors, indicesNodes);
                    if (indicesToL == null) {
                        return false;
                    }
                    if (baseToL == null) {
                        return false;
                    }
                    boolean acceptMethodImpl = true;
                    if (baseToL.getType().isArray()) {
                        if (indicesNodes.stream()
                                .map(x -> compilerContext.jTypeOrLambda(showFinalErrors, x))
                                .allMatch(x -> x.isType() && x.getType().boxed().name().equals("java.lang.Integer"))) {
                            // this is a regular array
                            JTypeArray arrType = (JTypeArray) baseToL.getType();
                            if (arrType.arrayDimension() >= indicesNodes.size()) {
                                acceptMethodImpl = false;
                                HNElementExpr element = (HNElementExpr) left.getElement();
                                element.setType(
                                        arrType.rootComponentType().toArray(arrType.arrayDimension() - indicesNodes.size())
                                );
                                //ok
                            } else {
                                acceptMethodImpl = false;
                                compilerContext.log().error("S000", null, "array type expected",
                                        indicesNodes.get(indicesNodes.size() - 1).startToken());
                            }
                        }
                    }
                    if (acceptMethodImpl) {
                        HNode[] nargs = JeepUtils.arrayAppend(HNode.class, base, indicesNodes.toArray(new HNode[0]), right);
                        JTypeOrLambda[] ntypes = JeepUtils.arrayAppend(JTypeOrLambda.class, baseToL, indicesToL, rightToL);
                        JInvokable m = compilerContext.lookupFunctionMatch(JOnError.TRACE, HLExtensionNames.BRACKET_SET_SHORT, HFunctionType.SPECIAL, ntypes, left.startToken());
                        if (m != null) {
                            HNElementMethod impl = new HNElementMethod(HLExtensionNames.BRACKET_SET_SHORT, m);
                            impl.setArgNodes(nargs);
                            setElement(left, impl);
                            inferType(base, m.signature().argType(0), compilerContext);
                            for (int i = 0; i < indicesNodes.size(); i++) {
                                inferType(indicesNodes.get(i), m.signature().argType(i + 1), compilerContext);
                            }
                        }
                    }else{
                        if(base.getElement().getKind()==HNElementKind.FIELD){
                            JField field = ((HNElementField) base.getElement()).getField();
                            if(field.isStatic()) {
                                left.setUserObject("StaticLHS");
                            }
                        }
                    }
                    return true;

                } else if (left instanceof HNTuple) {
                    HNTuple tuple = (HNTuple) left;
                    //de-constructor matcher
                    if (!right.getElement().getTypeOrLambda().isType()) {
                        compilerContext.log().error("S000", null, "invalid Tuple", right.startToken());
                    } else {
                        JType encountered = right.getElement().getTypeOrLambda().getType();
                        if (!HTypeUtils.isTupleType(encountered)) {
                            compilerContext.log().error("X000", null, "expected tuple type", right.startToken());
                            return false;
                        }
                        JType[] tupleArgTypes;
                        try {
                            tupleArgTypes = HTypeUtils.tupleArgTypes(encountered);
                        } catch (Exception ex) {
                            compilerContext.log().error("X000", null, "invalid tuple type", right.startToken());
                            return false;
                        }
                        if (tupleArgTypes.length != tuple.getItems().length) {
                            compilerContext.log().error("X000", null, "tuple mismatch " + tupleArgTypes.length + "!=" + tuple.getItems().length, right.startToken());
                            return false;
                        }
                        HNElement lelement = tuple.getElement();
                        ((HNElementExpr) lelement).setType(right.getElement().getTypeOrLambda());
                        //if(_checkTypeMismatch(expected,encountered,left.startToken(),compilerContext)){
                        for (int i = 0; i < tupleArgTypes.length; i++) {
                            HNOpDot tempRight = new HNOpDot(
                                    right.copy(),
                                    HNodeUtils.createToken("."),
                                    new HNIdentifier(HNodeUtils.createToken("_" + (i + 1))),
                                    null, null
                            );
                            setElement(tempRight, new HNElementExpr(tupleArgTypes[i]));
                            if (!onAssign_deconstruct(
                                    (HNode) tuple.getItems()[i],
                                    tempRight, compilerContext
                            )) {
                                return false;
                            }
                            if(tuple.getItems()[i].isSetUserObject("StaticLHS")){
                                left.setUserObject("StaticLHS");
                            }
                        }
                        //}
                    }
                } else {
                    compilerContext.log().error("X000", null, "invalid assignment", left.startToken());
                }
                break;
            }
        }
        return true;
    }

    private boolean onAssign0(HNAssign node, HLJCompilerContext compilerContext) {
        HNode left = (HNode) node.getLeft();
        HNode right = (HNode) node.getRight();
        HNElementAssign elementAssign = (HNElementAssign) node.getElement();
        HNode rightReplace = right;
        if (right.getElement() != null && HTypeUtils.isTupleType(right.getElement().getTypeOrLambda())) {
            String vn = compilerContext.nextVarName();
            HNIdentifier hi = new HNIdentifier(HNodeUtils.createToken(vn));
            HNElementLocalVar element = new HNElementLocalVar(hi.getName());
            element.setEffectiveType(right.getElement().getType());
            hi.setElement(element);
            elementAssign.setDeclareTempVarName(vn);
            rightReplace = hi;
        }

        if (onAssign_deconstruct0(left, rightReplace, compilerContext)) {
            ((HNElementAssign)node.getElement()).setType(left.getElement().getTypeOrLambda());
            return true;
        }
        return false;
    }

    private boolean onAssign_deconstruct0(HNode left, HNode right, HLJCompilerContext compilerContext) {
        JTypeOrLambda rightToL = compilerContext.jTypeOrLambda(showFinalErrors, right);
        if (rightToL == null) {
            return false;
        }
        switch (left.getElement().getKind()) {
            case LOCAL_VAR:
            case FIELD: {
                JTypeOrLambda leftToL = compilerContext.jTypeOrLambda(showFinalErrors, left);
                if (leftToL == null) {
                    return false;
                }
                return implicitConvert(leftToL, right, compilerContext);
            }
            case EXPR: {
                if (left instanceof HNBracketsPostfix) {
                    HNBracketsPostfix aleft = (HNBracketsPostfix) left;
                    HNode base = aleft.getLeft();
                    List<HNode> indicesNodes = aleft.getRight();
                    JTypeOrLambda baseToL = compilerContext.jTypeOrLambda(showFinalErrors, base);
                    JTypeOrLambda[] indicesToL = compilerContext.jTypeOrLambdas(showFinalErrors, indicesNodes);
                    if (indicesToL == null) {
                        return false;
                    }
                    if (baseToL == null) {
                        return false;
                    }
                    boolean acceptMethodImpl = true;
                    if (baseToL.getType().isArray()) {
                        if (indicesNodes.stream()
                                .map(x -> compilerContext.jTypeOrLambda(showFinalErrors, x))
                                .allMatch(x -> x.isType() && x.getType().boxed().name().equals("java.lang.Integer"))) {
                            // this is a regular array
                            JTypeArray arrType = (JTypeArray) baseToL.getType();
                            if (arrType.arrayDimension() >= indicesNodes.size()) {
                                acceptMethodImpl = false;
                                HNElementExpr element = (HNElementExpr) left.getElement();
                                element.setType(
                                        arrType.rootComponentType().toArray(arrType.arrayDimension() - indicesNodes.size())
                                );
                                //ok
                            } else {
                                acceptMethodImpl = false;
                                compilerContext.log().error("S000", null, "array type expected",
                                        indicesNodes.get(indicesNodes.size() - 1).startToken());
                            }
                        }
                    }
                    if (acceptMethodImpl) {
                        HNode[] nargs = JeepUtils.arrayAppend(HNode.class, base, indicesNodes.toArray(new HNode[0]), right);
                        JTypeOrLambda[] ntypes = JeepUtils.arrayAppend(JTypeOrLambda.class, baseToL, indicesToL, rightToL);
                        JInvokable m = compilerContext.lookupFunctionMatch(JOnError.TRACE, HLExtensionNames.BRACKET_SET_SHORT, HFunctionType.SPECIAL, ntypes, left.startToken());
                        if (m != null) {
                            HNElementMethod impl = new HNElementMethod(HLExtensionNames.BRACKET_SET_SHORT, m);
                            impl.setArgNodes(nargs);
                            setElement(left, impl);
                            inferType(base, m.signature().argType(0), compilerContext);
                            for (int i = 0; i < indicesNodes.size(); i++) {
                                inferType(indicesNodes.get(i), m.signature().argType(i + 1), compilerContext);
                            }
                        }
                    }
                    return true;

                } else if (left instanceof HNTuple) {
                    HNTuple tuple = (HNTuple) left;
                    //de-constructor matcher
                    if (!right.getElement().getTypeOrLambda().isType()) {
                        compilerContext.log().error("S000", null, "invalid Tuple", right.startToken());
                    } else {
                        JType encountered = right.getElement().getTypeOrLambda().getType();
                        if (!HTypeUtils.isTupleType(encountered)) {
                            compilerContext.log().error("X000", null, "expected tuple type", right.startToken());
                            return false;
                        }
                        JType[] tupleArgTypes;
                        try {
                            tupleArgTypes = HTypeUtils.tupleArgTypes(encountered);
                        } catch (Exception ex) {
                            compilerContext.log().error("X000", null, "invalid tuple type", right.startToken());
                            return false;
                        }
                        if (tupleArgTypes.length != tuple.getItems().length) {
                            compilerContext.log().error("X000", null, "tuple mismatch " + tupleArgTypes.length + "!=" + tuple.getItems().length, right.startToken());
                            return false;
                        }
                        HNElement lelement = tuple.getElement();
                        ((HNElementExpr) lelement).setType(right.getElement().getTypeOrLambda());
                        //if(_checkTypeMismatch(expected,encountered,left.startToken(),compilerContext)){
                        for (int i = 0; i < tupleArgTypes.length; i++) {
                            HNOpDot tempRight = new HNOpDot(
                                    right,
                                    HNodeUtils.createToken("."),
                                    new HNIdentifier(HNodeUtils.createToken("_" + (i + 1))),
                                    null, null
                            );
                            setElement(tempRight, new HNElementExpr(tupleArgTypes[i]));
                            if (!onAssign_deconstruct0(
                                    (HNode) tuple.getItems()[i],
                                    tempRight, compilerContext
                            )) {
                                return false;
                            }
                        }
                        //}
                    }
                } else {
                    throw new JShouldNeverHappenException();
                }
                break;
            }
        }
        return true;
    }

    private boolean onAssign(HNAssign node, HLJCompilerContext compilerContext) {
        HNode left = (HNode) node.getLeft();
        HNode right = (HNode) node.getRight();
        if (onAssign_deconstruct(left, right, compilerContext)) {
            ((HNElementAssign)node.getElement()).setType(left.getElement().getTypeOrLambda());
            if(left.isSetUserObject("StaticLHS")){
                node.setUserObject("StaticAssign");
            }
            return true;
        }
        return false;
    }


    private boolean onDeclareIdentifier(HNDeclareIdentifier node, HLJCompilerContext compilerContext) {
        HNode initValue = (HNode) node.getInitValue();
        boolean parentIsLambda = node.parentNode() instanceof HNLambdaExpression;
        if (parentIsLambda) {
            return true;
        }
        if (node.getIdentifierType() == null) {
            //inference of identifier type from value
            if (initValue != null) {
                JTypeOrLambda typeOrLambda = compilerContext.jTypeOrLambda(showFinalErrors, initValue);
                if (typeOrLambda == null) {
                    return false;
                }
                applyDeclareTokenType(node, typeOrLambda, compilerContext);
            } else {
                compilerContext.log().error("S000", null, "type inference failed with no init value", node.startToken());
            }
        } else {
            JType identifierType = node.getIdentifierType();
            applyDeclareTokenType(node, JTypeOrLambda.of(identifierType), compilerContext);
        }
        if (initValue != null && node.getIdentifierType() != null) {
            JTypeOrLambda jTypeOrLambda = initValue.getElement().getTypeOrLambda();
            if (jTypeOrLambda == null) {
                //error should be already reported!
                return true;
            }
            return implicitConvert(JTypeOrLambda.of(node.getIdentifierType()), initValue, compilerContext);
        }
        return true;
    }

    private void applyDeclareTokenType(HNDeclareIdentifier node, JTypeOrLambda identifierType, HLJCompilerContext compilerContext) {
        applyDeclareTokenType(node.getIdentifierToken(), identifierType, compilerContext);
        node.setEffectiveIdentifierType(identifierType.getType());
    }

    private void applyDeclareTokenType(HNDeclareToken identifierToken, JTypeOrLambda identifierType, HLJCompilerContext compilerContext) {
        if (identifierType.isLambda()) {
            compilerContext.log().error("S000", null, "type inference failed with unresolvable lambda expression", identifierToken.startToken());
            return;
        }
        if (identifierToken instanceof HNDeclareTokenIdentifier || identifierToken instanceof HNDeclareTokenList) {
            for (HNDeclareTokenIdentifier dti : HNodeUtils.flatten(identifierToken)) {
                HNElement e = dti.getElement();
                if (e.getKind() == HNElementKind.FIELD) {
                    JField field = ((HNElementField) e).getField();
                    if (field.type() == null) {
                        ((DefaultJField) field).setGenericType(identifierType.getType());
                        //reindex
                        compilerContext.indexer().indexField(new HLIndexedField(field, HUtils.getSourceName(identifierToken)));
                    }
                } else if (e.getKind() == HNElementKind.LOCAL_VAR) {
                    HNElementLocalVar lv = (HNElementLocalVar) e;
                    ((HNElementLocalVar) e).setEffectiveType(identifierType.getType());
                } else {
                    compilerContext.log().error("S000", null, "unexpected value", identifierToken.startToken());
                }
            }
        } else if (identifierToken instanceof HNDeclareTokenTuple) {
            HNDeclareTokenTuple tt = (HNDeclareTokenTuple) identifierToken;
            if (!HTypeUtils.isTupleType(identifierType.getType())) {
                compilerContext.log().error("S000", null, "expected tuple type, found " + identifierType.getType().name(), identifierToken.startToken());
            } else {
                JType[] jTypes = HTypeUtils.tupleArgTypes(identifierType.getType());
                if (jTypes.length != tt.getItems().length) {
                    compilerContext.log().error("S000", null, "expected tuple elements count mismatch  " + jTypes.length + "!=" + tt.getItems().length, identifierToken.startToken());
                } else {
//                HNElementExpr element = (HNElementExpr) tt.getElement();
//                element.setType(identifierType);
                    for (int i = 0; i < jTypes.length; i++) {
                        applyDeclareTokenType(tt.getItems()[i], JTypeOrLambda.of(jTypes[i]), compilerContext);
                    }
                }
            }
        }
    }

    private boolean onParsPostfix(HNParsPostfix node, HLJCompilerContext compilerContext) {
        //this is a method or field()
        // a.b(c) == HNOpDot(a,HNParsPostfix(b,c))
        //b can be
        //    a function (no a)      : example myFunction(...)
        //    a constructor          : example myPackage.MyTpe(...) or MyTpe(...) or
        //    a method               : example expression.myMethod(...) or this.myMethod(...) or myMethod(...) in non static context
        //    or '(' operator applied on field (when b is a field that supports '(' operator)
        //                           : example myField(...)
        //    or '(' operator applied on expression
        //                           : examples MyMethod(...)(...) or (matrix+matrix)(...)

        HNode left = node.getLeft();
        List<HNode> arguments = node.getRight();
        List<JTypeOrLambda> argTypes = new ArrayList<>();
        for (HNode item : arguments) {
            HNode hitem = item;
            JTypeOrLambda typeOrLambda = compilerContext.jTypeOrLambda(showFinalErrors, hitem);
            if (typeOrLambda == null) {
                return false;
            }
            argTypes.add(typeOrLambda);
        }
        HNode[] argumentsArr = arguments.toArray(new HNode[0]);
        switch (left.id()){
            case H_IDENTIFIER:{
                HNIdentifier ident = (HNIdentifier) left;
                if (node.parentNode() instanceof HNOpDot) {
                    HNOpDot d = (HNOpDot) node.parentNode();
                    if (d.getRight() == node) {
                        return onIdentifierWithPars(ident, node, d.getLeft(), argumentsArr, compilerContext);
                    }
                }
                return onIdentifierWithPars(ident, node, null, argumentsArr, compilerContext);
            }
            case H_OP_DOT:{
                HNOpDot d = (HNOpDot) left;
                if (d.getRight() instanceof HNIdentifier) {
                    HNIdentifier ident = (HNIdentifier) d.getRight();
                    boolean v = onIdentifierWithPars(ident, node, d.getLeft(), argumentsArr, compilerContext);
                    setElement(d, ident.getElement());
                    return v;
                }
                break;
            }
            case H_THIS:{
                HNThis d = (HNThis) left;
                HNode r = compilerContext.lookupEnclosingDeclaration(node, false);
                if(r instanceof HNDeclareInvokable){
                    HNDeclareInvokable di=(HNDeclareInvokable) r;
                    if(di.isConstr()) {
                        JType tt = compilerContext.getOrCreateType(di.getDeclaringType());
                        JInvokable c = compilerContext.findConstructorMatch(JOnError.TRACE, tt, argTypes.toArray(new JTypeOrLambda[0]), node.startToken(), null);
                        if (c != null) {
                            d.setElement(new HNElementConstructor(tt, c, arguments.toArray(new JNode[0])));
                        }
                        HNElementExpr.get(node).setType(JTypeUtils.forVoid(compilerContext.types()));
                        return true;
                    }
                }
                break;
            }
            case H_SUPER:{
                HNSuper d = (HNSuper) left;
                HNode r = compilerContext.lookupEnclosingDeclaration(node, false);
                if(r instanceof HNDeclareInvokable){
                    HNDeclareInvokable di=(HNDeclareInvokable) r;
                    if(di.isConstr()) {
                        JType tt = compilerContext.getOrCreateType(di.getDeclaringType());
                        JType st = tt.getSuperType();
                        if(st==null){
                            compilerContext.log().error("X000",null,"mismatch 'super' call",node.startToken());
                        }else {
                            JInvokable c = compilerContext.findConstructorMatch(JOnError.TRACE, st, argTypes.toArray(new JTypeOrLambda[0]), node.startToken(), null);
                            if (c != null) {
                                d.setElement(new HNElementConstructor(st, c, arguments.toArray(new JNode[0])));
                            }
                        }
                        HNElementExpr.get(node).setType(JTypeUtils.forVoid(compilerContext.types()));
                        return true;
                    }
                }
                break;
            }
        }

        JInvokable ctrInvokable = compilerContext.lookupFunctionMatch(JOnError.TRACE, HLExtensionNames.FUNCTION_APPLY, HFunctionType.SPECIAL, argTypes.toArray(new JTypeOrLambda[0]), node.startToken(), null);
        if (ctrInvokable != null) {
            HNElementMethod element = new HNElementMethod(HLExtensionNames.FUNCTION_APPLY, ctrInvokable);
            setElement(node, element);
        }
        return true;
    }

    private boolean onArrayNew(HNArrayNew node, HLJCompilerContext compilerContext) {
        if (node.getElement() instanceof HNElementExpr) {
            HNElementExpr expr = (HNElementExpr) node.getElement();
            expr.setType(node.getArrayTypeName().getTypeVal());
        }
        if (node.getConstructor() == null) {
            //do nothing this is a standard array creation...
        } else {
            List<JTypeOrLambda> argsTypes = new ArrayList<>();
            List<HNode> argsNodes = new ArrayList<>();
            boolean error = false;
            for (HNode init : node.getInits()) {
                JTypeOrLambda t = compilerContext.jTypeOrLambda(showFinalErrors, init);
                if (t == null) {
                    error = true;
                } else {
                    argsTypes.add(t);
                    argsNodes.add(init);
                }
            }
            JTypeOrLambda t = compilerContext.jTypeOrLambda(showFinalErrors, node.getConstructor());
            if (t == null) {
                error = true;
            } else {
                argsTypes.add(t);
                argsNodes.add(node.getConstructor());
            }
            if (error) {
                return false;
            }
            if(node.getArrayTypeName().getTypeVal()==null){
                return false;
            }
            String methodName = HUtils.getStaticConstructorName(node.getArrayTypeName().getTypeVal());
            JInvokable f = compilerContext.lookupFunctionMatch(JOnError.TRACE, methodName,
                    HFunctionType.SPECIAL, argsTypes.toArray(new JTypeOrLambda[0]), node.startToken());
            if (f != null) {
                HNElementMethod element = new HNElementMethod(methodName, f);
                setElement(node, element);
                //infer back types
                inferTypes(argsNodes.toArray(new HNode[0]), f.signature().argTypes(), compilerContext);
            }
        }
        return true;
    }

    protected void inferTypes(HNode[] nodes, JType[] types, HLJCompilerContext compilerContext) {
        for (int i = 0; i < nodes.length; i++) {
            HNode node = nodes[i];
            JTypeOrLambda t = compilerContext.jTypeOrLambda(false, node);
            if (t.isLambda()) {
                inferType(node, types[i], compilerContext);
            }
        }
    }

    protected void inferType(HNode node, JType type, HLJCompilerContext compilerContext) {
        JTypeOrLambda t = compilerContext.jTypeOrLambda(false, node);
        if (t.isLambda()) {
            HNode hnode = (HNode) node;
            HNElementLambda e = (HNElementLambda) hnode.getElement();
            e.setInferredType(type);
            List<HNDeclareIdentifier> arguments = ((HNLambdaExpression) node).getArguments();
            JType[] atypes = HTypeUtils.extractLambdaArgTypesOrError(type, arguments.size(), hnode.startToken(), compilerContext.log());
            if (atypes != null) {
                for (int i = 0; i < arguments.size(); i++) {
                    applyDeclareTokenType(arguments.get(i), JTypeOrLambda.of(atypes[i]), compilerContext);
                }
            }
        }
    }

    private boolean onDeclareInvokable(HNDeclareInvokable node, HLJCompilerContext compilerContext) {
        if (node.getReturnType() == null) {
            //inference of method type from body
            HNode initValue = (HNode) node.getBody();
            if (initValue == null) {
                //no body
                compilerContext.log().error("S000", null, "type inference failed for function/method without body", node.startToken());
            } else {
                HNode[] exitPoints = initValue.getExitPoints();
                if (exitPoints.length == 0) {
                    compilerContext.log().error("S000", null, "type inference failed for function/method without body", initValue.startToken());
                } else {
                    JTypeOrLambda[] args = compilerContext.jTypeOrLambdas(showFinalErrors, exitPoints);
                    if (args == null) {
                        return false;
                    }
                    boolean error = false;
                    JType ert = null;
                    for (int i = 0; i < args.length; i++) {
                        if (args[i].isLambda()) {
                            error = true;
                            compilerContext.log().error("S000", null, "type inference failed with unresolvable lambda expression", initValue.startToken());
                            break;
                        } else {
                            ert = ert == null ? args[i].getType() : ert.firstCommonSuperType(args[i].getType());
                        }
                    }
                    if (!error) {
                        if(node.getInvokable()instanceof DefaultJRawMethod) {
                            DefaultJRawMethod method = (DefaultJRawMethod) node.getInvokable();
                            method.setGenericReturnType(ert);
                            compilerContext.indexer().indexMethod(new HLIndexedMethod(method, HUtils.getSourceName(node)));
                            node.setEffectiveReturnType(ert);
                        }
                    }
                }
            }
//            compilerContext.log().error("S000", "type inference failed with unresolvable function/method result", node.startToken());
        }
        return true;
    }

    private boolean onOpBinaryCall(HNOpBinaryCall node, HLJCompilerContext compilerContext) {
        String opName = node.getNameToken().image;
        HNode left = node.getLeft();
        HNode right = node.getRight();
        JTypeOrLambda[] args = compilerContext.jTypeOrLambdas(showFinalErrors, left, right);
        if (args == null) {
            return false;
        }
//        if (args != null) {
        JInvokable f = compilerContext.lookupFunctionMatch(JOnError.TRACE, opName, HFunctionType.SPECIAL, args, node.startToken());
        if (f != null) {
            HNElementMethod element = new HNElementMethod(opName, f);
            element.setArgNodes(new HNode[]{node.getLeft(), node.getRight()});
            element.setArg0Kind(HNElementMethod.Arg0Kind.NONE);
            element.setArg0TypeProcessed(true);
            setElement(node, element);
            inferType(left, f.signature().argType(0), compilerContext);
            inferType(right, f.signature().argType(1), compilerContext);
        }
//        } else if (
//                node.getType() == null &&
//                        HUtils.isComparisonOperator(opName)
//                        &&
//                        (HUtils.isNullLiteral(left))
//                        || (HUtils.isNullLiteral(right))
//        ) {
//            node.setType(compilerContext.types().forName("boolean"));
//        }
        return true;
    }

    private boolean onOpUnaryCall(HNOpUnaryCall node, HLJCompilerContext compilerContext) {
        String opName = node.getNameToken().image;
        HNode expr = node.getExpr();
        JTypeOrLambda[] args = compilerContext.jTypeOrLambdas(showFinalErrors, expr);
        if (args == null) {
            return false;
        }
        switch (opName) {
            case "++":
            case "--": {
                if (args[0].isType()) {
                    switch (args[0].getType().name()) {
                        case "byte":
                        case "short":
                        case "char":
                        case "int":
                        case "long":
                        case "float":
                        case "double":
                        case "java.lang.Byte":
                        case "java.lang.Short":
                        case "java.lang.Character":
                        case "java.lang.Integer":
                        case "java.lang.Long":
                        case "java.lang.Float":
                        case "java.lang.Double": {
                            //this is a standard operator that cannot (for now)
                            //be treated as a method
                            HNElementExpr ee = (HNElementExpr) node.getElement();
                            ee.setType(args[0].getType());
                            return true;
                        }
                    }
                }
            }
        }
        JInvokable f = compilerContext.lookupFunctionMatch(JOnError.TRACE, opName,
                node.isPrefixOperator() ? HFunctionType.PREFIX_UNARY : HFunctionType.POSTFIX_UNARY, args, node.startToken());
        if (f != null) {
            HNElementMethod element = new HNElementMethod(opName);
            element.setInvokable(f);
            setElement(node, element);
            inferType(expr, f.signature().argType(0), compilerContext);
        }
        return true;
    }

    protected boolean onIdentifierWithPars(HNIdentifier childNodeIdent, HNParsPostfix parentNodeParsPostfix, HNode dotBase, HNode[] arguments, HLJCompilerContext compilerContext) {
        boolean lhs = false;
        if (arguments != null) {
            JTypeOrLambda[] argTypes = compilerContext.jTypeOrLambdas(showFinalErrors, arguments);
            if (argTypes == null) {
                return false;
            }
        }
        if (dotBase != null) {
            JTypeOrLambda a = compilerContext.jTypeOrLambda(showFinalErrors, dotBase);
            if (a == null) {
                return false;
            }
        }
        HNElement ee = compilerContext.lookupElement(JOnError.TRACE, childNodeIdent.getName(), dotBase, arguments, lhs, childNodeIdent.startToken(),
                childNodeIdent.parentNode(), null);
        if (ee != null) {
            setElement(childNodeIdent, ee);
            setElement(parentNodeParsPostfix, childNodeIdent.getElement());
            if (ee.getTypeOrLambda() == null) {
                return false;
            }
            return true;
        }
        return false;
    }

    protected boolean onIdentifierWithoutPars(HNIdentifier node, HNode dotBase, HLJCompilerContext compilerContext) {
        boolean lhs = false;
        if (dotBase != null) {
            HNElementKind kind = dotBase.getElement().getKind();
            if (kind != HNElementKind.PACKAGE && kind != HNElementKind.TYPE) {
                if (compilerContext.jTypeOrLambda(showFinalErrors, dotBase) == null) {
                    return false;
                }
            }
        }
        HNElement e = compilerContext.lookupElement(JOnError.TRACE, node.getName(), dotBase, null, lhs, node.startToken(),
                node.parentNode(), null);
        if (e != null) {
            setElement(node, e);
        }
        return e != null;
    }

    private boolean onLambdaExpression(HNLambdaExpression node, HLJCompilerContext compilerContext) {
        List<HNDeclareIdentifier> arguments = node.getArguments();
        List<JType> argTypes = new ArrayList<>();
        for (HNDeclareIdentifier argument : arguments) {
            JType eit = argument.getEffectiveIdentifierType();
            if (eit == null) {
                eit = argument.getIdentifierType();
            }
            argTypes.add(eit);
        }
        HNElementLambda element = (HNElementLambda) node.getElement();
        element.setArgTypes(argTypes.toArray(new JType[0]));
        return true;
    }

    protected boolean onIdentifier(HNIdentifier node, HLJCompilerContext compilerContext) {
        switch (node.fullChildInfo()) {
            case "HNOpDot:right": {
                HNode dotBase = (HNode) ((HNOpDot) node.parentNode()).getLeft();
                return onIdentifierWithoutPars(node, dotBase, compilerContext);
            }
            case "HNParsPostfix:left": {
                //will be processed later
                return true;
            }
            default: {
                return onIdentifierWithoutPars(node, null, compilerContext);
            }
        }
    }

    protected boolean onOpDot(HNOpDot n, HLJCompilerContext compilerContext) {
        HNode left = (HNode) n.getLeft();
        HNode right = (HNode) n.getRight();
        setElement(n, right.getElement());
        if (n.getElement() == null || n.getElement().getTypeOrLambda() == null) {
            return false;
        }
        return true;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // CHECK
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean processCompilerStageCurrentCheck(HNode node, HLJCompilerContext compilerContext) {
        String simpleName = node.getClass().getSimpleName();
        if (node.getElement() == null) {
            compilerContext.log().error("X000", null, "node.getElement()==null for " + simpleName, node.startToken());
        } else {
            HNElement element = node.getElement();
            switch (element.getKind()) {
                case CONSTRUCTOR: {
                    if (element.getTypeOrLambda() == null) {
                        compilerContext.log().error("X000", null, "HNElementConstructor.getTypeOrLambda()==null for " + simpleName, node.startToken());
                    }
                    HNElementConstructor c = (HNElementConstructor) element;
                    if (c.getInvokable() == null) {
                        compilerContext.log().error("X000", null, "HNElementConstructor.getInvokable()==null for " + simpleName, node.startToken());
                    }
                    if (c.getDeclaringType() == null) {
                        compilerContext.log().error("X000", null, "HNElementConstructor.getDeclaringType()==null for " + simpleName, node.startToken());
                    }
//                    if(node.getType()==null){
//                        compilerContext.log().error("X000","node.getType()==null for "+simpleName,node.startToken());
//                    }
                    break;
                }
                case METHOD: {
                    HNElementMethod c = (HNElementMethod) element;
                    if (element.getTypeOrLambda() == null) {
                        compilerContext.log().error("X000", null, "HNElementMethod.getTypeOrLambda()==null for " + simpleName, node.startToken());
                    }
                    if (c.getInvokable() == null) {
                        compilerContext.log().error("X000", null, "HNElementMethod.getInvokable()==null for " + simpleName, node.startToken());
                    }
//                    if(c.getDeclaringType()==null){
//                        compilerContext.log().error("X000","HNElementMethod.getDeclaringType()==null for "+simpleName,node.startToken());
//                    }
                    if (c.getReturnType() == null) {
                        compilerContext.log().error("X000", null, "HNElementMethod.getReturnType()==null for " + simpleName, node.startToken());
                    }
//                    if(node.getType()==null){
//                        compilerContext.log().error("X000","node.getType()==null for "+simpleName,node.startToken());
//                    }
                    break;
                }
                case FIELD: {
                    HNElementField c = (HNElementField) element;
                    if (element.getTypeOrLambda() == null) {
                        compilerContext.log().error("X000", null, "HNElementField.getTypeOrLambda()==null for " + simpleName, node.startToken());
                    }
                    if (c.getField() == null) {
                        compilerContext.log().error("X000", null, "HNElementField.getField()==null for " + simpleName, node.startToken());
                    }
                    if (c.getDeclaringType() == null) {
                        compilerContext.log().error("X000", null, "HNElementField.getDeclaringType()==null for " + simpleName, node.startToken());
                    }
                    if (c.getType() == null) {
                        compilerContext.log().error("X000", null, "HNElementField.getType()==null for " + simpleName, node.startToken());
                    }
//                    if(node.getType()==null){
//                        compilerContext.log().error("X000","node.getType()==null for "+simpleName,node.startToken());
//                    }
                    break;
                }
                case EXPR: {
                    HNElementExpr c = (HNElementExpr) element;
                    if (element.getTypeOrLambda() == null) {
                        compilerContext.log().error("X000", null, "HNElementExpr.getTypeOrLambda()==null for " + simpleName, node.startToken());
                    }
                    if (c.getType() == null) {
                        compilerContext.log().error("X000", null, "HNElementExpr.getType()==null for " + simpleName, node.startToken());
                    }
//                    if(node.getType()==null){
//                        compilerContext.log().error("X000","node.getType()==null for "+simpleName,node.startToken());
//                    }
                    break;
                }
                case LOCAL_VAR: {
                    HNElementLocalVar c = (HNElementLocalVar) element;
                    if (element.getTypeOrLambda() == null) {
                        compilerContext.log().error("X000", null, "HNElementLocalVar.getTypeOrLambda()==null for " + simpleName, node.startToken());
                    }
                    if (c.getType() == null) {
                        compilerContext.log().error("X000", null, "HNElementLocalVar.getType()==null for " + simpleName, node.startToken());
                    }
//                    if(node.getType()==null){
//                        compilerContext.log().error("X000","node.getType()==null for "+simpleName,node.startToken());
//                    }
                    break;
                }
                case TYPE: {
                    HNElementType c = (HNElementType) element;
                    if (c.getValue() == null) {
                        compilerContext.log().error("X000", null, "HNElementType.getType()==null for " + simpleName, node.startToken());
                    }
                    break;
                }
            }
        }
        return true;
    }

    private void setElement(HNode node, HNElement element) {
        if (element == null) {
            throw new NullPointerException();
        }
        node.setElement(element);
    }

    protected void processProjectReplay(HLProject project, HLCOptions options) {
        if (!replays.isEmpty()) {
            while (true) {
                LOG.log(Level.INFO, "process pending " + replays.size() + " nodes");
                List<HLJCompilerContext> oldRedos = new ArrayList<>(replays);
                replays.clear();
                int progress = 0;
                for (Iterator<HLJCompilerContext> iterator = oldRedos.iterator(); iterator.hasNext();) {
                    HLJCompilerContext ctx = iterator.next();
                    if (processCompilerStage0(ctx)) {
                        iterator.remove();
                        progress++;
                    }
                }
                if (progress == 0) {
                    //this will happen if there is recursive inference...
                    showFinalErrors = true;
                    oldRedos = new ArrayList<>(replays);
                    replays.clear();
                    for (Iterator<HLJCompilerContext> iterator = oldRedos.iterator(); iterator.hasNext();) {
                        HLJCompilerContext ctx = iterator.next();
                        if (processCompilerStage0(ctx)) {
                            iterator.remove();
                            progress++;
                        }
                    }
                    break;
                } else if (replays.isEmpty()) {
                    break;
                }
            }
        }
    }

    public boolean processCompilerStage0(JCompilerContext compilerContextBase) {
        HNode node = (HNode) compilerContextBase.node();
        HLJCompilerContext compilerContext = (HLJCompilerContext) compilerContextBase;
        boolean succeeded = true;
        try {
            try {
                if (!processCompilerStageCurrent((HNode) node, compilerContext)) {
                    succeeded = false;
                }
            } catch (HMissingLinkageException hmle) {
                succeeded = false;
            }
//            System.out.println("HLCStage05CallResolver.processCompilerStage " + compilerContext.path().getPathString()
//                    + " : " + ((HNode) compilerContext.node()).getElement()
//                    + " : " + JToken.escapeString(compilerContext.node().toString()));
            if (!succeeded) {
                replays.add(compilerContext);
            }
            return succeeded;
        } catch (Exception ex) {
            LOG.log(Level.INFO, "unexpected error : " + ex.toString(), ex);
            compilerContext.log().error("S000", "unexpected error", ex.toString(), compilerContext.node().startToken());
            throw ex;
        }
    }

    public boolean processCompilerStage(JCompilerContext compilerContextBase) {
//        boolean succeeded = true;
        HNode node = (HNode) compilerContextBase.node();
        if (node.id() == HNNodeId.H_DECLARE_META_PACKAGE && !inPreprocessor) {
            //do not go further
            return true;
        }
        if (!processAllNextCompilerStage(compilerContextBase)) {
            //succeeded=false;
        }
        return /*succeeded && */ processCompilerStage0(compilerContextBase);
    }

    protected void processProjectMain(HLProject project, HLCOptions options) {
        super.processProjectMain(project, options);
        processProjectReplay(project, options);
    }

}