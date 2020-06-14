package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.JType;

public interface HNDeclareTokenBase {
    JType getIdentifierType();
    String getName();
    JToken getToken();

}
