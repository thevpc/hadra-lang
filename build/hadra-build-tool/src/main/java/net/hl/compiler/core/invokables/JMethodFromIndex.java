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
import java.util.function.Supplier;

public class JMethodFromIndex extends DefaultJRawMethod {
    private HIndexedMethod method;
    private HIndexer indexer;

    public JMethodFromIndex(HIndexedMethod method, JType declaringType, HIndexer indexer, JTypes types) {
        this.method = method;
        this.indexer = indexer;
        setSourceName(method.getSource());
        setDefaultMethod(false);
        setDefaultValue(null);
        setArgNames(method.getParameterNames());
        setDeclaringType(declaringType);
        setGenericReturnType(types.forName(method.getReturnType()));
        setGenericSignature(JSignature.of(declaringType.getTypes(),method.getName(),method.getParameterTypes()));
        modifiers.addSupplier(new Supplier<List<JModifier>>() {
            @Override
            public List<JModifier> get() {
                List<JModifier> all=new ArrayList<JModifier>();
                for (AnnInfo annotation : method.getAnnotations()) {
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
                for (AnnInfo annotation : method.getAnnotations()) {
                    all.add(new JAnnotationInstanceFromIndex(annotation, indexer, types));
                }
                return all;
            }
        });
    }

    @Override
    public Object invoke(JInvokeContext context) {
        throw new IllegalArgumentException("Not invokable");
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
