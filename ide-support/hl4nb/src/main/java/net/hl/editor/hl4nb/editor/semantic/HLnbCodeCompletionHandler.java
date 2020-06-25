package net.hl.editor.hl4nb.editor.semantic;

import net.vpc.common.jeep.JCompletionProposal;
import net.vpc.common.jeep.JIndexStoreMemory;
import net.hl.compiler.core.HLCompletion;
import net.hl.editor.hl4nb.HadraLanguageSingleton;
import net.hl.editor.hl4nb.editor.parser.HLnbParserResult;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.editor.BaseDocument;
import org.netbeans.modules.csl.api.*;
import org.netbeans.modules.csl.spi.DefaultCompletionResult;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Source;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.net.URL;
import java.nio.channels.CompletionHandler;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HLnbCodeCompletionHandler implements CodeCompletionHandler {
    private static final Logger LOG = Logger.getLogger(CompletionHandler.class.getName());
    private String jdkJavaDocBase = null;


    public HLnbCodeCompletionHandler() {
        JavaPlatformManager platformMan = JavaPlatformManager.getDefault();
        JavaPlatform platform = platformMan.getDefaultPlatform();
        List<URL> docfolder = platform.getJavadocFolders();

        for (URL url : docfolder) {
            LOG.log(Level.FINEST, "JDK Doc path: {0}", url.toString()); // NOI18N
            jdkJavaDocBase = url.toString();
        }
    }

    @Override
    public CodeCompletionResult complete(CodeCompletionContext completionContext) {
        HLnbParserResult parserResult = (HLnbParserResult) completionContext.getParserResult();
        String prefix = completionContext.getPrefix();


        // Documentation says that @NonNull is return from getPrefix() but it's not true
        // Invoking "this.^" makes the return value null
        if (prefix == null) {
            prefix = "";
        }

        int lexOffset = completionContext.getCaretOffset();
//        int astOffset = ASTUtils.getAstOffset(parserResult, lexOffset);
        int anchor = lexOffset - prefix.length();

//        LOG.log(Level.FINEST, "complete(...), prefix      : {0}", prefix); // NOI18N
//        LOG.log(Level.FINEST, "complete(...), lexOffset   : {0}", lexOffset); // NOI18N
//        LOG.log(Level.FINEST, "complete(...), astOffset   : {0}", astOffset); // NOI18N

        Source fileSource = parserResult.getSnapshot().getSource();
        final Document document = fileSource.getDocument(false);
        if (document == null) {
            return CodeCompletionResult.NONE;
        }
        final BaseDocument doc = (BaseDocument) document;

        doc.readLock(); // Read-lock due to Token hierarchy use

        try {
            ArrayList<CompletionProposal> list = new ArrayList<>();
            try {
                Project owner = FileOwnerQuery.getOwner(fileSource.getFileObject());
                HLCompletion completion=null;
                if(owner!=null){
                    completion=new HLCompletion(new LHnbProjectContext(
                            HadraLanguageSingleton.getIndexStoreByProject(owner),
                            owner.getProjectDirectory().getPath()
                            ));
                }else{
                    completion=new HLCompletion(new LHnbProjectContext(new JIndexStoreMemory(),fileSource.getFileObject().getPath()));
                }
                completion.setCompilationUnit(parserResult.getCompilationUnit());
                for (JCompletionProposal proposal : completion.findProposals(completionContext.getCaretOffset(), 0)) {
                    list.add(new HLnbCompletionProposalBase(proposal));
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return new DefaultCompletionResult(list, false);
        } finally {
            doc.readUnlock();
        }
    }

    @Override
    public String document(ParserResult info, ElementHandle element) {
//        LOG.log(Level.FINEST, "document(), ElementHandle : {0}", element);
        return "<h2>Some Help is welcome</h2>";
    }

    @Override
    public ElementHandle resolveLink(String link, ElementHandle originalHandle) {
        // pass the original handle back. That's better than to throw an unsupported-exception.
        return originalHandle;
    }

    @Override
    public String getPrefix(ParserResult info, int caretOffset, boolean upToOffset) {
        return null;
    }

    @Override
    public QueryType getAutoQuery(JTextComponent component, String typedText) {
        char c = typedText.charAt(0);

        if (c == '.') {
            return QueryType.COMPLETION;
        }

        return QueryType.NONE;
    }

    @Override
    public String resolveTemplateVariable(String variable, ParserResult info, int caretOffset, String name, Map parameters) {
        return null;
    }

    @Override
    public Set<String> getApplicableTemplates(Document d, int selectionBegin, int selectionEnd) {
        return Collections.emptySet();
    }

    @Override
    public ParameterInfo parameters(ParserResult info, int caretOffset, CompletionProposal proposal) {
//        LOG.log(Level.FINEST, "parameters(), caretOffset = {0}", caretOffset); // NOI18N
        return ParameterInfo.NONE;
    }

}
