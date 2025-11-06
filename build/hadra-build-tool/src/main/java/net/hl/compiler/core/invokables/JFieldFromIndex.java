package net.hl.compiler.core.invokables;

import net.hl.compiler.index.*;
import net.thevpc.jeep.*;
import net.thevpc.jeep.core.types.AbstractJField;
import net.thevpc.jeep.core.types.DefaultJField;
import net.thevpc.jeep.impl.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class JFieldFromIndex extends DefaultJField {
    private final HIndexedField field;
    private final HIndexer indexer;

    public JFieldFromIndex(HIndexedField field, JType declaringType,HIndexer indexer, JTypes types) {
        this.field = field;
        this.indexer = indexer;
        setDeclaringType(declaringType);
//        setSource(field.getSource());

        modifiers.addSupplier(new Supplier<List<JModifier>>() {
            @Override
            public List<JModifier> get() {
                List<JModifier> all=new ArrayList<JModifier>();
                for (AnnInfo annotation : field.getAnnotations()) {
                    if (annotation.getName().equals(annotation.getName().toLowerCase())) {
                        all.add(new JPrimitiveModifier(annotation.getName()));
                    }
                }
                return all;
            }
        });
        annotations.addSupplier(new  Supplier<List<JAnnotationInstance>>() {
            @Override
            public List<JAnnotationInstance> get() {
                List<JAnnotationInstance> all=new ArrayList<>();
                for (AnnInfo annotation : field.getAnnotations()) {
                    all.add(new JAnnotationInstanceFromIndex(annotation, indexer, types));
                }
                return all;
            }
        });
        setName(field.getName());
        setGenericType(types.forNameOrNull(field.getType()));
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

}
