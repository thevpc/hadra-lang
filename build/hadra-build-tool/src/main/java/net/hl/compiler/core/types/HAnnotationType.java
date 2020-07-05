package net.hl.compiler.core.types;

import net.vpc.common.jeep.JTypeKind;
import net.vpc.common.jeep.JTypes;
import net.vpc.common.jeep.impl.types.DefaultJAnnotationInstanceList;
import net.vpc.common.jeep.impl.types.DefaultJAnnotationType;
import net.vpc.common.jeep.impl.types.DefaultJEnumType;

import java.lang.annotation.Annotation;

public class HAnnotationType extends DefaultJAnnotationType {

    public HAnnotationType(String name, JTypeKind kind, JTypes types) {
        super(name, kind, types);
    }
}
