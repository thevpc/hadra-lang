package net.hl.compiler.core.invokables;

import net.thevpc.jeep.JCallerInfo;
import net.thevpc.jeep.JType;

public class HCallerInfo implements JCallerInfo {
    private String source;
    private JType enclosingType;

    public HCallerInfo(String source, JType enclosingType) {
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
