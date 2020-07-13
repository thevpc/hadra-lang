package net.hl.compiler.parser;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JTypeName;

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
