package net.hl.compiler.stages;

import net.hl.compiler.core.invokables.NegateInvokable;
import net.thevpc.jeep.*;
import net.thevpc.jeep.core.types.DefaultJField;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.impl.types.DefaultJRawMethod;
import net.thevpc.jeep.util.JTypeUtils;
import net.thevpc.jeep.util.JeepUtils;
import net.hl.compiler.core.HFunctionType;
import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.HMissingLinkageException;
import net.hl.compiler.core.elements.*;
import net.hl.compiler.core.invokables.FindMatchFailInfo;
import net.hl.compiler.core.invokables.HLJCompilerContext;
import net.hl.compiler.index.HIndexedField;
import net.hl.compiler.index.HIndexedMethod;
import net.hl.compiler.ast.*;
import net.hl.compiler.utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.hl.compiler.HL;
import net.hl.compiler.core.HTask;

public class HStage05CallResolver extends HStageType2 {

    public static final Logger LOG = Logger.getLogger(HStage05CallResolver.class.getName());
    private List<HLJCompilerContext> replays = new ArrayList<>();
    private boolean showFinalErrors = false;
    private boolean inPreprocessor = false;
    private boolean implicitConvertAssignment = true;

    public HStage05CallResolver(boolean inPreprocessor) {
        this.inPreprocessor = inPreprocessor;
    }

    @Override
    public boolean isEnabled(HProject project, HL options) {
        return options.containsAnyTask(HTask.RESOLVED_AST, HTask.COMPILE, HTask.RUN);
    }

    @Override
    public HTask[] getTasks() {
        return new HTask[]{HTask.RESOLVED_AST};
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
            case H_DOT_THIS: {
                return onDotThis((HNDotThis) node, compilerContext);
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
            case H_CATCH: {
                return onCatch((HNTryCatch.CatchBranch) node, compilerContext);
            }
            case H_TRY_CATCH: {
                return onTryCatch((HNTryCatch) node, compilerContext);
            }
            case H_CAST: {
                return onCast((HNCast) node, compilerContext);
            }
            case H_TYPE_TOKEN: {
                return onTypeToken((HNTypeToken) node, compilerContext);
            }
            case H_LITERAL_DEFAULT:
            case H_LITERAL:
            case H_BLOCK:
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

            case X_INVOKABLE_CALL: {
                throw new JShouldNeverHappenException();
            }
        }
        //in stage 1 wont change node instance
        throw new JShouldNeverHappenException("Unsupported node class in " + getClass().getSimpleName() + ": " + node.getClass().getSimpleName());
//        return node;
    }

    private boolean onTypeToken(HNTypeToken node, HLJCompilerContext compilerContext) {
        if (node.getTypeVal() == null) {
            JType r = compilerContext.lookupType(node.getTypename());
            node.setTypeVal(r);
            return r != null;
        }
        return true;
    }

    private boolean onTryCatch(HNTryCatch node, HLJCompilerContext compilerContext) {
        JTypePattern res = null;
        //the resource init is never typed!!
//        if(node.getResource()!=null){
//            JTypePattern typePattern = compilerContext.getTypePattern(showFinalErrors, node.getResource());
//            if(typePattern==null){
//                return false;
//            }
//            res=JTypeUtils.firstCommonSuperTypePattern(res,typePattern,compilerContext.types());
//        }
        {
            JTypePattern typePattern = compilerContext.getTypePattern(showFinalErrors, node.getBody());
            if (typePattern == null) {
                return false;
            }
            res = JTypeUtils.firstCommonSuperTypePattern(res, typePattern, compilerContext.types());
        }
        List<JType> visitedExceptions = new ArrayList<>();
        for (HNTryCatch.CatchBranch aCatch : node.getCatches()) {
            for (HNTypeToken exceptionType : aCatch.getExceptionTypes()) {
                JType nev = exceptionType.getTypeVal();
                for (JType v : visitedExceptions) {
                    if (v.isAssignableFrom(nev)) {
                        compilerContext.getLog().jerror("X000", "catch", exceptionType.getNameToken(), "expected type already handled by previous " + v.getName());
                    }
                }
                visitedExceptions.add(nev);
            }
            JTypePattern typePattern = compilerContext.getTypePattern(showFinalErrors, aCatch);
            if (typePattern == null) {
                return false;
            }
            res = JTypeUtils.firstCommonSuperTypePattern(res, typePattern, compilerContext.types());
        }
        //the finally is never typed!!

        HNElementExpr.get(node).setType(res);
        return true;
    }

    private boolean onCatch(HNTryCatch.CatchBranch node, HLJCompilerContext compilerContext) {
        HNDeclareTokenIdentifier id = node.getIdentifier();
        HNTypeToken[] exceptionTypes = node.getExceptionTypes();
        if (exceptionTypes.length == 0) {
            if (id != null) {
                HNElementLocalVar.get(id).setEffectiveType(JTypeUtils.forException(compilerContext.types()));
            }
        } else {
            JType throwableType = JTypeUtils.forThrowable(compilerContext.types());
            JType s = null;
            for (HNTypeToken exceptionType : exceptionTypes) {
                if (throwableType.isAssignableFrom(exceptionType.getTypeVal())) {
                    s = JTypeUtils.firstCommonSuperType(s, exceptionType.getTypeVal(), compilerContext.types());
                } else {
                    compilerContext.getLog().jerror("X000", "catch", exceptionType.getNameToken(), "expected Throwable type");
                }
            }
            if (s == null) {
                s = JTypeUtils.forException(compilerContext.types());
            }
            if (id != null) {
                HNElementLocalVar.get(id).setEffectiveType(s);
            }
        }
        JTypePattern typePattern = compilerContext.getTypePattern(showFinalErrors, node.getDoNode());
        if (typePattern == null) {
            return false;
        }
        HNElementExpr.get(node).setType(typePattern);
        return true;
    }

    private boolean onExtends(HNExtends node, HLJCompilerContext compilerContext) {
        JType t = compilerContext.lookupType(node.getFullName());
        if (t == null) {
            compilerContext.getLog().jerror("X000", "extends", node.getStartToken(), "type not found : " + node.getFullName());
        }
        node.setElement(new HNElementType(
                t, compilerContext.types()
        ));
        return true;
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
                e.setType(JTypeUtils.classOf(tv));
            }
        }
        return true;
    }

    private boolean onDotThis(HNDotThis node, HLJCompilerContext compilerContext) {
        HNElementExpr e = (HNElementExpr) node.getElement();
        if (e.getType() == null) {
            JType tv = node.getTypeRefName().getTypeVal();
            if (tv != null) {
                e.setType(tv);
            }
        }

        JType tv = node.getTypeRefName().getTypeVal();
        if (tv != null) {
            JType y = compilerContext.lookupEnclosingType(node);
            while (y != null) {
                if (y == tv) {
                    return true;
                }
                y = y.getDeclaringType();
            }
            compilerContext.getLog().jerror("X000", null, node.getStartToken(), "not an enclosing type : " + tv.getName());
        }
        return true;
    }

    private boolean onReturn(HNReturn node, HLJCompilerContext compilerContext) {
        return true;
    }

    private boolean onSwitchIf(HNSwitch.SwitchIf node, HLJCompilerContext compilerContext) {
        HNode w = node.getWhenNode();
        HNode d = node.getDoNode();
        JTypePattern wType = compilerContext.getTypePattern(showFinalErrors, w);
        if (wType == null) {
            return false;
        }
        JTypePattern dType = compilerContext.getTypePattern(showFinalErrors, d);
        if (dType == null) {
            return false;
        }

        if (wType.isType() && JTypeUtils.isBooleanResolvableType(wType.getType())) {
            HNElementExpr.get(node).setType(dType);
            return true;
        }
        compilerContext.getLog().jerror("X000", "if statement", w.getStartToken(), "expected boolean condition");
        return true;
    }

    private boolean onSwitchIs(HNSwitch.SwitchIs node, HLJCompilerContext compilerContext) {
        if (node.getIdentifierToken() != null) {
            //when IdentifierToken is present, only one item is present in 'WhenTypes'
            HNElementLocalVar element = (HNElementLocalVar) node.getIdentifierToken().getElement();
            JType typeVal = node.getWhenTypes().get(0).getTypeVal();
            element.setEffectiveType(typeVal);
        }
        List<HNTypeToken> w = node.getWhenTypes();
        for (HNTypeToken hnTypeToken : w) {
            if (hnTypeToken.getTypeVal() == null) {
                return false;
            }
        }
        HNode d = node.getDoNode();
        JTypePattern dType = compilerContext.getTypePattern(showFinalErrors, d);
        if (dType == null) {
            return false;
        }
        HNElementExpr.get(node).setType(dType);
        return true;
    }

    private boolean onSwitchCase(HNSwitch.SwitchCase node, HLJCompilerContext compilerContext) {
        ((HNElementExpr) node.getElement()).setType(JTypeUtils.forVoid(compilerContext.types()));
        return true;
    }

    private boolean onCast(HNCast node, HLJCompilerContext compilerContext) {
        JType tv = ((HNTypeToken) node.getTypeNode()).getTypeVal();
        if (tv == null) {
            return false;
        }
        HNElementExpr.get(node).setType(tv);
        return true;
    }

    private boolean onSwitch(HNSwitch node, HLJCompilerContext compilerContext) {
        if (node.isExpressionMode()) {
            JTypePattern t = null;
            for (HNSwitch.SwitchBranch aCase : node.getCases()) {
                HNode e = aCase.getDoNode();
                JTypePattern t2 = e.getElement().getTypePattern();
                if (t2 == null) {
                    return false;
                }
                t = JTypeUtils.firstCommonSuperTypePattern(t, t2, compilerContext.types());
            }
            if (t == null) {
                return false;
            }
            HNElementExpr.get(node).setType(t);
        } else {
            HNElementExpr.get(node).setType(JTypeUtils.forVoid(compilerContext.types()));
        }
        return true;
    }

    private boolean onDeclareTokenTuple(HNDeclareTokenTuple node, HLJCompilerContext compilerContext) {
        return true;
    }

    private boolean onDeclareTokenList(HNDeclareTokenList node, HLJCompilerContext compilerContext) {
        return true;
    }

    private HNode lookupDeclarationStatement(HNDeclareTokenIdentifier node, HLJCompilerContext compilerContext) {
        HNode n = node.getParentNode();
        while (true) {
            if (n instanceof HNIs) {
                return n;
            } else if (n instanceof HNDeclareIdentifier) {
                return n;
            } else if (n instanceof HNSwitch.SwitchIs) {
                return n;
            } else if (n instanceof HNDeclareTokenList) {
                n = n.getParentNode();
            } else if (n instanceof HNDeclareTokenTuple) {
                n = n.getParentNode();
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
        JTypePattern exprTypePattern = compilerContext.getTypePattern(showFinalErrors, expr);
        HNode block = node.getBlock();
        JTypePattern blockTypePattern = compilerContext.getTypePattern(showFinalErrors, block);
        if (exprTypePattern == null || blockTypePattern == null) {
            return false;
        }
        if (exprTypePattern.isType() && JTypeUtils.isBooleanResolvableType(exprTypePattern)) {
            ((HNElementExpr) node.getElement()).setType(JTypeUtils.forVoid(compilerContext.types()));
        } else {
            JType exprType = compilerContext.types().forName("java.util.function.Supplier<" + exprTypePattern.getType().getName() + ">");
            JType blockType = compilerContext.types().forName("java.util.function.Supplier<" + blockTypePattern.getType().getName() + ">");
            FindMatchFailInfo failInfo = new FindMatchFailInfo("<while> function");
            JInvokable fct = compilerContext.lookupFunctionMatch(
                    JOnError.TRACE, "While",
                    HFunctionType.NORMAL, new JTypePattern[]{
                        JTypePattern.of(exprType),
                        JTypePattern.of(blockType)
                    },
                    node.getStartToken(), failInfo
            );
            if (fct != null) {
                setElement(node, new HNElementWhenDo("while", fct, exprTypePattern.getType(), blockTypePattern.getType(),
                        new HNIf.WhenDoBranchNode[]{
                            new HNIf.WhenDoBranchNode(expr, block, null)
                        }, null));
            }
        }
        return true;
    }

    private boolean onFor(HNFor node, HLJCompilerContext compilerContext) {
        JTypePattern bodyToL = node.getBody() == null ? null : compilerContext.getTypePattern(showFinalErrors, node.getBody());
        JTypePattern filterToL = node.getFilter() == null ? null : compilerContext.getTypePattern(showFinalErrors, node.getFilter());
        JTypePattern[] incsToL = compilerContext.getTypePattern(showFinalErrors, node.getIncs());
        JTypePattern[] initToL = compilerContext.getTypePattern(showFinalErrors, node.getInitExprs());

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
        JTypePattern whenToL = compilerContext.getTypePattern(showFinalErrors, whenNode);
        JTypePattern doToL = compilerContext.getTypePattern(showFinalErrors, doNode);
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
        JTypePattern condType = null;
        JTypePattern resultType = null;
        for (HNIf.WhenDoBranchNode branch : node.getBranches()) {
            HNode whenNode = (HNode) branch.getWhenNode();
            HNode doNode = (HNode) branch.getDoNode();
            JTypePattern whenToL = compilerContext.getTypePattern(showFinalErrors, whenNode);
            JTypePattern doToL = compilerContext.getTypePattern(showFinalErrors, doNode);
            if (whenToL == null || doToL == null) {
                return false;
            }
            if (whenToL.isLambda()) {
                whenNodeWithLambdas.add(whenNode);
            }
            if (stateStd) {
                condType = JTypeUtils.firstCommonSuperTypePattern(condType, whenToL, compilerContext.types());
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
                condType = JTypeUtils.firstCommonSuperTypePattern(condType, whenToL, compilerContext.types());
                node.setUserObject("WHEN_DO_TYPE", "METHOD");
                nonStd.add(branch);
            }
            resultType = JTypeUtils.firstCommonSuperTypePattern(resultType, doToL, compilerContext.types());
        }
        if (node.getElseNode() != null) {
            JTypePattern elseToL = compilerContext.getTypePattern(showFinalErrors, node.getElseNode());
            if (elseToL == null) {
                return false;
            }
            resultType = JTypeUtils.firstCommonSuperTypePattern(resultType, elseToL, compilerContext.types());
        }
        if (condType == null || condType.isLambda()) {
            compilerContext.getLog().jerror("X000", null, node.getStartToken(), "invalid condition type : " + condType);
            for (HNode whenNodeWithLambda : whenNodeWithLambdas) {
                compilerContext.getLog().jerror("X000", null, whenNodeWithLambda.getStartToken(), "invalid condition type");
            }
        }
        if (resultType == null || resultType.isLambda()) {
            resultType = JTypePattern.of(JTypeUtils.forObject(compilerContext.types()));
        }
        if (condType == null) {
            condType = JTypePattern.of(JTypeUtils.forBoolean(compilerContext.types()));
        }
        if (!JTypeUtils.isBooleanResolvableType(condType)) {
            JType branchesArrayType = compilerContext.types().forName("net.hl.lang.Branch<" + condType.getType().getName()
                    + ","
                    + resultType.getType().getName()
                    + ">");
            JType elseType = compilerContext.types().forName("java.util.function.Supplier<" + resultType.getType().getName() + ">");
            FindMatchFailInfo failInfo = new FindMatchFailInfo("<if> function");
            JInvokable fct = compilerContext.lookupFunctionMatch(
                    JOnError.TRACE, "if",
                    HFunctionType.SPECIAL, new JTypePattern[]{
                        JTypePattern.of(branchesArrayType.toArray()),
                        JTypePattern.of(elseType)
                    },
                    node.getStartToken(), failInfo
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
            HNode n = node.getItems()[0];
            if (node.getElement() == null) {
                node.setElement(n.getElement());
            }
            if (n.getElement().getTypePattern() != null) {
                if (node.getElement() instanceof HNElementExpr) {
                    HNElementExpr.get(node).setType(n.getElement().getTypePattern());
                } else {
                    node.setElement(new HNElementExpr(n.getElement().getTypePattern()));
                }
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
        if (left.getElement().getTypePattern() == null) {
            return false;
        }
        if (right.getElement().getTypePattern() == null) {
            return false;
        }
        ((HNElementExpr) node.getElement()).setType(
                left.getElement().getType().firstCommonSuperType(
                        right.getElement().getType()
                ));
        return true;
    }

    private boolean onTuple(HNTuple node, HLJCompilerContext compilerContext) {
        JTypePattern[] etypes = compilerContext.getTypePattern(showFinalErrors, node.getItems());
        if (etypes == null) {
            return false;
        }
        boolean err = false;
        for (int i = 0; i < etypes.length; i++) {
            JTypePattern etype = etypes[i];
            if (etype.isLambda()) {
                compilerContext.getLog().jerror("S000", null, node.getItems()[i].getStartToken(), "lambda expressions are not supported in Tuples");
                err = true;
            }
        }
        if (err) {
            return true;
        }
        HNElementExpr e = (HNElementExpr) node.getElement();
        e.setType(HTypeUtils.tupleType(compilerContext.types(), Arrays.stream(etypes).map(JTypePattern::getType).toArray(JType[]::new)));
        return true;
    }

    private boolean onBrackets(HNBrackets node, HLJCompilerContext compilerContext) {
        if (node.fullChildInfo().equals("HNBracketsPostfix:right")) {
            //will be processed later
        } else {
            //this is an array initialization
            JTypePattern[] allToL = compilerContext.getTypePattern(showFinalErrors, node.getItems());
            if (allToL == null) {
                return false;
            }
            JType arrType = null;
            if (allToL.length == 0) {
                compilerContext.getLog().jerror("S000", null, node.getStartToken(), "could not resolve empty array type");
            } else {
                for (int i = 0; i < allToL.length; i++) {
                    if (allToL[i].isLambda()) {
                        compilerContext.getLog().jerror("S000", null, node.getItems()[i].getStartToken(), "unsupported lambda expressions in array initialization");
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
        JTypePattern baseToL = compilerContext.getTypePattern(showFinalErrors, base);
        JTypePattern[] inodesToL = compilerContext.getTypePattern(showFinalErrors, inodes);
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
                        .map(x -> compilerContext.getTypePattern(showFinalErrors, x))
                        .allMatch(x -> x.isType() && x.getType().boxed().getName().equals("java.lang.Integer"))) {
                    // this is a regular array
                    JArrayType arrType = (JArrayType) baseToL.getType();
                    if (arrType.arrayDimension() >= inodes.length) {
                        acceptMethodImpl = false;
                        HNElementExpr element = (HNElementExpr) node.getElement();
                        JType tt = arrType;
                        for (int i = 0; i < inodes.length; i++) {
                            if (tt instanceof JArrayType) {
                                tt = ((JArrayType) tt).componentType();
                            } else {
                                compilerContext.getLog().jerror("X000", null, node.getStartToken(), "not an array " + tt);
                            }
                        }
                        element.setType(tt);
                        //ok
                    } else {
                        acceptMethodImpl = false;
                        compilerContext.getLog().jerror("S000", null,
                                inodes[inodes.length - 1].getStartToken(), "array type expected");
                    }
                }
            }
            if (acceptMethodImpl) {
                HNode[] nargs = JeepUtils.arrayAppend(HNode.class, base, inodes);
                JTypePattern[] ntypes = JeepUtils.arrayAppend(JTypePattern.class, baseToL, inodesToL);
                JInvokable m = compilerContext.lookupFunctionMatch(JOnError.TRACE, HExtensionNames.BRACKET_GET_SHORT, HFunctionType.SPECIAL, ntypes, node.getStartToken());
                if (m != null) {
                    HNElementMethod impl = new HNElementMethod(m);
                    impl.setArgNodes(nargs);
                    impl.setArg0TypeProcessed(true);
                    setElement(node, impl);
                    inferType(base, m.getSignature().argType(0), compilerContext);
                    for (int i = 0; i < inodes.length; i++) {
                        inferType(inodes[i], m.getSignature().argType(i + 1), compilerContext);
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
        if (node.getParentNode() instanceof HNTuple) {
            return onAssign_isLeft(node.getParentNode());
        }
        return false;
    }

    private boolean onObjectNew(HNObjectNew node, HLJCompilerContext compilerContext) {
        JTypePattern[] argTypes = compilerContext.getTypePattern(showFinalErrors, node.getInits());
        if (argTypes == null) {
            return false;
        }
        JType declaringType = node.getObjectTypeName().getTypeVal();
        if (declaringType == null) {
            return false;
        }
        JInvokable m = compilerContext.findConstructorMatch(JOnError.TRACE, declaringType, argTypes, node.getStartToken(), null);
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

    protected boolean implicitConvert(JTypePattern leftType, HNode rightNode, HLJCompilerContext compilerContext) {
        JTypePattern rightToL = rightNode.getElement().getTypePattern();
        JToken location = rightNode.getStartToken();

        if (JTypeUtils.isVoid(leftType) && JTypeUtils.isVoid(rightToL)) {
            compilerContext.getLog().jerror("S052", null, location, "void is not an expression");
            return true;
        }

        if (JTypeUtils.isVoid(leftType) || JTypeUtils.isVoid(rightToL)) {
            compilerContext.getLog().jerror("S052", null, location, "void is not an expression");
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
        compilerContext.getLog().jerror("S052", null, location, "type mismatch. expected " + leftType + " but found " + rightToL);
        return true;
    }

    private boolean onAssign_deconstruct(HNode left, HNode right, HLJCompilerContext compilerContext) {
        JTypePattern rightToL = compilerContext.getTypePattern(showFinalErrors, right);
        if (rightToL == null) {
            return false;
        }
        switch (left.getElement().getKind()) {
            case LOCAL_VAR:
            case FIELD: {
                JTypePattern leftToL = compilerContext.getTypePattern(showFinalErrors, left);
                if (leftToL == null) {
                    return false;
                }
                if (left.getElement().getKind() == HNElementKind.FIELD) {
                    JField field = ((HNElementField) left.getElement()).getField();
                    if (field.isStatic()) {
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
                    JTypePattern baseToL = compilerContext.getTypePattern(showFinalErrors, base);
                    JTypePattern[] indicesToL = compilerContext.getTypePattern(showFinalErrors, indicesNodes);
                    if (indicesToL == null) {
                        return false;
                    }
                    if (baseToL == null) {
                        return false;
                    }
                    boolean acceptMethodImpl = true;
                    if (baseToL.getType().isArray()) {
                        if (indicesNodes.stream()
                                .map(x -> compilerContext.getTypePattern(showFinalErrors, x))
                                .allMatch(x -> x.isType() && x.getType().boxed().getName().equals("java.lang.Integer"))) {
                            // this is a regular array
                            JArrayType arrType = (JArrayType) baseToL.getType();
                            if (arrType.arrayDimension() >= indicesNodes.size()) {
                                acceptMethodImpl = false;
                                HNElementExpr element = (HNElementExpr) left.getElement();
                                element.setType(
                                        arrType.rootComponentType().toArray(arrType.arrayDimension() - indicesNodes.size())
                                );
                                //ok
                            } else {
                                acceptMethodImpl = false;
                                compilerContext.getLog().jerror("S000", null,
                                        indicesNodes.get(indicesNodes.size() - 1).getStartToken(), "array type expected");
                            }
                        }
                    }
                    if (acceptMethodImpl) {
                        HNode[] nargs = JeepUtils.arrayAppend(HNode.class, base, indicesNodes.toArray(new HNode[0]), right);
                        JTypePattern[] ntypes = JeepUtils.arrayAppend(JTypePattern.class, baseToL, indicesToL, rightToL);
                        JInvokable m = compilerContext.lookupFunctionMatch(JOnError.TRACE, HExtensionNames.BRACKET_SET_SHORT, HFunctionType.SPECIAL, ntypes, left.getStartToken());
                        if (m != null) {
                            HNElementMethod impl = new HNElementMethod(m);
                            impl.setArgNodes(nargs);
                            impl.setArg0TypeProcessed(true);
                            setElement(left, impl);
                            inferType(base, m.getSignature().argType(0), compilerContext);
                            for (int i = 0; i < indicesNodes.size(); i++) {
                                inferType(indicesNodes.get(i), m.getSignature().argType(i + 1), compilerContext);
                            }
                        }
                    } else {
                        if (base.getElement().getKind() == HNElementKind.FIELD) {
                            JField field = ((HNElementField) base.getElement()).getField();
                            if (field.isStatic()) {
                                left.setUserObject("StaticLHS");
                            }
                        }
                    }
                    return true;

                } else if (left instanceof HNTuple) {
                    HNTuple tuple = (HNTuple) left;
                    //de-constructor matcher
                    if (!right.getElement().getTypePattern().isType()) {
                        compilerContext.getLog().jerror("S000", null, right.getStartToken(), "invalid Tuple");
                    } else {
                        JType encountered = right.getElement().getTypePattern().getType();
                        if (!HTypeUtils.isTupleType(encountered)) {
                            compilerContext.getLog().jerror("X000", null, right.getStartToken(), "expected tuple type");
                            return false;
                        }
                        JType[] tupleArgTypes;
                        try {
                            tupleArgTypes = HTypeUtils.tupleArgTypes(encountered);
                        } catch (Exception ex) {
                            compilerContext.getLog().jerror("X000", null, right.getStartToken(), "invalid tuple type");
                            return false;
                        }
                        if (tupleArgTypes.length != tuple.getItems().length) {
                            compilerContext.getLog().jerror("X000", null, right.getStartToken(), "tuple mismatch " + tupleArgTypes.length + "!=" + tuple.getItems().length);
                            return false;
                        }
                        HNElement lelement = tuple.getElement();
                        ((HNElementExpr) lelement).setType(right.getElement().getTypePattern());
                        //if(_checkTypeMismatch(expected,encountered,left.startToken(),compilerContext)){
                        for (int i = 0; i < tupleArgTypes.length; i++) {
                            HNOpDot tempRight = new HNOpDot(
                                    right.copy(),
                                    HTokenUtils.createToken("."),
                                    new HNIdentifier(HTokenUtils.createToken("_" + (i + 1))),
                                    null, null
                            );
                            setElement(tempRight, new HNElementExpr(tupleArgTypes[i]));
                            if (!onAssign_deconstruct(
                                    (HNode) tuple.getItems()[i],
                                    tempRight, compilerContext
                            )) {
                                return false;
                            }
                            if (tuple.getItems()[i].isSetUserObject("StaticLHS")) {
                                left.setUserObject("StaticLHS");
                            }
                        }
                        //}
                    }
                } else {
                    compilerContext.getLog().jerror("X000", null, left.getStartToken(), "invalid assignment");
                }
                break;
            }
        }
        return true;
    }

//    private boolean onAssign0(HNAssign node, HLJCompilerContext compilerContext) {
//        HNode left = (HNode) node.getLeft();
//        HNode right = (HNode) node.getRight();
//        HNElementAssign elementAssign = (HNElementAssign) node.getElement();
//        HNode rightReplace = right;
//        if (right.getElement() != null && HTypeUtils.isTupleType(right.getElement().getTypeOrLambda())) {
//            String vn = compilerContext.nextVarName();
//            HNIdentifier hi = new HNIdentifier(HNodeUtils.createToken(vn));
//            HNElementLocalVar element = new HNElementLocalVar(hi.getName());
//            element.setEffectiveType(right.getElement().getType());
//            hi.setElement(element);
////            elementAssign.setDeclareTempVarName(vn);
//            rightReplace = hi;
//        }
//
//        if (onAssign_deconstruct0(left, rightReplace, compilerContext)) {
//            ((HNElementAssign) node.getElement()).setType(left.getElement().getTypeOrLambda());
//            return true;
//        }
//        return false;
//    }
//    private boolean onAssign_deconstruct0(HNode left, HNode right, HLJCompilerContext compilerContext) {
//        JTypePattern rightToL = compilerContext.jTypeOrLambda(showFinalErrors, right);
//        if (rightToL == null) {
//            return false;
//        }
//        switch (left.getElement().getKind()) {
//            case LOCAL_VAR:
//            case FIELD: {
//                JTypePattern leftToL = compilerContext.jTypeOrLambda(showFinalErrors, left);
//                if (leftToL == null) {
//                    return false;
//                }
//                return implicitConvert(leftToL, right, compilerContext);
//            }
//            case EXPR: {
//                if (left instanceof HNBracketsPostfix) {
//                    HNBracketsPostfix aleft = (HNBracketsPostfix) left;
//                    HNode base = aleft.getLeft();
//                    List<HNode> indicesNodes = aleft.getRight();
//                    JTypePattern baseToL = compilerContext.jTypeOrLambda(showFinalErrors, base);
//                    JTypePattern[] indicesToL = compilerContext.jTypeOrLambdas(showFinalErrors, indicesNodes);
//                    if (indicesToL == null) {
//                        return false;
//                    }
//                    if (baseToL == null) {
//                        return false;
//                    }
//                    boolean acceptMethodImpl = true;
//                    if (baseToL.getType().isArray()) {
//                        if (indicesNodes.stream()
//                                .map(x -> compilerContext.jTypeOrLambda(showFinalErrors, x))
//                                .allMatch(x -> x.isType() && x.getType().boxed().name().equals("java.lang.Integer"))) {
//                            // this is a regular array
//                            JArrayType arrType = (JArrayType) baseToL.getType();
//                            if (arrType.arrayDimension() >= indicesNodes.size()) {
//                                acceptMethodImpl = false;
//                                HNElementExpr element = (HNElementExpr) left.getElement();
//                                element.setType(
//                                        arrType.rootComponentType().toArray(arrType.arrayDimension() - indicesNodes.size())
//                                );
//                                //ok
//                            } else {
//                                acceptMethodImpl = false;
//                                compilerContext.log().error("S000", null, "array type expected",
//                                        indicesNodes.get(indicesNodes.size() - 1).startToken());
//                            }
//                        }
//                    }
//                    if (acceptMethodImpl) {
//                        HNode[] nargs = JeepUtils.arrayAppend(HNode.class, base, indicesNodes.toArray(new HNode[0]), right);
//                        JTypePattern[] ntypes = JeepUtils.arrayAppend(JTypePattern.class, baseToL, indicesToL, rightToL);
//                        JInvokable m = compilerContext.lookupFunctionMatch(JOnError.TRACE, HLExtensionNames.BRACKET_SET_SHORT, HFunctionType.SPECIAL, ntypes, left.startToken());
//                        if (m != null) {
//                            HNElementMethod impl = new HNElementMethod(m);
//                            impl.setArgNodes(nargs);
//                            impl.setArg0TypeProcessed(true);
//                            setElement(left, impl);
//                            inferType(base, m.signature().argType(0), compilerContext);
//                            for (int i = 0; i < indicesNodes.size(); i++) {
//                                inferType(indicesNodes.get(i), m.signature().argType(i + 1), compilerContext);
//                            }
//                        }
//                    }
//                    return true;
//
//                } else if (left instanceof HNTuple) {
//                    HNTuple tuple = (HNTuple) left;
//                    //de-constructor matcher
//                    if (!right.getElement().getTypeOrLambda().isType()) {
//                        compilerContext.log().error("S000", null, "invalid Tuple", right.startToken());
//                    } else {
//                        JType encountered = right.getElement().getTypeOrLambda().getType();
//                        if (!HTypeUtils.isTupleType(encountered)) {
//                            compilerContext.log().error("X000", null, "expected tuple type", right.startToken());
//                            return false;
//                        }
//                        JType[] tupleArgTypes;
//                        try {
//                            tupleArgTypes = HTypeUtils.tupleArgTypes(encountered);
//                        } catch (Exception ex) {
//                            compilerContext.log().error("X000", null, "invalid tuple type", right.startToken());
//                            return false;
//                        }
//                        if (tupleArgTypes.length != tuple.getItems().length) {
//                            compilerContext.log().error("X000", null, "tuple mismatch " + tupleArgTypes.length + "!=" + tuple.getItems().length, right.startToken());
//                            return false;
//                        }
//                        HNElement lelement = tuple.getElement();
//                        ((HNElementExpr) lelement).setType(right.getElement().getTypeOrLambda());
//                        //if(_checkTypeMismatch(expected,encountered,left.startToken(),compilerContext)){
//                        for (int i = 0; i < tupleArgTypes.length; i++) {
//                            HNOpDot tempRight = new HNOpDot(
//                                    right,
//                                    HNodeUtils.createToken("."),
//                                    new HNIdentifier(HNodeUtils.createToken("_" + (i + 1))),
//                                    null, null
//                            );
//                            setElement(tempRight, new HNElementExpr(tupleArgTypes[i]));
//                            if (!onAssign_deconstruct0(
//                                    (HNode) tuple.getItems()[i],
//                                    tempRight, compilerContext
//                            )) {
//                                return false;
//                            }
//                        }
//                        //}
//                    }
//                } else {
//                    throw new JShouldNeverHappenException();
//                }
//                break;
//            }
//        }
//        return true;
//    }
    private boolean onAssign(HNAssign node, HLJCompilerContext compilerContext) {
        HNode left = node.getLeft();
        HNode right = node.getRight();
        if (onAssign_deconstruct(left, right, compilerContext)) {
            ((HNElementAssign) node.getElement()).setType(left.getElement().getTypePattern());
            if (left.isSetUserObject("StaticLHS")) {
                node.setUserObject("StaticAssign");
            }
            return true;
        }
        return false;
    }

    private boolean onDeclareIdentifier(HNDeclareIdentifier node, HLJCompilerContext compilerContext) {
        HNode initValue = node.getInitValue();
        boolean parentIsLambda = node.getParentNode() instanceof HNLambdaExpression;
        if (parentIsLambda) {
            return true;
        }
        if (node.getIdentifierType() == null) {
            //inference of identifier type from value
            if (initValue != null) {
                JTypePattern typePattern = compilerContext.getTypePattern(showFinalErrors, initValue);
                if (typePattern == null) {
                    return false;
                }
                applyDeclareTokenType(node, typePattern, compilerContext);
            } else {
                compilerContext.getLog().jerror("S000", null, node.getStartToken(), "type inference failed with no init value");
            }
        } else {
            JType identifierType = node.getIdentifierType();
            applyDeclareTokenType(node, JTypePattern.of(identifierType), compilerContext);
        }
        if (initValue != null && node.getIdentifierType() != null) {
            JTypePattern jTypePattern = initValue.getElement().getTypePattern();
            if (jTypePattern == null) {
                //error should be already reported!
                return true;
            }
            return implicitConvert(JTypePattern.of(node.getIdentifierType()), initValue, compilerContext);
        }
        return true;
    }

    private void applyDeclareTokenType(HNDeclareIdentifier node, JTypePattern identifierType, HLJCompilerContext compilerContext) {
        applyDeclareTokenType(node.getIdentifierToken(), identifierType, compilerContext);
        node.setEffectiveIdentifierType(identifierType.getType());
    }

    private void applyDeclareTokenType(HNDeclareToken identifierToken, JTypePattern identifierType, HLJCompilerContext compilerContext) {
        if (identifierType.isLambda()) {
            compilerContext.getLog().jerror("S000", null, identifierToken.getStartToken(), "type inference failed with unresolvable lambda expression");
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
                        compilerContext.indexer().indexField(new HIndexedField(field, HSharedUtils.getSourceName(identifierToken)));
                    }
                } else if (e.getKind() == HNElementKind.LOCAL_VAR) {
                    HNElementLocalVar lv = (HNElementLocalVar) e;
                    HNDeclareIdentifier id = compilerContext.lookupEnclosingDeclareIdentifier(identifierToken);
                    if (id != null) {
                        if (id.getAssignOperator() == null || id.getAssignOperator().isImage("=")) {
                            lv.setEffectiveType(identifierType.getType());
                        } else if (id.getAssignOperator().isImage(":")) {
                            ElementTypeAndConstraint cc = HTypeUtils.resolveIterableComponentType(identifierType.getType(), compilerContext.types());
                            if (cc == null) {
                                compilerContext.getLog().jerror("S000", null, identifierToken.getStartToken(), "expected iterable/iterator type");
                            } else {
                                lv.setEffectiveType(cc.valType);
                            }
                        } else {
                            throw new JShouldNeverHappenException();
                        }
                    } else {
                        throw new JShouldNeverHappenException();
                    }
                } else {
                    compilerContext.getLog().jerror("S000", null, identifierToken.getStartToken(), "unexpected value");
                }
            }
        } else if (identifierToken instanceof HNDeclareTokenTuple) {
            HNDeclareTokenTuple tt = (HNDeclareTokenTuple) identifierToken;
            if (!HTypeUtils.isTupleType(identifierType.getType())) {
                compilerContext.getLog().jerror("S000", null, identifierToken.getStartToken(), "expected tuple type, found " + identifierType.getType().getName());
            } else {
                JType[] jTypes = HTypeUtils.tupleArgTypes(identifierType.getType());
                if (jTypes.length != tt.getItems().length) {
                    compilerContext.getLog().jerror("S000", null, identifierToken.getStartToken(), "expected tuple elements count mismatch  " + jTypes.length + "!=" + tt.getItems().length);
                } else {
//                HNElementExpr element = (HNElementExpr) tt.getElement();
//                element.setType(identifierType);
                    for (int i = 0; i < jTypes.length; i++) {
                        applyDeclareTokenType(tt.getItems()[i], JTypePattern.of(jTypes[i]), compilerContext);
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
        List<JTypePattern> argTypes = new ArrayList<>();
        for (HNode item : arguments) {
            HNode hitem = item;
            JTypePattern typePattern = compilerContext.getTypePattern(showFinalErrors, hitem);
            if (typePattern == null) {
                return false;
            }
            argTypes.add(typePattern);
        }
        HNode[] argumentsArr = arguments.toArray(new HNode[0]);
        switch (left.id()) {
            case H_IDENTIFIER: {
                HNIdentifier ident = (HNIdentifier) left;
                if (node.getParentNode() instanceof HNOpDot) {
                    HNOpDot d = (HNOpDot) node.getParentNode();
                    if (d.getRight() == node) {
                        return onIdentifierWithPars(ident, node, d.getLeft(), argumentsArr, compilerContext);
                    }
                }
                return onIdentifierWithPars(ident, node, null, argumentsArr, compilerContext);
            }
            case H_OP_DOT: {
                HNOpDot d = (HNOpDot) left;
                if (d.getRight() instanceof HNIdentifier) {
                    HNIdentifier ident = (HNIdentifier) d.getRight();
                    boolean v = onIdentifierWithPars(ident, node, d.getLeft(), argumentsArr, compilerContext);
                    setElement(d, ident.getElement());
                    return v;
                }
                break;
            }
            case H_THIS: {
                HNThis d = (HNThis) left;
                HNode r = compilerContext.lookupEnclosingDeclaration(node, false);
                if (r instanceof HNDeclareInvokable) {
                    HNDeclareInvokable di = (HNDeclareInvokable) r;
                    if (di.isConstr()) {
                        JType tt = compilerContext.getOrCreateType(di.getDeclaringType());
                        JInvokable c = compilerContext.findConstructorMatch(JOnError.TRACE, tt, argTypes.toArray(new JTypePattern[0]), node.getStartToken(), null);
                        if (c != null) {
                            d.setElement(new HNElementConstructor(tt, c, arguments.toArray(new HNode[0])));
                        }
                        HNElementExpr.get(node).setType(JTypeUtils.forVoid(compilerContext.types()));
                        return true;
                    }
                }
                break;
            }
            case H_SUPER: {
                HNSuper d = (HNSuper) left;
                HNode r = compilerContext.lookupEnclosingDeclaration(node, false);
                if (r instanceof HNDeclareInvokable) {
                    HNDeclareInvokable di = (HNDeclareInvokable) r;
                    if (di.isConstr()) {
                        JType tt = compilerContext.getOrCreateType(di.getDeclaringType());
                        JType st = tt.getSuperType();
                        if (st == null) {
                            compilerContext.getLog().jerror("X000", null, node.getStartToken(), "mismatch 'super' call");
                        } else {
                            JInvokable c = compilerContext.findConstructorMatch(JOnError.TRACE, st, argTypes.toArray(new JTypePattern[0]), node.getStartToken(), null);
                            if (c != null) {
                                d.setElement(new HNElementConstructor(st, c, arguments.toArray(new HNode[0])));
                            }
                        }
                        HNElementExpr.get(node).setType(JTypeUtils.forVoid(compilerContext.types()));
                        return true;
                    }
                }
                break;
            }
            case H_TYPE_TOKEN: {
                HNTypeToken d = (HNTypeToken) left;
                JType declaringType = d.getTypeVal();
                if (declaringType == null) {
                    return false;
                }
                JInvokable m = compilerContext.findConstructorMatch(JOnError.TRACE, declaringType, argTypes.toArray(new JTypePattern[0]), node.getStartToken(), null);
                if (m != null) {
                    HNElement e = node.getElement();
                    if (e instanceof HNElementConstructor) {
                        HNElementConstructor cc = (HNElementConstructor) e;
                        cc.setInvokable(m);
                        cc.setArgNodes(arguments.toArray(new HNode[0]));
                    } else {
                        setElement(node, new HNElementConstructor(declaringType, m, arguments.toArray(new HNode[0])));
                    }
                }
                return true;

            }
        }
        JTypePattern t = compilerContext.getTypePattern(node.getLeft());
        if (t == null) {
            return false;
        }
        if (t.isLambda()) {
            compilerContext.getLog().jerror("X000", null, node.getRight().get(0).getStartToken(), "unresolved call of a lambda expression");
            return true;
        }
        argTypes.add(0, t);
        arguments.add(0, node.getLeft());
        JInvokable ctrInvokable = compilerContext.lookupFunctionMatch(JOnError.TRACE, HExtensionNames.FUNCTION_APPLY, HFunctionType.SPECIAL, argTypes.toArray(new JTypePattern[0]), node.getStartToken(), null);
        if (ctrInvokable != null) {
            HNElementMethod element = new HNElementMethod(ctrInvokable);
            element.setArgNodes(arguments.toArray(new HNode[0]));
            element.setArg0TypeProcessed(true);
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
            List<JTypePattern> argsTypes = new ArrayList<>();
            List<HNode> argsNodes = new ArrayList<>();
            boolean error = false;
            for (HNode init : node.getInits()) {
                JTypePattern t = compilerContext.getTypePattern(showFinalErrors, init);
                if (t == null) {
                    error = true;
                } else {
                    argsTypes.add(t);
                    argsNodes.add(init);
                }
            }
            JTypePattern t = compilerContext.getTypePattern(showFinalErrors, node.getConstructor());
            if (t == null) {
                error = true;
            } else {
                argsTypes.add(t);
                argsNodes.add(node.getConstructor());
            }
            if (error) {
                return false;
            }
            if (node.getArrayTypeName().getTypeVal() == null) {
                return false;
            }
            String methodName = HSharedUtils.getStaticConstructorName(node.getArrayTypeName().getTypeVal());
            JInvokable f = compilerContext.lookupFunctionMatch(JOnError.TRACE, methodName,
                    HFunctionType.SPECIAL, argsTypes.toArray(new JTypePattern[0]), node.getStartToken());
            if (f != null) {
                HNElementMethod element = new HNElementMethod(f);
                element.setArgNodes(argsNodes.toArray(new HNode[0]));
                element.setArg0TypeProcessed(true);
                setElement(node, element);
                //infer back types
                inferTypes(argsNodes.toArray(new HNode[0]), f.getSignature().argTypes(), compilerContext);
            }
        }
        return true;
    }

    protected void inferTypes(HNode[] nodes, JType[] types, HLJCompilerContext compilerContext) {
        for (int i = 0; i < nodes.length; i++) {
            HNode node = nodes[i];
            JTypePattern t = compilerContext.getTypePattern(false, node);
            if (t.isLambda()) {
                inferType(node, types[i], compilerContext);
            }
        }
    }

    protected void inferType(HNode node, JType type, HLJCompilerContext compilerContext) {
        JTypePattern t = compilerContext.getTypePattern(false, node);
        if (t.isLambda()) {
            HNode hnode = (HNode) node;
            HNElementLambda e = (HNElementLambda) hnode.getElement();
            e.setInferredType(type);
            List<HNDeclareIdentifier> arguments = ((HNLambdaExpression) node).getArguments();
            JSignature atypes = JTypeUtils.extractLambdaArgTypesOrError(type, arguments.size(), hnode.getStartToken(), compilerContext.getLog());
            if (atypes != null) {
                for (int i = 0; i < arguments.size(); i++) {
                    //TODO may be call : atypes.acceptAndExpand()
                    applyDeclareTokenType(arguments.get(i), JTypePattern.of(atypes.argType(i)), compilerContext);
                }
            }
        }
    }

    private boolean onDeclareInvokable(HNDeclareInvokable node, HLJCompilerContext compilerContext) {
        if (node.getReturnType() == null) {
            //inference of method type from body
            HNode initValue = node.getBody();
            if (initValue == null) {
                //no body
                compilerContext.getLog().jerror("S000", null, node.getStartToken(), "type inference failed for function/method without body");
            } else {
                if (node.getInvokable() instanceof DefaultJRawMethod) {
                    DefaultJRawMethod method = (DefaultJRawMethod) node.getInvokable();
                    if (method.getGenericReturnType() == null) {
                        JType ert = null;
                        if (method.getSignature().toString().equals("main(java.lang.String[])")) {
                            ert = JTypeUtils.forVoid(compilerContext.types());
                            method.setGenericReturnType(ert);
                            compilerContext.indexer().indexMethod(new HIndexedMethod(method, HSharedUtils.getSourceName(node)));
                        } else {
                            HNode[] exitPoints = initValue.getExitPoints();
                            if (exitPoints.length == 0) {
                                compilerContext.getLog().jerror("S000", null, initValue.getStartToken(), "type inference failed for function/method without body");
                            } else {
                                JTypePattern[] args = compilerContext.getTypePattern(showFinalErrors, exitPoints);
                                if (args == null) {
                                    return false;
                                }
                                boolean error = false;
                                for (int i = 0; i < args.length; i++) {
                                    if (args[i].isLambda()) {
                                        error = true;
                                        compilerContext.getLog().jerror("S000", null, initValue.getStartToken(), "type inference failed with unresolvable lambda expression");
                                        break;
                                    } else {
                                        ert = ert == null ? args[i].getType() : ert.firstCommonSuperType(args[i].getType());
                                    }
                                }
                                if (!error && ert != null) {
                                    //ert = method.genericReturnType();
                                    method.setGenericReturnType(ert);
                                    compilerContext.indexer().indexMethod(new HIndexedMethod(method, HSharedUtils.getSourceName(node)));
                                }
                            }
                        }
                        ert = method.getGenericReturnType();
                        node.setEffectiveReturnType(ert);
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
        JTypePattern[] args = compilerContext.getTypePattern(showFinalErrors, left, right);
        if (args == null) {
            return false;
        }
        boolean negated = false;
        if (opName.equals("!in")) {
            opName = "in";
            negated = true;
        }
//        if (args != null) {
        JInvokable f = compilerContext.lookupFunctionMatch(JOnError.TRACE, opName, HFunctionType.SPECIAL, args, node.getStartToken());
        if (f != null) {
            if (negated) {
                f = new NegateInvokable(f, f.getSignature(), compilerContext.getContext());
            }
            HNElementMethod element = new HNElementMethod(f);
            element.setArgNodes(new HNode[]{node.getLeft(), node.getRight()});
            element.setArg0Kind(HNElementMethod.Arg0Kind.NONE);
            element.setArg0TypeProcessed(true);
            setElement(node, element);
            inferType(left, f.getSignature().argType(0), compilerContext);
            inferType(right, f.getSignature().argType(1), compilerContext);
        }
//        } else if (
//                node.getType() == null &&
//                        HSharedUtils.isComparisonOperator(opName)
//                        &&
//                        (HSharedUtils.isNullLiteral(left))
//                        || (HSharedUtils.isNullLiteral(right))
//        ) {
//            node.setType(compilerContext.types().forName("boolean"));
//        }
        return true;
    }

    private boolean onOpUnaryCall(HNOpUnaryCall node, HLJCompilerContext compilerContext) {
        String opName = node.getNameToken().image;
        HNode expr = node.getExpr();
        JTypePattern args = compilerContext.getTypePattern(showFinalErrors, expr);
        if (args == null) {
            return false;
        }
        switch (opName) {
            case "++":
            case "--": {
                if (args.isType()) {
                    switch (args.getType().getName()) {
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
                            ee.setType(args.getType());
                            return true;
                        }
                    }
                }
            }
        }
        JInvokable f = compilerContext.lookupFunctionMatch(JOnError.TRACE, opName,
                node.isPrefixOperator() ? HFunctionType.PREFIX_UNARY : HFunctionType.POSTFIX_UNARY, new JTypePattern[]{args}, node.getStartToken());
        if (f != null) {
            HNElementMethod element = new HNElementMethod(f);
            element.setArgNodes(new HNode[]{expr});
            element.setArg0TypeProcessed(true);
            setElement(node, element);
            inferType(expr, f.getSignature().argType(0), compilerContext);
        }
        return true;
    }

    protected boolean onIdentifierWithPars(HNIdentifier childNodeIdent, HNParsPostfix parentNodeParsPostfix, HNode dotBase, HNode[] arguments, HLJCompilerContext compilerContext) {
        boolean lhs = false;
        if (arguments != null) {
            JTypePattern[] argTypes = compilerContext.getTypePattern(showFinalErrors, arguments);
            if (argTypes == null) {
                return false;
            }
        }
        if (dotBase != null) {
            JTypePattern a = compilerContext.getTypePattern(showFinalErrors, dotBase);
            if (a == null) {
                return false;
            }
        }
        HNElement ee = compilerContext.lookupElement(JOnError.TRACE, childNodeIdent.getName(), dotBase, arguments, lhs, childNodeIdent.getStartToken(),
                childNodeIdent.getParentNode(), null);
        if (ee != null) {
            setElement(childNodeIdent, ee);
            setElement(parentNodeParsPostfix, childNodeIdent.getElement());
            if (ee.getTypePattern() == null) {
                return false;
            }
            return true;
        }
        return false;
    }

    protected boolean onIdentifierWithoutPars(HNIdentifier node, HNode dotBase, HLJCompilerContext compilerContext) {
        if(node.getParentNode() instanceof HNMetaImportPackage){
            return true;
        }
        boolean lhs = false;
        if (dotBase != null) {
            HNElementKind kind = dotBase.getElement().getKind();
            if (kind != HNElementKind.PACKAGE && kind != HNElementKind.TYPE) {
                if (compilerContext.getTypePattern(showFinalErrors, dotBase) == null) {
                    return false;
                }
            }
        }
        HNElement e = compilerContext.lookupElement(JOnError.TRACE, node.getName(), dotBase, null, lhs, node.getStartToken(),
                node.getParentNode(), null);
        if (e != null) {
            setElement(node, e);
        }
        return e != null;
    }

    private boolean onLambdaExpression(HNLambdaExpression node, HLJCompilerContext compilerContext) {
        List<HNDeclareIdentifier> arguments = node.getArguments();
        boolean ok = true;
        List<JType> argTypes = new ArrayList<>();
        for (HNDeclareIdentifier argument : arguments) {
            JType eit = argument.getEffectiveIdentifierType();
            if (eit == null) {
                eit = argument.getIdentifierType();
            }
            if (eit == null) {
                ok = false;
//                return false;
            }
            argTypes.add(eit);
        }
        HNode b = node.getBody();
        JTypePattern t = compilerContext.getTypePattern(false, b);
        if (t == null) {
            ok = false;
//            return false;
        }
        HNElementLambda element = (HNElementLambda) node.getElement();
        element.setArgTypes(argTypes.toArray(new JType[0]), t == null ? null : t.getType());
        return ok;
    }

    protected boolean onIdentifier(HNIdentifier node, HLJCompilerContext compilerContext) {
        switch (node.fullChildInfo()) {
            case "HNOpDot:right": {
                HNode dotBase = (HNode) ((HNOpDot) node.getParentNode()).getLeft();
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
        if (n.getElement() == null || n.getElement().getTypePattern() == null) {
            return false;
        }
        return true;
    }

    private void setElement(HNode node, HNElement element) {
        if (element == null) {
            throw new NullPointerException();
        }
        node.setElement(element);
    }

    protected void processProjectReplay(HProject project, HOptions options) {
        if (!replays.isEmpty()) {
            while (true) {
                LOG.log(Level.FINE, "processing " + replays.size() + " nodes");
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
        HNode node = (HNode) compilerContextBase.getNode();
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
            LOG.log(Level.FINE, "unexpected error : " + ex.toString(), ex);
            compilerContext.getLog().jerror("S000", "unexpected error", compilerContext.getNode().getStartToken(), ex.toString());
            throw ex;
        }
    }

    public boolean processCompilerStage(JCompilerContext compilerContextBase) {
//        boolean succeeded = true;
        HNode node = (HNode) compilerContextBase.getNode();
        if (node.id() == HNNodeId.H_DECLARE_META_PACKAGE && !inPreprocessor) {
            //do not go further
            return true;
        }
        if (!processAllNextCompilerStage(compilerContextBase)) {
            //succeeded=false;
        }
        return /*succeeded && */ processCompilerStage0(compilerContextBase);
    }

    @Override
    public boolean isRequiredCheck(HProject project, HOptions options) {
        return super.isRequiredCheck(project, options) && project.log().isSuccessful();
    }

    protected void processProjectMain(HProject project, HOptions options) {
        super.processProjectMain(project, options);
        processProjectReplay(project, options);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    // CHECK
    /////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean processCompilerStageCurrentCheck(HNode node, HLJCompilerContext compilerContext) {
        String simpleName = node.getClass().getSimpleName();
        if (node.getElement() == null) {
            compilerContext.getLog().jerror("X000", null, node.getStartToken(), "node.getElement()==null for " + simpleName);
        } else {
            HNElement element = node.getElement();
            switch (element.getKind()) {
                case CONSTRUCTOR: {
                    if (element.getTypePattern() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementConstructor.getTypeOrLambda()==null for " + simpleName);
                    }
                    HNElementConstructor c = (HNElementConstructor) element;
                    if (c.getInvokable() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementConstructor.getInvokable()==null for " + simpleName);
                    }
                    if (c.getDeclaringType() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementConstructor.getDeclaringType()==null for " + simpleName);
                    }
//                    if(node.getType()==null){
//                        compilerContext.log().error("X000","node.getType()==null for "+simpleName,node.startToken());
//                    }
                    break;
                }
                case METHOD: {
                    HNElementMethod c = (HNElementMethod) element;
                    if (element.getTypePattern() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementMethod.getTypeOrLambda()==null for " + simpleName);
                    }
                    if (c.getInvokable() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementMethod.getInvokable()==null for " + simpleName);
                    }
//                    if(c.getDeclaringType()==null){
//                        compilerContext.log().error("X000","HNElementMethod.getDeclaringType()==null for "+simpleName,node.startToken());
//                    }
                    if (c.getReturnType() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementMethod.getReturnType()==null for " + simpleName);
                    }
//                    if(node.getType()==null){
//                        compilerContext.log().error("X000","node.getType()==null for "+simpleName,node.startToken());
//                    }
                    break;
                }
                case FIELD: {
                    HNElementField c = (HNElementField) element;
                    if (element.getTypePattern() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementField.getTypeOrLambda()==null for " + simpleName);
                    }
                    if (c.getField() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementField.getField()==null for " + simpleName);
                    }
                    if (c.getDeclaringType() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementField.getDeclaringType()==null for " + simpleName);
                    }
                    if (c.getType() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementField.getType()==null for " + simpleName);
                    }
//                    if(node.getType()==null){
//                        compilerContext.log().error("X000","node.getType()==null for "+simpleName,node.startToken());
//                    }
                    break;
                }
                case EXPR: {
                    HNElementExpr c = (HNElementExpr) element;
                    if (element.getTypePattern() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementExpr.getTypeOrLambda()==null for " + simpleName);
                    }
                    if (c.getType() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementExpr.getType()==null for " + simpleName);
                    }
//                    if(node.getType()==null){
//                        compilerContext.log().error("X000","node.getType()==null for "+simpleName,node.startToken());
//                    }
                    break;
                }
                case LOCAL_VAR: {
                    HNElementLocalVar c = (HNElementLocalVar) element;
                    if (element.getTypePattern() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementLocalVar.getTypeOrLambda()==null for " + simpleName);
                    }
                    if (c.getType() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementLocalVar.getType()==null for " + simpleName);
                    }
//                    if(node.getType()==null){
//                        compilerContext.log().error("X000","node.getType()==null for "+simpleName,node.startToken());
//                    }
                    break;
                }
                case TYPE: {
                    HNElementType c = (HNElementType) element;
                    if (c.getValue() == null) {
                        compilerContext.getLog().jerror("X000", null, node.getStartToken(), "HNElementType.getType()==null for " + simpleName);
                    }
                    break;
                }
            }
        }
        return true;
    }

}
