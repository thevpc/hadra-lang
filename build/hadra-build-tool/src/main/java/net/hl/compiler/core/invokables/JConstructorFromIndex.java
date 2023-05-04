package net.hl.compiler.core.invokables;

import net.hl.compiler.index.AnnInfo;
import net.hl.compiler.index.HIndexedConstructor;
import net.hl.compiler.index.HIndexer;
import net.thevpc.jeep.*;
import net.thevpc.jeep.impl.functions.JNameSignature;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.impl.types.*;

import java.util.Arrays;

public class JConstructorFromIndex extends AbstractJConstructor {
    private HIndexedConstructor ctr;
    private HIndexer indexer;
    private JTypes types;
    private JType declaringType;

    public JConstructorFromIndex(HIndexedConstructor ctr, JType declaringType, HIndexer indexer, JTypes types) {
        this.declaringType = declaringType;
        this.ctr = ctr;
        this.indexer = indexer;
        this.types = types;
    }

    @Override
    public JType getDeclaringType() {
        return declaringType;
    }

    @Override
    public String getName() {
        return getDeclaringType().getName();
    }

    @Override
    public JModifierList getModifiers() {
        DefaultJModifierList e = new DefaultJModifierList();
        for (AnnInfo annotation : ctr.getAnnotations()) {
            if (annotation.getName().equals(annotation.getName().toLowerCase())) {
                e.add(new JPrimitiveModifier(annotation.getName()));
            }
        }
        return e;
    }

    @Override
    public JAnnotationInstanceList getAnnotations() {
        DefaultJAnnotationInstanceList a = new DefaultJAnnotationInstanceList();
        for (AnnInfo annotation : ctr.getAnnotations()) {
            a.add(new JAnnotationInstanceFromIndex(annotation, indexer, types));
        }
        return a;
    }

    @Override
    public Object invoke(JInvokeContext context) {
        throw new IllegalArgumentException("Not invokable, still source");
    }

    @Override
    public String getSourceName() {
        return ctr.getSource();
    }

    @Override
    public JTypes getTypes() {
        return types;
    }

    @Override
    public String[] getArgNames() {
        return ctr.getParameterNames();
    }

    @Override
    public JType[] getArgTypes() {
        return Arrays.stream(ctr.getParameterTypes())
                .map(x->types.forName(x))
                .toArray(JType[]::new);
    }

    @Override
    public JTypeVariable[] getTypeParameters() {
        return new JTypeVariable[0];
    }

    @Override
    public JType getReturnType() {
        return declaringType;
    }

    @Override
    public JSignature getSignature() {
        return JSignature.of(types,ctr.getSignature());
    }

    @Override
    public JDeclaration getDeclaration() {
        return null;
    }

    @Override
    public JType getGenericReturnType() {
        return super.getGenericReturnType();
    }
}
