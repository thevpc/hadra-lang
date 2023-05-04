package net.hl.compiler.core.invokables;

import net.hl.compiler.index.AnnValue;
import net.thevpc.jeep.JAnnotationField;
import net.thevpc.jeep.JAnnotationInstanceField;
import net.thevpc.jeep.JFixMeLaterException;

public class JAnnotationInstanceFieldFromIndex implements JAnnotationInstanceField {
    private String name;
    private AnnValue value;

    public JAnnotationInstanceFieldFromIndex(String name, AnnValue value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public JAnnotationField getAnnotationField() {
        throw new JFixMeLaterException("JAnnotationInstanceFieldFromIndex::getAnnotationField()");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue() {
        return this.value.getValue();
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }
}
