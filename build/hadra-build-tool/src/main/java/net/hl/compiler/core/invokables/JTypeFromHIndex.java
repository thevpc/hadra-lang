package net.hl.compiler.core.invokables;

import net.hl.compiler.index.*;
import net.thevpc.jeep.*;
import net.thevpc.jeep.core.JStaticObject;
import net.thevpc.jeep.core.types.DefaultJField;
import net.thevpc.jeep.impl.JTypesSPI;
import net.thevpc.jeep.impl.types.DefaultJAnnotationInstanceList;
import net.thevpc.jeep.impl.types.DefaultJModifierList;
import net.thevpc.jeep.impl.types.JAnnotationInstanceList;
import net.thevpc.jeep.impl.types.JModifierList;
import net.thevpc.jeep.impl.types.host.AbstractJType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JTypeFromHIndex extends AbstractJType {
    private final HIndexer indexer;
    private final HIndexedClass hType;
    private final JTypes types;
    private List<JField> fields;
    private List<JMethod> methods;
    private List<JConstructor> constructors;
    private JType declaringType;

    public JTypeFromHIndex(HIndexedClass hType, JType declaringType, HIndexer indexer, JTypes types) {
        super(types);
        this.hType = hType;
        this.indexer = indexer;
        this.types = types;
        this.declaringType = declaringType;
    }

    @Override
    public JTypeVariable[] getTypeParameters() {
        return new JTypeVariable[0];
    }

    @Override
    public JType getRawType() {
        return this;
    }

    @Override
    public JStaticObject getStaticObject() {
        return null;
    }

    @Override
    public String getName() {
        return hType.getFullName();
    }

    @Override
    public String simpleName() {
        return hType.getSimpleName();
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
    public boolean isNullable() {
        return true;
    }

    @Override
    public JType toArray(int count) {
        return JTypesSPI.getRegisteredOrRegister(
                types2().createArrayType0(this,count)
                , getTypes());
    }

    @Override
    public JType getSuperType() {
        List<JType> superTypes = Arrays.stream(hType.getSuperTypes())
                .map(types::forName)
                .collect(Collectors.toList());
        if(superTypes.isEmpty()){
            return null;
        }
        return superTypes.get(0);
    }

    @Override
    public JType[] getInterfaces() {
        List<JType> superTypes = Arrays.stream(hType.getSuperTypes())
                .map(types::forName)
                .collect(Collectors.toList());
        if(!superTypes.isEmpty()){
            if(superTypes.get(0).isInterface()){
                return superTypes.toArray(new JType[0]);
            }
            superTypes.remove(0);
        }
        return superTypes.toArray(new JType[0]);
    }

    @Override
    public JConstructor[] getDeclaredConstructors() {
        if (constructors == null) {
            Set<HIndexedConstructor> hIndexedMethod = indexer.searchConstructors(getName());
            List<JConstructor> all = new ArrayList<>();
            for (HIndexedConstructor m : hIndexedMethod) {
                all.add(new JConstructorFromIndex(m, this, indexer, types));
            }
            this.constructors = all;
        }
        return constructors.toArray(new JConstructor[0]);
    }

    @Override
    public JField[] getDeclaredFields() {
        if (fields == null) {
            List<JField> fields = new ArrayList<>();
            for (HIndexedField fullName : indexer.searchFields(getName(), null, false)) {
                JFieldFromIndex a = new JFieldFromIndex(fullName, this, indexer, getTypes());
                fields.add(a);
            }
            this.fields = fields;
        }
        return fields.toArray(new JField[0]);
    }

    @Override
    public JMethod[] getDeclaredMethods() {
        if (methods == null) {
            Set<HIndexedMethod> hIndexedMethod = indexer.searchMethods(getName(), null, true);
            List<JMethod> all = new ArrayList<>();
            for (HIndexedMethod m : hIndexedMethod) {
                all.add(new JMethodFromIndex(m, this, indexer, types));
            }
            this.methods = all;
        }
        return methods.toArray(new JMethod[0]);
    }

    @Override
    public JType[] getDeclaredInnerTypes() {
        return new JType[0];
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public JType getDeclaringType() {
        return declaringType;
    }

    @Override
    public String getPackageName() {
        return hType.getPackageName();
    }

    @Override
    public String[] getExports() {
        return hType.getExports();
    }

    @Override
    public boolean isInterface() {
        return Arrays.stream(hType.getAnnotations()).anyMatch(x -> x.getName().equals("annotation"));
    }

    @Override
    public JModifierList getModifiers() {
        DefaultJModifierList e = new DefaultJModifierList();
        for (AnnInfo annotation : hType.getAnnotations()) {
            if (annotation.getName().equals(annotation.getName().toLowerCase())) {
                e.add(new JPrimitiveModifier(annotation.getName()));
            }
        }
        return e;
    }

    @Override
    public JAnnotationInstanceList getAnnotations() {
        DefaultJAnnotationInstanceList a = new DefaultJAnnotationInstanceList();
        for (AnnInfo annotation : hType.getAnnotations()) {
            a.add(new JAnnotationInstanceFromIndex(annotation, indexer, types));
        }
        return a;
    }

    @Override
    public String getSourceName() {
        return hType.getSource();
    }


    public JField addField(String name, JType type, JModifier[] modifiers, JAnnotationInstance[] annotations, boolean redefine) {
        JField old = findDeclaredFieldOrNull(name);
        if (old != null) {
            if (redefine) {
                //old.dispose();
            } else {
                throw new IllegalArgumentException("field already declared : " + getName() + "." + name);
            }
        }
//        if (type == null) {
//            throw new IllegalArgumentException("Field type cannot be null : " + name() + "." + name);
//        }
        DefaultJField f = new DefaultJField();
        f.setDeclaringType(this);
        f.setName(name);
        f.setGenericType(type);
        ((DefaultJModifierList) f.getModifiers()).addAll(modifiers);
        ((DefaultJAnnotationInstanceList) f.getAnnotations()).addAll(annotations);
        if(fields==null){
            fields=new ArrayList<>();
        }
        fields.add(f);
        return f;
    }

    @Override
    public void addMethod(JMethod m) {
        methods.add(m);
    }
}
