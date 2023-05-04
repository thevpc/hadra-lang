package net.hl.compiler.core.invokables;

import net.hl.compiler.index.AnnInfo;
import net.hl.compiler.index.HIndexedClass;
import net.hl.compiler.index.HIndexedMethod;
import net.hl.compiler.index.HIndexer;
import net.thevpc.jeep.*;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.impl.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JMethodFromIndex extends AbstractJMethod {
    private HIndexedMethod method;
    private HIndexer indexer;
    private JTypes types;
    private JType declaringType;

    public JMethodFromIndex(HIndexedMethod method, JType declaringType, HIndexer indexer, JTypes types) {
        this.method = method;
        this.indexer = indexer;
        this.types = types;
        this.declaringType = declaringType;
    }

    @Override
    public JTypes getTypes() {
        return types;
    }

    @Override
    public JType getDeclaringType() {
        return declaringType;
    }

    @Override
    public JType[] getArgTypes() {
        String[] ptypes = method.getParameterTypes();
        List<JType> all = new ArrayList<>();
        for (String jType : ptypes) {
            all.add(types.forName(jType));
        }
        return all.toArray(new JType[0]);
    }

    @Override
    public String[] getArgNames() {
        return method.getParameterNames();
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public JTypeVariable[] getTypeParameters() {
        return new JTypeVariable[0];
    }

    @Override
    public JMethod parametrize(JType... parameters) {
        throw new JFixMeLaterException("JMethodFromIndex::parametrize");
    }

    @Override
    public Object invoke(JInvokeContext context) {
        throw new IllegalArgumentException("Not invokable");
    }

    @Override
    public JSignature getSignature() {
        return JSignature.of(types, method.getSignature());
    }

    @Override
    public JType getReturnType() {
        return types.forName(method.getReturnType());
    }

    @Override
    public JModifierList getModifiers() {
        DefaultJModifierList e = new DefaultJModifierList();
        for (AnnInfo annotation : method.getAnnotations()) {
            if (annotation.getName().equals(annotation.getName().toLowerCase())) {
                e.add(new JPrimitiveModifier(annotation.getName()));
            }
        }
        return e;
    }

    @Override
    public JAnnotationInstanceList getAnnotations() {
        DefaultJAnnotationInstanceList a = new DefaultJAnnotationInstanceList();
        for (AnnInfo annotation : method.getAnnotations()) {
            a.add(new JAnnotationInstanceFromIndex(annotation, indexer, types));
        }
        return a;
    }

    @Override
    public String getSourceName() {
        return null;
    }

    @Override
    public boolean isPublic() {
        return Arrays.stream(method.getAnnotations()).anyMatch(x -> x.getName().equals("public"));
    }

    @Override
    public boolean isAbstract() {
        return Arrays.stream(method.getAnnotations()).anyMatch(x -> x.getName().equals("abstract"));
    }

    @Override
    public boolean isStatic() {
        return Arrays.stream(method.getAnnotations()).anyMatch(x -> x.getName().equals("static"));
    }
}
