package net.hl.ide.hl4nb.editor.semantic;

import net.thevpc.jeep.JCompletionProposal;
import net.thevpc.jeep.JToken;
import net.thevpc.jeep.JTokensString;
import net.hl.compiler.core.HCompletionProposals;
import org.netbeans.modules.csl.api.ElementHandle;
import org.netbeans.modules.csl.api.ElementKind;
import org.netbeans.modules.csl.api.HtmlFormatter;
import org.netbeans.modules.csl.spi.DefaultCompletionProposal;

class HLnbCompletionProposalBase extends DefaultCompletionProposal {
    private JCompletionProposal proposal;

    public HLnbCompletionProposalBase(JCompletionProposal proposal) {
        this.proposal = proposal;
    }

    @Override
    public String getName() {
        return proposal.name();
    }


    @Override
    public int getAnchorOffset() {
        return proposal.insertOffset();
    }

    @Override
    public String getInsertPrefix() {
        return proposal.insertPrefix();
    }

    @Override
    public ElementHandle getElement() {
        return new ElementHandle.UrlHandle("www.google.com");
    }

    @Override
    public ElementKind getKind() {
        return elementKindFor(proposal.category());
    }

    @Override
    public String getLhsHtml(HtmlFormatter formatter) {
        return format(proposal.getLhsHtml(),formatter);
    }

    @Override
    public String getRhsHtml(HtmlFormatter formatter) {
        return format(proposal.getRhsHtml(),formatter);
    }

    private String format(JTokensString s,HtmlFormatter formatter){
        if(s==null){
            return null;
        }
        for (JToken token : s) {
            ElementKind kind = elementKindFor(token.catId);
            formatter.name(kind, true);
            formatter.appendText(token.image);
            formatter.name(kind, false);
        }
//        HNElement kind = getKind();
//        formatter.name(kind, true);
//        formatter.appendText(getName());
//        formatter.name(kind, false);
//
        return formatter.getText();
    }

    private ElementKind elementKindFor(int catId){
        switch (catId){
            case HCompletionProposals
                    .CAT_CLASS:return ElementKind.CLASS;
            case HCompletionProposals
                    .CAT_METHOD:return ElementKind.METHOD;
            case HCompletionProposals
                    .CAT_CONSTRUCTOR:return ElementKind.CONSTRUCTOR;
            case HCompletionProposals
                    .CAT_FIELD:return ElementKind.FIELD;
            case HCompletionProposals
                    .CAT_KEYWORD:return ElementKind.KEYWORD;
            case HCompletionProposals
                    .CAT_MODULE:return ElementKind.MODULE;
            case HCompletionProposals
                    .CAT_PACKAGE:return ElementKind.PACKAGE;
            case HCompletionProposals
                    .CAT_VARIABLE:return ElementKind.VARIABLE;
            case HCompletionProposals
                    .CAT_SEPARATOR:return ElementKind.GLOBAL;
            default:return ElementKind.GLOBAL;
        }
    }
}
