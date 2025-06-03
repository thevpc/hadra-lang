package net.hl.compiler.core.types;

import net.hl.compiler.core.invokables.JTypeFromHIndex;
import net.hl.compiler.index.HIndexedClass;
import net.hl.compiler.index.HIndexer;
import net.thevpc.jeep.*;
import net.thevpc.jeep.impl.types.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class HLJTypes extends DefaultJTypes {
    private HIndexer indexer;

    public HLJTypes(JContext context, ClassLoader classLoader) {
        super(context, classLoader);
        DefaultJAnnotationInstanceList t = (DefaultJAnnotationInstanceList) forName("null").getAnnotations();
        t.add(HType.PUBLIC);
    }

    public HIndexer getIndexer() {
        return indexer;
    }

    public JType getRegisteredOrAliasCurrent(String jt, boolean checkAliases, boolean checkTypes) {
        JType jType = super.getRegisteredOrAliasCurrent(jt, checkAliases, checkTypes);
        if (jType != null) {
            return jType;
        }
        if (indexer != null) {
            HIndexedClass hIndexedClass = indexer.searchType(jt);
            if (hIndexedClass != null) {
                JTypeFromHIndex t = new JTypeFromHIndex(hIndexedClass, null, indexer, this);
                super.registerType(t);
                return t;
            }
        }
        return null;
    }

    public HLJTypes setIndexer(HIndexer indexer) {
        this.indexer = indexer;
        return this;
    }

    @Override
    public JType createArrayType0(JType root, int dim) {
        return super.createArrayType0(root, dim);
    }

    @Override
    public JType createNullType0() {
        return super.createNullType0();
    }

    @Override
    public JMutableRawType createMutableType0(String name, JTypeKind kind) {
        return new HType(name, kind, this);
    }

    @Override
    public JType createVarType0(String name, JType[] lowerBounds, JType[] upperBounds, JDeclaration declaration) {
        return super.createVarType0(name, lowerBounds, upperBounds, declaration);
    }

    @Override
    public JParameterizedType createParameterizedType0(JType rootRaw, JType[] parameters, JType declaringType) {
        return super.createParameterizedType0(rootRaw, parameters, declaringType);
    }


    @Override
    public JType createHostType0(String name) {
        switch (name) {
            case "Object":
            case "object":
                return createHostType0(Object.class);
            case "Date":
                return createHostType0(java.util.Date.class);
            case "Character":
                return createHostType0(Character.class);
            case "String":
            case "string":
                return createHostType0(String.class);
            case "stringb":
                return createHostType0(StringBuilder.class);
            case "Int":
            case "Integer":
                return createHostType0(Integer.class);
            case "Long":
                return createHostType0(Long.class);
            case "Double":
                return createHostType0(Double.class);
            case "Float":
                return createHostType0(Float.class);
            case "Short":
                return createHostType0(Short.class);
            case "Byte":
                return createHostType0(Byte.class);
            case "Boolean":
                return createHostType0(Boolean.class);
            case "Void":
                return createHostType0(Void.class);

            case "char":
                return createHostType0(char.class);
            case "int":
                return createHostType0(int.class);
            case "long":
                return createHostType0(long.class);
            case "double":
                return createHostType0(double.class);
            case "float":
                return createHostType0(float.class);
            case "short":
                return createHostType0(short.class);
            case "byte":
                return createHostType0(byte.class);
            case "bool":
            case "boolean":
                return createHostType0(boolean.class);
            case "void":
                return createHostType0(void.class);
            case "date":
                return createHostType0(LocalDate.class);
            case "datetime":
                return createHostType0(LocalDateTime.class);
            case "time":
                return createHostType0(LocalTime.class);
        }
        ClassLoader hostClassLoader = this.hostClassLoader();
        Class<?> t = null;
        try {
            //i should replace this witch
            if (hostClassLoader == null) {
                return createHostType0(Class.forName(name));
            } else {
                return createHostType0(Class.forName(name, false, hostClassLoader));
            }
        } catch (ClassNotFoundException e) {
            //
        }
        return null;
    }

    public boolean isPublic(JAnnotationInstanceList c) {
        if (c.contains(HType.PUBLIC)) {
            return true;
        }
        if (c.contains(HType.PRIVATE)) {
            return false;
        }
        if (c.contains(HType.PROTECTED)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isPublicType(JType c) {
        return isPublic(c.getAnnotations());
    }

    @Override
    public boolean isPublicConstructor(JConstructor c) {
        return isPublic(c.getAnnotations());
    }

    @Override
    public boolean isPublicMethod(JMethod c) {
        return isPublic(c.getAnnotations());
    }

    @Override
    public boolean isSyntheticMethod(JMethod c) {
        return super.isSyntheticMethod(c);
    }

    @Override
    public boolean isAbstractMethod(JMethod c) {
        return c.getAnnotations().contains(HType.ABSTRACT);
    }

    @Override
    public boolean isAbstractType(JType c) {
        return c.getAnnotations().contains(HType.ABSTRACT);
    }

    @Override
    public boolean isPublicField(JField c) {
        return isPublic(c.getAnnotations());
    }

    @Override
    public boolean isStaticType(JType c) {
        return c.getAnnotations().contains(HType.STATIC);
    }

    @Override
    public boolean isStaticMethod(JMethod c) {
        return c.getAnnotations().contains(HType.STATIC);
    }

    @Override
    public boolean isStaticField(JField c) {
        return c.getAnnotations().contains(HType.STATIC);
    }

    @Override
    public boolean isFinalField(JField c) {
        return c.getAnnotations().contains(HType.FINAL);
    }

}
