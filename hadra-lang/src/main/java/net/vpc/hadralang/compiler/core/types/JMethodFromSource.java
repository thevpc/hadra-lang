//package net.vpc.hadralang.compiler.core.types;
//
//import net.vpc.common.jeep.*;
//import net.vpc.common.jeep.impl.functions.JSignature;
//import net.vpc.hadralang.compiler.parser.ast.HNDeclareInvokable;
//import net.vpc.hadralang.compiler.utils.HUtils;
//
//public class JMethodFromSource implements JMethod {
//    private HNDeclareInvokable declaration;
//    private JCompilerContext compilerContext;;
//
//    public JMethodFromSource(HNDeclareInvokable declaration, JCompilerContext compilerContext) {
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
//    public JType declaringType() {
//        return declaration.getDeclaringType().getjType();
//    }
//
//    @Override
//    public boolean isStatic() {
//        return HUtils.isStatic(declaration.getModifiers());
//    }
//
//    @Override
//    public boolean isPublic() {
//        return HUtils.isPublic(declaration.getModifiers());
//    }
//
//    @Override
//    public Object invoke(JInvokeContext context) {
//        throw new JParseException("Not evaluable");
//    }
//
//    @Override
//    public JSignature signature() {
//        return declaration.getSignature();
//    }
//
//    @Override
//    public JType returnType() {
//        return linker.lookupType(declaration.getReturnTypeName(),compilerContext);
//    }
//}
