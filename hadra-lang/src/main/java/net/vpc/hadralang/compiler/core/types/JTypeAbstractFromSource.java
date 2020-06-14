//package net.vpc.hadralang.compiler.core.types;
//
//import net.vpc.common.jeep.JArray;
//import net.vpc.common.jeep.JEvalException;
//import net.vpc.common.jeep.JType;
//import net.vpc.common.jeep.core.JStaticObject;
//import net.vpc.common.jeep.impl.types.host.AbstractJType;
//import net.vpc.hadralang.compiler.core.invokables.HLJCompilerContext;
//
//public abstract class JTypeAbstractFromSource extends AbstractJType {
//    protected HLJCompilerContext compilerContext;
//    public JTypeAbstractFromSource(HLJCompilerContext compilerContext) {
//        super(null);
//        this.compilerContext=compilerContext;
//    }
//    @Override
//    public JStaticObject staticObject() {
//        throw new JEvalException("Not evaluable");
//    }
//
//    @Override
//    public boolean isPrimitive() {
//        return false;
//    }
//
//    @Override
//    public JType toArrayImpl(int count) {
//        return new JTypeArrayFromSource((JTypeFromSource) rootComponentType(),arrayDimension()+count);
//    }
//
//    @Override
//    public Object cast(Object o) {
//        throw new JEvalException("Not evaluable");
//    }
//
//    @Override
//    public JType boxed() {
//        return this;
//    }
//
//    @Override
//    public Object newArray(int... len) {
//        throw new JEvalException("Not evaluable");
//    }
//
//    @Override
//    public JArray asArray(Object o) {
//        throw new JEvalException("Not evaluable");
//    }
//
//}
