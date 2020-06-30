package net.hl.compiler.parser;

import net.vpc.common.jeep.core.JExpressionOptions;

import java.util.HashSet;
import java.util.Set;

public class HLExpressionOptions extends JExpressionOptions<HLExpressionOptions> {
    private boolean acceptModifiersAsAnnotation = true;
    private Set<String> excludedModifiersAsAnnotation = new HashSet<>();

    public boolean isAcceptModifiersAsAnnotation() {
        return acceptModifiersAsAnnotation;
    }

    public HLExpressionOptions setAcceptModifiersAsAnnotation(boolean acceptModifiersAsAnnotation) {
        this.acceptModifiersAsAnnotation = acceptModifiersAsAnnotation;
        return this;
    }

    public HLExpressionOptions copy() {
        HLExpressionOptions e=(HLExpressionOptions) super.copy();
        e.excludedModifiersAsAnnotation=excludedModifiersAsAnnotation==null?null:new HashSet<>(excludedModifiersAsAnnotation);
        return e;
    }
}
