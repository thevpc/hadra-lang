//package net.vpc.hadralang.compiler.core.types;
//
//import net.vpc.common.jeep.*;
//import net.vpc.common.jeep.impl.functions.JSignature;
//
//public class JTypeArrayFromSource extends JTypeAbstractFromSource {
//    private JTypeFromSource root;
//    private int dim;
//
//    public JTypeArrayFromSource(JTypeFromSource root, int dim) {
//        super(root.compilerContext);
//        this.root = root;
//        this.dim = dim;
//    }
//
//    @Override
//    public String name() {
//        StringBuilder s = new StringBuilder(root.name());
//        int dim = arrayDimension();
//        for (int i = 0; i < dim; i++) {
//            s.append("[]");
//        }
//        return s.toString();
//    }
//
//    @Override
//    public String simpleName() {
//        StringBuilder s = new StringBuilder(root.simpleName());
//        int dim = arrayDimension();
//        for (int i = 0; i < dim; i++) {
//            s.append("[]");
//        }
//        return s.toString();
//    }
//
//    @Override
//    public JType matchTypeName(JTypeName name) {
//        return null;
//    }
//
//    @Override
//    public JType superclass() {
//        return null;
//    }
//
//    @Override
//    public JType[] interfaces() {
//        return new JType[0];
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
//        return null;
//    }
//
//    @Override
//    public JType parametrize(JType... parameters) {
//        return null;
//    }
//
//    @Override
//    public JMethod[] declaredMethods() {
//        return new JMethod[0];
//    }
//
//    @Override
//    public JMethod declaredMethodOrNull(JSignature sig) {
//        return null;
//    }
//
//    @Override
//    public JMethod addMethod(JSignature signature, JType returnType, JInvoke handler, int modifiers, boolean redefine) {
//        return null;
//    }
//
//    @Override
//    public JConstructor declaredConstructorOrNull(JSignature sig) {
//        return null;
//    }
//
//    @Override
//    public JField addField(String name, JType type, int modifiers, boolean redefine) {
//        return null;
//    }
//
//    @Override
//    public JConstructor addConstructor(JSignature signature, JInvoke handler, int modifiers, boolean redefine) {
//        return null;
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
//        return root.packageName();
//    }
//}
