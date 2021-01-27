package net.hl.compiler.core.elements;

import net.thevpc.jeep.JInvokable;
import net.hl.compiler.ast.HNode;

public abstract class HNElementInvokable extends HNElement implements Cloneable{
    public HNElementInvokable(HNElementKind kind) {
        super(kind);
    }
    public abstract JInvokable getInvokable();
    public abstract HNode[] getArgNodes();
    public abstract HNElement setArgNodes(HNode[] argNodes);

}
