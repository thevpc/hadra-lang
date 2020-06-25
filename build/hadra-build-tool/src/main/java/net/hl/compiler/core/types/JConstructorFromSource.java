//package net.hl.compiler.core.types;
//
//import net.vpc.common.jeep.*;
//import net.vpc.common.jeep.impl.functions.JSignature;
//import net.hl.compiler.parser.ast.HNDeclareInvokable;
//import net.hl.compiler.utils.HUtils;
//
//public class JConstructorFromSource implements JConstructor {
//    private HNDeclareInvokable declaration;
//    private JCompilerContext compilerContext;;
//
//    public JConstructorFromSource(HNDeclareInvokable declaration, JCompilerContext compilerContext) {
//        this.declaration = declaration;
//        this.compilerContext = compilerContext;
//    }
//
//    @Override
//    public JType declaringType() {
//        return declaration.getDeclaringType().getjType();
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
