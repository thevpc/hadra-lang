package net.hl.compiler.ast;

import net.thevpc.jeep.JToken;
import net.thevpc.jeep.JType;

public interface HNDeclareTokenBase {
    JType getIdentifierType();
    String getName();
    JToken getToken();

}
