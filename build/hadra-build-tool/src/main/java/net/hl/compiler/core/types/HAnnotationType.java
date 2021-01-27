package net.hl.compiler.core.types;

import net.thevpc.jeep.JTypeKind;
import net.thevpc.jeep.JTypes;
import net.thevpc.jeep.impl.types.DefaultJAnnotationInstanceList;
import net.thevpc.jeep.impl.types.DefaultJAnnotationType;
import net.thevpc.jeep.impl.types.DefaultJEnumType;

import java.lang.annotation.Annotation;

public class HAnnotationType extends DefaultJAnnotationType {

    public HAnnotationType(String name, JTypeKind kind, JTypes types) {
        super(name, kind, types);
    }
}
