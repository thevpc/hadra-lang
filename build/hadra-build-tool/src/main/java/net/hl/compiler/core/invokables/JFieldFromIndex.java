package net.hl.compiler.core.invokables;

import net.hl.compiler.index.*;
import net.thevpc.jeep.*;
import net.thevpc.jeep.core.types.AbstractJField;
import net.thevpc.jeep.impl.types.*;

import java.util.Arrays;

public class JFieldFromIndex extends AbstractJField {
    private final HIndexedField field;
    private final HIndexer indexer;
    private final JTypes types;
    private final JType declaringType;

    public JFieldFromIndex(HIndexedField field, JType declaringType,HIndexer indexer, JTypes types) {
        this.field = field;
        this.indexer = indexer;
        this.types = types;
        this.declaringType = declaringType;
    }

    @Override
    public String name() {
        return field.getName();
    }

    @Override
    public JType type() {
        return types.forNameOrNull(field.getType());
    }

    @Override
    public Object get(Object instance) {
        throw new IllegalArgumentException("unsupported");
    }

    @Override
    public void set(Object instance, Object value) {
        throw new IllegalArgumentException("unsupported");
    }

    @Override
    public boolean isPublic() {
        return Arrays.stream(field.getAnnotations()).anyMatch(x -> x.getName().equals("public"));
    }

    @Override
    public boolean isStatic() {
        return Arrays.stream(field.getAnnotations()).anyMatch(x -> x.getName().equals("static"));
    }

    @Override
    public boolean isFinal() {
        return Arrays.stream(field.getAnnotations()).anyMatch(x -> x.getName().equals("final"));
    }

    @Override
    public JType getDeclaringType() {
        return declaringType;
    }

    @Override
    public JAnnotationInstanceList getAnnotations() {
        DefaultJAnnotationInstanceList a=new DefaultJAnnotationInstanceList();
        for (AnnInfo annotation : field.getAnnotations()) {
            a.add(new JAnnotationInstanceFromIndex(annotation,indexer,types));
        }
        return a;
    }

    @Override
    public JModifierList getModifiers() {
        DefaultJModifierList e = new DefaultJModifierList();
        for (AnnInfo annotation : field.getAnnotations()) {
            if (annotation.getName().equals(annotation.getName().toLowerCase())) {
                e.add(new JPrimitiveModifier(annotation.getName()));
            }
        }
        return e;
    }

    @Override
    public JTypes getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return "JFieldFromHIndexedField{" +
                "fullName=" + field +
                '}';
    }

}
