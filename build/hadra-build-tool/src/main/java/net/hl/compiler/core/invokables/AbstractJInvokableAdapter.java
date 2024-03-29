package net.hl.compiler.core.invokables;

import net.thevpc.jeep.JInvokable;
import net.thevpc.jeep.JTypes;
import net.thevpc.jeep.impl.functions.AbstractJInvokable;

public abstract class AbstractJInvokableAdapter extends AbstractJInvokable {
    private JInvokable base;

    public AbstractJInvokableAdapter(JInvokable base) {
        this.base = base;
    }
    @Override
    public JTypes getTypes() {
        return getBase().getTypes();
    }

    public JInvokable getBase() {
        return base;
    }
}
