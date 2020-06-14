/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.editor.hl4nb.editor;

import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import net.vpc.hadralang.editor.hl4nb.editor.lexer.HLnbLanguageHierarchy;
import net.vpc.hadralang.editor.hl4nb.editor.semantic.HLnbCodeCompletionHandler;
import net.vpc.hadralang.editor.hl4nb.editor.semantic.HLnbSemanticAnalyzer;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.api.CodeCompletionHandler;
import org.netbeans.modules.csl.api.SemanticAnalyzer;
import org.netbeans.modules.csl.spi.CommentHandler;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.indexing.PathRecognizerRegistration;
import org.openide.filesystems.MIMEResolver;
import net.vpc.hadralang.editor.hl4nb.editor.parser.*;

/**
 *
 * @author vpc
 */
@MIMEResolver.ExtensionRegistration(
        mimeType = "text/x-hl",
        displayName = "#HLResolver",
        extension = "hl",
        position = 281
)
@LanguageRegistration(
        mimeType = "text/x-hl",
        useMultiview = false
)
@PathRecognizerRegistration(
        mimeTypes = "text/x-hl",
        sourcePathIds = ClassPath.SOURCE,
        libraryPathIds = ClassPath.BOOT,
        binaryLibraryPathIds = {}
)
//@ActionReferences({
//    @ActionReference(id = @ActionID(category = "System", id = "org.openide.actions.OpenAction"), path = "Loaders/text/x-hl/Actions", position = 100),
//    @ActionReference(id = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"), path = "Loaders/text/x-hl/Actions", position = 300, separatorBefore = 200),
//    @ActionReference(id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"), path = "Loaders/text/x-hl/Actions", position = 400),
//    @ActionReference(id = @ActionID(category = "Edit", id = "org.openide.actions.PasteAction"), path = "Loaders/text/x-hl/Actions", position = 500, separatorAfter = 600),
//    @ActionReference(id = @ActionID(category = "System", id = "org.openide.actions.NewAction"), path = "Loaders/text/x-hl/Actions", position = 700),
//    @ActionReference(id = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"), path = "Loaders/text/x-hl/Actions", position = 800),
//    @ActionReference(id = @ActionID(category = "System", id = "org.openide.actions.RenameAction"), path = "Loaders/text/x-hl/Actions", position = 900, separatorAfter = 1000),
//    @ActionReference(id = @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"), path = "Loaders/text/x-hl/Actions", position = 1100, separatorAfter = 1200),
//    @ActionReference(id = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"), path = "Loaders/text/x-hl/Actions", position = 1300, separatorAfter = 1400),
//    @ActionReference(id = @ActionID(category = "System", id = "org.openide.actions.ToolsAction"), path = "Loaders/text/x-hl/Actions", position = 1500),
//    @ActionReference(id = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"), path = "Loaders/text/x-hl/Actions", position = 1600)
//})
public class HLnbLanguageConfig extends DefaultLanguageConfig {

    public static final String HADRALANG_MIME_TYPE = "text/x-hl";
    private static final Logger LOG = Logger.getLogger(HLnbLanguageConfig.class.getName());

    public HLnbLanguageConfig() {
//        LOG.info("# created " + getClass().getName());
    }

    @Override
    public Language getLexerLanguage() {
        return HLnbLanguageHierarchy.getLanguage();
    }

    @Override
    public String getDisplayName() {
        return "HadraLang"; // NOI18N
    }

    @Override
    public String getPreferredExtension() {
        return "hl"; // NOI18N
    }

    @Override
    public boolean isIdentifierChar(char c) {
        return Character.isJavaIdentifierPart(c) || (c == '$');
    }

    @Override
    public Parser getParser() {
        return new HLnbParser();
    }

    @Override
    public String getLineCommentPrefix() {
        return "//"; // NOI18N
    }

     @Override
    public Set<String> getSourcePathIds() {
        return Collections.singleton(ClassPath.SOURCE);
    }
     @Override
    public Set<String> getLibraryPathIds() {
        return java.util.Collections.singleton(ClassPath.BOOT);
    }

    @Override
    public SemanticAnalyzer getSemanticAnalyzer() {
        return new HLnbSemanticAnalyzer();
    }

    @Override
    public CodeCompletionHandler getCompletionHandler() {
        return new HLnbCodeCompletionHandler();
    }

    @Override
    public CommentHandler getCommentHandler() {
        return super.getCommentHandler();
    }
}
