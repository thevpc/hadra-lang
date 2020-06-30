package net.hl.compiler.core.types;

import net.vpc.common.jeep.JAnnotationInstance;
import net.vpc.common.jeep.JAnnotationInstanceField;

import java.util.Objects;

public class JPrimitiveModifierAnnotationInstance implements JAnnotationInstance {
    public static final JPrimitiveModifierAnnotationInstance STRICTFP = new JPrimitiveModifierAnnotationInstance("strictfp");
    public static final JPrimitiveModifierAnnotationInstance TRANSIENT = new JPrimitiveModifierAnnotationInstance("transient");
    public static final JPrimitiveModifierAnnotationInstance VOLATILE = new JPrimitiveModifierAnnotationInstance("volatile");
    public static final JPrimitiveModifierAnnotationInstance SYNCHRONIZED = new JPrimitiveModifierAnnotationInstance("synchronized");
    public static final JPrimitiveModifierAnnotationInstance STATIC = new JPrimitiveModifierAnnotationInstance("static");
    public static final JPrimitiveModifierAnnotationInstance PUBLIC = new JPrimitiveModifierAnnotationInstance("public");
    public static final JPrimitiveModifierAnnotationInstance PROTECTED = new JPrimitiveModifierAnnotationInstance("protected");
    public static final JPrimitiveModifierAnnotationInstance PRIVATE = new JPrimitiveModifierAnnotationInstance("private");
    public static final JPrimitiveModifierAnnotationInstance NATIVE = new JPrimitiveModifierAnnotationInstance("native");
    public static final JPrimitiveModifierAnnotationInstance INTERFACE = new JPrimitiveModifierAnnotationInstance("interface");
    public static final JPrimitiveModifierAnnotationInstance FINAL = new JPrimitiveModifierAnnotationInstance("final");
    public static final JPrimitiveModifierAnnotationInstance ABSTRACT = new JPrimitiveModifierAnnotationInstance("abstract");
    public static final JPrimitiveModifierAnnotationInstance ENUM = new JPrimitiveModifierAnnotationInstance("enum");
    public static final JPrimitiveModifierAnnotationInstance EXCEPTION = new JPrimitiveModifierAnnotationInstance("exception");
    public static final JPrimitiveModifierAnnotationInstance CONST = new JPrimitiveModifierAnnotationInstance("const");
    private String name;
    public JPrimitiveModifierAnnotationInstance(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JPrimitiveModifierAnnotationInstance that = (JPrimitiveModifierAnnotationInstance) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

    @Override
    public JAnnotationInstanceField[] getFields() {
        return new JAnnotationInstanceField[0];
    }
}
