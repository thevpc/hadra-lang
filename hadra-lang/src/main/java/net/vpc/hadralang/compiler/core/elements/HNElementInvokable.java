package net.vpc.hadralang.compiler.core.elements;

import net.vpc.common.jeep.JInvokable;
import net.vpc.common.jeep.JNode;
import net.vpc.hadralang.compiler.parser.ast.HNode;

public abstract class HNElementInvokable extends HNElement implements Cloneable{
    public HNElementInvokable(HNElementKind kind) {
        super(kind);
    }
    public abstract JInvokable getInvokable();
    public abstract HNode[] getArgNodes();
    public abstract HNElement setArgNodes(HNode[] argNodes);

}
