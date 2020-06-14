/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.JToken;

import java.util.Arrays;
import java.util.List;
import net.vpc.common.jeep.JNodeCopyFactory;
import net.vpc.common.jeep.util.JNodeUtils;
import net.vpc.hadralang.compiler.core.HTokenId;

/**
 * @author vpc
 */
public class HNStringInterop extends HNode {
    private JToken[] tokens;
    private JNode[] expressions;

    private HNStringInterop() {
        super(HNNodeId.H_STRING_INTEROP);
    }

    public HNStringInterop(JToken[] stringTokens, JNode[] expressions, JToken startToken, JToken endToken) {
        this();
        setTokens(stringTokens);
        setExpressions(expressions);
        setStartToken(startToken);
        setEndToken(endToken);
    }

    public JToken[] getTokens() {
        return tokens;
    }

    public HNStringInterop setTokens(JToken[] tokens) {
        this.tokens = tokens;
        return this;
    }

    public JNode[] getExpressions() {
        return expressions;
    }

    public HNStringInterop setExpressions(JNode[] expressions) {
        this.expressions = JNodeUtils.bind(this,expressions, "expressions");
        return this;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean format) {
        StringBuilder sb = new StringBuilder();
        int exprIndex = 0;
        for (JToken token : tokens) {
            switch (token.def.id) {
                case HTokenId.STRING_INTERP_START: {
                    if (format) {
                        sb.append('"');
                    } else {
                        sb.append(token.image);
                    }
                    break;
                }
                case HTokenId.STRING_INTERP_TEXT: {
                    sb.append(JToken.escapeString(token.sval));
                    break;
                }
                case HTokenId.STRING_INTERP_END: {
                    sb.append(token.image);
                    break;
                }
                case HTokenId.STRING_INTERP_DOLLAR_START: {
                    if (format) {
                        sb.append("{");
                        sb.append(exprIndex++);
                        sb.append("}");
                    } else {
                        sb.append(expressions[exprIndex++]);
                    }
                    break;
                }
                default: {
                    sb.append(token.image);
                }
            }
        }
        return sb.toString();
    }

    public void copyFrom(JNode node,JNodeCopyFactory copyFactory) {
        super.copyFrom(node,copyFactory);
        if (node instanceof HNStringInterop) {
            HNStringInterop o = (HNStringInterop) node;
            this.expressions = JNodeUtils.bindCopy(this, copyFactory, o.expressions);
            this.tokens = o.tokens;
        }
    }

    @Override
    public List<JNode> childrenNodes() {
        return Arrays.asList(expressions);
    }

    @Override
    protected void findAndReplaceChildren(JNodeFindAndReplace findAndReplace) {
    }

    public String getJavaMessageFormatString() {
        StringBuilder text = new StringBuilder();
        int varIndex = 0;
        for (JToken token : tokens) {
            switch (token.def.id) {
                case HTokenId.STRING_INTERP_TEXT: {
                    //escape format
                    for (char c : token.sval.toCharArray()) {
                        switch (c) {
                            case '\'': {
                                text.append("''");
                                break;
                            }
                            case '{': {
                                text.append("'{'");
                                break;
                            }
                            default: {
                                text.append(c);
                            }
                        }
                    }
                    break;
                }
                case HTokenId.STRING_INTERP_START:
                case HTokenId.STRING_INTERP_END:
                case HTokenId.STRING_INTERP_DOLLAR_END:
                case HTokenId.IDENTIFIER:{
                    break;
                }
                case HTokenId.STRING_INTERP_DOLLAR_START: {
                    text.append("{").append(varIndex++).append("}");
                    break;
                }
            }
        }
        return text.toString();
    }
}
