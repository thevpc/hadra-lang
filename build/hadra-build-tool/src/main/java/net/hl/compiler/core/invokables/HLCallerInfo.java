package net.hl.compiler.core.invokables;

import net.vpc.common.jeep.JCallerInfo;
import net.vpc.common.jeep.JType;

public class HLCallerInfo implements JCallerInfo {
    private String source;
    private JType enclosingType;

    public HLCallerInfo(String source, JType enclosingType) {
        this.source = source;
        this.enclosingType = enclosingType;
    }

    public String getSource() {
        return source;
    }

    public JType getEnclosingType() {
        return enclosingType;
    }
}
