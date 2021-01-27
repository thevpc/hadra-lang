package net.hl.compiler.core;

import net.thevpc.jeep.JCompletionProposal;
import net.thevpc.jeep.JTokenType;
import net.thevpc.jeep.JType;
import net.thevpc.jeep.core.editor.DefaultJCompletionProposal;
import net.thevpc.jeep.core.tokens.JTokensStringBuilder;
import net.thevpc.jeep.impl.tokens.JTokensStringFormatPlain;

public class HCompletionProposals {
    public static final int CAT_CLASS=2;
    public static final int CAT_METHOD=3;
    public static final int CAT_FIELD=4;
    public static final int CAT_CONSTRUCTOR=5;
    public static final int CAT_MODULE=6;
    public static final int CAT_PACKAGE=7;
    public static final int CAT_VARIABLE=8;
    public static final int CAT_KEYWORD=9;
    public static final int CAT_PARAMETER=10;
    public static final int CAT_SEPARATOR=11;

//    ATTRIBUTE, CONSTANT, OTHER, GLOBAL,
//    PROPERTY, ERROR, DB, CALL, TAG, RULE, FILE, TEST, INTERFACE

    static JCompletionProposal proposeBase(int category,int caretOffset, String insertPrefix, String sortPrefix,JTokensStringBuilder lhs) {
        return new DefaultJCompletionProposal(
                category, caretOffset,
                insertPrefix,
                JTokensStringFormatPlain.INSTANCE.format(lhs),
                sortPrefix + JTokensStringFormatPlain.INSTANCE.format(lhs),
                lhs,
                null,
                null
        );
    }

    static JCompletionProposal proposeMethod(int caretOffset, String name, String[] argNames, String[] argTypes) {
        JTokensStringBuilder tstring = new JTokensStringBuilder();
        tstring.addToken(0, JTokenType.TT_IDENTIFIER,CAT_METHOD,name);
        tstring.addToken(0, JTokenType.TT_SEPARATOR,0,"(");
        for (int i = 0; i < argTypes.length; i++) {
            tstring.addToken(0, JTokenType.TT_IDENTIFIER,CAT_CLASS,resolveTypeSimpleName(argTypes[i]));
            tstring.addSpace();
            tstring.addToken(0, JTokenType.TT_IDENTIFIER,CAT_PARAMETER,argNames[i]);
        }
        tstring.addToken(0, JTokenType.TT_SEPARATOR,0,")");

        return proposeBase(CAT_METHOD,caretOffset,name+"(","02_method:",tstring);
    }

    static JCompletionProposal proposeConstructor(int caretOffset, String name, String[] argNames, String[] argTypes) {
        JTokensStringBuilder tstring = new JTokensStringBuilder();
        tstring.addToken(0, JTokenType.TT_IDENTIFIER,CAT_CONSTRUCTOR,name);
        tstring.addToken(0, JTokenType.TT_SEPARATOR,0,"(");
        for (int i = 0; i < argTypes.length; i++) {
            tstring.addToken(0, JTokenType.TT_IDENTIFIER,CAT_CLASS,resolveTypeSimpleName(argTypes[i]));
            tstring.addSpace();
            tstring.addToken(0, JTokenType.TT_IDENTIFIER,CAT_PARAMETER,argNames[i]);
        }
        tstring.addToken(0, JTokenType.TT_SEPARATOR,0,")");

        return proposeBase(CAT_CONSTRUCTOR,caretOffset,name+"(","02_method:",tstring);
    }

    static JCompletionProposal proposeKeyword(int offset, String keyword) {
        return proposeBase(CAT_KEYWORD,offset,keyword,"00_keyword:",new JTokensStringBuilder().addToken(0, JTokenType.TT_KEYWORD,0,keyword));
    }

    static JCompletionProposal proposeVar(int offset, String varName) {
        return proposeBase(CAT_VARIABLE,offset,varName,"01_var:",new JTokensStringBuilder().addToken(0, JTokenType.TT_IDENTIFIER,CAT_VARIABLE,varName));
    }



    static JCompletionProposal proposeType(int offset, String jtype) {
        String typeName = jtype;
        String html = typeName;
        String namePart = typeName;
        int dot = typeName.lastIndexOf('.');
        if (dot > 0) {
            namePart = typeName.substring(dot + 1);
            html = "<html>" + namePart + "   <span style=\"color:gray\">" + typeName + "</span></html>";
        }
        return proposeBase(CAT_CLASS,offset,jtype,
                "04_type:" + namePart,
                new JTokensStringBuilder().addToken(0, JTokenType.TT_IDENTIFIER,CAT_CLASS,typeName)
                );
    }

    static JCompletionProposal proposeType(int offset, JType jtype) {
        return proposeType(offset,jtype.getName());
    }


    static String resolveTypeSimpleName(String typeName){
        String namePart = typeName;
        int dot = typeName.lastIndexOf('.');
        if (dot > 0) {
            return typeName.substring(dot + 1);
        }
        return namePart;
    }
}
