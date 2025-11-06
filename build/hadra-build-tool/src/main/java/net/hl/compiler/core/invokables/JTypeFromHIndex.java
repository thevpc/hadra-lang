package net.hl.compiler.core.invokables;

import net.hl.compiler.index.*;
import net.thevpc.jeep.*;
import net.thevpc.jeep.core.JStaticObject;
import net.thevpc.jeep.core.types.DefaultJField;
import net.thevpc.jeep.impl.JTypesSPI;
import net.thevpc.jeep.impl.types.DefaultJModifierList;
import net.thevpc.jeep.impl.types.DefaultJType;
import net.thevpc.jeep.impl.types.JAnnotationInstanceList;
import net.thevpc.jeep.impl.types.JModifierList;
import net.thevpc.jeep.impl.types.host.AbstractJType;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JTypeFromHIndex extends DefaultJType {
    private final HIndexer indexer;
    private final HIndexedClass hType;

    public JTypeFromHIndex(HIndexedClass hType, JType declaringType, HIndexer indexer, JTypes types) {
        super(hType.getFullName(),JTypeKind.CLASS, declaringType,types);
        this.hType = hType;
        this.indexer = indexer;
        setSourceName(hType.getSource());
        addOnPostRegister(new Runnable() {
            @Override
            public void run() {
                List<JType> superTypes = Arrays.stream(hType.getSuperTypes())
                        .map(types::forName)
                        .collect(Collectors.toList());
                if(!superTypes.isEmpty()){
                    setSuperType(superTypes.get(0));
                }
            }
        });
        interfaces.addSupplier(new Supplier<List<JType>>() {
            @Override
            public List<JType> get() {
                List<JType> superTypes = Arrays.stream(hType.getSuperTypes())
                        .map(types::forName)
                        .collect(Collectors.toList());
                if(!superTypes.isEmpty()){
                    if(superTypes.get(0).isInterface()){
                        return superTypes;
                    }
                    superTypes.remove(0);
                }
                return superTypes;
            }
        });
        constructors.addSupplier(new Supplier<List<JConstructor>>() {
            @Override
            public List<JConstructor> get() {
                Set<HIndexedConstructor> hIndexedMethod = indexer.searchConstructors(getName());
                List<JConstructor> all = new ArrayList<>();
                for (HIndexedConstructor m : hIndexedMethod) {
                    all.add(new JConstructorFromIndex(m, JTypeFromHIndex.this, indexer, types));
                }
                return all;
            }
        });
        fields.addSupplier(new Supplier<List<JField>>() {
            @Override
            public List<JField> get() {
                List<JField> fields = new ArrayList<>();
                for (HIndexedField fullName : indexer.searchFields(getName(), null, false)) {
                    JFieldFromIndex a = new JFieldFromIndex(fullName, JTypeFromHIndex.this, indexer, getTypes());
                    fields.add(a);
                }
                return fields;
            }
        });
        methods.addSupplier(new Supplier<List<JMethod>>() {
            @Override
            public List<JMethod> get() {
                Set<HIndexedMethod> hIndexedMethod = indexer.searchMethods(getName(), null, true);
                List<JMethod> all = new ArrayList<>();
                for (HIndexedMethod m : hIndexedMethod) {
                    all.add(new JMethodFromIndex(m, JTypeFromHIndex.this, indexer, types));
                }
                return all;
            }
        });
        modifiers.addSupplier(new Supplier<List<JModifier>>() {
            @Override
            public List<JModifier> get() {
                List<JModifier> e=new ArrayList<>();
                for (AnnInfo annotation : hType.getAnnotations()) {
                    if (annotation.getName().equals(annotation.getName().toLowerCase())) {
                        e.add(new JPrimitiveModifier(annotation.getName()));
                    }
                }
                return e;
            }
        });
        annotations.addSupplier(new  Supplier<List<JAnnotationInstance>>() {
            @Override
            public List<JAnnotationInstance> get() {
                List<JAnnotationInstance> a=new ArrayList<>();
                for (AnnInfo annotation : hType.getAnnotations()) {
                    a.add(new JAnnotationInstanceFromIndex(annotation, indexer, types));
                }
                return a;
            }
        });
        addExports(hType.getExports());
    }

    @Override
    public boolean isPublic() {
        return Arrays.stream(hType.getAnnotations()).anyMatch(x -> x.getName().equals("public"));
    }

    @Override
    public boolean isStatic() {
        return Arrays.stream(hType.getAnnotations()).anyMatch(x -> x.getName().equals("static"));
    }

    @Override
    public String[] getExports() {
        return hType.getExports();
    }

    @Override
    public boolean isInterface() {
        return Arrays.stream(hType.getAnnotations()).anyMatch(x -> x.getName().equals("annotation"));
    }

}
