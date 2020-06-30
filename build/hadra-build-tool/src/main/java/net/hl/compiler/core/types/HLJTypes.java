package net.hl.compiler.core.types;

import net.vpc.common.jeep.JContext;
import net.vpc.common.jeep.JDeclaration;
import net.vpc.common.jeep.JParameterizedType;
import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.impl.types.DefaultJTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class HLJTypes extends DefaultJTypes {
    public HLJTypes(JContext context, ClassLoader classLoader) {
        super(context, classLoader);
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
    public JType createMutableType0(String name) {
        return new HType(name, this);
    }

    @Override
    public JType createVarType0(String name, JType[] lowerBounds, JType[] upperBounds, JDeclaration declaration) {
        return super.createVarType0(name, lowerBounds, upperBounds, declaration);
    }

    @Override
    public JParameterizedType createParameterizedType0(JType rootRaw, JType[] parameters, JType declaringType) {
        return super.createParameterizedType0(rootRaw, parameters, declaringType);
    }



    private JType forName0(Class name){
        return new HHostJClassType(name, this);
    }

    public JType createHostType0(String name){
        switch (name) {
            case "Object":
            case "object":
                return forName0(Object.class);
            case "Date":
                return forName0(java.util.Date.class);
            case "Character":
                return forName0(Character.class);
            case "String":
            case "string":
                return forName0(String.class);
            case "stringb":
                return forName0(StringBuilder.class);
            case "Int":
            case "Integer":
                return forName0(Integer.class);
            case "Long":
                return forName0(Long.class);
            case "Double":
                return forName0(Double.class);
            case "Float":
                return forName0(Float.class);
            case "Short":
                return forName0(Short.class);
            case "Byte":
                return forName0(Byte.class);
            case "Boolean":
                return forName0(Boolean.class);
            case "Void":
                return forName0(Void.class);

            case "char":
                return forName0(char.class);
            case "int":
                return forName0(int.class);
            case "long":
                return forName0(long.class);
            case "double":
                return forName0(double.class);
            case "float":
                return forName0(float.class);
            case "short":
                return forName0(short.class);
            case "byte":
                return forName0(byte.class);
            case "bool":
            case "boolean":
                return forName0(boolean.class);
            case "void":
                return forName0(void.class);
            case "date":
                return forName0(LocalDate.class);
            case "datetime":
                return forName0(LocalDateTime.class);
            case "time":
                return forName0(LocalTime.class);
        }
        ClassLoader hostClassLoader=this.hostClassLoader();
        Class<?> t = null;
        try {
            //i should replace this witch
            if (hostClassLoader == null) {
                return forName0(Class.forName(name));
            } else {
                return forName0(Class.forName(name, false, hostClassLoader));
            }
        } catch (ClassNotFoundException e) {
            //
        }
        return null;
    }
}
