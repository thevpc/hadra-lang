package net.hl.compiler.core.types;

import net.thevpc.jeep.impl.types.DefaultJConstructor;

public class HConstructor extends DefaultJConstructor {
    public HConstructor() {
    }

    @Override
    public boolean isPublic() {
        return getAnnotations().contains(HType.PUBLIC);
    }
}
