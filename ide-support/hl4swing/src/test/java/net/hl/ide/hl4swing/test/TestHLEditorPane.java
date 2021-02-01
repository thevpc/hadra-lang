package net.hl.ide.hl4swing.test;

import net.thevpc.common.textsource.JTextSourceFactory;
import net.thevpc.jeep.editor.JEditorPaneBuilder;
import net.thevpc.jeep.editor.JSyntaxUtils;
import net.thevpc.jeep.editor.comp.JSyntaxPosLabel;
import net.hl.compiler.core.DefaultHLProjectContext;
import net.hl.compiler.core.HCompletion;
import net.hl.compiler.core.HadraLanguage;
import net.hl.compiler.index.HIndexerImpl;
import net.hl.ide.hl4swing.HLJSyntaxKit;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class TestHLEditorPane {

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        JFrame f = new JFrame("Test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JEditorPaneBuilder editorBuilder = new JEditorPaneBuilder();
        HadraLanguage jContext = HadraLanguage.getSingleton();
        DefaultHLProjectContext projectContext = new DefaultHLProjectContext(jContext, new HIndexerImpl(), null);

        HCompletion completion = new HCompletion(projectContext);
        JEditorPane e = editorBuilder.editor();
        JComponent editor = editorBuilder
                .addAutoComplete(completion)
                .addLineNumbers()
                .footer()
                .add(new JSyntaxPosLabel(e, completion))
                .addGlue()
                .addCaret()
                .end()
                .setEditorKit(HadraLanguage.MIME_TYPE, new HLJSyntaxKit(jContext))
                .component();
        ;
        f.setContentPane(editor);
        f.setMinimumSize(new Dimension(800, 600));
        f.pack();
        f.setVisible(true);

        URL resource = TestHLEditorPane.class.getResource("Example.hl");
//        JSyntaxUtils.setText(editorBuilder.editor(),JTextSourceFactory.fromURL(resource));
        JSyntaxUtils.setText(editorBuilder.editor(), JTextSourceFactory.fromString("", "<text>"));

//        long allTime = 0;
//        int count = 0;
//        for (int i = 0; i < text.length(); i++) {
//            Runtime.getRuntime().gc();
//            Chrono ch = Chrono.start("");
//            String subText = text.substring(0, i);
//            editorBuilder.editor().setText(subText);
//            ch.stop();
//            allTime += ch.nanos();
//            count++;
//            System.out.println("Compilation in " + String.valueOf(ch.nanos() / 1000000000.0) + "; average:" + String.valueOf(1.0* allTime / count / 1000000000.0));
//        }
    }
}
