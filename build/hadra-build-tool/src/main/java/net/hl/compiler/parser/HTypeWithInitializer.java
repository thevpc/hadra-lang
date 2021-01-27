package net.hl.compiler.parser;

import net.thevpc.jeep.JNode;
import net.thevpc.jeep.JTypeName;

public class HTypeWithInitializer {
    boolean varArgs;
    boolean visitedVarVal = false;
    boolean visitedVoid = false;
    boolean visitedParams = false;
    boolean visitedBrackets = false;
    JTypeName type = null;
    JNode[] init = null;

    public HTypeWithInitializer() {
    }
}
