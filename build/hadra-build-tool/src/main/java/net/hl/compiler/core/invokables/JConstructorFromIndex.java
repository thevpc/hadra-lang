package net.hl.compiler.core.invokables;

import net.hl.compiler.index.AnnInfo;
import net.hl.compiler.index.HIndexedConstructor;
import net.hl.compiler.index.HIndexer;
import net.thevpc.jeep.*;
import net.thevpc.jeep.impl.functions.JNameSignature;
import net.thevpc.jeep.impl.functions.JSignature;
import net.thevpc.jeep.impl.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class JConstructorFromIndex extends DefaultJConstructor {
    private HIndexedConstructor ctr;
    private HIndexer indexer;

    public JConstructorFromIndex(HIndexedConstructor ctr, JType declaringType, HIndexer indexer, JTypes types) {
        setDeclaringType(declaringType);
        setSourceName(ctr.getSource());
        setArgNames(ctr.getParameterNames());
        this.ctr = ctr;
        this.indexer = indexer;
        modifiers.addSupplier(new Supplier<List<JModifier>>() {
            @Override
            public List<JModifier> get() {
                List<JModifier> all=new ArrayList<JModifier>();
                for (AnnInfo annotation : ctr.getAnnotations()) {
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
                for (AnnInfo annotation : ctr.getAnnotations()) {
                    all.add(new JAnnotationInstanceFromIndex(annotation, indexer, types));
                }
                return all;
            }
        });
        setGenericSignature(JSignature.of(declaringType.getTypes(),"",ctr.getParameterTypes()));
    }


    @Override
    public Object invoke(JInvokeContext context) {
        throw new IllegalArgumentException("Not invokable, still source");
    }
}
