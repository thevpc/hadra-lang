package net.hl.compiler.core.types;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.types.DefaultJObject;
import net.vpc.common.jeep.impl.functions.JSignature;
import net.vpc.common.jeep.impl.types.*;
import net.vpc.common.jeep.util.JTypeUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class HEnumType extends DefaultJEnumType {

    public HEnumType(String name, JTypeKind kind, JTypes types) {
        super(name, kind, types);
    }
}
