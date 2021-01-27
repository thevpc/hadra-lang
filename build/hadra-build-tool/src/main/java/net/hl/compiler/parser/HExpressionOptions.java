package net.hl.compiler.parser;

import net.thevpc.jeep.core.JExpressionOptions;

import java.util.HashSet;
import java.util.Set;

public class HExpressionOptions extends JExpressionOptions<HExpressionOptions> {
    private boolean acceptModifiersAsAnnotation = true;
    private Set<String> excludedModifiersAsAnnotation = new HashSet<>();

    public boolean isAcceptModifiersAsAnnotation() {
        return acceptModifiersAsAnnotation;
    }

    public HExpressionOptions setAcceptModifiersAsAnnotation(boolean acceptModifiersAsAnnotation) {
        this.acceptModifiersAsAnnotation = acceptModifiersAsAnnotation;
        return this;
    }

    public HExpressionOptions copy() {
        HExpressionOptions e=(HExpressionOptions) super.copy();
        e.excludedModifiersAsAnnotation=excludedModifiersAsAnnotation==null?null:new HashSet<>(excludedModifiersAsAnnotation);
        return e;
    }
}
