package net.hl.ide.hl4swing;

import net.thevpc.jeep.JTokenType;
import net.thevpc.jeep.core.tokens.JTokenDef;
import net.thevpc.jeep.editor.JSyntaxKit;
import net.thevpc.jeep.editor.JSyntaxStyle;
import net.thevpc.jeep.editor.JSyntaxStyleManager;

import java.awt.*;
import net.hl.compiler.core.HTokenId;
import net.hl.compiler.core.HadraContext;
import net.hl.compiler.core.HadraLanguage;
import net.thevpc.jeep.editor.ColorResource;
import net.thevpc.nuts.NutsSession;

public class HLJSyntaxKit extends JSyntaxKit {

    private static NutsSession session;

    public HLJSyntaxKit() {
        this(null);
    }

    public HLJSyntaxKit(HadraContext jContext) {
        super();
        if (jContext == null) {
            jContext = HadraLanguage.getSingleton();
        }
        JSyntaxStyleManager styles = new JSyntaxStyleManager();
        JSyntaxStyle keywords = new JSyntaxStyle(ColorResource.of(UI_KEY_RESERVED_WORD, Color.decode("#735db7")), JSyntaxStyle.BOLD);
        JSyntaxStyle comments = new JSyntaxStyle(ColorResource.of(UI_KEY_COMMENTS, Color.DARK_GRAY), JSyntaxStyle.ITALIC);
        JSyntaxStyle strings = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_STRING, Color.decode("#2b9946")), JSyntaxStyle.BOLD);
        JSyntaxStyle numbers = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_NUMBER, Color.RED.darker()), JSyntaxStyle.PLAIN);
        JSyntaxStyle operators = new JSyntaxStyle(ColorResource.of(UI_KEY_OPERATOR, Color.cyan.darker()), JSyntaxStyle.PLAIN);
        JSyntaxStyle separators = new JSyntaxStyle(ColorResource.of(UI_KEY_SEPARATOR, Color.red), JSyntaxStyle.PLAIN);
        JSyntaxStyle regexs = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_REGEXP, Color.MAGENTA.darker()), JSyntaxStyle.PLAIN);
        JSyntaxStyle temporals = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_DATE, Color.pink.darker()), JSyntaxStyle.PLAIN);
        JSyntaxStyle primitiveTypes = new JSyntaxStyle(ColorResource.of(UI_KEY_TYPE_PRIMITIVE, Color.decode("#aa557f")), JSyntaxStyle.BOLD);
        JSyntaxStyle trueFalseLiterals = new JSyntaxStyle(ColorResource.of(UI_KEY_LITERAL_BOOLEAN, Color.decode("#f1a100")), JSyntaxStyle.BOLD);
        for (JTokenDef o : jContext.tokens().tokenDefinitions()) {
            switch (o.ttype) {
                case JTokenType.TT_KEYWORD: {
                    switch (o.idName) {
                        case "int":
                        case "void":
                        case "boolean":
                        case "char":
                        case "byte":
                        case "short":
                        case "long":
                        case "float":
                        case "double": {
                            styles.setTokenIdStyle(o.id, primitiveTypes);
                            break;
                        }
                        case "true":
                        case "false":
                        case "null": {
                            styles.setTokenIdStyle(o.id, trueFalseLiterals);
                            break;
                        }
                        default: {
                            styles.setTokenIdStyle(o.id, keywords);
                        }
                    }
                    break;
                }
                case JTokenType.TT_BLOCK_COMMENTS:
                case JTokenType.TT_LINE_COMMENTS: {
                    styles.setTokenIdStyle(o.id, comments);
                    break;
                }
                case JTokenType.TT_STRING: {
                    styles.setTokenIdStyle(o.id, strings);
                    break;
                }
                case JTokenType.TT_STRING_INTERP: {
                    switch (o.id) {
                        case HTokenId.STRING_INTERP_DOLLAR_START:
                        case HTokenId.STRING_INTERP_DOLLAR_END: {
                            styles.setTokenIdStyle(o.id, separators);
                            break;
                        }
                        case HTokenId.STRING_INTERP_START:
                        case HTokenId.STRING_INTERP_END:
                        default: {
                            styles.setTokenIdStyle(o.id, strings);
                        }
                    }
                    break;
                }
                case JTokenType.TT_NUMBER: {
                    styles.setTokenIdStyle(o.id, numbers);
                    break;
                }
                case JTokenType.TT_OPERATOR: {
                    styles.setTokenIdStyle(o.id, operators);
                    break;
                }
                case JTokenType.TT_GROUP_SEPARATOR:
                case JTokenType.TT_SEPARATOR: {
                    styles.setTokenIdStyle(o.id, separators);
                    break;
                }
                case JTokenType.TT_REGEX: {
                    styles.setTokenIdStyle(o.id, regexs);
                    break;
                }
                case JTokenType.TT_TEMPORAL: {
                    styles.setTokenIdStyle(o.id, temporals);
                    break;
                }
            }
        }
        setJcontext(jContext);
        setStyles(styles);
        //setCompletionSupplier(new HLJCompletionSupplier(jContext));
    }

}
