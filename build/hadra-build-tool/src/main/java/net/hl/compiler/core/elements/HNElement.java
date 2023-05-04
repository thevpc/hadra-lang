package net.hl.compiler.core.elements;

import net.thevpc.jeep.*;
import net.thevpc.jeep.log.JSourceMessage;
import net.thevpc.jeep.source.JTextSource;

public abstract class HNElement {
    private HNElementKind kind;
    private JToken location;
    private JTextSource source;
    private JInvokable convertInvokable;

    public HNElement(HNElementKind kind) {
        this.kind = kind;
    }

    public JToken getLocation() {
        return location;
    }

    public HNElement setLocation(JToken location) {
        this.location = location;
        return this;
    }

    public JTextSource getSource() {
        return source;
    }

    public HNElement setSource(JTextSource source) {
        this.source = source;
        return this;
    }

    public HNElementKind getKind() {
        return kind;
    }

    public abstract JTypePattern getTypePattern();

    public JType getType() {
        JTypePattern typePattern = getTypePattern();
        return typePattern == null ? null : typePattern.getType();
    }

    @Override
    public String toString() {
        return "HNElement{" +
                "kind=" + kind +
                '}';
    }

    public String toDescString() {
        if (getLocation() != null && getSource() != null) {
            return JSourceMessage.toRangeString(getLocation(), getSource(), true);
        }
        return toString();
    }

    public void setConverterInvokable(JInvokable convertInvokable) {
        this.convertInvokable = convertInvokable;
    }

    public JInvokable getConvertInvokable() {
        return convertInvokable;
    }

    public HNElement copy() {
        try {
            return (HNElement) clone();
        } catch (CloneNotSupportedException e) {
            throw new JShouldNeverHappenException();
        }
    }
}
