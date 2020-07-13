package net.hl.compiler.parser;

import net.vpc.common.jeep.JTokenType;
import net.vpc.common.jeep.core.JExpressionBinaryOptions;
import net.vpc.common.jeep.core.JExpressionUnaryOptions;
import net.vpc.common.jeep.core.tokens.JTokenDef;
import net.hl.compiler.core.HTokenId;

public class HParserOptions {
    static final HDeclarationOptions DECL_ANY_1 = new HDeclarationOptions()
           .setAcceptInValue(false)
           .setAcceptMultiVars(true)
           .setAcceptVarArg(false)
           .setNoTypeNameOption(HDeclarationOptions.NoTypeNameOption.ERROR);
    static final HDeclarationOptions DECL_ANY_2 = new HDeclarationOptions()
           .setAcceptInValue(false)
           .setAcceptVarArg(false)
           .setNoTypeNameOption(HDeclarationOptions.NoTypeNameOption.ERROR);
    static final HDeclarationOptions DECL_ANY_3 = new HDeclarationOptions()
           .setAcceptInValue(false)
           .setAcceptMultiVars(true)
           .setAcceptVarArg(false)
           .setNoTypeNameOption(HDeclarationOptions.NoTypeNameOption.ERROR);
    static final JTokenDef DEF_GT = new JTokenDef(HTokenId.GT, "GT", JTokenType.TT_OPERATOR, "TT_OPERATOR", ">");
    static final JTokenDef DEF_GT2 = new JTokenDef(HTokenId.GT2, "GT2", JTokenType.TT_OPERATOR, "TT_OPERATOR", ">>");
    static final JTokenDef DEF_GT3 = new JTokenDef(HTokenId.GT3, "GT3", JTokenType.TT_OPERATOR, "TT_OPERATOR", ">>>");
    static final HDeclarationOptions DECLARE_CONSTR_ARG = new HDeclarationOptions()
           .setAcceptModifiers(false)
           .setAcceptVar(false).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(true).setAcceptInValue(false)
           .setAcceptVarArg(true).setAcceptMultiVars(false)
           .setNoTypeNameOption(HDeclarationOptions.NoTypeNameOption.ERROR);
    static final HDeclarationOptions DECLARE_CATCH = new HDeclarationOptions()
           .setAcceptModifiers(false)
           .setAcceptVar(true).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(false).setAcceptInValue(false)
           .setAcceptVarArg(true).setAcceptMultiVars(true).setMultiVarSeparator(HTokenId.PIPE)
           .setNoTypeNameOption(HDeclarationOptions.NoTypeNameOption.TYPE);
    static final HDeclarationOptions DECLARE_DEFAULT_CONSTR_ARG = new HDeclarationOptions()
           .setAcceptModifiers(true)
           .setAcceptVar(true).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(true).setAcceptInValue(false)
           .setAcceptVarArg(true).setAcceptMultiVars(false)
           .setNoTypeNameOption(HDeclarationOptions.NoTypeNameOption.TYPE);
    static final HDeclarationOptions DECLARE_FUNCTION_ARG = new HDeclarationOptions()
           .setAcceptModifiers(true)
           .setAcceptVar(false).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(false).setAcceptInValue(false)
           .setAcceptVarArg(true).setAcceptMultiVars(false)
           .setNoTypeNameOption(HDeclarationOptions.NoTypeNameOption.TYPE);
    static final HDeclarationOptions DECLARE_LAMBDA_ARG = new HDeclarationOptions()
           .setAcceptModifiers(false)
           .setAcceptVar(false).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(false).setAcceptInValue(false)
           .setAcceptVarArg(true).setAcceptMultiVars(false)
           .setNoTypeNameOption(HDeclarationOptions.NoTypeNameOption.NAME);
    static final HDeclarationOptions DECLARE_ASSIGN_EQ = new HDeclarationOptions().setAcceptClass(false).setAcceptFunction(false)
           .setAcceptModifiers(false)
           .setAcceptVar(true).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(true).setAcceptInValue(false)
           .setAcceptVarArg(false).setAcceptMultiVars(false)
           .setNoTypeNameOption(HDeclarationOptions.NoTypeNameOption.NAME);
    static final HDeclarationOptions DECLARE_ASSIGN_COLON = new HDeclarationOptions().setAcceptClass(false).setAcceptFunction(false)
           .setAcceptModifiers(false)
           .setAcceptVar(true).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(false).setAcceptInValue(true)
           .setAcceptVarArg(false).setAcceptMultiVars(false)
           .setNoTypeNameOption(HDeclarationOptions.NoTypeNameOption.NAME);
    static final HDeclarationOptions DECLARE_ASSIGN_EQ_OR_COLON = new HDeclarationOptions().setAcceptClass(false).setAcceptFunction(false)
           .setAcceptModifiers(false)
           .setAcceptVar(true).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(true).setAcceptInValue(true)
           .setAcceptVarArg(false).setAcceptMultiVars(false)
           .setNoTypeNameOption(HDeclarationOptions.NoTypeNameOption.NAME);
    static final HExpressionOptions hDefaultExpr = new HExpressionOptions()
                   .setBinary(new JExpressionBinaryOptions()
                           .setExcludedListOperator(true)
                           .setExcludedImplicitOperator(true)
                           .setExcludedBinaryOperators())
                   .setUnary(new JExpressionUnaryOptions()
                           .setExcludedPrefixParenthesis(false)
                           .setExcludedPrefixBrackets(true)
                           .setExcludedPrefixBraces(true)
                           .setExcludedPostfixParenthesis(false)
                           .setExcludedPostfixBrackets(false)
                           .setExcludedPostfixBraces(true)
                           .setExcludedPrefixUnaryOperators()
                           .setExcludedPostfixUnaryOperators());
    static HExpressionOptions noBracesExpressionOptions= hDefaultExpr.copy()
            .setUnary(hDefaultExpr.getUnary().copy()
                    .setExcludedPrefixBraces(true)
                    .setExcludedPostfixBraces(true)
                    .setExcludedTerminalBraces(true)
            );
    static HExpressionOptions simpleExpressionOptions=new HExpressionOptions()
            .setBinary(new JExpressionBinaryOptions()
                    .setExcludedListOperator(true)
                    .setExcludedImplicitOperator(true)
                    .setExcludedBinaryOperators(":", "->")
            )
            .setUnary(new JExpressionUnaryOptions()
                    .setExcludedPrefixParenthesis(true)
                    .setExcludedPrefixBrackets(true)
                    .setExcludedPrefixBraces(true)
                    .setExcludedPostfixParenthesis(true)
                    .setExcludedPostfixBrackets(true)
                    .setExcludedPostfixBraces(true)
                    .setExcludedPrefixUnaryOperators("++", "--")
                    .setExcludedPostfixUnaryOperators("++", "--")
            );

    static HExpressionOptions tryResourceExprOptions= hDefaultExpr.copy()
            .setUnary(hDefaultExpr.getUnary().copy()
                    .setExcludedPrefixBraces(true)
                    .setExcludedPostfixBraces(true)
                    .setExcludedTerminalBraces(true)
            ).setBinary(hDefaultExpr.getBinary().copy()
                    .setExcludedBinaryOperators("catch")
            );
    static HExpressionOptions tryBlockExprOptions= hDefaultExpr.copy()
            .setUnary(hDefaultExpr.getUnary().copy()
                    .setExcludedPrefixBraces(true)
                    .setExcludedPostfixBraces(true)
                    .setExcludedTerminalBraces(true)
            ).setBinary(hDefaultExpr.getBinary().copy()
                    .setExcludedBinaryOperators("catch")
            );
    static HExpressionOptions tryCatchDeclExprOptions= hDefaultExpr.copy()
            .setUnary(hDefaultExpr.getUnary().copy()
                    .setExcludedPrefixBraces(true)
                    .setExcludedPostfixBraces(true)
                    .setExcludedTerminalBraces(true)
            ).setBinary(hDefaultExpr.getBinary().copy()
                    .setExcludedBinaryOperators("catch")
            );
    static HExpressionOptions tryCatchBlockExprOptions= hDefaultExpr.copy()
            .setUnary(hDefaultExpr.getUnary().copy()
                    .setExcludedPrefixBraces(true)
                    .setExcludedPostfixBraces(true)
                    .setExcludedTerminalBraces(true)
            ).setBinary(hDefaultExpr.getBinary().copy()
                    .setExcludedBinaryOperators("catch")
            );
    static HExpressionOptions annotationsOptions= hDefaultExpr.copy()
            .setUnary(hDefaultExpr.getUnary().copy()
                    .setExcludedAnnotations(true)
            );
}
