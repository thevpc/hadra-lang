//package net.hl.compiler.core.types;
//
//import net.vpc.common.jeep.*;
//import net.vpc.common.jeep.core.types.DefaultTypeName;
//import net.vpc.common.jeep.impl.functions.JSignature;
//import net.hl.compiler.parser.ast.HNExtends;
//import net.hl.compiler.parser.ast.HNDeclareInvokable;
//import net.hl.compiler.parser.ast.HNDeclareType;
//import net.hl.compiler.core.invokables.HLJCompilerContext;
//
//public class JTypeFromSource extends JTypeAbstractFromSource {
//    private HNDeclareType declaration;
//    private String globalName;
//    private String name;
//    private JInvoke instanceInitializer;
//    private JInvoke staticInitializer;
//
//    public JTypeFromSource(HNDeclareType declaration, HLJCompilerContext context) {
//        super(context);
//        this.declaration=declaration;
//        this.globalName=declaration.getGlobalName();
//        this.name=declaration.getName();
//    }
//
//    public HNDeclareType getDeclaration() {
//        return declaration;
//    }
//
//    @Override
//    public JType matchTypeName(JTypeName name) {
//        return null;
//    }
//
//    @Override
//    public String name() {
//        return globalName;
//    }
//
//    @Override
//    public JTypeName typeName() {
//        return DefaultTypeName.of(declaration.getName());
//    }
//
//    @Override
//    public String simpleName() {
//        return declaration.getName();
//    }
//
//    @Override
//    public JType rootComponentType() {
//        return null;
//    }
//
//    @Override
//    public JType superclass() {
//        if(!declaration.getExtends().isEmpty()){
//            HNExtends u = declaration.getExtends().get(0);
//            //u.getName()
//        }
//        return null;
//    }
//
//    @Override
//    public JType[] interfaces() {
//        return new JType[0];
//    }
//
//    @Override
//    public JConstructor[] publicConstructors() {
//        return new JConstructor[0];
//    }
//
//    @Override
//    public JConstructor defaultConstructorOrNull() {
//        return null;
//    }
//
//    @Override
//    public JConstructor defaultConstructor() {
//        return null;
//    }
//
//    @Override
//    public JConstructor[] declaredConstructors() {
//        return new JConstructor[0];
//    }
//
//    @Override
//    public JField declaredFieldOrNull(String fieldName) {
//        return declaration.getFieldDeclarations().stream()
//                .filter(x -> x.getName().equals(fieldName))
//                .map(x->(JField) x.getjField()).findFirst().orElse(null);
//    }
//
//    @Override
//    public JType parametrize(JType... parameters) {
//        return this;
//    }
//
//    @Override
//    public JMethod[] declaredMethods() {
//        return declaration.getMethodDeclarations().stream()
//                .map(x->(JMethod) x.getInvokable()).toArray(JMethod[]::new);
//    }
//
//    @Override
//    public JMethod[] declaredMethods(String name) {
//        return declaration.getMethodDeclarations().stream().filter(
//                x -> x.getName().equals(name)
//        ).map(x->(JMethod) x.getInvokable()).toArray(JMethod[]::new);
//    }
//
//    @Override
//    public JMethod declaredMethodOrNull(JSignature sig) {
//        return declaration.getMethodDeclarations().stream()
//                .filter(x -> x.getSignature().equals(sig))
//                .map(x->(JMethod) x.getInvokable()).findFirst().orElse(null);
//    }
//
//    @Override
//    public JMethod addMethod(JSignature signature, JType returnType, JInvoke handler, int modifiers, boolean redefine) {
//        throw new JParseException("Not evaluable");
//    }
//
//
//    @Override
//    public JConstructor declaredConstructorOrNull(JSignature sig) {
//        HNDeclareInvokable e = declaration.getConstructorDeclarations().stream().filter(
//                x -> x.getSignature().equals(sig)
//        ).findFirst().orElse(null);
//        if(e!=null){
//            return (JConstructor) e.getInvokable();
//        }
//        return null;
//    }
//
//    @Override
//    public JField addField(String name, JType type, int modifiers, boolean redefine) {
//        throw new JParseException("Not evaluable");
//    }
//
//    @Override
//    public JConstructor addConstructor(JSignature signature, JInvoke handler, int modifiers, boolean redefine) {
//        throw new JParseException("Not evaluable");
//    }
//
//    @Override
//    public Object defaultValue() {
//        return null;
//    }
//
//    @Override
//    public JType declaringType() {
//        return null;
//    }
//
//    @Override
//    public String packageName() {
//        return declaration.packageName();
//    }
//
//    public JInvoke instanceInitializer() {
//        return instanceInitializer;
//    }
//
//    public void instanceInitializer(JInvoke instanceInitializer) {
//        this.instanceInitializer = instanceInitializer;
//    }
//
//    public JInvoke staticInitializer() {
//        return staticInitializer;
//    }
//
//    public void staticInitializer(JInvoke classInitializer) {
//        this.staticInitializer = classInitializer;
//    }
//
//}
