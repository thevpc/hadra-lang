package net.hl.compiler.parser;

import net.vpc.common.jeep.JTokenType;
import net.vpc.common.jeep.core.JExpressionBinaryOptions;
import net.vpc.common.jeep.core.JExpressionOptions;
import net.vpc.common.jeep.core.JExpressionUnaryOptions;
import net.vpc.common.jeep.core.tokens.JTokenDef;
import net.hl.compiler.core.HTokenId;

public class HLParserOptions {
    static final HLDeclarationOptions DECL_ANY_1 = new HLDeclarationOptions()
           .setAcceptInValue(false)
           .setAcceptMultiVars(true)
           .setAcceptVarArg(false)
           .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.ERROR);
    static final HLDeclarationOptions DECL_ANY_2 = new HLDeclarationOptions()
           .setAcceptInValue(false)
           .setAcceptVarArg(false)
           .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.ERROR);
    static final HLDeclarationOptions DECL_ANY_3 = new HLDeclarationOptions()
           .setAcceptInValue(false)
           .setAcceptMultiVars(true)
           .setAcceptVarArg(false)
           .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.ERROR);
    static final JTokenDef DEF_GT = new JTokenDef(HTokenId.GT, "GT", JTokenType.TT_OPERATOR, "TT_OPERATOR", ">");
    static final JTokenDef DEF_GT2 = new JTokenDef(HTokenId.GT2, "GT2", JTokenType.TT_OPERATOR, "TT_OPERATOR", ">>");
    static final JTokenDef DEF_GT3 = new JTokenDef(HTokenId.GT3, "GT3", JTokenType.TT_OPERATOR, "TT_OPERATOR", ">>>");
    static final HLDeclarationOptions DECLARE_CONSTR_ARG = new HLDeclarationOptions()
           .setAcceptModifiers(false)
           .setAcceptVar(false).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(true).setAcceptInValue(false)
           .setAcceptVarArg(true).setAcceptMultiVars(false)
           .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.ERROR);
    static final HLDeclarationOptions DECLARE_CATCH = new HLDeclarationOptions()
           .setAcceptModifiers(false)
           .setAcceptVar(true).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(false).setAcceptInValue(false)
           .setAcceptVarArg(true).setAcceptMultiVars(true).setMultiVarSeparator(HTokenId.PIPE)
           .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.TYPE);
    static final HLDeclarationOptions DECLARE_DEFAULT_CONSTR_ARG = new HLDeclarationOptions()
           .setAcceptModifiers(true)
           .setAcceptVar(true).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(true).setAcceptInValue(false)
           .setAcceptVarArg(true).setAcceptMultiVars(false)
           .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.TYPE);
    static final HLDeclarationOptions DECLARE_FUNCTION_ARG = new HLDeclarationOptions()
           .setAcceptModifiers(true)
           .setAcceptVar(false).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(false).setAcceptInValue(false)
           .setAcceptVarArg(true).setAcceptMultiVars(false)
           .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.TYPE);
    static final HLDeclarationOptions DECLARE_LAMBDA_ARG = new HLDeclarationOptions()
           .setAcceptModifiers(false)
           .setAcceptVar(false).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(false).setAcceptInValue(false)
           .setAcceptVarArg(true).setAcceptMultiVars(false)
           .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.NAME);
    static final HLDeclarationOptions DECLARE_ASSIGN_EQ = new HLDeclarationOptions().setAcceptClass(false).setAcceptFunction(false)
           .setAcceptModifiers(false)
           .setAcceptVar(true).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(true).setAcceptInValue(false)
           .setAcceptVarArg(false).setAcceptMultiVars(false)
           .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.NAME);
    static final HLDeclarationOptions DECLARE_ASSIGN_COLON = new HLDeclarationOptions().setAcceptClass(false).setAcceptFunction(false)
           .setAcceptModifiers(false)
           .setAcceptVar(true).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(false).setAcceptInValue(true)
           .setAcceptVarArg(false).setAcceptMultiVars(false)
           .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.NAME);
    static final HLDeclarationOptions DECLARE_ASSIGN_EQ_OR_COLON = new HLDeclarationOptions().setAcceptClass(false).setAcceptFunction(false)
           .setAcceptModifiers(false)
           .setAcceptVar(true).setAcceptDotName(false)
           .setAcceptFunction(false).setAcceptClass(false)
           .setAcceptEqValue(true).setAcceptInValue(true)
           .setAcceptVarArg(false).setAcceptMultiVars(false)
           .setNoTypeNameOption(HLDeclarationOptions.NoTypeNameOption.NAME);
    static final JExpressionOptions hDefaultExpr = new JExpressionOptions()
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
    static JExpressionOptions noBracesExpressionOptions= hDefaultExpr.copy()
            .setUnary(hDefaultExpr.getUnary().copy()
                    .setExcludedPrefixBraces(true)
                    .setExcludedPostfixBraces(true)
                    .setExcludedTerminalBraces(true)
            );
    static JExpressionOptions simpleExpressionOptions=new JExpressionOptions()
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

    static JExpressionOptions tryResourceExprOptions= hDefaultExpr.copy()
            .setUnary(hDefaultExpr.getUnary().copy()
                    .setExcludedPrefixBraces(true)
                    .setExcludedPostfixBraces(true)
                    .setExcludedTerminalBraces(true)
            ).setBinary(hDefaultExpr.getBinary().copy()
                    .setExcludedBinaryOperators("catch")
            );
    static JExpressionOptions tryBlockExprOptions= hDefaultExpr.copy()
            .setUnary(hDefaultExpr.getUnary().copy()
                    .setExcludedPrefixBraces(true)
                    .setExcludedPostfixBraces(true)
                    .setExcludedTerminalBraces(true)
            ).setBinary(hDefaultExpr.getBinary().copy()
                    .setExcludedBinaryOperators("catch")
            );
    static JExpressionOptions tryCatchDeclExprOptions= hDefaultExpr.copy()
            .setUnary(hDefaultExpr.getUnary().copy()
                    .setExcludedPrefixBraces(true)
                    .setExcludedPostfixBraces(true)
                    .setExcludedTerminalBraces(true)
            ).setBinary(hDefaultExpr.getBinary().copy()
                    .setExcludedBinaryOperators("catch")
            );
    static JExpressionOptions tryCatchBlockExprOptions= hDefaultExpr.copy()
            .setUnary(hDefaultExpr.getUnary().copy()
                    .setExcludedPrefixBraces(true)
                    .setExcludedPostfixBraces(true)
                    .setExcludedTerminalBraces(true)
            ).setBinary(hDefaultExpr.getBinary().copy()
                    .setExcludedBinaryOperators("catch")
            );
}
