package net.vpc.hadralang.compiler.core;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.impl.DefaultJeepFactory;
import net.vpc.common.jeep.impl.types.DefaultJTypes;

public class HLJeepFactory extends DefaultJeepFactory {
    @Override
    public JTypes createTypes(JContext context, ClassLoader classLoader) {
        return new DefaultJTypes(context, classLoader){
            @Override
            public JType createHostType0(String name) {
                return super.createHostType0(name);
            }

            @Override
            public JType createMutableType0(String name) {
                return super.createMutableType0(name);
            }

            @Override
            public JType createVarType0(String name, JType[] lowerBounds, JType[] upperBounds, JDeclaration declaration) {
                return super.createVarType0(name, lowerBounds, upperBounds, declaration);
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
            public JParameterizedType createParameterizedType0(JType rootRaw, JType[] parameters, JType declaringType) {
                return super.createParameterizedType0(rootRaw, parameters, declaringType);
            }
        };
    }
}
