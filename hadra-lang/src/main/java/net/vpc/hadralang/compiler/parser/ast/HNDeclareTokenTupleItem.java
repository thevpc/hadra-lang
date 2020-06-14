package net.vpc.hadralang.compiler.parser.ast;

import net.vpc.common.jeep.JNode;
import net.vpc.common.jeep.JNodeFindAndReplace;
import net.vpc.common.jeep.JShouldNeverHappenException;
import net.vpc.common.jeep.JToken;
import net.vpc.common.jeep.util.JNodeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class HNDeclareTokenTupleItem extends HNDeclareToken {

    public HNDeclareTokenTupleItem(HNNodeId id) {
        super(id);
    }

}
