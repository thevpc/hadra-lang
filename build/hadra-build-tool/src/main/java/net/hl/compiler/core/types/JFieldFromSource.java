//package net.hl.compiler.core.types;
//
//import net.vpc.common.jeep.JEvalException;
//import net.vpc.common.jeep.JField;
//import net.vpc.common.jeep.JType;
//import net.hl.compiler.parser.ast.HNDeclareIdentifier;
//import net.hl.compiler.core.invokables.HLJCompilerContext;
//import net.hl.compiler.utils.HUtils;
//
//public class JFieldFromSource implements JField {
//    private HNDeclareIdentifier declaration;
//    private HLJCompilerContext compilerContext;
//
//    public JFieldFromSource(HNDeclareIdentifier declaration, HLJCompilerContext compilerContext) {
//        this.declaration = declaration;
//        this.compilerContext = compilerContext;
//    }
//
//    @Override
//    public String name() {
//        return declaration.getName();
//    }
//
//    @Override
//    public JType type() {
//        return compilerContext.lookupType(declaration.getIdentifierTypeName());
//    }
//
//    @Override
//    public Object get(Object instance) {
//        throw new JEvalException("Not evaluable");
//    }
//
//    @Override
//    public void set(Object instance, Object value) {
//        throw new JEvalException("Not evaluable");
//    }
//
//    @Override
//    public boolean isPublic() {
//        return HUtils.isPublic(declaration.getModifiers());
//    }
//
//    @Override
//    public boolean isStatic() {
//        return HUtils.isStatic(declaration.getModifiers());
//    }
//
//    @Override
//    public JType declaringType() {
//        return declaration.getDeclaringTypeNode().getjType();
//    }
//}
